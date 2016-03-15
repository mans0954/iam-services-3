package org.openiam.redis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.Tuple;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RedisTaskScheduler {

	private static final Log log = LogFactory.getLog(RedisTaskScheduler.class);

    private static final String SCHEDULE_KEY = "redis-scheduler.%s";
    private static final String DEFAULT_SCHEDULER_NAME = "scheduler";

    private StandardClock clock = new StandardClock();
    private RedisTemplate redisTemplate;
    private TaskTriggerListener taskTriggerListener;

    /**
     * Delay between each polling of the scheduled tasks. The lower the value, the best precision in triggering tasks.
     * However, the lower the value, the higher the load on Redis.
     */
    private int pollingDelayMillis = 10000;

    /**
     * If you need multiple schedulers for the same application, customize their names to differentiate in logs.
     */
    private String schedulerName = DEFAULT_SCHEDULER_NAME;

    private PollingThread pollingThread;
    private int maxRetriesOnConnectionFailure = 1;
    
    //private static final long NUM_OF_MS_IN_DAY = 24 * 3600 * 1000;
    private static final long NUM_OF_MS_IN_DAY = 100;

    @SuppressWarnings("unchecked")
    public void schedule(final String taskId, final Object object, final Date triggerTime) {
    	
    	/* add to sorted set, so that you can query based on timeout (score) */
        redisTemplate.opsForZSet().add(keyForScheduler(), taskId, triggerTime.getTime());
        
        /* create a new hash with the same key, so that you can refernece the object later */
        redisTemplate.opsForHash().put(taskId, taskId, object);
        
        /* expire in redis one day later, just in case */
        redisTemplate.expire(taskId, triggerTime.getTime() + NUM_OF_MS_IN_DAY, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    public void unschedule(String taskId) {

        redisTemplate.opsForZSet().remove(keyForScheduler(), taskId);
    }

    @SuppressWarnings("unchecked")
    public void unscheduleAllTasks() {
        redisTemplate.delete(keyForScheduler());
    }

    @PostConstruct
    public void initialize() {
        pollingThread = new PollingThread();
        pollingThread.setName(schedulerName + "-polling");

        pollingThread.start();

        log.info(String.format("[%s] Started Redis Scheduler (polling freq: [%sms])", schedulerName, pollingDelayMillis));
    }

    @PreDestroy
    public void destroy() {
        if (pollingThread != null) {
            pollingThread.requestStop();
        }
    }

    public void setTaskTriggerListener(TaskTriggerListener taskTriggerListener) {
        this.taskTriggerListener = taskTriggerListener;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setSchedulerName(String schedulerName) {
        this.schedulerName = schedulerName;
    }

    public void setPollingDelayMillis(int pollingDelayMillis) {
        this.pollingDelayMillis = pollingDelayMillis;
    }

    public void setMaxRetriesOnConnectionFailure(int maxRetriesOnConnectionFailure) {
        this.maxRetriesOnConnectionFailure = maxRetriesOnConnectionFailure;
    }

    private String keyForScheduler() {
        return String.format(SCHEDULE_KEY, schedulerName);
    }

    @SuppressWarnings("unchecked")
    private boolean triggerNextTaskIfFound() {

        return (Boolean) redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                boolean taskWasTriggered = false;
                final String key = keyForScheduler();

                redisOperations.watch(key);

                final Tuple<String, Object> tuple = findFirstTaskDueForExecution(redisOperations);
                final String taskId = tuple.getKey();

                if (taskId == null) {
                    redisOperations.unwatch();
                } else {
                    redisOperations.multi();
                    redisOperations.opsForZSet().remove(key, taskId);
                    redisOperations.opsForHash().delete(taskId);
                    boolean executionSuccess = (redisOperations.exec() != null);

                    if (executionSuccess) {
                        log.debug(String.format("[%s] Triggering execution of task [%s]", schedulerName, taskId));
                        tryTaskExecution(taskId, tuple.getValue());
                        taskWasTriggered = true;
                    } else {
                        log.warn(String.format("[%s] Race condition detected for triggering of task [%s]. " +
                                "The task has probably been triggered by another instance of this application.", schedulerName, taskId));
                    }
                }

                return taskWasTriggered;
            }
        });
    }

    private void tryTaskExecution(final String task, final Object object) {
        try {
            taskTriggerListener.taskTriggered(task, object);
        } catch (Exception e) {
            log.error(String.format("[%s] Error during execution of task [%s]", schedulerName, task), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Tuple<String, Object> findFirstTaskDueForExecution(RedisOperations ops) {
        final long minScore = 0;
        final long maxScore = clock.now().getTimeInMillis();

        // we unfortunately need to go wild here, the default API does not allow us to limit the number
        // of items returned by the ZRANGEBYSCORE operation.
        Set<byte[]> found = (Set<byte[]>) ops.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                String key = keyForScheduler();
                return redisConnection.zRangeByScore(key.getBytes(), minScore, maxScore, 0, 1);
            }
        });

        String taskId = null;
        if (found != null && !found.isEmpty()) {
            byte[] valueRaw = found.iterator().next();
            Object valueObj = ops.getValueSerializer().deserialize(valueRaw);
            taskId = (valueObj != null) ? valueObj.toString() : null;
        }
        final Object value = redisTemplate.boundHashOps(taskId).get(taskId);
        return new Tuple<String, Object>(taskId, value);
    }

    private class PollingThread extends Thread {
        private boolean stopRequested = false;
        private int numRetriesAttempted = 0;

        public void requestStop() {
            stopRequested = true;
        }

        @Override
        public void run() {
            try {
                while (!stopRequested && !isMaxRetriesAttemptsReached()) {

                    try {
                        attemptTriggerNextTask();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error(String.format(
                        "[%s] Error while polling scheduled tasks. " +
                        "No additional scheduled task will be triggered until the application is restarted.", schedulerName), e);
            }

            if (isMaxRetriesAttemptsReached()) {
                log.error(String.format("[%s] Maximum number of retries (%s) after Redis connection failure has been reached. " +
                        "No additional scheduled task will be triggered until the application is restarted.", schedulerName, maxRetriesOnConnectionFailure));
            } else {
                log.info("[%s] Redis Scheduler stopped");
            }
        }

        private void attemptTriggerNextTask() throws InterruptedException {
            try {
                boolean taskTriggered = triggerNextTaskIfFound();

                // if a task was triggered, we'll try again immediately. This will help to speed up the execution
                // process if a few tasks were due for execution.
                if (!taskTriggered) {
                    sleep(pollingDelayMillis);
                }

                resetRetriesAttemptsCount();
            } catch (RedisConnectionFailureException e) {
                incrementRetriesAttemptsCount();
                log.warn(String.format("Connection failure during scheduler polling (attempt %s/%s)", numRetriesAttempted, maxRetriesOnConnectionFailure));
            }
        }

        private boolean isMaxRetriesAttemptsReached() {
            return numRetriesAttempted >= maxRetriesOnConnectionFailure;
        }

        private void resetRetriesAttemptsCount() {
            numRetriesAttempted = 0;
        }

        private void incrementRetriesAttemptsCount() {
            numRetriesAttempted++;
        }
    }
    
    private static class StandardClock {

        public Calendar now() {
            return Calendar.getInstance();
        }
    }
}
