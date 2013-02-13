package org.openiam.am.srvc.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.am.srvc.uriauth.model.ContentProviderTree;
import org.openiam.am.srvc.uriauth.model.URIPatternSearchResult;
import org.openiam.am.srvc.uriauth.model.URIPatternTree;
import org.openiam.am.srvc.uriauth.rule.URIPatternRule;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

@Service
public class URIFederationServiceImpl implements URIFederationService, InitializingBean, ApplicationContextAware {
	
	private static Logger LOG = Logger.getLogger(URIFederationServiceImpl.class);
	private ApplicationContext ctx;

	private ContentProviderTree contentProviderTree;
	
	@Autowired
	private AuthorizationManagerService authorizationManager;
	
	public void sweep() {
		final ContentProviderTree tempTree = new ContentProviderTree();
		
		synchronized(this) {
			contentProviderTree = tempTree;
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		sweep();
	}

	@Override
	public URIFederationResponse federateProxyURI(final String userId, final int authLevel, final String proxyURI) {
		final URIFederationResponse response = new URIFederationResponse();
		try {
			final URI uri = new URI(proxyURI);
			final ContentProviderNode cpNode = contentProviderTree.find(uri);
			if(cpNode == null) {
				throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND);
			}
			final ContentProvider cp = cpNode.getContentProvider();
			if(!cp.getIsPublic() && !isEntitled(userId, cp.getResourceId())) {
				throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
			}
			
			final URIPatternSearchResult uriPatternToken = (cpNode.getPatternTree() != null) ? cpNode.getPatternTree().find(uri) : null;
			if(uriPatternToken == null || !uriPatternToken.hasPatterns()) { /* means that no matching pattern has been found for this URI - check against the CP */
				/* means that the Content Provider Auth Level is higher than the current for this user */
				if(cp.getAuthLevel().gt(authLevel)) {
					throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP);
				}
			}
			
			
			/* check entitlements and auth level */
			for(final URIPattern pattern : uriPatternToken.getFoundPatterns()) {
				if(!pattern.getIsPublic() && !isEntitled(userId, pattern.getResourceId())) {
					throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN, pattern.getPattern());
				}
				
				if(pattern.getAuthLevel().gt(authLevel)) {
					throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_PATTERN, pattern.getId());
				}
			}
			
			/* do rule processes */
			for(final URIPattern pattern : uriPatternToken.getFoundPatterns()) {
				if(CollectionUtils.isNotEmpty(pattern.getMetaEntitySet())) {
					for(final URIPatternMeta meta : pattern.getMetaEntitySet()) {
						final URIPatternMetaType type = meta.getMetaType();
						if(type != null) {
							final String springBeanName = type.getSpringBeanName();
							try {
								final  URIPatternRule rule = ctx.getBean(springBeanName, URIPatternRule.class);
								if(rule != null) {
									final Set<URIPatternMetaValue> valueSet = meta.getMetaValueSet();
									rule.process(userId, uri, type, valueSet);
								}
							} catch(Throwable e) {
								LOG.error("Error processing rule", e);
							}
						}
					}
				}
			}
			
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setResponseValue(e.getResponseValue());
		} catch(URISyntaxException e) {
			response.setErrorCode(ResponseCode.INVALID_URI);
			LOG.error("URI Syntax Exception", e);
		} catch(Throwable e) {
			response.setErrorCode(ResponseCode.FAIL_OTHER);
			LOG.error("Unkonwn error while processing proxy request", e);
		}
		return response;
	}
	
	private boolean isEntitled(final String userId, final String resourceId) {
		final AuthorizationResource resource = new AuthorizationResource();
		resource.setId(resourceId);
		return authorizationManager.isEntitled(userId, resource);
	}

	@Override
	public void setApplicationContext(final ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}
}
