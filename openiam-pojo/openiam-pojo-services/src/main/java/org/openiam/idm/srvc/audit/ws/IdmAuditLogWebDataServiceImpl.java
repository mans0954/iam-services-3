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
package org.openiam.idm.srvc.audit.ws;

import org.apache.log4j.Logger;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.AuditLogBuilderDozerConverter;
import org.openiam.dozer.converter.IdmAuditLogDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.AuditLogBuilderDto;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.SearchAudit;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.Date;
import java.util.List;

/**
 * @author suneet
 *
 */
@WebService(endpointInterface = "org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService", targetNamespace = "urn:idm.openiam.org/srvc/audit/service", portName = "AuditWebServicePort", serviceName = "AuditService")
@Service("auditWS")
public class IdmAuditLogWebDataServiceImpl implements IdmAuditLogWebDataService {
	
	@Autowired
	private AuditLogService auditLogService;
    
    @Autowired
    private IdmAuditLogDozerConverter converter;
    
    @Autowired
    private AuditLogBuilderDozerConverter auditBuilderConverter;
    
    private static Logger LOG = Logger.getLogger(IdmAuditLogWebDataServiceImpl.class);

	@Override
	public Response addLogs(List<AuditLogBuilderDto> logList) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(logList == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		for(final AuditLogBuilderDto log : logList) {
    			auditLogService.enqueue(auditBuilderConverter.convertToEntity(log, true));
    		}
    	} catch(BasicDataServiceException e) {
    		resp.fail();
    		resp.setErrorCode(e.getCode());
    		
    	} catch(Throwable e) {
    		LOG.error("Can't add log", e);
    		resp.fail();
    	}
        return resp;
	}

	@Override
	@Transactional(readOnly=true)
	public List<IdmAuditLog> findBeans(final AuditLogSearchBean searchBean, final int from, final int size) {
		final List<IdmAuditLogEntity> entityList = auditLogService.findBeans(searchBean, from, size);
		return converter.convertToDTOList(entityList, searchBean.isDeepCopy());
	}

	@Override
	public int count(final AuditLogSearchBean searchBean) {
		return auditLogService.count(searchBean);
	}

    /*
    @Override
    public IdmAuditLogListResponse searchEvents(SearchAudit search, Integer from, Integer size){
        IdmAuditLogListResponse resp = new IdmAuditLogListResponse(
                ResponseStatus.SUCCESS);
        List<IdmAuditLog> logList = auditDataService.search(search,from, size);
        if (logList != null) {
            resp.setLogList(logList);
        } else {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;
    }
    
    @Override
    public Integer countEvents(SearchAudit search){
        return auditDataService.countEvents(search);
    }

    @Override
    public IdmAuditLogListResponse eventsAboutUser(String principal, Date startDate) {
        return searchEventsAboutUser(principal, startDate, null, -1,-1);
    }
    
    @Override
    public IdmAuditLogListResponse searchEventsAboutUser(String principal, Date startDate, Date endDate, Integer from, Integer size){
        IdmAuditLogListResponse resp = new IdmAuditLogListResponse(
                ResponseStatus.SUCCESS);
        List<IdmAuditLog> logList = auditDataService.eventsAboutUser(principal, startDate, endDate, from, size);
        if (logList != null) {
            resp.setLogList(logList);
        } else {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;
    }

    @Override
    public Integer countEventsAboutUser(String principal, Date startDate, Date endDate){
        return auditDataService.countEventsAboutUser(principal, startDate, endDate);
    }
    */
}
