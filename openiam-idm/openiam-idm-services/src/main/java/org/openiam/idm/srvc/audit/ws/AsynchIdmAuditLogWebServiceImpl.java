package org.openiam.idm.srvc.audit.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.module.client.MuleClient;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.util.MuleContextProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import javax.jws.WebService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebService(endpointInterface = "org.openiam.idm.srvc.audit.ws.AsynchIdmAuditLogWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/audit/service",
		portName = "AsynchAuditDataServicePort",
		serviceName = "AsynchAuditDataService")
@Component("asyncAuditWS")
public class AsynchIdmAuditLogWebServiceImpl implements AsynchIdmAuditLogWebService {

    protected static final Log l = LogFactory.getLog(AsynchIdmAuditLogWebServiceImpl.class);

    @Value("${openiam.service_base}")
    private String serviceHost;
    
    @Value("${openiam.idm.ws.path}")
    private String serviceContext;


    public void createLog(IdmAuditLog log) {

         try {


            l.debug("MuleContext = " + MuleContextProvider.getCtx());


            Map<String, String> msgPropMap = new HashMap<String, String>();
            msgPropMap.put("SERVICE_HOST", serviceHost);
            msgPropMap.put("SERVICE_CONTEXT", serviceContext);


            //Create the client with the context
            MuleClient client = new MuleClient(MuleContextProvider.getCtx());
            client.sendAsync("vm://logAuditEvent", (IdmAuditLog) log, msgPropMap);

        } catch (Exception e) {
            l.debug("EXCEPTION:AsynchIdmAuditLogWebServiceImpl");
            l.error(e);

        }
    }



    public void createLinkedLogs( List<IdmAuditLog> logList) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
