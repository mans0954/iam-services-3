package org.openiam.am.srvc.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.openiam.am.srvc.dao.AuthLevelGroupingDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.am.srvc.domain.URIPatternMetaValueEntity;
import org.openiam.am.srvc.dozer.converter.AuthLevelGroupingDozerConverter;
import org.openiam.am.srvc.dozer.converter.ContentProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaValueDozerConverter;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.AuthLevelGroupingContentProviderXref;
import org.openiam.am.srvc.dto.AuthLevelGroupingURIPatternXref;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.groovy.URIFederationGroovyProcessor;
import org.openiam.am.srvc.uriauth.dto.URIAuthLevelAttribute;
import org.openiam.am.srvc.uriauth.dto.URIAuthLevelToken;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpMethod;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("uriFederationService")
//@ManagedResource(objectName="org.openiam.am.srvc.service:name=URIFederationService")
public class URIFederationServiceImpl implements URIFederationService, ApplicationContextAware, InitializingBean, Sweepable {

	private static Logger LOG = Logger.getLogger(URIFederationServiceImpl.class);
	private ApplicationContext ctx;

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
						if(CollectionUtils.isNotEmpty(cpEntity.getGroupingXrefs())) {
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
	public AuthenticationRequest createAuthenticationRequest(final String principal, final String proxyURI) throws BasicDataServiceException {
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

			final AuthenticationRequest request = new AuthenticationRequest();
			request.setPrincipal(primaryLogin.getLogin());

			final String password = loginDS.decryptPassword(primaryLogin.getUserId(), primaryLogin.getPassword());
			if(StringUtils.isBlank(password)) {
				LOG.warn(String.format("Null password for user %s:%s.  This user will likely not be allowed to succesfully login", primaryLogin.getUserId(), primaryLogin.getLogin()));
			}
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
	public URIFederationResponse getMetadata(String proxyURI) {
		final URIFederationResponse response = new URIFederationResponse();
		final StopWatch sw = new StopWatch();
		sw.start();

		final List<AuthLevelGrouping> groupingList = new LinkedList<AuthLevelGrouping>();
		ContentProvider cp = null;
		URIPattern uriPattern = null;
		try {
			final URI uri = new URI(proxyURI);
			final ContentProviderNode cpNode = contentProviderTree.find(uri);
			if(cpNode == null) {
				throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND);
			}
			cp = cpNode.getContentProvider();

			final URIPatternSearchResult uriPatternToken = (cpNode.getPatternTree() != null) ? cpNode.getPatternTree().find(uri) : null;

			/* means that no matching pattern has been found for this URI (i.e. none configured) - check against the CP */
			if(uriPatternToken != null && uriPatternToken.hasPatterns()) {

				/* check entitlements and auth level on patterns */
				for(final URIPattern pattern : uriPatternToken.getFoundPatterns()) {
					uriPattern = pattern;
					break;
				}
			}

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
			if(uriPattern != null) {
				response.setPatternId(uriPattern.getId());
			}
			if(cp != null) {
				response.setAuthProviderId(cp.getAuthProviderId());
				response.setCpId(cp.getId());
				response.setServer(cp.getNextServer());
				response.setPostbackURLParamName(cp.getPostbackURLParamName());
				response.setLoginURL(cp.getLoginURL());
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
							attributeToken.setTypeName(attribute.getType().getDescription());
							token.addAttribute(attributeToken);
						}
					}
					response.addAuthLevelToken(token);
				}
			}
			sw.stop();
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("URI Fedration took: %s ms", sw.getTime()));
			}
		}
		return response;
	}

	@Override
	public URIFederationResponse federateProxyURI(final String userId, final int authLevel, final String proxyURI) {
		final URIFederationResponse response = new URIFederationResponse();
		final StopWatch sw = new StopWatch();
		sw.start();

		ContentProvider cp = null;
		URIPattern uriPattern = null;
		final List<AuthLevelGrouping> groupingList = new LinkedList<AuthLevelGrouping>();
		try {
			final URI uri = new URI(proxyURI);
			final ContentProviderNode cpNode = contentProviderTree.find(uri);
			if(cpNode == null) {
				throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_CONTENT_PROVIDER_NOT_FOUND);
			}
			cp = cpNode.getContentProvider();
			if(!cp.getIsPublic() && !isEntitled(userId, cp.getResourceId())) {
				throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_CONTENT_PROVIDER);
			}

			final URIPatternSearchResult uriPatternToken = (cpNode.getPatternTree() != null) ? cpNode.getPatternTree().find(uri) : null;

			/* means that no matching pattern has been found for this URI (i.e. none configured) - check against the CP */
			if(uriPatternToken != null && uriPatternToken.hasPatterns()) {

				/* check entitlements and auth level on patterns */
				for(final URIPattern pattern : uriPatternToken.getFoundPatterns()) {
					if(!pattern.getIsPublic() && !isEntitled(userId, pattern.getResourceId())) {
						throw new BasicDataServiceException(ResponseCode.URI_FEDERATION_NOT_ENTITLED_TO_PATTERN, pattern.getPattern());
					}
					//TODO:  set auth levels here
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
					uriPattern = pattern;
				}
			}

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
			if(uriPattern != null) {
				response.setPatternId(uriPattern.getId());
			}
			if(cp != null) {
				response.setAuthProviderId(cp.getAuthProviderId());
				response.setCpId(cp.getId());
				response.setServer(cp.getNextServer());
				response.setPostbackURLParamName(cp.getPostbackURLParamName());
				response.setLoginURL(cp.getLoginURL());
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
							attributeToken.setTypeName(attribute.getType().getDescription());
							token.addAttribute(attributeToken);
						}
					}
					response.addAuthLevelToken(token);
				}
			}
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
