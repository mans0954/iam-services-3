package org.openiam.srvc.encryption;

import org.openiam.base.ws.Response;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Created by: Alexander Duckardt
 * Date: 19.10.12
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/res/service", name = "KeyManagementWS")
public interface KeyManagementWS {
    @WebMethod
    Response initKeyManagement();
    @WebMethod
    Response generateMasterKey();
    @WebMethod
    Response migrateData(@WebParam(name = "secretKey") String secretKey);
    @WebMethod
    byte[] getCookieKey()throws Exception;
    @WebMethod
    byte[] generateCookieKey()throws Exception;
    @WebMethod
    String encryptData(@WebParam(name = "data") String data);
    @WebMethod
    String decryptData(@WebParam(name = "encryptedData") String encryptedData);
}
