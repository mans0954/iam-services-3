package org.openiam.idm.srvc.batch.service;

import org.openiam.concurrent.OpenIAMRunnable;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.springframework.scheduling.Trigger;

import java.util.Date;
import java.util.List;

public interface BatchService {
    List<BatchTaskEntity> findEntityBeans(BatchTaskSearchBean searchBean, int from, int size);
    List<BatchTask> findBeans(final BatchTaskSearchBean searchBean, final int from, final int size);
    int count(BatchTaskScheduleSearchBean searchBean);
    int count(BatchTaskSearchBean searchBean);
    
    void save(final BatchTaskEntity entity, final boolean purgeNonExecutedTasks);
    String save(final BatchTask dto, final boolean purgeNonExecutedTasks) throws BasicDataServiceException;

    void delete(final String id);

    BatchTaskEntity findById(final String id);
    BatchTask findDto(final String id);
    
    OpenIAMRunnable getRunnable(final String id, final List<BatchTaskScheduleEntity> scheduledTasks);
    
    Trigger getCronTrigger(final String id);
    
    void run(String id, boolean synchronous);
    
    void schedule(final String id, final Date when);
    
    boolean isScheduledNear(final String id, final Date when, final long leniancy);
    
    void markTaskAsCompleted(final String scheduleId);
    
    void markTaskAsRunning(final String scheduleId);
    
    List<BatchTaskSchedule> getSchedulesForTask(final BatchTaskScheduleSearchBean searchBean, final int from, final int size);
    
    void deleteScheduledTask(final String id);
    
    List<BatchTaskScheduleEntity> getIncompleteSchduledTasksBefore(final Date date);
}
