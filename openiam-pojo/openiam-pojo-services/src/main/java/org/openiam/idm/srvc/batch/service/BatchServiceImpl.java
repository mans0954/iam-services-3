package org.openiam.idm.srvc.batch.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.util.ReflectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.BatchTaskDozerConverter;
import org.openiam.dozer.converter.BatchTaskScheduleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.dao.BatchConfigDAO;
import org.openiam.idm.srvc.batch.dao.BatchScheduleDAO;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.openiam.idm.srvc.batch.thread.BatchTaskGroovyThread;
import org.openiam.idm.srvc.batch.thread.BatchTaskSpringThread;
import org.openiam.script.ScriptIntegration;
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

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class BatchServiceImpl implements BatchService, ApplicationContextAware {
	
	private static final Log log = LogFactory.getLog(BatchServiceImpl.class);

    @Autowired
    private BatchConfigDAO batchDao;
    
    private ApplicationContext ctx;
    
    @Autowired
    private BatchScheduleDAO batchScheduleDAO;
	@Autowired
	private BatchTaskDozerConverter converter;
	@Autowired
	private BatchTaskScheduleDozerConverter taskDozerConverter;
	@Autowired
	@Qualifier("configurableGroovyScriptEngine")
	private ScriptIntegration scriptRunner;
    
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
	public List<BatchTaskEntity> findEntityBeans(BatchTaskSearchBean searchBean, int from, int size) {
		return batchDao.getByExample(searchBean, from, size);
	}
    @Override
    @Transactional(readOnly=true)
    public List<BatchTask> findBeans(BatchTaskSearchBean searchBean, int from, int size) {
		List<BatchTaskEntity> entityList = batchDao.getByExample(searchBean, from, size);
		return converter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false);
    }

    @Override
    @Transactional
    public void save(BatchTaskEntity entity, final boolean purgeNonExecutedTasks) {
    	if(entity.getId() != null) {
    		final BatchTaskEntity dbEntity = batchDao.findById(entity.getId());
    		entity.setScheduledTasks(dbEntity.getScheduledTasks());
    		if(purgeNonExecutedTasks) {
    			if(entity.getScheduledTasks() != null) {
    				for(final Iterator<BatchTaskScheduleEntity> it = entity.getScheduledTasks().iterator(); it.hasNext();) {
    					final BatchTaskScheduleEntity schedule = it.next();
    					if(schedule != null && !schedule.isCompleted() && !schedule.isRunning()) {
    						it.remove();
    					} else {
    						schedule.setTask(entity);
    					}
    				}
    			}
    		}
    		
    		batchDao.merge(entity);
    	} else {
    		batchDao.save(entity);
    	}
    }
	@Override
	@Transactional
	public String save(final BatchTask task, final boolean purgeNonExecutedTasks) throws BasicDataServiceException {
		if(task == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}

		if(StringUtils.isBlank(task.getName())) {
			throw new BasicDataServiceException(ResponseCode.NO_NAME);
		}

		if(StringUtils.isBlank(task.getCronExpression()) && task.getRunOn() == null) {
			throw new BasicDataServiceException(ResponseCode.NO_EXEUCUTION_TIME);
		}

		if(StringUtils.isNotBlank(task.getCronExpression())) {
			try {
				new CronTrigger(task.getCronExpression());
			} catch(Throwable e) {
				throw new BasicDataServiceException(ResponseCode.INVALID_CRON_EXRPESSION);
			}
			task.setRunOn(null);
		}

		if(task.getRunOn() != null) {
			if(task.getRunOn().before(new Date())) {
				throw new BasicDataServiceException(ResponseCode.DATE_INVALID);
			}
			task.setCronExpression(null);
		}

		if(StringUtils.isBlank(task.getTaskUrl()) &&
		  (StringUtils.isBlank(task.getSpringBean()) || StringUtils.isBlank(task.getSpringBeanMethod()))) {
			throw new BasicDataServiceException(ResponseCode.SPRING_BEAN_OR_SCRIPT_REQUIRED);
		}

		if(StringUtils.isNotBlank(task.getTaskUrl())) {
			if(!scriptRunner.scriptExists(task.getTaskUrl())) {
				throw new BasicDataServiceException(ResponseCode.FILE_DOES_NOT_EXIST);
			}
			task.setSpringBean(null);
			task.setSpringBeanMethod(null);
		} else {
			boolean validBeanDefinition = false;
			try {
				if(ctx.containsBean(task.getSpringBean())) {
					final Object bean = ctx.getBean(task.getSpringBean());
					final Method method = ReflectionUtils.getMethod(bean, task.getSpringBeanMethod());
					if(method != null && method.getParameterTypes().length == 0) {
						validBeanDefinition = true;
						task.setTaskUrl(null);
					}
				}
			} catch(Throwable beanE) {
				validBeanDefinition = false;
			}

			if(!validBeanDefinition) {
				throw new BasicDataServiceException(ResponseCode.INVALID_SPRING_BEAN);
			}
		}
		final BatchTaskEntity entity = converter.convertToEntity(task, true);
		this.save(entity, purgeNonExecutedTasks);
		return entity.getId();
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
	public BatchTask findDto(final String id){
		return converter.convertToDTO(findById(id), true);
	}

	@Override
	@Transactional(readOnly=true)
	public Runnable getRunnable(final String id, final List<BatchTaskScheduleEntity> scheduledTasks) {
		final BatchTaskEntity entity = batchDao.findById(id);
		Runnable runnable = null;
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getSpringBean()) && StringUtils.isNotBlank(entity.getSpringBeanMethod())) {
				return new BatchTaskSpringThread(entity, ctx, scheduledTasks);
			} else if(StringUtils.isNotBlank(entity.getTaskUrl())) {
				return new BatchTaskGroovyThread(entity, ctx, scheduledTasks);
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
    	final Runnable runnable = getRunnable(id, null);
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

	@Override
	@Transactional
	public void schedule(String id, Date when) {
		final BatchTaskEntity entity = batchDao.findById(id);
		if(entity != null) {
			final BatchTaskScheduleEntity schedule = new BatchTaskScheduleEntity();
			schedule.setCompleted(false);
			schedule.setNextScheduledRun(when);
			schedule.setTask(entity);
			entity.addScheduledTask(schedule);
			batchDao.update(entity);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public boolean isScheduledNear(String id, Date when, long leniancy) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(when);
		calendar.roll(Calendar.MILLISECOND, (int)leniancy);
		final Date lo = calendar.getTime();
		calendar.add(Calendar.MILLISECOND, ((int)leniancy) * 2);
		final Date hi = calendar.getTime();
		
		final BatchTaskScheduleSearchBean sb = new BatchTaskScheduleSearchBean();
		sb.setCompleted(false);
		sb.setNextScheduledRunFrom(lo);
		sb.setNextScheduledRunTo(hi);
		return CollectionUtils.isNotEmpty(getSchedulesForTask(sb, 0, Integer.MAX_VALUE));
	}

	@Override
	@Transactional
	public void markTaskAsCompleted(String scheduleId) {
		final BatchTaskScheduleEntity entity = batchScheduleDAO.findById(scheduleId);
		if(entity != null) {
			entity.setRunning(false);
			entity.setCompleted(true);
			batchScheduleDAO.update(entity);
		}
	}
	
	@Override
	@Transactional
	public void markTaskAsRunning(String scheduleId) {
		final BatchTaskScheduleEntity entity = batchScheduleDAO.findById(scheduleId);
		if(entity != null) {
			entity.setRunning(true);
			batchScheduleDAO.update(entity);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<BatchTaskSchedule> getSchedulesForTask(final BatchTaskScheduleSearchBean searchBean,
													   int from, int size) {
		List<BatchTaskScheduleEntity> entityList = batchScheduleDAO.getByExample(searchBean, from, size);
		return taskDozerConverter.convertToDTOList(entityList, true);
	}
	
	@Override
	@Transactional(readOnly=true)
	public int count(BatchTaskScheduleSearchBean searchBean) {
		return batchScheduleDAO.count(searchBean);
	}

	@Override
	@Transactional
	public void deleteScheduledTask(String id) {
		final BatchTaskScheduleEntity entity = batchScheduleDAO.findById(id);
		if(entity != null) {
			batchScheduleDAO.delete(entity);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<BatchTaskScheduleEntity> getIncompleteSchduledTasksBefore(final Date date) {
		final BatchTaskScheduleSearchBean searchBean = new BatchTaskScheduleSearchBean();
		searchBean.setNextScheduledRunTo(date);
		searchBean.setCompleted(false);
		return batchScheduleDAO.getByExample(searchBean, 0, Integer.MAX_VALUE);
	}

	@Override
	public int count(BatchTaskSearchBean searchBean) {
		return batchDao.count(searchBean);
	}


}
