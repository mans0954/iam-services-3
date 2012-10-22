
package org.openiam.idm.srvc.res.service;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.1
 * 2012-10-19T20:37:11.673+04:00
 * Generated source version: 2.5.1
 * 
 */
public final class KeyManagementWS_KeyManagementWSPort_Client {

    private static final QName SERVICE_NAME = new QName("urn:idm.openiam.org/srvc/res/service", "KeyManagementWS");

    private KeyManagementWS_KeyManagementWSPort_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = KeyManagementWS_Service.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        KeyManagementWS_Service ss = new KeyManagementWS_Service(wsdlURL, SERVICE_NAME);
        KeyManagementWS port = ss.getKeyManagementWSPort();  
        
        {
        System.out.println("Invoking refreshUserKeys...");
        org.openiam.idm.srvc.res.service.Response _refreshUserKeys__return = port.refreshUserKeys();
        System.out.println("refreshUserKeys.result=" + _refreshUserKeys__return);


        }

        System.exit(0);
    }

}
