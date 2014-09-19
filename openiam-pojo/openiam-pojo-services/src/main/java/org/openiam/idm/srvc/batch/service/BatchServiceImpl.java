package org.openiam.idm.srvc.batch.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.dao.BatchConfigDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.thread.BatchTaskGroovyThread;
import org.openiam.idm.srvc.batch.thread.BatchTaskSpringThread;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
public class BatchServiceImpl implements BatchService, ApplicationContextAware {
	
	private static final Log log = LogFactory.getLog(BatchServiceImpl.class);

    @Autowired
    private BatchConfigDAO batchDao;
    
    private ApplicationContext ctx;
    
    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;
    
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.ctx = applicationContext;
	}

    @Override
    @Transactional(readOnly=true)
    public List<BatchTaskEntity> findBeans(BatchTaskEntity entity, int from,
            int size) {
        return batchDao.getByExample(entity, from, size);
    }
    @Override
    @Transactional(readOnly=true)
    public List<BatchTaskEntity> findBeans(BatchTaskSearchBean searchBean, int from,
                                           int size) {
        return batchDao.getByExample(searchBean, from, size);
    }


    @Override
    @Transactional(readOnly=true)
    public int count(BatchTaskEntity entity) {
        return batchDao.count(entity);
    }

    @Override
    @Transactional
    public void save(BatchTaskEntity entity) {
        batchDao.save(entity);
    }

    @Override
    @Transactional
    public void delete(String id) {
        if (StringUtils.isNotBlank(id)) {
            final BatchTaskEntity entity = batchDao.findById(id);
            if (entity != null) {
                batchDao.delete(entity);
            }
        }
    }

    @Override
    @Transactional(readOnly=true)
    public BatchTaskEntity findById(String id) {
        return batchDao.findById(id);
    }

	@Override
	@Transactional(readOnly=true)
	public Runnable getRunnable(String id) {
		final BatchTaskEntity entity = batchDao.findById(id);
		Runnable runnable = null;
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getSpringBean()) && StringUtils.isNotBlank(entity.getSpringBeanMethod())) {
				return new BatchTaskSpringThread(entity, ctx);
			} else if(StringUtils.isNotBlank(entity.getTaskUrl())) {
				return new BatchTaskGroovyThread(entity, ctx);
			}
		}
		return runnable;
	}

	@Override
	@Transactional(readOnly=true)
	public Trigger getCronTrigger(String id) {
		final BatchTaskEntity entity = batchDao.findById(id);
		Trigger retVal = null;
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getCronExpression())) {
				try {
					return new CronTrigger(entity.getCronExpression());
				} catch(Throwable e) {
					log.error(String.format("Can't create CRON trigger from %s", entity), e);
				}
			}
		}
		return retVal;
	}

	@Override
	@Transactional(readOnly=true)
	public void run(String id, boolean synchronous) {
    	final Runnable runnable = getRunnable(id);
    	if(runnable != null) {
	        if(synchronous) {
	        	runnable.run();
	        } else {
	        	final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
	            transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
	            transactionTemplate.execute(new TransactionCallback<Void>() {
	
					@Override
					public Void doInTransaction(TransactionStatus status) {
						runnable.run();
						return null;
					}
	            	
				});
	        }
    	}
	}


}
