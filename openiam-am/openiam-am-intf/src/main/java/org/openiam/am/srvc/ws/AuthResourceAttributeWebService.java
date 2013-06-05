package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.dto.AuthResourceAMAttribute;
import org.openiam.am.srvc.dto.AuthResourceAttributeMap;
import org.openiam.am.srvc.dto.SSOAttribute;
import org.openiam.base.ws.Response;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "AuthResourceAttributeWebService")
public interface AuthResourceAttributeWebService {
    /*
    *==================================================
    * AuthResourceAMAttribute section
    *===================================================
    */
    @WebMethod
    public AuthResourceAMAttribute getAmAttribute(@WebParam(name = "attributeId", targetNamespace = "") String attributeId);
    @WebMethod
    public List<AuthResourceAMAttribute> getAmAttributeList();
    @WebMethod
    public Response saveAmAttribute(@WebParam(name = "attribute", targetNamespace = "") AuthResourceAMAttribute attribute);
    @WebMethod
    public Response deleteAmAttribute(@WebParam(name = "attributeId", targetNamespace = "") String attributeId);

    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */
    @WebMethod
    public AuthResourceAttributeMap getAttributeMap(@WebParam(name = "attributeMapId", targetNamespace = "") String attributeMapId);
    @WebMethod
    public List<AuthResourceAttributeMap> getAttributeMapList(@WebParam(name = "providerId", targetNamespace = "") String providerId);
    @WebMethod
    public Response saveAttributeMap(@WebParam(name = "attributeMap", targetNamespace = "")AuthResourceAttributeMap attributeMap);
    @WebMethod
    public Response addAttributeMapCollection(@WebParam(name = "attributeMapList", targetNamespace = "") List<AuthResourceAttributeMap> attributeMapList);
    @WebMethod
    public Response removeAttributeMap(@WebParam(name = "attributeMapId", targetNamespace = "")String attributeMapId);
    @WebMethod
    public Response removeAttributeMaps(@WebParam(name = "providerId", targetNamespace = "")String providerId);
    @WebMethod
    public List<SSOAttribute> getSSOAttributes(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                               @WebParam(name = "userId", targetNamespace = "") String userId);
}
