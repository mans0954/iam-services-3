package org.openiam.srvc.am;

import java.util.Collections;
import java.util.List;

import javax.jws.WebService;

import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.mq.constants.api.AuthProviderAPI;
import org.openiam.mq.constants.queue.am.AuthProviderQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("authProviderWS")
@WebService(endpointInterface = "org.openiam.srvc.am.AuthProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthProviderWebServicePort",
            serviceName = "AuthProviderWebService")
public class AuthProviderWebServiceImpl extends AbstractApiService implements AuthProviderWebService {

    @Autowired
    public AuthProviderWebServiceImpl(AuthProviderQueue queue) {
        super(queue);
    }

    @Override
    public List<AuthAttribute> findAuthAttributeBeans(AuthAttributeSearchBean searchBean, int from, int size) {
        BaseSearchServiceRequest<AuthAttributeSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
        AuthAttributeListResponse response = this.manageApiRequest(AuthProviderAPI.FindAuthAttributes, request, AuthAttributeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAttributeList();
    }

    /*
    *==================================================
    * AuthProviderType section
    *===================================================
    */
    @Override
    public AuthProviderType getAuthProviderType(String providerType) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(providerType);

        AuthProviderTypeResponse response = this.manageApiRequest(AuthProviderAPI.GetAuthProviderType, request, AuthProviderTypeResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getAuthProviderType();
    }

    @Override
    public List<AuthProviderType> getAuthProviderTypeList() {
        AuthProviderTypeListResponse response = this.manageApiRequest(AuthProviderAPI.GetAuthProviderTypeList, new EmptyServiceRequest(), AuthProviderTypeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderTypeList();
    }

    @Override
    public List<AuthProviderType> getSocialAuthProviderTypeList(){
        AuthProviderTypeListResponse response = this.manageApiRequest(AuthProviderAPI.GetSocialAuthProviderTypeList, new EmptyServiceRequest(), AuthProviderTypeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderTypeList();
    }

    @Override
    public Response addProviderType(AuthProviderType providerType) {
        BaseCrudServiceRequest<AuthProviderType> request = new BaseCrudServiceRequest<>(providerType);
        return this.manageApiRequest(AuthProviderAPI.AddProviderType, request, Response.class);
    }

    /*
    *==================================================
    *  AuthProviderEntity section
    *===================================================
    */
    @Override
    public List<AuthProvider> findAuthProviderBeans(final AuthProviderSearchBean searchBean, final int from, final int size) {
        BaseSearchServiceRequest<AuthProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);

        AuthProviderListResponse response = this.manageApiRequest(AuthProviderAPI.FindAuthProviders, request, AuthProviderListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderList();
    }


    @Override
    public int countAuthProviderBeans(AuthProviderSearchBean searchBean) {
        BaseSearchServiceRequest<AuthProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
        IntResponse response = this.manageApiRequest(AuthProviderAPI.CountAuthProviders, request, IntResponse.class);
        if(response.isFailure()){
            return 0;
        }
        return response.getValue();
    }
    @Override
    public AuthProvider getAuthProvider(String providerId) {
        IdServiceRequest request=new IdServiceRequest();
        request.setId(providerId);
        return this.getValue(AuthProviderAPI.GetAuthProvider, request, AuthProviderResponse.class);
    }

    @Override
    public Response saveAuthProvider(AuthProvider provider, final String requestorId) {
        BaseCrudServiceRequest<AuthProvider> request = new BaseCrudServiceRequest<>(provider);
        request.setRequesterId(requestorId);
        StringResponse response = this.manageApiRequest(AuthProviderAPI.SaveAuthProvider, request, StringResponse.class);
        return response.convertToBase();
    }




    @Override
    public Response deleteAuthProvider(String providerId) {
        AuthProvider obj=new AuthProvider();
        obj.setId(providerId);
        return this.manageCrudApiRequest(AuthProviderAPI.DeleteAuthProvider, obj);
    }
}
