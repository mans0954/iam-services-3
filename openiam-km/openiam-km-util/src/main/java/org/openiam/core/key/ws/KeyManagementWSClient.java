package org.openiam.core.key.ws;

import org.openiam.idm.srvc.res.service.KeyManagementWS;
import org.openiam.idm.srvc.res.service.KeyManagementWS_Service;
import org.openiam.idm.srvc.res.service.Response;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by: Alexander Duckardt
 * Date: 19.10.12
 */
public class KeyManagementWSClient {
    private String wsdlLocation;
    private KeyManagementWS service;


    public KeyManagementWSClient(){

    }
    public KeyManagementWSClient(String wsdlLocation){
        this.wsdlLocation=wsdlLocation;
    }

    private KeyManagementWS getService() throws Exception {
        if (service == null) {
            service = createService();
        }
        return service;
    }


    private KeyManagementWS createService() throws Exception {
       URL wsdlURL = KeyManagementWS_Service.getWsdlLocation();
       if (wsdlLocation!=null && !wsdlLocation.isEmpty()) {
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
       KeyManagementWS_Service ss = new KeyManagementWS_Service(wsdlURL);
       return ss.getKeyManagementWSPort();
    }

    public Response generateMasterKey() throws Exception{
        return this.getService().generateMasterKey();
    }
}
