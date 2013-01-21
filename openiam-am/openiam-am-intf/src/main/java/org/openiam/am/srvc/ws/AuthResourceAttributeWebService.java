package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.dto.Attribute;
import org.openiam.am.srvc.dto.AttributeMap;
import org.openiam.am.srvc.searchbeans.AuthResourceAttributeSearchBean;
import org.openiam.base.ws.Response;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "AuthResourceAttributeWebService")
public interface AuthResourceAttributeWebService {
    @WebMethod
    AttributeMap getAttributeMap(@WebParam(name = "attributeId", targetNamespace = "") String attributeId) throws Exception;

    @WebMethod
    List<AttributeMap> getAllAttributeMapListByResourceId(@WebParam(name = "resourceId", targetNamespace = "") String resourceId) throws Exception;

    @WebMethod
    public List<AttributeMap> getAttributeMapListByResourceId(@WebParam(name = "resourceId", targetNamespace = "") String resourceId,
                                                              @WebParam(name = "from", targetNamespace = "") Integer from,
                                                              @WebParam(name = "size", targetNamespace = "") Integer size) throws Exception;
    @WebMethod
    public List<AttributeMap> getAllAttributeMapListBySearchCriteria(@WebParam(name = "searchBean", targetNamespace = "") AuthResourceAttributeSearchBean searchBean) throws Exception;
    @WebMethod
    public List<AttributeMap> getAttributeMapListBySearchCriteria(@WebParam(name = "searchBean", targetNamespace = "") AuthResourceAttributeSearchBean searchBean,
                                                                  @WebParam(name = "from", targetNamespace = "") Integer from,
                                                                  @WebParam(name = "size", targetNamespace = "") Integer size) throws Exception;

    @WebMethod
    Response addAttributeMap(@WebParam(name = "attribute", targetNamespace = "") AttributeMap attribute) throws Exception;

    @WebMethod
    Response addAttributeMapCollection(@WebParam(name = "attributeList", targetNamespace = "")  List<AttributeMap> attributeList) throws Exception;

    @WebMethod
    Response updateAttributeMap(@WebParam(name = "attribute", targetNamespace = "") AttributeMap attribute) throws Exception;

    @WebMethod
    Response removeAttributeMap(@WebParam(name = "attributeId", targetNamespace = "") String attributeId) throws Exception;


    @WebMethod
    Response removeResourceAttributeMaps(@WebParam(name = "resourceId", targetNamespace = "") String resourceId)throws Exception;



    @WebMethod
    List<Attribute> getSSOAttributes(@WebParam(name = "resourceId", targetNamespace = "") String resourceId,
                                     @WebParam(name = "principalName", targetNamespace = "") String principalName,
                                     @WebParam(name = "securityDomain", targetNamespace = "") String securityDomain,
                                     @WebParam(name = "managedSysId", targetNamespace = "")  String managedSysId);

    @WebMethod
    List<Attribute> getSSOAttributesByPages(@WebParam(name = "resourceId", targetNamespace = "") String resourceId,
                                            @WebParam(name = "principalName", targetNamespace = "") String principalName,
                                            @WebParam(name = "securityDomain", targetNamespace = "") String securityDomain,
                                            @WebParam(name = "managedSysId", targetNamespace = "")  String managedSysId,
                                            @WebParam(name = "from", targetNamespace = "") Integer from,
                                            @WebParam(name = "size", targetNamespace = "") Integer size);
    @WebMethod
    public Integer getNumOfAttributeMapList(@WebParam(name = "resourceId", targetNamespace = "") String resourceId) throws Exception;
    @WebMethod
    public Integer getNumOfAttributeMapListBySearchCriteria(@WebParam(name = "searchBean", targetNamespace = "") AuthResourceAttributeSearchBean searchBean) throws Exception;
    @WebMethod
    public Integer getNumOfSSOAttributes(@WebParam(name = "resourceId", targetNamespace = "") String resourceId)throws Exception;

}
