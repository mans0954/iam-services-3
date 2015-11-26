package org.openiam.am.srvc.ws;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.dozer.converter.AuthProviderDozerConverter;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.Resource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

import javax.annotation.PostConstruct;
import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alexander on 06/07/15.
 */
@Service("OAuthWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.OAuthWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "OAuthWebServicePort",
        serviceName = "OAuthWebService")
public class OAuthWebServiceImpl implements OAuthWebService, InitializingBean, MessageListener<String> {
    private static Logger log = Logger.getLogger(OAuthWebServiceImpl.class);

    private Map<String, AuthProvider> idCache = new HashMap<String, AuthProvider>();
    private Map<String, AuthProvider> nameCache = new HashMap<String, AuthProvider>();
    
    @Autowired
    private AuthProviderService authProviderService;


    @Autowired
    private ResourceDozerConverter resourceDozerConverter;

    @Autowired
    private HazelcastConfiguration hazelcastConfiguration;

    @Override
    public AuthProvider getClient(String clientId) {
        return authProviderService.getOAuthClient(clientId);
    }

    @Override
    public OAuthScopesResponse getScopesForAuthrorization(String clientId, String userId, Language language) {
    	final OAuthScopesResponse scopes = new OAuthScopesResponse();
    	try {
    		final List<Resource> resources = authProviderService.getScopesForAuthrorization(clientId, userId, language);

            scopes.setOauthScopeList(resources);
            scopes.setClientId(clientId);
            scopes.succeed();
        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            scopes.setStatus(ResponseStatus.FAILURE);
            scopes.setErrorCode(e.getCode());
        }  catch(Throwable e) {
    		log.error("Can't get scopes for authorization", e);
            scopes.fail();
    	}
    	return scopes;
    }

    @Override
    public Response saveClientScopeAuthorization(String providerId, String userId,
                                                 List<OAuthUserClientXref> oauthUserClientXrefList) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try{
            authProviderService.saveClientScopeAuthorization(providerId, userId, oauthUserClientXrefList);
        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
            response.setErrorTokenList(e.getErrorTokenList());
        } catch(Throwable e) {
            log.error("Error while saving scope authorizations", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    @Override
    public Response saveOAuthCode(OAuthCode oAuthCode){
        final Response response = new Response(ResponseStatus.SUCCESS);
        try{
            authProviderService.saveOAuthCode(oAuthCode);
        } catch(Throwable e) {
            log.error("Error while saving token info", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    public OAuthCode getOAuthCode(String code){
        return authProviderService.getOAuthCode(code);
    }

    @Override
    public OAuthToken getOAuthToken(String token) {
        return authProviderService.getOAuthToken(token);
    }

    @Override
    public OAuthToken getOAuthTokenByRefreshToken(String refreshToken){
        return authProviderService.getOAuthTokenByRefreshToken(refreshToken);
    }

    @Override
    public Response saveOAuthToken(OAuthToken oAuthToken){
        final Response response = new Response(ResponseStatus.SUCCESS);
        try{
            OAuthToken token = authProviderService.saveOAuthToken(oAuthToken);
            response.setResponseValue(token);
        } catch(Throwable e) {
            log.error("Error while saving token info", e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public List<Resource> getAuthorizedScopes(String clientId, String userId, Language language) {
        return authProviderService.getAuthorizedScopes(clientId, userId, language);
    }

    
	@Override
	@Scheduled(fixedRateString="${org.openiam.am.oauth.client.threadsweep}", initialDelay=0)
	public void sweep() {
		final Map<String, AuthProvider> tempIdCache = new HashMap<String, AuthProvider>();
		final Map<String, AuthProvider> tempNameCache = new HashMap<String, AuthProvider>();
		
		final List<AuthProvider> providers = authProviderService.getOAuthClients();
		if(CollectionUtils.isNotEmpty(providers)) {
			providers.forEach(provider -> {
				tempIdCache.put(provider.getId(), provider);
				tempNameCache.put(provider.getName(), provider);
				provider.generateId2ValueAttributeMap();
			});
		}
		
		synchronized(this) {
			idCache = tempIdCache;
			nameCache = tempNameCache;
		}
	}

	@Override
	public AuthProvider getCachedOAuthProviderById(String id) {
		return idCache.get(id);
	}

	@Override
	public AuthProvider getCachedOAuthProviderByName(String name) {
		return nameCache.get(name);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		onMessage(null);
		hazelcastConfiguration.getTopic("oAuthProviderTopic").addMessageListener(this);
	}
	
	/* this is here so that different nodes can send messages using the publish() method on ITopics */
	@Override
	public void onMessage(final Message<String> message) {
		sweep();
	}
}
