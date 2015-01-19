package org.openiam.idm.srvc.batch;

import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.exception.LockObtainException;
import org.openiam.exception.UnlockException;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.batch.dao.BatchConfigDAO;
import org.openiam.idm.srvc.batch.dao.LockTableDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.openiam.idm.srvc.batch.service.LockService;
import org.openiam.idm.srvc.batch.thread.BatchTaskGroovyThread;
import org.openiam.idm.srvc.batch.thread.BatchTaskSpringThread;
import org.openiam.script.ScriptIntegration;
import org.openiam.thread.Sweepable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Component("batchTaskScheduler")
public class BatchTaskScheduler extends AbstractBaseService implements InitializingBean {
	
	private static final Log log = LogFactory.getLog(BatchTaskScheduler.class);
	
	@Autowired
	private BatchService batchService;
	
    @Autowired
    private BatchConfigDAO batchDao;
    
    @Autowired
    @Qualifier("batchTaskThreadExecutor")
    private ThreadPoolTaskExecutor batchTaskThreadExecutor;
    
    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    
    @Autowired
    private LockService lockService;
    
    @Scheduled(fixedRateString="${org.openiam.batch.task.execute.time}")
    public void executeTasks() throws LockObtainException, UnlockException {
    	final StopWatch sw = new StopWatch();
    	sw.start();
    	lockService.lock("BATCH_TASK_RUNNER");
    	log.info("Starting batch executeTasks() operation");
    	try {
    		final Date now = new Date();
    		
    		//execute tasks that are before now, but haven't yet been run
    		final List<BatchTaskScheduleEntity> scheduledTaskList = batchService.getIncompleteSchduledTasksBefore(now);
    		final Map<String, List<BatchTaskScheduleEntity>> scheduledTaskMap = new HashMap<String, List<BatchTaskScheduleEntity>>();
    		if(scheduledTaskList != null) {
    			scheduledTaskList.forEach(scheduledTask -> {
    				final String taskId = scheduledTask.getTask().getId();
    				if(!scheduledTaskMap.containsKey(taskId)) {
    					scheduledTaskMap.put(taskId, new LinkedList<BatchTaskScheduleEntity>());
    				}
    				scheduledTaskMap.get(taskId).add(scheduledTask);
    			});
    		}
    		scheduledTaskMap.forEach((taskId, scheduledTasks) -> {
    			/*
    			final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
    	        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
    	        transactionTemplate.execute(new TransactionCallback<Boolean>() {

					@Override
					public Boolean doInTransaction(TransactionStatus status) {
						scheduledTasks.forEach(scheduledTask -> {
							batchService.markTaskAsRunning(scheduledTask.getId());
		    			});
						return null;
					}
    	        	
    	        });
    	        */
    			execute(taskId, scheduledTasks);
    		});
    		
    	} finally {
    		sw.stop();
    		log.info(String.format("Finished batch executeTasks() operation. Took:  %s ms", sw.getTime()));
    		lockService.unlock("BATCH_TASK_RUNNER");
        }
    }
    
    public void execute(final String id, final List<BatchTaskScheduleEntity> scheduledTaskList) {
		final Runnable task = batchService.getRunnable(id, scheduledTaskList);
		if(task != null) {
			batchTaskThreadExecutor.execute(task);
		}
    }
    
	@Scheduled(fixedRateString="${org.openiam.batch.task.sweep.time}")
	public void sweep() throws LockObtainException, UnlockException {
		final StopWatch sw = new StopWatch();
    	sw.start();
    	log.info("Starting batch sweep() operation");
		final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
        transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
		
        lockService.lock("BATCH_SCHEDULER");
        try {
        	transactionTemplate.execute(new TransactionCallback<Boolean>() {
        		@Override
        		public Boolean doInTransaction(TransactionStatus status) {
        			final BatchTaskSearchBean searchBean = new BatchTaskSearchBean();
        			searchBean.setEnabled(true);
        			final List<BatchTaskEntity> batchList = batchService.findBeans(searchBean, 0, Integer.MAX_VALUE);
        			//final Map<String, BatchTaskEntity> batchMap = new HashMap<String, BatchTaskEntity>();
        			if(CollectionUtils.isNotEmpty(batchList)) {
        				for(final BatchTaskEntity entity : batchList) {
        					schedule(entity);
        					final IdmAuditLog idmAuditLog = new IdmAuditLog();
        					auditLogService.enqueue(idmAuditLog);
        					batchDao.save(entity);
        				}
        			}
        			return null;
        		}
        	});
        } finally {
        	sw.stop();
        	log.info(String.format("Finished batch sweep() operation. Took: %s ms", sw.getTime()));
        	lockService.unlock("BATCH_SCHEDULER");
        }
	}
	
	public void schedule(final BatchTaskEntity entity) {
		final Date now = new Date();
		try {
			if(entity.getRunOn() != null) {
				if(entity.getRunOn().after(now)) {
					if(!batchService.isScheduledNear(entity.getId(), entity.getRunOn(), 30000)) {
						final BatchTaskScheduleEntity schedule = new BatchTaskScheduleEntity();
						schedule.setCompleted(false);
						schedule.setNextScheduledRun(entity.getRunOn());
						schedule.setTask(entity);
						entity.addScheduledTask(schedule);
					}
				}
			} else if(entity.getCronExpression() != null) {
				final Trigger trigger = batchService.getCronTrigger(entity.getId());
				if(trigger != null) {
					Date lastExecutionTime = entity.getLastExecTime();
					
					/* schedule next 10 */
					Date date = getNextExecutionTimeAfterNow(now, lastExecutionTime, trigger);
					for(int i = 0; i < 10; i++) {
						if(!batchService.isScheduledNear(entity.getId(), date, 30000)) {
							final BatchTaskScheduleEntity schedule = new BatchTaskScheduleEntity();
							schedule.setCompleted(false);
							schedule.setNextScheduledRun(date);
							schedule.setTask(entity);
							entity.addScheduledTask(schedule);
						}
						date = getNextExecutionTimeAfterNow(now, date, trigger);
					}
				}
			}
		} catch(Throwable e) {
			log.error(String.format("Can't schedule task: %s", entity), e);
		}
	}
	
	private Date getNextExecutionTimeAfterNow(final Date now, final Date lastExecutionTime, final Trigger trigger) {
		final TriggerContext triggerContext = new SimpleTriggerContext(lastExecutionTime, lastExecutionTime, lastExecutionTime); 
		final Date nextExecutionTime = trigger.nextExecutionTime(triggerContext);
		if(!nextExecutionTime.after(now)) {
			return getNextExecutionTimeAfterNow(now, nextExecutionTime, trigger);
		} else { /* is after */
			return nextExecutionTime;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		sweep();
	}
	
}
