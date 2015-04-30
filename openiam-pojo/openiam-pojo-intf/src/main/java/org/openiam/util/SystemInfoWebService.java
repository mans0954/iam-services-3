package org.openiam.util;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.ServletContext;

@WebService(targetNamespace = "urn:idm.openiam.org/util/service", name = "SystemInfoWebService")
public interface SystemInfoWebService {

    @WebMethod
    public Boolean isDevelopmentMode();

    @WebMethod
    public String getWarManifestInfo(@WebParam(name = "attrName", targetNamespace = "") String attrName);

    @WebMethod
    public String getJarManifestInfo(@WebParam(name = "resName", targetNamespace = "") String resName,
                                  @WebParam(name = "attrName", targetNamespace = "") String attrName);

    @WebMethod
    public String getOsInfo(@WebParam(name = "param", targetNamespace = "") String param);

    @WebMethod
    public String getJavaInfo(@WebParam(name = "param", targetNamespace = "") String param);

    @WebMethod
    public String getMemInfo(@WebParam(name = "param", targetNamespace = "") String param);

    @WebMethod
    public Boolean isWindows();

    @WebMethod
    public Boolean isLinux();
    
    @WebMethod
    public String getProjectVersion();

}
