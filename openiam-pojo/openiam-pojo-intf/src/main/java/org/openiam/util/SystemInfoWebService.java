package org.openiam.util;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletContext;

@WebService(targetNamespace = "urn:idm.openiam.org/util/service", name = "SystemInfoWebService")
public interface SystemInfoWebService {

    @WebMethod
    Boolean isDevelopmentMode();

    @WebMethod
    String getWarManifestInfo(@WebParam(name = "attrName", targetNamespace = "") String attrName);

    @WebMethod
    String getJarManifestInfo(@WebParam(name = "resName", targetNamespace = "") String resName,
                              @WebParam(name = "attrName", targetNamespace = "") String attrName);

    @WebMethod
    String getOsInfo(@WebParam(name = "param", targetNamespace = "") String param);

    @WebMethod
    String getJavaInfo(@WebParam(name = "param", targetNamespace = "") String param);

    @WebMethod
    String getMemInfo(@WebParam(name = "param", targetNamespace = "") String param);

    @WebMethod
    Boolean isWindows();

    @WebMethod
    Boolean isLinux();
    
    @WebMethod
    String getProjectVersion();

}
