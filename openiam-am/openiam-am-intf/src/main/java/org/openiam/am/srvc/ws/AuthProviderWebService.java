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
	public List<AuthAttribute> findAuthAttributeBeans(@WebParam(name = "searchBean", targetNamespace = "")AuthAttributeSearchBean searchBean,
													  @WebParam(name = "from", targetNamespace = "")int from,
													  @WebParam(name = "size", targetNamespace = "")int size);
    @WebMethod
    public AuthProviderType getAuthProviderType(@WebParam(name = "providerType", targetNamespace = "")String providerType);
    @WebMethod
    public List<AuthProviderType> getAuthProviderTypeList();

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @WebMethod
    public int countAuthProviderBeans(@WebParam(name = "searchBean", targetNamespace = "") AuthProviderSearchBean searchBean);
    
    @WebMethod
    public List<AuthProvider> findAuthProviderBeans(@WebParam(name = "searchBean", targetNamespace = "") AuthProviderSearchBean searchBean,
                                                    @WebParam(name = "size", targetNamespace = "")Integer size,
                                                    @WebParam(name = "from", targetNamespace = "")Integer from);

    @WebMethod
    public Response saveAuthProvider(@WebParam(name = "provider", targetNamespace = "")AuthProvider provider,
    							    @WebParam(name = "requestorId", targetNamespace = "")final String requestorId);
    @WebMethod
    public Response deleteAuthProvider(@WebParam(name = "providerId", targetNamespace = "")String providerId);


    /*
    *==================================================
    *  AuthProviderAttribute section
    *===================================================
    */
}
