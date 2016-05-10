package org.openiam.imprt.key;


import org.openiam.idm.srvc.key.service.KeyManagementWS;
import org.openiam.idm.srvc.key.service.KeyManagementWS_Service;
import org.openiam.idm.srvc.key.service.Response;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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
       URL wsdlURL = KeyManagementWS_Service.WSDL_LOCATION;
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

    public Response generateKeysForUser(String userId) throws Exception{
        return this.getService().generateKeysForUser(userId);
    }

    public Response generateKeysForUserList(List<String> userIds) throws Exception{
        return this.getService().generateKeysForUserList(userIds);
    }
}
