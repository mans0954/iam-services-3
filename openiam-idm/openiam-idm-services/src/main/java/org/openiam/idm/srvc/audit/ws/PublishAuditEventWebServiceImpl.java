package org.openiam.idm.srvc.audit.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import javax.jws.WebService;
import org.openiam.idm.srvc.audit.export.ExportAuditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@WebService(endpointInterface = "org.openiam.idm.srvc.audit.ws.PublishAuditEventWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/audit/service",
		portName = "PublishAuditEventWebServicePort",
		serviceName = "PublishAuditEventService")
@Component("asyncPublishAuditWS")
public class PublishAuditEventWebServiceImpl implements PublishAuditEventWebService{
    protected static final Log l = LogFactory.getLog(PublishAuditEventWebServiceImpl.class);

    @Autowired
    @Qualifier("configurableAuditLogEventHandler")
    private ExportAuditEvent eventHandler;
    
    public void publishEvent(IdmAuditLog log) {

        l.debug("PublishEvent operation called..");

        try {
            eventHandler.event(log);
        }catch (Exception e) {
           l.error(e.toString());
        }
    }

    public boolean isAlive() {
        l.debug("PublishEvent isAlive() called..");

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
