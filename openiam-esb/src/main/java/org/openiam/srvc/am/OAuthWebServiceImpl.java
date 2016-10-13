package org.openiam.srvc.am;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.OAuthScopesRequest;
import org.openiam.base.request.model.OAuthClientScopeModel;
import org.openiam.base.response.AuthProviderResponse;
import org.openiam.base.response.OAuthCodeResponse;
import org.openiam.base.response.OAuthTokenResponse;
import org.openiam.base.ws.Response;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.mq.constants.OAuthAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public OAuthWebServiceImpl() {
        super(OpenIAMQueue.OAuthQueue);
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
        return this.manageGrudApiRequest(OAuthAPI.SaveClientScopeAuthorization, model);
    }
    @Override
    public Response saveOAuthCode(OAuthCode oAuthCode){
        return this.manageGrudApiRequest(OAuthAPI.SaveOAuthCode, oAuthCode);
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
        return this.manageGrudApiRequest(OAuthAPI.SaveOAuthToken, oAuthToken,OAuthTokenResponse.class);
    }


	@Override
	public AuthProvider getCachedOAuthProviderById(String id) {
        return this.getValue(OAuthAPI.GetCachedOAuthProviderById, new IdServiceRequest(id), AuthProviderResponse.class);
	}

	@Override
	public AuthProvider getCachedOAuthProviderByName(String name) {
        return this.getValue(OAuthAPI.GetCachedOAuthProviderByName, new IdServiceRequest(name), AuthProviderResponse.class);
	}

}
