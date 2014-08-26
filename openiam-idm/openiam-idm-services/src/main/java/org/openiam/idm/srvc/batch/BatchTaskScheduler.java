package org.openiam.idm.srvc.batch;

import java.net.ConnectException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.module.client.MuleClient;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.service.BatchService;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("batchTaskScheduler")
public class BatchTaskScheduler implements InitializingBean, Sweepable {
	
	private Map<String, ScheduledFuture<Void>> synchronizedBatchScheduleMap = new ConcurrentHashMap<String, ScheduledFuture<Void>>();
	
	private static final Log log = LogFactory.getLog(BatchTaskScheduler.class);
	
	@Autowired
	private BatchService batchService;
    
    @Autowired
    @Qualifier("batchTaskInternalScheduler")
    private ThreadPoolTaskScheduler taskScheduler;

    @Value("${IS_PRIMARY}")
    private boolean isPrimary;
    
    private Date lastRun = null;

	@Transactional
	public void sweep() {
		if(shouldRun()) {
			final List<BatchTaskEntity> batchList = batchService.findBeans(new BatchTaskEntity(), 0, Integer.MAX_VALUE);
			final Map<String, BatchTaskEntity> batchMap = new HashMap<String, BatchTaskEntity>();
			if(CollectionUtils.isNotEmpty(batchList)) {
				for(final BatchTaskEntity entity : batchList) {
					batchMap.put(entity.getId(), entity);
				}
				
				//first, remove what's been deleted, or has been inactivated
				final Set<String> currentScheduledTasks = synchronizedBatchScheduleMap.keySet();
				if(CollectionUtils.isNotEmpty(currentScheduledTasks)) {
					for(final String id : currentScheduledTasks) {
						final BatchTaskEntity entity = batchMap.get(id);
						if(entity == null || !entity.isEnabled()) {
							unSchedule(id, true);
						}
					}
				}
				
				//TODO: run the tasks that are overdue
				
				
				//handle new and existing tasks
				for(final BatchTaskEntity entity : batchList) {
					if(entity.isEnabled()) {
						if(!currentScheduledTasks.contains(entity.getId())) {
							
							//schedule new tasks
							schedule(entity);
						} else {
							//for existing tasks:
							// a) if the BatchTask was modified since the last run, cancel the current task, and schedule a new one
							boolean modified = lastRun != null && entity.getLastModifiedDate() != null && entity.getLastModifiedDate().after(lastRun);
							
							if(modified) {
								unSchedule(entity, false);
								schedule(entity);
							} else {
								//if the Task is not a cron job, remove if it's done running
								if(entity.getCronExpression() == null && isDone(entity)) {
									unSchedule(entity, true);
								}
								
								//now, it's a cron job that hasn't been modified.  Just let it run
							}
						}
					}
				}
			} else {
				unscheduleAll(false);
			}
		} else  {
			unscheduleAll(false);
		}
		
		lastRun = new Date();
	}
	
	public boolean isDone(final BatchTaskEntity entity) {
		final ScheduledFuture<Void> future = synchronizedBatchScheduleMap.get(entity.getId());
		return (future != null) ? future.isDone() : false;
	}
	
	public boolean unSchedule(final BatchTaskEntity entity, boolean forceInterrupt) {
		boolean success = true;
		final ScheduledFuture<Void> future = synchronizedBatchScheduleMap.remove(entity.getId());
		if(future != null) {
			try {
				future.cancel(forceInterrupt);
			} catch(Throwable e) {
				success = false;
				final String message = String.format("Can't cancel task with ID: %s", entity.getId());
				log.error(message, e);
			}
		}
		return success;
	}
	
	public boolean unSchedule(final String id, boolean forceInterrupt) {
		boolean success = true;
		final ScheduledFuture<Void> future = synchronizedBatchScheduleMap.remove(id);
		if(future != null) {
			try {
				future.cancel(forceInterrupt);
			} catch(Throwable e) {
				success = false;
				log.error(String.format("Can't cancel task with ID: %s", id), e);
			}
		}
		return success;
	}
	
	public boolean isScheduled(final BatchTaskEntity entity) {
		return (entity != null) ? synchronizedBatchScheduleMap.containsKey(entity) : false;
	}
	
	public void schedule(final BatchTaskEntity entity) {
		if(entity != null) {
			boolean unScheduled = true;
			if(isScheduled(entity)) {
				unScheduled = unSchedule(entity, false);
			}
			if(unScheduled) {
				try {
					final Runnable runnable = batchService.getRunnable(entity.getId());
					if(runnable != null) {
						if(entity.getCronExpression() == null && entity.getRunOn() != null) {
							if(entity.getRunOn().after(new Date())) {
								final ScheduledFuture<Void> future = taskScheduler.schedule(runnable, entity.getRunOn());
								synchronizedBatchScheduleMap.put(entity.getId(), future);
							}
						} else {
							final Trigger trigger = batchService.getCronTrigger(entity.getId());
							if(trigger != null) {
								final ScheduledFuture<Void> future = taskScheduler.schedule(runnable, trigger);
								synchronizedBatchScheduleMap.put(entity.getId(), future);
							}
						}
					}
				} catch(Throwable e) {
					log.error(String.format("Can't schedule task: %s", entity), e);
				}
			} else {
				log.warn(String.format("Could not unschedule previous task - not scheduling %s", entity));
			}
		}
	}
	
	public void schedule(final String id) {
		schedule(batchService.findById(id));
	}
	
	private void unscheduleAll(final boolean forceInterrupt) {
		final Set<String> currentTasks = synchronizedBatchScheduleMap.keySet();
		for(final String id : currentTasks) {
			unSchedule(id, forceInterrupt);
		}
	}
	
	private boolean shouldRun() {
		//return (isPrimary || (!isPrimary && !isPrimaryNodeAlive()));
		return isPrimary;
	}
	
	/*
	private boolean isPrimaryNodeAlive() {
        Map<String, String> msgPropMap = new HashMap<String, String>();
        msgPropMap.put("SERVICE_HOST", serviceHost);
        msgPropMap.put("SERVICE_CONTEXT", serviceContext);

        // Create the client with the context
        try {

            MuleClient client = new MuleClient(muleContext);
            client.send("vm://heartBeatIsAlive", null, msgPropMap);

        } catch (Exception ce) {
            log.error(ce.toString());

            if (ce instanceof ConnectException) {
                return false;
            }

        }
        return true;
    }
    */

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info(taskScheduler);
		sweep();
	}
	
}
