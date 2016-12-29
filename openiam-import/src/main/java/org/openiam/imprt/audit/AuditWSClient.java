package org.openiam.imprt.audit;


import org.openiam.idm.srvc.audit.service.*;
import org.openiam.idm.srvc.key.service.KeyManagementWS_Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author zaporozhec
 */
public class AuditWSClient {
    private String wsdlLocation;
    private AuditDataService service;


    public AuditWSClient() {

    }

    public AuditWSClient(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    private AuditDataService getService() throws Exception {
        if (service == null) {
            service = createService();
        }
        return service;
    }


    private AuditDataService createService() throws Exception {
        URL wsdlURL = KeyManagementWS_Service.WSDL_LOCATION;
        if (wsdlLocation != null && !wsdlLocation.isEmpty()) {
            File wsdlFile = new File(wsdlLocation);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(wsdlLocation);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        AuditService ss = new AuditService(wsdlURL);
        return ss.getAuditWebServicePort();
    }

    public Response addLog(IdmAuditLog log)  {
        Response response = null;
        try {
            response = this.getService().addLog(log);
        } catch (Exception e) {
            response = new Response();
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
}
