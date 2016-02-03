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
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
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

    private static Logger LOG = Logger.getLogger(IdmAuditLogWebDataServiceImpl.class);

    @Override
    public Response addLog(IdmAuditLog record) {
        final Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            if(record == null) {
                throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
            }

            auditLogService.enqueue(record);

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
	public Response addLogs(List<IdmAuditLog> events) {
		final Response resp = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(events == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		for(final IdmAuditLog log : events) {
    			auditLogService.enqueue(log);
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
	public List<IdmAuditLog> findBeans(final AuditLogSearchBean searchBean, final int from, final int size) {
		final List<IdmAuditLog> entityList = auditLogService.findBeans(searchBean, from, size, false);
		return entityList;
	}

    @Override
    public List<String> getIds(@WebParam(name = "searchBean", targetNamespace = "") AuditLogSearchBean searchBean, @WebParam(name = "from", targetNamespace = "") int from, @WebParam(name = "size", targetNamespace = "") int size) {
        final List<String> ids = auditLogService.findIDs(searchBean, from, size);
        return ids;
    }

    @Override
	public int count(final AuditLogSearchBean searchBean) {
		return auditLogService.count(searchBean);
	}

	@Override
	public IdmAuditLog getLogRecord(final String id) {
		final IdmAuditLog entity = auditLogService.findById(id);
		return entity;
	}

}
