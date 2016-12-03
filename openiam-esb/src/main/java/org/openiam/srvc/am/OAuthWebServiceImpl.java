package org.openiam.srvc.am;

import org.apache.log4j.Logger;
import org.openiam.am.srvc.dto.*;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.OAuthScopesRequest;
import org.openiam.base.request.model.OAuthClientScopeModel;
import org.openiam.base.response.data.AuthProviderResponse;
import org.openiam.base.response.data.OAuthCodeResponse;
import org.openiam.base.response.data.OAuthTokenResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.mq.constants.api.OAuthAPI;
import org.openiam.mq.constants.queue.am.OAuthQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

import java.util.List;

/**
 * Created by alexander on 06/07/15.
 */
@Service("OAuthWS")
@DependsOn("springContextProvider")
@WebService(endpointInterface = "org.openiam.srvc.am.OAuthWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "OAuthWebServicePort",
        serviceName = "OAuthWebService")
public class OAuthWebServiceImpl extends AbstractApiService implements OAuthWebService {
    private static Logger log = Logger.getLogger(OAuthWebServiceImpl.class);

    @Autowired
    public OAuthWebServiceImpl(OAuthQueue queue) {
        super(queue);
    }

    @Override
    public AuthProvider getClient(String clientId) {
        return this.getValue(OAuthAPI.GetClient, new IdServiceRequest(clientId), AuthProviderResponse.class);
    }
    @Override
    public List<Resource> getAuthorizedScopes(String clientId, OAuthToken oAuthToken, Language language) {
        OAuthScopesRequest request = new OAuthScopesRequest();
        request.setToken(oAuthToken);
        request.setClientId(clientId);
        request.setLanguage(language);
        return this.getValueList(OAuthAPI.GetAuthorizedScopes, request, OAuthScopesResponse.class);
    }
    @Override
    public List<Resource> getAuthorizedScopesByUser(String clientId, String userId, Language language){
        OAuthScopesRequest request = new OAuthScopesRequest();
        request.setUserId(userId);
        request.setClientId(clientId);
        request.setLanguage(language);
        return this.getValueList(OAuthAPI.GetAuthorizedScopesByUser, request, OAuthScopesResponse.class);
    }

    @Override
    public OAuthScopesResponse getScopesForAuthrorization(String clientId, String userId, Language language) {
        OAuthScopesRequest request = new OAuthScopesRequest();
        request.setUserId(userId);
        request.setClientId(clientId);
        request.setLanguage(language);
        return this.getResponse(OAuthAPI.GetScopesForAuthrorization, request, OAuthScopesResponse.class);
    }

    @Override
    public Response saveClientScopeAuthorization(String providerId, String userId,
                                                 List<OAuthUserClientXref> oauthUserClientXrefList) {
        OAuthClientScopeModel model = new OAuthClientScopeModel();
        model.setUserId(userId);
        model.setId(providerId);
        model.setOauthUserClientXrefList(oauthUserClientXrefList);
        return this.manageCrudApiRequest(OAuthAPI.SaveClientScopeAuthorization, model);
    }
    @Override
    public Response saveOAuthCode(OAuthCode oAuthCode){
        return this.manageCrudApiRequest(OAuthAPI.SaveOAuthCode, oAuthCode);
    }

    public OAuthCode getOAuthCode(String code){
        return this.getValue(OAuthAPI.GetOAuthCode, new IdServiceRequest(code), OAuthCodeResponse.class);
    }

    @Override
    public OAuthToken getOAuthToken(String token) {
        return this.getValue(OAuthAPI.GetOAuthToken, new IdServiceRequest(token), OAuthTokenResponse.class);
//        return authProviderService.getOAuthToken(token);
    }

    @Override
    public OAuthToken getOAuthTokenByRefreshToken(String refreshToken){
        return this.getValue(OAuthAPI.GetOAuthTokenByRefreshToken, new IdServiceRequest(refreshToken), OAuthTokenResponse.class);
    }

    @Override
    public Response saveOAuthToken(OAuthToken oAuthToken){
        return this.manageCrudApiRequest(OAuthAPI.SaveOAuthToken, oAuthToken,OAuthTokenResponse.class);
    }


	@Override
	public AuthProvider getCachedOAuthProviderById(String id) {
        return this.getValue(OAuthAPI.GetCachedOAuthProviderById, new IdServiceRequest(id), AuthProviderResponse.class);
	}

	@Override
	public AuthProvider getCachedOAuthProviderByName(String name) {
        return this.getValue(OAuthAPI.GetCachedOAuthProviderByName, new IdServiceRequest(name), AuthProviderResponse.class);
	}


    @Override
    public Response cleanAuthorizedScopes(){
        this.sendAsync(OAuthAPI.CleanAuthorizedScopes, new EmptyServiceRequest());
        return new Response();
    }
    @Override
    public Response deAuthorizeClient(String clientId, String userId){
        OAuthScopesRequest request = new OAuthScopesRequest();
        request.setUserId(userId);
        request.setClientId(clientId);
        return this.getResponse(OAuthAPI.DeAuthorizeClient, request, Response.class);
    }

}
