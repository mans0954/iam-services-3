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
package org.openiam.idm.srvc.batch.service;

import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.BatchTaskDozerConverter;
import org.openiam.idm.srvc.batch.domain.BatchTaskEntity;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation for BatchDataService that will allow you to access and manage batch tasks.
 * @author suneet
 *
 */
@Service("batchDataService")
@WebService(endpointInterface = "org.openiam.idm.srvc.batch.service.BatchDataService", 
		targetNamespace = "urn:idm.openiam.org/srvc/batch/service", 
		portName = "BatchDataWebServicePort", 
		serviceName = "BatchDataWebService")
@Transactional
public class BatchDataServiceImpl implements BatchDataService {
	
	private static Logger LOG = Logger.getLogger(BatchDataServiceImpl.class);
	
	@Autowired
	private BatchTaskDozerConverter converter;
	
	@Autowired
	private BatchService batchService;
	
	@Override
	public List<BatchTask> getAllTasks() {
		final List<BatchTaskEntity> entityList = batchService.findBeans(new BatchTaskEntity(), 0, Integer.MAX_VALUE);
		return converter.convertToDTOList(entityList, true);
	}

	@Override
	public Response save(BatchTask task) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(task == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			final BatchTaskEntity entity = converter.convertToEntity(task, true);
			batchService.save(entity);
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			LOG.error("Can't save", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public BatchTask getBatchTask(String taskId) {
		final BatchTaskEntity entity = batchService.findById(taskId);
		return converter.convertToDTO(entity, true);
	}

	@Override
	public Response removeBatchTask(String taskId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if(taskId == null) {
				throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			}
			
			batchService.delete(taskId);
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			LOG.error("Can't save", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}
}
