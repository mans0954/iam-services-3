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


    
    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    private HazelcastConfiguration hazelcastConfiguration;

    public OAuthWebServiceImpl() {
        super(OpenIAMQueue.OAuthQueue);
    }

    @Override
    public AuthProvider getClient(String clientId) {
        return this.getValue(OAuthAPI.GetClient, new IdServiceRequest(clientId), AuthProviderResponse.class);
//        return authProviderService.getOAuthClient(clientId);
    }
    @Override
    public List<Resource> getAuthorizedScopes(String clientId, String userId, Language language) {
        OAuthScopesRequest request = new OAuthScopesRequest();
        request.setUserId(userId);
        request.setClientId(clientId);
        request.setLanguage(language);
        return this.getValueList(OAuthAPI.GetAuthorizedScopes, request, OAuthScopesResponse.class);
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

//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try{
//            authProviderService.saveClientScopeAuthorization(providerId, userId, oauthUserClientXrefList);
//        } catch(BasicDataServiceException e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//            response.setErrorTokenList(e.getErrorTokenList());
//        } catch(Throwable e) {
//            log.error("Error while saving scope authorizations", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }
    @Override
    public Response saveOAuthCode(OAuthCode oAuthCode){
        return this.manageGrudApiRequest(OAuthAPI.SaveOAuthCode, oAuthCode);

//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try{
//            authProviderService.saveOAuthCode(oAuthCode);
//        } catch(Throwable e) {
//            log.error("Error while saving token info", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }

    public OAuthCode getOAuthCode(String code){
        return this.getValue(OAuthAPI.GetOAuthCode, new IdServiceRequest(code), OAuthCodeResponse.class);

//        return authProviderService.getOAuthCode(code);
    }

    @Override
    public OAuthToken getOAuthToken(String token) {
        return this.getValue(OAuthAPI.GetOAuthToken, new IdServiceRequest(token), OAuthTokenResponse.class);
//        return authProviderService.getOAuthToken(token);
    }

    @Override
    public OAuthToken getOAuthTokenByRefreshToken(String refreshToken){
        return this.getValue(OAuthAPI.GetOAuthTokenByRefreshToken, new IdServiceRequest(refreshToken), OAuthTokenResponse.class);
//        return authProviderService.getOAuthTokenByRefreshToken(refreshToken);
    }

    @Override
    public Response saveOAuthToken(OAuthToken oAuthToken){
        return this.manageGrudApiRequest(OAuthAPI.SaveOAuthToken, oAuthToken,OAuthTokenResponse.class);
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try{
//            OAuthToken token = authProviderService.saveOAuthToken(oAuthToken);
//            response.setResponseValue(token);
//        } catch(Throwable e) {
//            log.error("Error while saving token info", e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }


//	@Override
//	@Scheduled(fixedRateString="${org.openiam.am.oauth.client.threadsweep}", initialDelay=0)
//	public void sweep() {
//		final Map<String, AuthProvider> tempIdCache = new HashMap<String, AuthProvider>();
//		final Map<String, AuthProvider> tempNameCache = new HashMap<String, AuthProvider>();
//
//		final List<AuthProvider> providers = authProviderService.getOAuthClients();
//		if(CollectionUtils.isNotEmpty(providers)) {
//			providers.forEach(provider -> {
//				tempIdCache.put(provider.getId(), provider);
//				tempNameCache.put(provider.getName(), provider);
//				provider.generateId2ValueAttributeMap();
//			});
//		}
//
//		synchronized(this) {
//			idCache = tempIdCache;
//			nameCache = tempNameCache;
//		}
//	}

	@Override
	public AuthProvider getCachedOAuthProviderById(String id) {
        return this.getValue(OAuthAPI.GetCachedOAuthProviderById, new IdServiceRequest(id), AuthProviderResponse.class);

//		return idCache.get(id);
	}

	@Override
	public AuthProvider getCachedOAuthProviderByName(String name) {
        return this.getValue(OAuthAPI.GetCachedOAuthProviderByName, new IdServiceRequest(name), AuthProviderResponse.class);
//		return nameCache.get(name);
	}

//	@Override
//	public void afterPropertiesSet() throws Exception {
//		onMessage(null);
//		hazelcastConfiguration.getTopic("oAuthProviderTopic").addMessageListener(this);
//	}
	
	/* this is here so that different nodes can send messages using the publish() method on ITopics */
//	@Override
//	public void onMessage(final Message<String> message) {
//		sweep();
//	}
}
