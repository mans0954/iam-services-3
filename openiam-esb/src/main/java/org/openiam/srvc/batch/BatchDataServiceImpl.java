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

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import org.openiam.base.request.*;
import org.openiam.base.response.data.BatchTaskResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.list.BatchTaskListResponse;
import org.openiam.base.response.list.BatchTaskScheduleListResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.BatchTaskScheduleSearchBean;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.batch.dto.BatchTaskSchedule;
import org.openiam.mq.constants.api.BatchTaskAPI;
import org.openiam.mq.constants.queue.common.BatchTaskQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BatchDataServiceImpl extends AbstractApiService implements BatchDataService {

	@Autowired
	public BatchDataServiceImpl(BatchTaskQueue queue) {
		super(queue);
	}

	@Override
	public Response save(final BatchTask task) {
		BatchTaskSaveRequest request = new BatchTaskSaveRequest(task);
		request.setPurgeNonExecutedTasks(true);
		return this.manageCrudApiRequest(BatchTaskAPI.Save, request);
	}

	@Override
	public BatchTask getBatchTask(String taskId) {
		IdServiceRequest request = new IdServiceRequest(taskId);
		return this.getValue(BatchTaskAPI.GetBatchTask, request, BatchTaskResponse.class);
	}

	@Override
	public Response removeBatchTask(String taskId) {
		BatchTask obj = new BatchTask();
		obj.setId(taskId);
		return this.manageCrudApiRequest(BatchTaskAPI.Delete, obj);
	}

	@Override
    @Transactional(readOnly = true)
	public List<BatchTask> findBeans(final BatchTaskSearchBean searchBean, final int from, final int size) {
		BaseSearchServiceRequest<BatchTaskSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
		return this.getValueList(BatchTaskAPI.FindBeans, request, BatchTaskListResponse.class);
	}
	@Override
	public int count(BatchTaskSearchBean searchBean) {
		BaseSearchServiceRequest<BatchTaskSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		return this.getValue(BatchTaskAPI.Count, request, IntResponse.class);
	}

	@Override
	public Response run(String id, boolean synchronous) {
		StartBatchTaskRequest request = new StartBatchTaskRequest();
		request.setId(id);
		request.setSynchronous(synchronous);
		return manageApiRequest(BatchTaskAPI.Run, request, Response.class);
	}

	@Override
	public Response schedule(String id, Date when) {
		StartBatchTaskRequest request = new StartBatchTaskRequest();
		request.setId(id);
		request.setWhen(when);
		return manageApiRequest(BatchTaskAPI.Schedule, request, Response.class);
	}

	@Override
	public List<BatchTaskSchedule> getSchedulesForTask(final BatchTaskScheduleSearchBean searchBean, final int from, final int size) {
		BaseSearchServiceRequest<BatchTaskScheduleSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
		return this.getValueList(BatchTaskAPI.GetSchedulesForTask, request, BatchTaskScheduleListResponse.class);
	}
	
	@Override
	public int getNumOfSchedulesForTask(BatchTaskScheduleSearchBean searchBean) {
		BaseSearchServiceRequest<BatchTaskScheduleSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		return this.getValue(BatchTaskAPI.GetNumOfSchedulesForTask, request, IntResponse.class);
	}

	@Override
	public Response deleteScheduledTask(String id) {
		BatchTaskSchedule obj = new BatchTaskSchedule();
		obj.setId(id);
		return this.manageCrudApiRequest(BatchTaskAPI.DeleteScheduledTask, obj);
	}
}
