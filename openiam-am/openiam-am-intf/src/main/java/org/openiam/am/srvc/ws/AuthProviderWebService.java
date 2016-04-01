package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbeans.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.continfo.dto.Phone;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/am/service", name = "AuthProviderWebService")
public interface AuthProviderWebService {
    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */

    @WebMethod
    public Response addProviderType(@WebParam(name = "entity", targetNamespace = "")AuthProviderType entity);


    @WebMethod
    List<AuthAttribute> findAuthAttributeBeans(@WebParam(name = "searchBean", targetNamespace = "") AuthAttributeSearchBean searchBean,
                                               @WebParam(name = "from", targetNamespace = "") int from,
                                               @WebParam(name = "size", targetNamespace = "") int size);
    @WebMethod
    AuthProviderType getAuthProviderType(@WebParam(name = "providerType", targetNamespace = "") String providerType);
    @WebMethod
    List<AuthProviderType> getAuthProviderTypeList();
    @WebMethod
    List<AuthProviderType> getSocialAuthProviderTypeList();

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @WebMethod
    int countAuthProviderBeans(@WebParam(name = "searchBean", targetNamespace = "") AuthProviderSearchBean searchBean);
    
    @WebMethod
    AuthProvider getAuthProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);
    
    @WebMethod
    List<AuthProvider> findAuthProviderBeans(@WebParam(name = "searchBean", targetNamespace = "") AuthProviderSearchBean searchBean,
                                             @WebParam(name = "from", targetNamespace = "") int from,
                                             @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    Response saveAuthProvider(@WebParam(name = "provider", targetNamespace = "") AuthProvider provider,
                              @WebParam(name = "requestorId", targetNamespace = "") final String requestorId);
    @WebMethod
    Response deleteAuthProvider(@WebParam(name = "providerId", targetNamespace = "") String providerId);


    /*
    *==================================================
    *  AuthProviderAttribute section
    *===================================================
    */
}
