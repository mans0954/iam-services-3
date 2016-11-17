/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.srvc.audit;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.IdmAuditLogListResponse;
import org.openiam.base.response.IdmAuditLogResponse;
import org.openiam.base.response.IntResponse;
import org.openiam.base.response.StringListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.mq.constants.api.AuditLogAPI;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.constants.queue.audit.AuditLogQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.util.AuditLogHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.srvc.audit.IdmAuditLogWebDataService", targetNamespace = "urn:idm.openiam.org/srvc/audit/service", portName = "AuditWebServicePort", serviceName = "AuditService")
@Service("auditWS")
public class IdmAuditLogWebDataServiceImpl extends AbstractApiService implements IdmAuditLogWebDataService {
	
	@Autowired
	private AuditLogHelper auditLogHelper;

	private static final Log LOG = LogFactory.getLog(IdmAuditLogWebDataServiceImpl.class);
	@Autowired
	public IdmAuditLogWebDataServiceImpl(AuditLogQueue queue) {
		super(queue);
	}

	@Override
    public Response addLog(IdmAuditLogEntity record) {
		auditLogHelper.enqueue(record);
        return new Response(ResponseStatus.SUCCESS);
    }

    @Override
	public Response addLogs(List<IdmAuditLogEntity> events) {
		if(CollectionUtils.isNotEmpty(events)) {
			for(final IdmAuditLogEntity log : events) {
				auditLogHelper.enqueue(log);
			}
		}
        return new Response(ResponseStatus.SUCCESS);
	}

	@Override
	public List<IdmAuditLogEntity> findBeans(final AuditLogSearchBean searchBean, final int from, final int size) {
		BaseSearchServiceRequest<AuditLogSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
		return this.getValueList(AuditLogAPI.FindBeans, request, IdmAuditLogListResponse.class);
	}

    @Override
    public List<String> getIds(AuditLogSearchBean searchBean, int from, int size) {
		BaseSearchServiceRequest<AuditLogSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
		return this.getValueList(AuditLogAPI.GetIds, request, StringListResponse.class);
    }

    @Override
	public int count(final AuditLogSearchBean searchBean) {
		BaseSearchServiceRequest<AuditLogSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
		return this.getValue(AuditLogAPI.Count, request, IntResponse.class);
	}

	@Override
	public IdmAuditLogEntity getLogRecord(final String id) {
		IdServiceRequest request = new IdServiceRequest(id);
		return this.getValue(AuditLogAPI.GetLogRecord, request, IdmAuditLogResponse.class);
	}

}
