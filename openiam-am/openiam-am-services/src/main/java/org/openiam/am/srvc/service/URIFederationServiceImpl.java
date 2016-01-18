package org.openiam.am.srvc.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthLevelGroupingDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.dozer.converter.AuthLevelGroupingDozerConverter;
import org.openiam.am.srvc.dozer.converter.ContentProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaValueDozerConverter;
import org.openiam.am.srvc.dto.AbstractMeta;
import org.openiam.am.srvc.dto.AbstractParameter;
import org.openiam.am.srvc.dto.AbstractPatternMetaValue;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternErrorMapping;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodMeta;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.dto.URIPatternSubstitution;
import org.openiam.am.srvc.groovy.AbstractRedirectURLGroovyProcessor;
import org.openiam.am.srvc.groovy.URIFederationGroovyProcessor;
import org.openiam.am.srvc.model.URIPatternSearchResult;
import org.openiam.am.srvc.uriauth.dto.URIAuthLevelAttribute;
import org.openiam.am.srvc.uriauth.dto.URIAuthLevelToken;
import org.openiam.am.srvc.uriauth.dto.URIFederationResponse;
import org.openiam.am.srvc.uriauth.dto.URIPatternRuleToken;
import org.openiam.am.srvc.uriauth.dto.URIPatternRuleValue;
import org.openiam.am.srvc.uriauth.dto.URISubstitutionToken;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.am.srvc.uriauth.model.ContentProviderTree;
import org.openiam.am.srvc.uriauth.rule.URIPatternRule;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.script.ScriptIntegration;
import org.openiam.thread.Sweepable;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

@Service("uriFederationService")
@DependsOn("springContextProvider") /* otherwise sweep() fails */
//@ManagedResource(objectName="org.openiam.am.srvc.service:name=URIFederationService")
public class URIFederationServiceImpl implements URIFederationService, ApplicationContextAware, InitializingBean, Sweepable, MessageListener<String> {
	
	private static final Log LOG = LogFactory.getLog(URIFederationServiceImpl.class);
	private ApplicationContext ctx;

	private Map<String, ContentProvider> contentProviderCache;
	private Map<String, URIPattern> uriPatternCache;
	private ContentProviderTree contentProviderTree;
	
	private Map<String, AuthLevelGrouping> groupingMap;
	
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
	private LoginDataService loginDS;
	
	@Autowired
	private AuthLevelGroupingDao authLevelGroupingDAO;
	
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    
    @Autowired
    private AuthLevelGroupingDozerConverter authLevelGroupingDozerConverter;
    
    @Autowired
    private HazelcastConfiguration hazelcastConfiguration;

    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;
    
    @Value("${org.openiam.auth.level.kerberos.id}")
    private String kerberosAuthId;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		onMessage(null);
		hazelcastConfiguration.getTopic("uriFederationTopic").addMessageListener(this);
	}
	
	/* this is here so that different nodes can send messages using the publish() method on ITopics */
	@Override
	public void onMessage(final Message<String> message) {
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				sweep();
	    		return null;
			}
    		
		});
	}
	
	public <T extends AbstractPatternMetaValue> boolean setPatternMeta(final URIPattern pattern, final AbstractMeta<T> meta) {
		final URIPatternMetaType type = meta.getMetaType();
		boolean success = false;
		
		if(type != null && StringUtils.isNotBlank(type.getSpringBeanName())) {
			final String springBeanName = type.getSpringBeanName();
			try {
				final URIPatternRule rule = ctx.getBean(springBeanName, URIPatternRule.class);
				LOG.info(String.format("Spring Bean %s will be used for URI Pattern %s", springBeanName, pattern));
		
				/* convert values */
				if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
					final Map<String, AbstractPatternMetaValue> valueMap = new LinkedHashMap<String, AbstractPatternMetaValue>();
					for(final AbstractPatternMetaValue value : meta.getMetaValueSet()) {
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
					valueMap.forEach((a, b) -> {
						meta.addMetaValue(b);
					});
				}
				success = true;
			} catch(NoSuchBeanDefinitionException e) {
				LOG.warn(String.format("Spring Bean '%s' on URI Pattern %s will not be used", springBeanName, pattern), e);
			} catch(BeanNotOfRequiredTypeException e) {
				LOG.warn(String.format("Spring Bean '%s' on URI Pattern %s will not be used", springBeanName, pattern), e);
			}
		}
		return success;
	}
	
	private void normalize(final AbstractParameter param) {
		if(param.getName() != null) {
			param.setName(param.getName().trim().toLowerCase());
		}
		if(param.getValues() != null) {
			final List<String> values = new LinkedList<String>();
			param.getValues().forEach(value -> { 
				values.add(value.trim().toLowerCase());
			});
			Collections.sort(values);
		}
	}
	
	/*
	 * Caches DTO Objects, so that we're not tied to the Hibernate Session
	 */
	@ManagedOperation(description="sweep the Content Provider Cache")
	@Transactional
	@Scheduled(fixedRateString="${org.openiam.am.uri.federation.threadsweep}", initialDelay=0)
	public void sweep() {
		try {
			final Map<String, ContentProvider> tempContentProviderMap = new HashMap<String, ContentProvider>();
			final Map<String, URIPattern> tempURIPatternMap = new HashMap<String, URIPattern>();
			LOG.info("Attemtping to refresh Content Provider Cache...");
			final ContentProviderTree tempTree = new ContentProviderTree();
			
			/* get all content providers */
			final List<ContentProviderEntity> contentProviderEntityList = contentProviderDAO.findAll();
			if(CollectionUtils.isNotEmpty(contentProviderEntityList)) {
				for(final ContentProviderEntity cpEntity : contentProviderEntityList) {
					
					/* dont' cache CPs who don't have servers */
					if(CollectionUtils.isNotEmpty(cpEntity.getServerSet())) {
						if(CollectionUtils.isNotEmpty(cpEntity.getGroupingXrefs())) {
							/* convert the content provider to a DTO */
							final ContentProvider cp = cpDozerConverter.convertToDTO(cpEntity, true);
							tempContentProviderMap.put(cp.getId(), cp);
							if(CollectionUtils.isNotEmpty(cp.getPatternSet())) {
								
								/* process the URI patterns, but converting any subcollections to DTOs manually, since our Dozer converter will only go 2 levels deep */
								final Map<String, URIPattern> uriPatternMap = new LinkedHashMap<String, URIPattern>();
								for(final URIPattern pattern : cp.getPatternSet()) {
									tempURIPatternMap.put(pattern.getId(), pattern);
									uriPatternMap.put(pattern.getId(), pattern);
									if(CollectionUtils.isNotEmpty(pattern.getMetaEntitySet())) {
										
										/* convert meta */
										final Map<String, URIPatternMeta> metaMap = new LinkedHashMap<String, URIPatternMeta>();
										for(final URIPatternMeta meta : pattern.getMetaEntitySet()) {
											if(setPatternMeta(pattern, meta)) {
												metaMap.put(meta.getId(), meta);
											}
										}
										
										pattern.setMetaEntitySet(new LinkedHashSet<URIPatternMeta>(metaMap.values()));
									}
									
									if(CollectionUtils.isNotEmpty(pattern.getMethods())) {
										for(final URIPatternMethod method : pattern.getMethods()) {
											if(CollectionUtils.isNotEmpty(method.getMetaEntitySet())) {
												final Map<String, URIPatternMethodMeta> metaMap = new LinkedHashMap<String, URIPatternMethodMeta>();
												for(final URIPatternMethodMeta meta : method.getMetaEntitySet()) {
													if(setPatternMeta(pattern, meta)) {
														metaMap.put(meta.getId(), meta);
													}
												}
												method.setMetaEntitySet(new LinkedHashSet<URIPatternMethodMeta>(metaMap.values()));
											}
											
											if(CollectionUtils.isNotEmpty(method.getParams())) {
												for(final URIPatternMethodParameter param : method.getParams()) {
													normalize(param);
												}
											}
										}
									}
									
									if(StringUtils.isNotBlank(pattern.getRedirectToGroovyScript())) {
										try {
											final AbstractRedirectURLGroovyProcessor processor = (AbstractRedirectURLGroovyProcessor)scriptRunner.instantiateClass(null, pattern.getRedirectToGroovyScript());
											pattern.setRedirectProcessor(processor);
										} catch(Throwable e) {
											pattern.setRedirectProcessor(null);
											pattern.setRedirectToGroovyScript(null);
										}
									}
									
									if(CollectionUtils.isNotEmpty(pattern.getParams())) {
										for(final URIPatternParameter param : pattern.getParams()) {
											normalize(param);
										}
									}
									pattern.initTreeSet();
								}
								cp.setPatternSet(new LinkedHashSet<URIPattern>(uriPatternMap.values()));									
							}
							
							tempTree.addContentProvider(cp);
						} else {
							LOG.error(String.format("Content Provider %s will not be used - there are no Auth Level Grouping xrefs", cpEntity.getName()));
						}
					} else {
						LOG.error(String.format("Content Provider %s will not be used - there are no Content Provider Servers", cpEntity.getName()));
					}
				}
			}
			
			final List<AuthLevelGroupingEntity> groupingEntityList = authLevelGroupingDAO.findAll();
			final Map<String, AuthLevelGrouping> tempGroupingMap = new HashMap<String, AuthLevelGrouping>();
			if(CollectionUtils.isNotEmpty(groupingEntityList)) {
				for(final AuthLevelGroupingEntity entity : groupingEntityList) {
					final AuthLevelGrouping grouping = authLevelGroupingDozerConverter.convertToDTO(entity, true);
					tempGroupingMap.put(grouping.getId(), grouping);
				}
			}
			
			synchronized(this) {
				contentProviderTree = tempTree;
				groupingMap = tempGroupingMap;
				contentProviderCache = tempContentProviderMap;
				uriPatternCache = tempURIPatternMap;
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

	@Override
	public AuthenticationRequest createAuthenticationRequest(final String principal, final String proxyURI, final HttpMethod method) throws BasicDataServiceException {
		try {
			final URI uri = new URI(proxyURI);
			final ContentProviderNode cpNode = contentProviderTree.find(uri);
			if(cpNode == null) {
				throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND);
			}
			final ContentProvider cp = cpNode.getContentProvider();
			final String managedSysId = cp.getManagedSysId();
			final LoginEntity login = loginDS.getLoginByManagedSys(principal, managedSysId);
			if(login == null) {
				LOG.error(String.format("Proxy identity not found for principal '%s', proxyURI: '%s", principal, proxyURI));
				throw new BasicDataServiceException(ResponseCode.IDENTITY_NOT_FOUND);
			}
				
			final LoginEntity primaryLogin = loginDS.getPrimaryIdentity(login.getUserId());
			if(primaryLogin == null) {
				LOG.error(String.format("Primary identity not found for principal '%s', proxyURI: '%s", principal, proxyURI));
				throw new BasicDataServiceException(ResponseCode.IDENTITY_NOT_FOUND);
			}
			
			final URIPatternSearchResult patternNode = cpNode.getURIPattern(uri, method);
			final URIPattern uriPattern = patternNode.getPattern();
			final URIPatternMethod uriMethod = patternNode.getMethod();
			
			final List<AuthLevelGrouping> groupingList = getGroupingList(cp, uriPattern);
			final boolean isKerberosAuth = groupingList.stream().filter(e -> e.getId().equals(kerberosAuthId)).count() > 0;
				
			final AuthenticationRequest request = new AuthenticationRequest();
			request.setPrincipal(primaryLogin.getLogin());
			request.setKerberosAuth(isKerberosAuth);
			
			if(uriPattern != null) {
				request.setPatternId(uriPattern.getId());
			}
			if(uriMethod != null) {
				request.setMethodId(uriMethod.getId());
			}
			
			final String password = loginDS.decryptPassword(primaryLogin.getUserId(), primaryLogin.getPassword());
			request.setPassword(password);
			return request;
		} catch(BasicDataServiceException e) {
			throw e;
		} catch(Throwable e) {
			LOG.error("Unkonwn exception", e);
			throw new BasicDataServiceException(ResponseCode.FAIL_OTHER);
		}
	}
	
	@Override
	public URIFederationResponse getMetadata(final String proxyURI, final HttpMethod method) {
		final URIFederationResponse response = new URIFederationResponse();
		final StopWatch sw = new StopWatch();
		sw.start();
		
		final List<AuthLevelGrouping> groupingList = new LinkedList<AuthLevelGrouping>();
		ContentProvider cp = null;
		URIPattern uriPattern = null;
		URIPatternMethod uriMethod = null;
		try {
			final URI uri = new URI(proxyURI);
			final ContentProviderNode cpNode = contentProviderTree.find(uri);
			if(cpNode == null) {
				response.setConfigured(false);
				//throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND);
			} else {
				response.setConfigured(true);
				cp = cpNode.getContentProvider();
				final URIPatternSearchResult patternNode = cpNode.getURIPattern(uri, method);
				uriPattern = patternNode.getPattern();
				uriMethod = patternNode.getMethod();
				
				if(uriPattern != null && CollectionUtils.isNotEmpty(uriPattern.getGroupingXrefs())) {
					for(final AuthLevelGroupingURIPatternXref xref : uriPattern.getOrderedGroupingXrefs()) {
						final String groupingId = xref.getId().getGroupingId();
						final AuthLevelGrouping grouping = groupingMap.get(groupingId);
						if(grouping != null) {
							groupingList.add(grouping);
						}
					}
				} else {
					if(CollectionUtils.isNotEmpty(cp.getGroupingXrefs())) {
						for(final AuthLevelGroupingContentProviderXref xref : cp.getOrderedGroupingXrefs()) {
							final String groupingId = xref.getId().getGroupingId();
							final AuthLevelGrouping grouping = groupingMap.get(groupingId);
							if(grouping != null) {
								groupingList.add(grouping);
							}
						}
					}
				}
			}
			response.succeed();
		/*
		} catch(BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setResponseValue(e.getResponseValue());
			response.setStatus(ResponseStatus.FAILURE);
			//LOG.info(String.format("CP or Pattern Exception: %s", e.getMessage()));
		*/
		} catch(URISyntaxException e) {
			response.setErrorCode(ResponseCode.INVALID_URI);
			response.setStatus(ResponseStatus.FAILURE);
			LOG.error(String.format("URI Syntax Exception: %s", e));
		} catch(Throwable e) {
			response.setErrorCode(ResponseCode.FAIL_OTHER);
			response.setStatus(ResponseStatus.FAILURE);
			LOG.error("Unkonwn error while processing proxy request", e);
		} finally {
			if(uriPattern != null) {
				response.setPatternId(uriPattern.getId());
				response.setServer(uriPattern.getNextServer());
			}
			if(cp != null) {
				response.setAuthCookieDomain(cp.getAuthCookieDomain());
				response.setAuthCookieName(cp.getAuthCookieName());
				response.setCpId(cp.getId());
				response.setServer(cp.getNextServer());
				response.setPostbackURLParamName(cp.getPostbackURLParamName());
				response.setLoginURL(cp.getLoginURL());
				if(response.getServer() == null) {
					response.setServer(cp.getNextServer());
				}
			}
			if(uriMethod != null) {
				response.setMethodId(uriMethod.getId());
			}
			if(CollectionUtils.isNotEmpty(groupingList)) {
				for(final AuthLevelGrouping grouping : groupingList) {
					final AuthLevel level = grouping.getAuthLevel();
					final Set<AuthLevelAttribute> attributes = grouping.getAttributes();
					
					final URIAuthLevelToken token = new URIAuthLevelToken();
					token.setAuthLevelId(level.getId());
					if(CollectionUtils.isNotEmpty(attributes)) {
						for(final AuthLevelAttribute attribute : attributes) {
							final URIAuthLevelAttribute attributeToken = new URIAuthLevelAttribute();
							attributeToken.setName(attribute.getName());
							attributeToken.setValueAsString(attribute.getValueAsString());
							attributeToken.setValueAsByteArray(attribute.getValueAsByteArray());
							attributeToken.setTypeId(attribute.getType().getId());
							attributeToken.setTypeName(attribute.getType().getName());
							token.addAttribute(attributeToken);
						}
					}
					response.addAuthLevelToken(token);
				}
			}
			sw.stop();
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("URI Fedration.  Proxy URI: '%s'. Result: '%s', Time: %s ms", proxyURI, response, sw.getTime()));
			}
		}
		return response;
	}
	
	private <T extends AbstractPatternMetaValue> void processMeta(final URIFederationResponse response,
																  final String userId, 
																  final URI uri, 
																  final URIPattern pattern,
																  final URIPatternMethod method,
																  final ContentProvider cp, 
																  final AbstractMeta<T> meta) throws BasicDataServiceException {
		final URIPatternMetaType type = meta.getMetaType();
		if(type != null) {
			final String springBeanName = type.getSpringBeanName();
			URIPatternRuleToken ruleToken = null;
			try {
				final URIPatternRule rule = ctx.getBean(springBeanName, URIPatternRule.class);
				if(rule != null) {
					final Set<T> valueSet = meta.getMetaValueSet();
					final Set<AbstractPatternMetaValue> abstractValueSet = new HashSet<AbstractPatternMetaValue>();
					if(valueSet != null) {
						abstractValueSet.addAll(valueSet);
					}
					ruleToken = rule.process(userId, uri, type, abstractValueSet, pattern, method, cp, meta);
					response.addRuleToken(ruleToken);
				}
			} catch(Throwable e) {
				if(ruleToken != null) {
					if(CollectionUtils.isNotEmpty(ruleToken.getValueList())) {
						for(final Iterator<URIPatternRuleValue> it = ruleToken.getValueList().iterator(); it.hasNext();) {
							final URIPatternRuleValue rule = it.next();
							if(!rule.isPropagateOnError()) {
								LOG.warn(String.format("Rule %s will not propagate to the proxy due to an error", rule));
								it.remove();
							}
						}
					}
				}
				LOG.error("Error processing rule", e);
				throw new BasicDataServiceException(ResponseCode.URI_PATTERN_RULE_PROCESS_ERROR, e);
			}
		}
	}

	@Override
	public URIFederationResponse federateProxyURI(final String userId, final String proxyURI, final HttpMethod method) {
		final URIFederationResponse response = new URIFederationResponse();
		final StopWatch sw = new StopWatch();
		sw.start();
		
		ContentProvider cp = null;
		URIPattern uriPattern = null;
		URIPatternMethod uriMethod = null;
		List<AuthLevelGrouping> groupingList = new LinkedList<AuthLevelGrouping>();
		try {
			final URI uri = new URI(proxyURI);
			final ContentProviderNode cpNode = contentProviderTree.find(uri);
			if(cpNode == null) {
				response.setConfigured(false);
				//throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND);
			} else {
				response.setConfigured(true);
				cp = cpNode.getContentProvider();
				if(!cp.getIsPublic() && !isEntitled(userId, cp.getResourceId())) {
					throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
				}
				
				final URIPatternSearchResult patternNode = cpNode.getURIPattern(uri, method);
				uriPattern = patternNode.getPattern();
				uriMethod = patternNode.getMethod();
				
				/* means that no matching pattern has been found for this URI (i.e. none configured) - check against the CP */
				if(uriPattern != null) {
					
					/* check entitlements and auth level on patterns */
					if(!uriPattern.getIsPublic() && !isEntitled(userId, uriPattern.getResourceId())) {
						throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN, uriPattern.getPattern());
					}
					
					if(uriMethod != null) {
						if(!isEntitled(userId, uriMethod.getResourceId())) {
							throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_METHOD, uriMethod.getId());
						}
					}
					
					if(StringUtils.isNotBlank(uriPattern.getRedirectTo())) {
						response.setRedirectTo(uriPattern.getRedirectTo());
					} else if(uriPattern.getRedirectProcessor() != null) {
						response.setRedirectTo(uriPattern.getRedirectProcessor().getRedirectURL(userId, cp, uriPattern, uriMethod));
					}
				
					/* do rule processes */				
					if(uriMethod != null) {
						if(CollectionUtils.isNotEmpty(uriMethod.getMetaEntitySet())) {
							for(final URIPatternMethodMeta meta : uriMethod.getMetaEntitySet()) {
								processMeta(response, userId, uri, uriPattern, uriMethod, cp, meta);
							}
						}
					} else {
						if(CollectionUtils.isNotEmpty(uriPattern.getMetaEntitySet())) {
							for(final URIPatternMeta meta : uriPattern.getMetaEntitySet()) {
								processMeta(response, userId, uri, uriPattern, uriMethod, cp, meta);
							}
						}
					}
				} else /*if(!patternNode.isUriPatternFound()) {*/ {
					throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_PATTERN_NOT_FOUND);
				}
				
				groupingList = getGroupingList(cp, uriPattern);
				if(uriPattern != null && CollectionUtils.isNotEmpty(uriPattern.getGroupingXrefs())) {
					for(final AuthLevelGroupingURIPatternXref xref : uriPattern.getOrderedGroupingXrefs()) {
						final String groupingId = xref.getId().getGroupingId();
						final AuthLevelGrouping grouping = groupingMap.get(groupingId);
						if(grouping != null) {
							groupingList.add(grouping);
						}
					}
				} else {
					if(CollectionUtils.isNotEmpty(cp.getGroupingXrefs())) {
						for(final AuthLevelGroupingContentProviderXref xref : cp.getOrderedGroupingXrefs()) {
							final String groupingId = xref.getId().getGroupingId();
							final AuthLevelGrouping grouping = groupingMap.get(groupingId);
							if(grouping != null) {
								groupingList.add(grouping);
							}
						}
					}
				}
				
				if(CollectionUtils.isEmpty(groupingList)) {
					throw new BasicDataServiceException(ResponseCode.FAIL_OTHER);
				}
				
				boolean requiresAuthentication = false;
				for(final AuthLevelGrouping grouping : groupingList) {
					if(grouping.getAuthLevel().isRequiresAuthentication()) {
						requiresAuthentication = true;
					}
				}
				
				if(StringUtils.isEmpty(userId) && requiresAuthentication) {
					throw new BasicDataServiceException(ResponseCode.UNAUTHORIZED);
				}
				
				if(uriPattern != null && CollectionUtils.isNotEmpty(uriPattern.getSubstititonOrderedSet())) {
					for(final URIPatternSubstitution substition : uriPattern.getSubstititonOrderedSet()) {
						response.addSubstitution(new URISubstitutionToken(substition.getQuery(), substition.getReplaceWith(), substition.isExactMatch(), substition.isFastSearch()));
					}
				}
				
	
				if(cp.isUnavailable()) {
					if(!isEntitled(userId, cp.getUnavailableResourceId())) {
						response.setRedirectTo(StringUtils.trimToNull(cp.getUnavailableURL()));
					}
				}
				
				/* lastly, add caching metadata for proxy */
				if(uriPattern != null) {
					response.setCacheable(uriPattern.isCacheable());
					response.setCacheTTL(uriPattern.getCacheTTL());
				}
			}
			
			response.succeed();
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
			if(uriPattern != null) {
				response.setPatternId(uriPattern.getId());
				response.setServer(uriPattern.getNextServer());
				
				if(CollectionUtils.isNotEmpty(uriPattern.getErrorMappings())) {
					for(final URIPatternErrorMapping mapping : uriPattern.getErrorMappings()) {
						response.addErrorMapping(mapping.getErrorCode(), mapping.getRedirectURL());
					}
				}
			}
			if(cp != null) {
				response.setCpId(cp.getId());
				response.setServer(cp.getNextServer());
				response.setPostbackURLParamName(cp.getPostbackURLParamName());
				response.setLoginURL(cp.getLoginURL());
				response.setAuthCookieDomain(cp.getAuthCookieDomain());
				response.setAuthCookieName(cp.getAuthCookieName());
				
				/* fallback, in case URI Pattern not defined */
				if(response.getServer() == null) {
					response.setServer(cp.getNextServer());
				}
			}
			if(uriMethod != null) {
				response.setMethodId(uriMethod.getId());
			}
			if(CollectionUtils.isNotEmpty(groupingList)) {
				for(final AuthLevelGrouping grouping : groupingList) {
					final AuthLevel level = grouping.getAuthLevel();
					final Set<AuthLevelAttribute> attributes = grouping.getAttributes();
					
					final URIAuthLevelToken token = new URIAuthLevelToken();
					token.setAuthLevelId(level.getId());
					if(CollectionUtils.isNotEmpty(attributes)) {
						for(final AuthLevelAttribute attribute : attributes) {
							final URIAuthLevelAttribute attributeToken = new URIAuthLevelAttribute();
							attributeToken.setName(attribute.getName());
							attributeToken.setValueAsString(attribute.getValueAsString());
							attributeToken.setValueAsByteArray(attribute.getValueAsByteArray());
							attributeToken.setTypeId(attribute.getType().getId());
							attributeToken.setTypeName(attribute.getType().getName());
							token.addAttribute(attributeToken);
						}
					}
					response.addAuthLevelToken(token);
				}
			}
			sw.stop();
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("URI Fedration.  Proxy URI: '%s'.  UserId: '%s'.  Output: '%s'.  Time: %s ms", proxyURI, userId, response, sw.getTime()));
			}
		}
		return response;
	}
	
	private List<AuthLevelGrouping> getGroupingList(final ContentProvider cp, final URIPattern uriPattern) {
		final List<AuthLevelGrouping> groupingList = new LinkedList<AuthLevelGrouping>();
		if(uriPattern != null && CollectionUtils.isNotEmpty(uriPattern.getGroupingXrefs())) {
			for(final AuthLevelGroupingURIPatternXref xref : uriPattern.getOrderedGroupingXrefs()) {
				final String groupingId = xref.getId().getGroupingId();
				final AuthLevelGrouping grouping = groupingMap.get(groupingId);
				if(grouping != null) {
					groupingList.add(grouping);
				}
			}
		} else {
			if(CollectionUtils.isNotEmpty(cp.getGroupingXrefs())) {
				for(final AuthLevelGroupingContentProviderXref xref : cp.getOrderedGroupingXrefs()) {
					final String groupingId = xref.getId().getGroupingId();
					final AuthLevelGrouping grouping = groupingMap.get(groupingId);
					if(grouping != null) {
						groupingList.add(grouping);
					}
				}
			}
		}
		return groupingList;
	}
	
	private boolean isEntitled(final String userId, final String resourceId) {
		return authorizationManager.isEntitled(userId, resourceId);
	}

	@Override
	public void setApplicationContext(final ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

	@Override
	public ContentProvider getCachedContentProvider(String providerId) {
		return contentProviderCache.get(providerId);
	}

	@Override
	public URIPattern getCachedURIPattern(String patternId) {
		return uriPatternCache.get(patternId);
	}
}
