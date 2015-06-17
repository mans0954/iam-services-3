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
    List<AuthResourceAMAttribute> getAmAttributeList();
    /*
    *==================================================
    * AuthResourceAttributeMap section
    *===================================================
    */
    @WebMethod
    AuthResourceAttributeMap getAttribute(@WebParam(name = "attributeMapId", targetNamespace = "") String attributeMapId);
    @WebMethod
    Response saveAttributeMap(@WebParam(name = "attributeMap", targetNamespace = "") AuthResourceAttributeMap attributeMap);
    @WebMethod
    Response removeAttributeMap(@WebParam(name = "attributeMapId", targetNamespace = "") String attributeMapId);
    @WebMethod
    List<SSOAttribute> getSSOAttributes(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                        @WebParam(name = "userId", targetNamespace = "") String userId);
}
