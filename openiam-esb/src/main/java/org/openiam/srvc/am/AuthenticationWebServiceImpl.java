package org.openiam.srvc.am;


import org.openiam.base.request.*;
import org.openiam.base.response.*;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.service.AuthenticationModule;
import org.openiam.mq.constants.AuthenticationAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alexander on 11/08/16.
 */
@Service("authenticate")
@WebService(endpointInterface = "org.openiam.srvc.am.AuthenticationService", targetNamespace = "urn:idm.openiam.org/srvc/auth/service",
            portName = "AuthenticationServicePort", serviceName = "AuthenticationService")
public class AuthenticationWebServiceImpl extends AbstractApiService implements AuthenticationService {

    public AuthenticationWebServiceImpl() {
        super(OpenIAMQueue.AuthenticationQueue);
    }

    @Override
    public void globalLogout(String userId) throws Throwable {
        final LogoutRequest request = new LogoutRequest();
        request.setUserId(userId);
        globalLogoutRequest(request);
    }

    @Override
    public Response globalLogoutRequest(LogoutRequest request) {
        return this.manageApiRequest(AuthenticationAPI.GlobalLogoutRequest, request, Response.class);
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        return this.manageApiRequest(AuthenticationAPI.Authenticate, request, AuthenticationResponse.class);
    }

    @Override
    public Response clearOTPActiveStatus(OTPServiceRequest request) {
        return this.manageApiRequest(AuthenticationAPI.ClearOTPActiveStatus, request, Response.class);
    }

    @Override
    public boolean isOTPActive(OTPServiceRequest request) {
        BooleanResponse response = this.manageApiRequest(AuthenticationAPI.IsOTPActive, request, BooleanResponse.class);
        if(response.isFailure()){
            return false;
        }
        return response.getValue();
    }

    @Override
    public Response sendOTPToken(OTPServiceRequest request) {
        return this.manageApiRequest(AuthenticationAPI.SendOTPToken, request, Response.class);
    }

    @Override
    public Response confirmOTPToken(OTPServiceRequest request) {
        return this.manageApiRequest(AuthenticationAPI.ConfirmOTPToken, request, BooleanResponse.class);
    }

    @Override
    public Response getOTPSecretKey(OTPServiceRequest request) {
        StringResponse resp =  this.manageApiRequest(AuthenticationAPI.GetOTPSecretKey, request, StringResponse.class);
        return resp.convertToBase();
    }

    @Override
    public Response renewToken(String principal, String token, String tokenType, String patternId) {
        RenewTokenRequest request = new RenewTokenRequest();
        request.setPrincipal(principal);
        request.setToken(token);
        request.setTokenType(tokenType);
        request.setPatternId(patternId);

        SSOTokenResponse response = this.manageApiRequest(AuthenticationAPI.RenewToken, request, SSOTokenResponse.class);
        return response.convertToBase();
    }

    @Override
    public List<AuthStateEntity> findBeans(AuthStateSearchBean searchBean, int from, int size) {
        BaseSearchServiceRequest<AuthStateSearchBean> request = new BaseSearchServiceRequest<AuthStateSearchBean>(searchBean, from, size);
        AuthStateListResponse response = this.manageApiRequest(AuthenticationAPI.FindAuthState, request, AuthStateListResponse.class);
        if(response.isFailure()){
            return null;
        }
        return response.getAuthStateList();
    }

    @Override
    public Response save(AuthStateEntity entity) {
        AuthStateCrudServiceRequest request = new AuthStateCrudServiceRequest(entity);
        return this.manageApiRequest(AuthenticationAPI.SaveAuthState, request, Response.class);
    }

	@Override
	public List<String> getAllLoginModuleSpringBeans() {
		return applicationContext.getBeansOfType(AuthenticationModule.class).keySet().stream().collect(Collectors.toList());
	}
}
