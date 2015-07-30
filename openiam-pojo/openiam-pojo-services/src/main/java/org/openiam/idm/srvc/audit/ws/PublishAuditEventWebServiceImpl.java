package org.openiam.idm.srvc.audit.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;

import javax.jws.WebService;

import org.openiam.idm.srvc.audit.service.AuditEventHandlerFactory;
import org.openiam.idm.srvc.audit.service.ExportAuditEvent;
import org.openiam.idm.srvc.audit.service.IHEAuditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@WebService(endpointInterface = "org.openiam.idm.srvc.audit.ws.PublishAuditEventWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/audit/service",
        portName = "PublishAuditEventWebServicePort",
        serviceName = "PublishAuditEventService")
@Service("publishA")
public class PublishAuditEventWebServiceImpl implements PublishAuditEventWebService {
    protected static final Log l = LogFactory.getLog(PublishAuditEventWebServiceImpl.class);

    @Autowired
    protected  ExportAuditEvent iheAuditEvent;

    public void publishEvent(IdmAuditLog log) throws Exception{

        l.debug("PublishEvent operation called..");

        if (iheAuditEvent == null) {
            return;
        }

        try {
            iheAuditEvent.event(log);
        } catch (Exception e) {
            l.error(e.toString());
            throw new BasicDataServiceException(ResponseCode.FAIL_CONNECTION);

        }
    }

    public boolean isAlive() {
        l.debug("PublishEvent isAlive() called..");

        if (iheAuditEvent == null) {
            return false;
        }

        try {
            if (iheAuditEvent.isAlive()) {
                return true;
            }

        } catch (Exception e) {
            l.error(e.toString());
            return false;
        }
        return false;

    }

}
