package org.openiam.am.srvc.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.am.srvc.domain.URIPatternMetaValueEntity;
import org.openiam.am.srvc.dozer.converter.ContentProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaValueDozerConverter;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.groovy.URIFederationGroovyProcessor;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.am.srvc.uriauth.dto.URIPatternRuleToken;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.am.srvc.uriauth.model.ContentProviderTree;
import org.openiam.am.srvc.uriauth.model.URIPatternSearchResult;
import org.openiam.am.srvc.uriauth.rule.URIPatternRule;
import org.openiam.authmanager.common.model.AuthorizationResource;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.script.ScriptIntegration;
import org.openiam.thread.Sweepable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("uriFederationService")
//@ManagedResource(objectName="org.openiam.am.srvc.service:name=URIFederationService")
public class URIFederationServiceImpl implements URIFederationService, ApplicationContextAware, InitializingBean, Sweepable {
	
	private static Logger LOG = Logger.getLogger(URIFederationServiceImpl.class);
	private ApplicationContext ctx;

	private ContentProviderTree contentProviderTree;
	
	@Autowired
	private ContentProviderDao contentProviderDAO;
	
	@Autowired
	private AuthorizationManagerService authorizationManager;
	
	@Autowired
	private ContentProviderDozerConverter cpDozerConverter;
	
	@Autowired
	private URIPatternDozerConverter patternDozerConverter;
	
	@Autowired
	private URIPatternMetaDozerConverter patternMetaDozerConverter;
	
	@Autowired
	private URIPatternMetaValueDozerConverter patternValueDozerConverter;
	
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		sweep();
	}
	
	/*
	 * Caches DTO Objects, so that we're not tied to the Hibernate Session
	 */
	@ManagedOperation(description="sweep the Content Provider Cache")
	@Transactional
	public void sweep() {
		try {
			LOG.info("Attemtping to refresh Content Provider Cache...");
			final ContentProviderTree tempTree = new ContentProviderTree();
			
			/* get all content providers */
			final List<ContentProviderEntity> contentProviderEntityList = contentProviderDAO.findAll();
			if(CollectionUtils.isNotEmpty(contentProviderEntityList)) {
				for(final ContentProviderEntity cpEntity : contentProviderEntityList) {
					
					/* dont' cache CPs who don't have servers */
					if(CollectionUtils.isNotEmpty(cpEntity.getServerSet())) {
						/* convert the content provider to a DTO */
						final ContentProvider cp = cpDozerConverter.convertToDTO(cpEntity, true);
						if(CollectionUtils.isNotEmpty(cpEntity.getPatternSet())) {
							
							/* process the URI patterns, but converting any subcollections to DTOs manually, since our Dozer converter will only go 2 levels deep */
							final Map<String, URIPattern> uriPatternMap = new LinkedHashMap<String, URIPattern>();
							for(final URIPatternEntity uriEntity : cpEntity.getPatternSet()) {
								
								/* convert pattern */
								final URIPattern pattern = patternDozerConverter.convertToDTO(uriEntity, true);
								uriPatternMap.put(pattern.getId(), pattern);
								if(CollectionUtils.isNotEmpty(pattern.getMetaEntitySet())) {
									
									/* convert meta */
									final Map<String, URIPatternMeta> metaMap = new LinkedHashMap<String, URIPatternMeta>();
									for(final URIPatternMetaEntity metaEntity : uriEntity.getMetaEntitySet()) {
										final URIPatternMeta meta = patternMetaDozerConverter.convertToDTO(metaEntity, true);
										
										/* check that the spring bean exists */
										final URIPatternMetaType type = meta.getMetaType();
										if(type != null && StringUtils.isNotBlank(type.getSpringBeanName())) {
											final String springBeanName = type.getSpringBeanName();
											try {
												final URIPatternRule rule = ctx.getBean(springBeanName, URIPatternRule.class);
												LOG.info(String.format("Spring Bean %s will be used for URI Pattern %s", springBeanName, pattern));
												metaMap.put(meta.getId(), meta);
										
												/* convert values */
												if(CollectionUtils.isNotEmpty(metaEntity.getMetaValueSet())) {
													final Map<String, URIPatternMetaValue> valueMap = new LinkedHashMap<String, URIPatternMetaValue>();
													for(final URIPatternMetaValueEntity valueEntity : metaEntity.getMetaValueSet()) {
														final URIPatternMetaValue value = patternValueDozerConverter.convertToDTO(valueEntity, true);
														if(StringUtils.isNotBlank(value.getGroovyScript())) {
															try {
																final URIFederationGroovyProcessor processor = (URIFederationGroovyProcessor)scriptRunner.instantiateClass(null, value.getGroovyScript());
																if(processor != null) {
																	value.setGroovyProcessor(processor);
																}
															} catch(Throwable e) {
																LOG.warn(String.format("Can't define groovy script for rule %s.  This groovy script wont' be used", value.getName()), e);
															}
														}
														valueMap.put(value.getId(), value);
													}
													meta.setMetaValueSet(new LinkedHashSet<URIPatternMetaValue>(valueMap.values()));
												}
											} catch(NoSuchBeanDefinitionException e) {
												LOG.warn(String.format("Spring Bean '%s' on URI Pattern %s will not be used", springBeanName, pattern), e);
											} catch(BeanNotOfRequiredTypeException e) {
												LOG.warn(String.format("Spring Bean '%s' on URI Pattern %s will not be used", springBeanName, pattern), e);
											}
										}
									}
									
									pattern.setMetaEntitySet(new LinkedHashSet<URIPatternMeta>(metaMap.values()));
								}
							}
							cp.setPatternSet(new LinkedHashSet<URIPattern>(uriPatternMap.values()));
						}
						tempTree.addContentProvider(cp);
					}
				}
			}
			synchronized(this) {
				contentProviderTree = tempTree;
			}
		} catch(Throwable e) {
			LOG.error("Can't refresh content provider cache", e);
		}
	}
	
	@ManagedOperation(description="Print Cache Contents")
	public String printCacheContents() {
		final String ls = System.getProperty("line.separator");
		
		final StringBuilder cacheContents = new StringBuilder();
		cacheContents.append((contentProviderTree != null) ? contentProviderTree : "");
		
		return cacheContents.toString();
	}
	
	@ManagedOperation(description="Test Federation agains parameters")
	public String federateProxyURIJMX(final String userId, final int authLevel, final String proxyURI) {
		return federateProxyURI(userId, authLevel, proxyURI).toString();
	}

	@Override
	public URIFederationResponse federateProxyURI(final String userId, final int authLevel, final String proxyURI) {
		final URIFederationResponse response = new URIFederationResponse();
		final StopWatch sw = new StopWatch();
		sw.start();
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
			
			/* means that no matching pattern has been found for this URI (i.e. none configured) - check against the CP */
			if(uriPatternToken == null || !uriPatternToken.hasPatterns()) {
				/* means that the Content Provider Auth Level is higher than the current for this user */
				if(cp.getAuthLevel().gt(authLevel)) {
					response.setRequiredAuthLevel(cp.getAuthLevel().getLevel());
					throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_AUTH_LEVEL_DOES_NOT_MEET_MIN_AUTH_LEVEL_ON_CP);
				}
			} else {
				/* check entitlements and auth level on patterns */
				for(final URIPattern pattern : uriPatternToken.getFoundPatterns()) {
					if(!pattern.getIsPublic() && !isEntitled(userId, pattern.getResourceId())) {
						throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN, pattern.getPattern());
					}
				
					if(pattern.getAuthLevel().gt(authLevel)) {
						response.setRequiredAuthLevel(pattern.getAuthLevel().getLevel());
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
										final URIPatternRuleToken ruleToken = rule.process(userId, uri, type, valueSet, pattern, cp);
										response.addRuleToken(ruleToken);
									}
								} catch(Throwable e) {
									LOG.error("Error processing rule", e);
									throw new BasicDataServiceException(ResponseCode.URI_PATTERN_RULE_PROCESS_ERROR, e);
								}
							}
						}
					}
					response.setPatternId(pattern.getId());
				}
			}
			
			response.setCpId(cp.getId());
			response.setServer(cp.getNextServer());
			response.setStatus(ResponseStatus.SUCCESS);
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setResponseValue(e.getResponseValue());
			response.setStatus(ResponseStatus.FAILURE);
			//LOG.info(String.format("CP or Pattern Exception: %s", e.getMessage()));
		} catch(URISyntaxException e) {
			response.setErrorCode(ResponseCode.INVALID_URI);
			response.setStatus(ResponseStatus.FAILURE);
			LOG.error(String.format("URI Syntax Exception: %s", e));
		} catch(Throwable e) {
			response.setErrorCode(ResponseCode.FAIL_OTHER);
			response.setStatus(ResponseStatus.FAILURE);
			LOG.error("Unkonwn error while processing proxy request", e);
		} finally {
			sw.stop();
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("URI Fedration took: %s ms", sw.getTime()));
			}
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
