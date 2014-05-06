package org.openiam.idm.srvc.key.ws;

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
    public Response initKeyManagement();
    @WebMethod
    public Response generateMasterKey();
    @WebMethod
    public Response migrateData(@WebParam(name = "secretKey")String secretKey);
    @WebMethod
    public byte[] getCookieKey()throws Exception;
    @WebMethod
    public byte[] generateCookieKey()throws Exception;
    @WebMethod
    public String encryptData(@WebParam(name = "data") String data);
    @WebMethod
    public String decryptData(@WebParam(name = "encryptedData") String encryptedData);
    @WebMethod
    public String encryptUserData(@WebParam(name = "userId") String userId, @WebParam(name = "data") String data);
    @WebMethod
    public String decryptUserData(@WebParam(name = "userId") String userId, @WebParam(name = "encryptedData") String encryptedData);
}
