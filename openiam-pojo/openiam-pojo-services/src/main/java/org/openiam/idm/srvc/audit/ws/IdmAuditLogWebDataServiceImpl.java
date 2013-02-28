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

import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.dto.SearchAudit;
import org.openiam.idm.srvc.audit.service.IdmAuditLogDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    IdmAuditLogDataService auditDataService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService#addLog(org.openiam
     * .idm.srvc.audit.dto.IdmAuditLog)
     */
    public IdmAuditLogResponse addLog(IdmAuditLog log) {
        IdmAuditLogResponse resp = new IdmAuditLogResponse(
                ResponseStatus.SUCCESS);
        auditDataService.addLog(log);
        if (log.getLogId() != null) {
            resp.setLog(log);
        } else {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;
    }

    public void updateLog(IdmAuditLog log) {
        auditDataService.updateLog(log);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService#getCompleteLog()
     */
    public IdmAuditLogListResponse getCompleteLog() {
        IdmAuditLogListResponse resp = new IdmAuditLogListResponse(
                ResponseStatus.SUCCESS);
        List<IdmAuditLog> logList = auditDataService.getCompleteLog();
        if (logList != null) {
            resp.setLogList(logList);
            ;
        } else {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService#getPasswordChangeLog
     * ()
     */
    public IdmAuditLogListResponse getPasswordChangeLog() {
        IdmAuditLogListResponse resp = new IdmAuditLogListResponse(
                ResponseStatus.SUCCESS);
        List<IdmAuditLog> logList = auditDataService.getPasswordChangeLog();
        if (logList != null) {
            resp.setLogList(logList);
            ;
        } else {
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.audit.ws.IdmAuditLogWebDataService#search(org.openiam
     * .idm.srvc.audit.dto.SearchAudit)
     */
    public IdmAuditLogListResponse search(SearchAudit search) {
        return searchEvents(search, -1, -1);
    }

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
    public Integer countEvents(SearchAudit search){
        return auditDataService.countEvents(search);
    }

    public IdmAuditLogListResponse eventsAboutUser(String principal, Date startDate) {
        return searchEventsAboutUser(principal, startDate, null, -1,-1);
    }
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

    public Integer countEventsAboutUser(String principal, Date startDate, Date endDate){
        return auditDataService.countEventsAboutUser(principal, startDate, endDate);
    }

    public IdmAuditLogDataService getAuditDataService() {
        return auditDataService;
    }

    public void setAuditDataService(IdmAuditLogDataService auditDataService) {
        this.auditDataService = auditDataService;
    }

}
