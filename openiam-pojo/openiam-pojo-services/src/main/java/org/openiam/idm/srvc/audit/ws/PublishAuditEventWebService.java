package org.openiam.idm.srvc.audit.ws;

import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * Created by anton on 18.07.15.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/audit/service", name = "PublishAuditEventWebService")
public interface PublishAuditEventWebService {

    @WebMethod
    public void publishEvent(IdmAuditLog log) throws Exception;

    @WebMethod
    public boolean isAlive();
}
