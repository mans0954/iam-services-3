/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.srvc.batch;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.util.ReflectionUtils;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.BatchTaskDozerConverter;
import org.openiam.dozer.converter.BatchTaskScheduleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.domain.BatchTaskScheduleEntity;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.openiam.idm.srvc.batch.service.BatchService;
import org.openiam.mq.constants.BatchTaskAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.script.ScriptIntegration;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation for BatchDataService that will allow you to access and manage batch tasks.
 * @author suneet
 *
 */
@Service("batchDataService")
@WebService(endpointInterface = "org.openiam.srvc.batch.BatchDataService",
		targetNamespace = "urn:idm.openiam.org/srvc/batch/service", 
		portName = "BatchDataWebServicePort", 
		serviceName = "BatchDataWebService")
//@Transactional
public class BatchDataServiceImpl extends AbstractApiService implements BatchDataService {
	
	private static final Log LOG = LogFactory.getLog(BatchDataServiceImpl.class);
	
	private ApplicationContext ctx;
	
	@Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
	
	@Autowired
	private BatchTaskDozerConverter converter;
	
	@Autowired
	private BatchTaskScheduleDozerConverter taskDozerConverter;
	
	@Autowired
	private BatchService batchService;

	public BatchDataServiceImpl() {
		super(OpenIAMQueue.BatchTaskQueue);
	}

	@Override
	public Response save(final BatchTask task) {
		BatchTaskSaveRequest request = new BatchTaskSaveRequest(task);
		request.setPurgeNonExecutedTasks(true);
		return this.manageGrudApiRequest(BatchTaskAPI.Save, request, StringResponse.class);
//		final Response response = new Response(ResponseStatus.SUCCESS);
//		try {
//			if(task == null) {
//				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
//			}
//
//			if(StringUtils.isBlank(task.getName())) {
//				throw new BasicDataServiceException(ResponseCode.NO_NAME);
//			}
//
//			if(StringUtils.isBlank(task.getCronExpression()) && task.getRunOn() == null) {
//				throw new BasicDataServiceException(ResponseCode.NO_EXEUCUTION_TIME);
//			}
//
//			if(StringUtils.isNotBlank(task.getCronExpression())) {
//				try {
//					new CronTrigger(task.getCronExpression());
//				} catch(Throwable e) {
//					throw new BasicDataServiceException(ResponseCode.INVALID_CRON_EXRPESSION);
//				}
//				task.setRunOn(null);
//			}
//
//			if(task.getRunOn() != null) {
//				if(task.getRunOn().before(new Date())) {
//					throw new BasicDataServiceException(ResponseCode.DATE_INVALID);
//				}
//				task.setCronExpression(null);
//			}
//
//			if(StringUtils.isBlank(task.getTaskUrl()) &&
//			  (StringUtils.isBlank(task.getSpringBean()) || StringUtils.isBlank(task.getSpringBeanMethod()))) {
//				throw new BasicDataServiceException(ResponseCode.SPRING_BEAN_OR_SCRIPT_REQUIRED);
//			}
//
//			if(StringUtils.isNotBlank(task.getTaskUrl())) {
//				if(!scriptRunner.scriptExists(task.getTaskUrl())) {
//					throw new BasicDataServiceException(ResponseCode.FILE_DOES_NOT_EXIST);
//				}
//				task.setSpringBean(null);
//				task.setSpringBeanMethod(null);
//			} else {
//				boolean validBeanDefinition = false;
//				try {
//					if(ctx.containsBean(task.getSpringBean())) {
//						final Object bean = ctx.getBean(task.getSpringBean());
//						final Method method = ReflectionUtils.getMethod(bean, task.getSpringBeanMethod());
//						if(method != null && method.getParameterTypes().length == 0) {
//							validBeanDefinition = true;
//							task.setTaskUrl(null);
//						}
//					}
//				} catch(Throwable beanE) {
//					validBeanDefinition = false;
//				}
//
//				if(!validBeanDefinition) {
//					throw new BasicDataServiceException(ResponseCode.INVALID_SPRING_BEAN);
//				}
//			}
//
//			final BatchTaskEntity entity = converter.convertToEntity(task, true);
//			batchService.save(entity, true);
//			response.setResponseValue(entity.getId());
//		} catch (BasicDataServiceException e) {
//			response.setErrorCode(e.getCode());
//			response.setStatus(ResponseStatus.FAILURE);
//		} catch (Throwable e) {
//			LOG.error("Can't save", e);
//			response.setErrorText(e.getMessage());
//			response.setStatus(ResponseStatus.FAILURE);
//		}
//		return response;
	}

	@Override
//    @Transactional(readOnly = true)
	public BatchTask getBatchTask(String taskId) {
		IdServiceRequest request = new IdServiceRequest(taskId);
		return this.getValue(BatchTaskAPI.GetBatchTask, request, BatchTaskResponse.class);
//		final BatchTaskEntity entity = batchService.findById(taskId);
//		return converter.convertToDTO(entity, true);
	}

	@Override
	public Response removeBatchTask(String taskId) {
		return this.manageGrudApiRequest(BatchTaskAPI.Delete, taskId);
//		final Response response = new Response(ResponseStatus.SUCCESS);
//		try {
//			if(taskId == null) {
//				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
//			}
//
//			batchService.delete(taskId);
//		} catch (BasicDataServiceException e) {
//			response.setErrorCode(e.getCode());
//			response.setStatus(ResponseStatus.FAILURE);
//		} catch (Throwable e) {
//			LOG.error("Can't save", e);
//			response.setErrorText(e.getMessage());
//			response.setStatus(ResponseStatus.FAILURE);
//		}
//		return response;
	}

	@Override
    @Transactional(readOnly = true)
	public List<BatchTask> findBeans(final BatchTaskSearchBean searchBean, final int from, final int size) {
		BaseSearchServiceRequest<BatchTaskSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
		return this.getValueList(BatchTaskAPI.FindBeans, request, BatchTaskListResponse.class);
//		final List<BatchTaskEntity> entityList = batchService.findBeans(searchBean, from, size);
//		return converter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false);
	}

//	@Override
//	public void setApplicationContext(final ApplicationContext ctx) throws BeansException {
//		this.ctx = ctx;
//	}

	@Override
	public int count(BatchTaskSearchBean searchBean) {
		BaseSearchServiceRequest<BatchTaskSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		return this.getValue(BatchTaskAPI.Count, request, IntResponse.class);
//		return batchService.count(searchBean);
	}

	@Override
	public Response run(String id, boolean synchronous) {
		StartBatchTaskRequest request = new StartBatchTaskRequest();
		request.setId(id);
		request.setSynchronous(synchronous);
		return manageApiRequest(BatchTaskAPI.Run, request, Response.class);

//		final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	batchService.run(id, synchronous);
//        } catch (Throwable e) {
//        	LOG.error("Can't validate resource", e);
//            response.setErrorText(e.getMessage());
//            response.setStatus(ResponseStatus.FAILURE);
//        }
//        return response;
	}

	@Override
	public Response schedule(String id, Date when) {
		StartBatchTaskRequest request = new StartBatchTaskRequest();
		request.setId(id);
		request.setWhen(when);
		return manageApiRequest(BatchTaskAPI.Schedule, request, Response.class);
//		final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	batchService.schedule(id, when);
//        } catch (Throwable e) {
//        	LOG.error("Can't schedule task", e);
//            response.setErrorText(e.getMessage());
//            response.setStatus(ResponseStatus.FAILURE);
//        }
//        return response;
	}

	@Override
	public List<BatchTaskSchedule> getSchedulesForTask(final BatchTaskScheduleSearchBean searchBean, final int from, final int size) {
		BaseSearchServiceRequest<BatchTaskScheduleSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
		return this.getValueList(BatchTaskAPI.GetSchedulesForTask, request, BatchTaskScheduleListResponse.class);
//
//		final List<BatchTaskScheduleEntity> entityList = batchService.getSchedulesForTask(searchBean, from, size);
//		return taskDozerConverter.convertToDTOList(entityList, true);
	}
	
	@Override
	public int getNumOfSchedulesForTask(BatchTaskScheduleSearchBean searchBean) {
		BaseSearchServiceRequest<BatchTaskScheduleSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		return this.getValue(BatchTaskAPI.GetNumOfSchedulesForTask, request, IntResponse.class);
//		return batchService.count(searchBean);
	}

	@Override
	public Response deleteScheduledTask(String id) {
		return this.manageGrudApiRequest(BatchTaskAPI.DeleteScheduledTask, id);
//		final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	batchService.deleteScheduledTask(id);
//        } catch (Throwable e) {
//        	LOG.error("Can't schedule task", e);
//            response.setErrorText(e.getMessage());
//            response.setStatus(ResponseStatus.FAILURE);
//        }
//        return response;
	}
}
