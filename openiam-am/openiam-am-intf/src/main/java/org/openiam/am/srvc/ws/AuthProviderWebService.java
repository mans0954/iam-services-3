package org.openiam.am.srvc.ws;

import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbeans.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbeans.AuthProviderSearchBean;
import org.openiam.base.ws.Response;

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
    public AuthProviderType getAuthProviderType(@WebParam(name = "providerType", targetNamespace = "")String providerType);
    @WebMethod
    public List<AuthProviderType> getAuthProviderTypeList();

    /**
     * Add new provider type to AM.
     */
    @WebMethod
    public Response addProviderType(@WebParam(name = "entity", targetNamespace = "")AuthProviderType entity);

    /**
     * Delete existing provider type from AM
     * @param providerType
     */
    @WebMethod
    public Response deleteProviderType(@WebParam(name = "providerType", targetNamespace = "")String providerType);

    /*
    *==================================================
    * AuthAttributeEntity section
    *===================================================
    */
    @WebMethod
    public List<AuthAttribute> findAuthAttributeBeans(@WebParam(name = "searchBean", targetNamespace = "")AuthAttributeSearchBean searchBean,
                                                      @WebParam(name = "size", targetNamespace = "")Integer size,
                                                      @WebParam(name = "from", targetNamespace = "")Integer from);
    @WebMethod
    public Response addAuthAttribute(@WebParam(name = "attribute", targetNamespace = "")AuthAttribute attribute);
    @WebMethod
    public Response updateAuthAttribute(@WebParam(name = "attribute", targetNamespace = "")AuthAttribute attribute);
    @WebMethod
    public Response deleteAuthAttribute(@WebParam(name = "authAttributeId", targetNamespace = "")String authAttributeId);
    @WebMethod
    public Response deleteAuthAttributesByType(@WebParam(name = "providerType", targetNamespace = "")String providerType);

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @WebMethod
    public List<AuthProvider> findAuthProviderBeans(@WebParam(name = "searchBean", targetNamespace = "") AuthProviderSearchBean searchBean,
                                                    @WebParam(name = "size", targetNamespace = "")Integer size,
                                                    @WebParam(name = "from", targetNamespace = "")Integer from);
    @WebMethod
    public Response addAuthProvider(@WebParam(name = "provider", targetNamespace = "")AuthProvider provider);
    @WebMethod
    public Response updateAuthProvider(@WebParam(name = "provider", targetNamespace = "")AuthProvider provider);
    @WebMethod
    public Response deleteAuthProvider(@WebParam(name = "providerId", targetNamespace = "")String providerId);
    @WebMethod
    public Response deleteAuthProviderByType(@WebParam(name = "providerType", targetNamespace = "")String providerType);


    /*
    *==================================================
    *  AuthProviderAttribute section
    *===================================================
    */
    @WebMethod
    public AuthProviderAttribute getAuthProviderAttribute(@WebParam(name = "providerId", targetNamespace = "") String providerId,
                                                          @WebParam(name = "name", targetNamespace = "") String name);
    @WebMethod
    public List<AuthProviderAttribute> getAuthProviderAttributeList(@WebParam(name = "providerId", targetNamespace = "")String providerId,
                                                                    @WebParam(name = "size", targetNamespace = "")Integer size,
                                                                    @WebParam(name = "from", targetNamespace = "")Integer from);
    @WebMethod
    public Response addAuthProviderAttribute(@WebParam(name = "attribute", targetNamespace = "")AuthProviderAttribute attribute);
    @WebMethod
    public Response updateAuthProviderAttribute(@WebParam(name = "attribute", targetNamespace = "")AuthProviderAttribute attribute);
    @WebMethod
    public Response deleteAuthProviderAttributeByName(@WebParam(name = "providerId", targetNamespace = "")String providerId,
                                                  @WebParam(name = "attributeId", targetNamespace = "")String attributeId);
    @WebMethod
    public Response deleteAuthProviderAttributes(@WebParam(name = "providerId", targetNamespace = "")String providerId);
}
