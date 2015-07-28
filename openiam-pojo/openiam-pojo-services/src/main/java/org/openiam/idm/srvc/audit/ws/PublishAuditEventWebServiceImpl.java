package org.openiam.idm.srvc.audit.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import javax.jws.WebService;
import org.openiam.idm.srvc.audit.service.AuditEventHandlerFactory;
import org.openiam.idm.srvc.audit.service.ExportAuditEvent;
import org.springframework.stereotype.Service;


@WebService(endpointInterface = "org.openiam.idm.srvc.audit.ws.PublishAuditEventWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/audit/service",
		portName = "PublishAuditEventWebServicePort",
		serviceName = "PublishAuditEventService")
@Service("publishA")
public class PublishAuditEventWebServiceImpl implements PublishAuditEventWebService{
    protected static final Log l = LogFactory.getLog(PublishAuditEventWebServiceImpl.class);

    public void publishEvent(IdmAuditLog log) {

        l.debug("PublishEvent operation called..");

        ExportAuditEvent eventHandler =  AuditEventHandlerFactory.createInstance();
        if (eventHandler == null) {
            return;
        }

        try {
            eventHandler.event(log);
        }catch (Exception e) {
           l.error(e.toString());
        }
    }

    public boolean isAlive() {
        l.debug("PublishEvent isAlive() called..");

        ExportAuditEvent eventHandler =  AuditEventHandlerFactory.createInstance();
        if (eventHandler == null) {
            return false;
        }

        try {
            if (eventHandler.isAlive()) {
                return true;
            }

        }catch (Exception e) {
           l.error(e.toString());
           return false;
        }
        return false;

    }

}
