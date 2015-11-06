package org.openiam.am.srvc.ws;

import org.apache.log4j.Logger;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.dozer.converter.AuthProviderDozerConverter;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.ResourceDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.res.dto.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Created by alexander on 06/07/15.
 */
@Service("OAuthWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.OAuthWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "OAuthWebServicePort",
        serviceName = "OAuthWebService")
public class OAuthWebServiceImpl implements OAuthWebService {
    private static Logger log = Logger.getLogger(OAuthWebServiceImpl.class);

    @Autowired
    private AuthProviderService authProviderService;


    @Autowired
    private ResourceDozerConverter resourceDozerConverter;


    @Override
    public AuthProvider getClient(String clientId) {
        return authProviderService.getOAuthClient(clientId);
    }

    @Override
    public Response getScopesForAuthrorization(String clientId, String userId, Language language) {
    	final Response response = new Response();
    	try {
    		final List<Resource> resources = authProviderService.getScopesForAuthrorization(clientId, userId, language);
    		response.succeed();
    		response.setResponseValue(resources);
    	} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.fail();
    	} catch(Throwable e) {
    		log.error("Can't get scopes for authorization", e);
    		response.fail();
    	}
    	return response;
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
}
