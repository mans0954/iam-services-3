package org.openiam.idm.srvc.batch.service;

import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.springframework.scheduling.Trigger;

import java.util.List;

public interface BatchService {

    public int count(final BatchTaskEntity entity);

    public List<BatchTaskEntity> findBeans(final BatchTaskEntity entity,
            final int from, final int size);
    public List<BatchTaskEntity> findBeans(final BatchTaskSearchBean searchBean,
                                           final int from, final int size);

    public void save(final BatchTaskEntity entity);

    public void delete(final String id);

    public BatchTaskEntity findById(final String id);
    
    public Runnable getRunnable(final String id);
    
    public Trigger getCronTrigger(final String id);
    
    public void run(String id, boolean synchronous);
}
