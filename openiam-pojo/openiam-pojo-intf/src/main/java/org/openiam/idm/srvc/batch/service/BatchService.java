package org.openiam.idm.srvc.batch.service;

import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.List;

public interface BatchService {

    public int count(final BatchTaskEntity entity);

    public List<BatchTaskEntity> findBeans(final BatchTaskEntity entity, final int from, final int size);
    public List<BatchTaskEntity> findBeans(final BatchTaskSearchBean searchBean, final int from, final int size);
    public int count(BatchTaskScheduleSearchBean searchBean);
    
    public void save(final BatchTaskEntity entity, final boolean purgeNonExecutedTasks);

    public void delete(final String id);

    public BatchTaskEntity findById(final String id);
    
    public Runnable getRunnable(final String id, final List<BatchTaskScheduleEntity> scheduledTasks);
    
    public Trigger getCronTrigger(final String id);
    
    public void run(String id, boolean synchronous);
    
    public void schedule(final String id, final Date when);
    
    public boolean isScheduledNear(final String id, final Date when, final long leniancy);
    
    public void markTaskAsCompleted(final String scheduleId);
    
    public void markTaskAsRunning(final String scheduleId);
    
    public List<BatchTaskScheduleEntity> getSchedulesForTask(final BatchTaskScheduleSearchBean searchBean, final int from, final int size);
    
    public void deleteScheduledTask(final String id);
    
    public List<BatchTaskScheduleEntity> getIncompleteSchduledTasksBefore(final Date date);
}
