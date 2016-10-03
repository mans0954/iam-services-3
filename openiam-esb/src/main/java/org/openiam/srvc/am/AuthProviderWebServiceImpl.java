package org.openiam.srvc.am;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.cert.groovy.DefaultCertToIdentityConverter;
import org.openiam.am.srvc.domain.AuthAttributeEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.dozer.converter.AuthAttributeDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthProviderTypeDozerConverter;
import org.openiam.am.srvc.dto.AuthAttribute;
import org.openiam.am.srvc.dto.AuthProvider;
import org.openiam.am.srvc.dto.AuthProviderAttribute;
import org.openiam.am.srvc.dto.AuthProviderType;
import org.openiam.am.srvc.searchbean.AuthAttributeSearchBean;
import org.openiam.am.srvc.searchbean.AuthProviderSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EsbErrorToken;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.spi.AbstractSMSOTPModule;
import org.openiam.idm.srvc.auth.spi.AbstractScriptableLoginModule;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.mq.constants.AuthProviderAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.script.ScriptIntegration;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("authProviderWS")
@WebService(endpointInterface = "org.openiam.srvc.am.AuthProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "AuthProviderWebServicePort",
            serviceName = "AuthProviderWebService")
public class AuthProviderWebServiceImpl extends AbstractApiService implements AuthProviderWebService {

    public AuthProviderWebServiceImpl() {
        super(OpenIAMQueue.AuthProviderQueue);
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
        AuthProviderTypeListResponse response = this.manageApiRequest(AuthProviderAPI.GetAuthProviderTypeList, new BaseServiceRequest(), AuthProviderTypeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderTypeList();
    }

    @Override
    public List<AuthProviderType> getSocialAuthProviderTypeList(){
        AuthProviderTypeListResponse response = this.manageApiRequest(AuthProviderAPI.GetSocialAuthProviderTypeList, new BaseServiceRequest(), AuthProviderTypeListResponse.class);
        if(response.isFailure()){
            return Collections.EMPTY_LIST;
        }
        return response.getAuthProviderTypeList();
    }

    @Override
    public Response addProviderType(AuthProviderType providerType) {
        BaseGrudServiceRequest<AuthProviderType> request = new BaseGrudServiceRequest<>(providerType);
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
        BaseGrudServiceRequest<AuthProvider> request = new BaseGrudServiceRequest<>(provider);
        request.setRequesterId(requestorId);
        StringResponse response = this.manageApiRequest(AuthProviderAPI.SaveAuthProvider, request, StringResponse.class);
        return response.convertToBase();
    }




    @Override
    public Response deleteAuthProvider(String providerId) {
        IdServiceRequest request=new IdServiceRequest();
        request.setId(providerId);

        return this.manageApiRequest(AuthProviderAPI.DeleteAuthProvider, request, Response.class);
    }
}
