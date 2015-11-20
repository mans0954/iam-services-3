package org.openiam.am.srvc.ws;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthLevelAttributeEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.dozer.converter.*;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.groovy.AbstractRedirectURLGroovyProcessor;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.am.srvc.searchbeans.converter.ContentProviderSearchBeanConverter;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebParam;
import javax.jws.WebService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("contentProviderWS")
@WebService(endpointInterface = "org.openiam.am.srvc.ws.ContentProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "ContentProviderWebServicePort",
            serviceName = "ContentProviderWebService")
public class ContentProviderWebServiceImpl implements ContentProviderWebService{
    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ContentProviderService contentProviderService;
    @Autowired
    private ContentProviderDozerConverter contentProviderDozerConverter;
    @Autowired
    private ContentProviderSearchBeanConverter contentProviderSearchBeanConverter;

    @Autowired
    private AuthLevelDozerConverter authLevelDozerConverter;
    
    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    private ContentProviderServerDoserConverter contentProviderServerDoserConverter;
    @Autowired
    private URIPatternDozerConverter uriPatternDozerConverter;
    @Autowired
    private URIPatternMetaDozerConverter uriPatternMetaDozerConverter;

    @Autowired
    private URIPatternMetaTypeDozerConverter uriPatternMetaTypeDozerConverter;
    
    @Autowired
    private AuthLevelGroupingDozerConverter authLevelGroupingDozerConverter;
    
    @Autowired
    private MetadataTypeDAO metadataTypeDAO;
    
    @Autowired
    private AuthLevelAttributeDozerConverter authLevelAttributeDozerConverter;
    
    @Value("${org.openiam.uri.pattern.meta.type.form.post.pattern.rule.id}")
    private String formPostURIPatternRule;
    
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    
	@Override
    @Transactional(readOnly = true)
	public AuthLevelAttribute getAuthLevelAttribute(String id) {
		final AuthLevelAttributeEntity entity = contentProviderService.getAuthLevelAttribute(id);
		return authLevelAttributeDozerConverter.convertToDTO(entity, true);
	}

	@Override
	public Response saveAuthLevelAttribute(AuthLevelAttribute attribute) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	if(attribute == null) {
        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        	}
        	
        	if(StringUtils.isBlank(attribute.getName())) {
        		throw new BasicDataServiceException(ResponseCode.NO_NAME);
        	}
        	
        	if(attribute.getType() == null || StringUtils.isBlank(attribute.getType().getId())) {
        		throw new BasicDataServiceException(ResponseCode.TYPE_REQUIRED);
        	}
        	
        	final MetadataTypeEntity type = metadataTypeDAO.findById(attribute.getType().getId());
        	if(type == null) {
        		throw new BasicDataServiceException(ResponseCode.TYPE_REQUIRED);
        	}
        	
        	if(attribute.getGrouping() == null || StringUtils.isBlank(attribute.getGrouping().getId())) {
        		throw new BasicDataServiceException(ResponseCode.GROUPING_REQUIRED);
        	}
        	
        	if(type.isBinary()) {
        		attribute.setValueAsString(null);
        	} else {
        		attribute.setValueAsByteArray(null);
        	}
        	
        	if(StringUtils.isBlank(attribute.getValueAsString()) && ArrayUtils.isEmpty(attribute.getValueAsByteArray())) {
        		throw new BasicDataServiceException(ResponseCode.VALUE_REQUIRED);
        	}
        	
        	final AuthLevelAttributeEntity entity = authLevelAttributeDozerConverter.convertToEntity(attribute, true);
        	contentProviderService.saveAuthLevelAttibute(entity);
        	response.setResponseValue(entity.getId());
        } catch(BasicDataServiceException e) {
            log.info(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}

	@Override
	public Response deleteAuthLevelAttribute(String id) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	if(id == null) {
        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        	}
        	contentProviderService.deleteAuthLevelAttribute(id);
        } catch(BasicDataServiceException e) {
            log.info(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}
    
    @Override
    public Response saveAuthLevelGrouping(final AuthLevelGrouping grouping) {
    	final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	final AuthLevelGroupingEntity entity = authLevelGroupingDozerConverter.convertToEntity(grouping, true);
        	contentProviderService.validateSaveAuthLevelGrouping(entity);
        	contentProviderService.saveAuthLevelGrouping(entity);
        	response.setResponseValue(entity.getId());
        } catch(BasicDataServiceException e) {
            log.info(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

	@Override
	public Response deleteAuthLevelGrouping(String id) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	contentProviderService.validateDeleteAuthLevelGrouping(id);
        	contentProviderService.deleteAuthLevelGrouping(id);
        } catch(BasicDataServiceException e) {
            log.info(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}

	@Override
    @Transactional(readOnly = true)
	public AuthLevelGrouping getAuthLevelGrouping(String id) {
		final AuthLevelGroupingEntity entity = contentProviderService.getAuthLevelGrouping(id);
		return authLevelGroupingDozerConverter.convertToDTO(entity, true);
	}

	@Override
    public List<AuthLevel> getAuthLevelList() {
		return authLevelDozerConverter.convertToDTOList(contentProviderService.getAuthLevelList(), false);
	}
	
    @Override
    @Transactional(readOnly = true)
    public List<AuthLevelGrouping> getAuthLevelGroupingList() {
    	return authLevelGroupingDozerConverter.convertToDTOList(contentProviderService.getAuthLevelGroupingList(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public ContentProvider getContentProvider(String providerId) {
    	final ContentProviderEntity entity = contentProviderService.getContentProvider(providerId);
        final ContentProvider dto = (entity != null) ? contentProviderDozerConverter.convertToDTO(entity, true) : null;
        return dto;
    }

    @Override
    public List<ContentProvider> findBeans(ContentProviderSearchBean searchBean,int from, int size) {
        return contentProviderService.findBeans(searchBean, from, size);
    }

    @Override
    public int getNumOfContentProviders(ContentProviderSearchBean searchBean) {
        return contentProviderService.getNumOfContentProviders(searchBean);
    }
    

	@Override
	public Response setupApplication(final ContentProvider provider) {
		final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	validate(provider);
        	final ContentProviderEntity contentProvider = contentProviderDozerConverter.convertToEntity(provider,true);
        	contentProviderService.setupApplication(contentProvider);
        	response.setResponseValue(contentProvider.getId());
        } catch(BasicDataServiceException e) {
            log.info(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}
	
	private void validate(final ContentProvider provider) throws BasicDataServiceException {
		if (provider == null) {
            throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        if (StringUtils.isBlank(provider.getName())) {
            throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NAME_NOT_SET);
        }
        if (provider.getDomainPattern()==null || StringUtils.isBlank(provider.getDomainPattern())) {
            throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET);
        }
        
        if(CollectionUtils.isEmpty(provider.getServerSet())) {
        	throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_SERVER_REQUIRED);
        }
        

        if(provider.isUnavailable()) {
        	if(StringUtils.isBlank(provider.getUnavailableURL())) {
        		throw new BasicDataServiceException(ResponseCode.UNAVAILABLE_URL_REQUIRED);
        	}
        }
        
        for(final ContentProviderServer server : provider.getServerSet()) {
        	if(StringUtils.isEmpty(server.getServerURL())) {
        		throw new  BasicDataServiceException(ResponseCode.SERVER_URL_NOT_SET);
        	}
        }
        
        if(StringUtils.isBlank(provider.getAuthCookieDomain())) {
        	throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_COOKIE_DOMAIN_REQUIRED);
        }
        
        if(StringUtils.isBlank(provider.getAuthCookieName())) {
        	throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_COOKIE_NAME_REQUIRED);
        }
        
        if(CollectionUtils.isEmpty(provider.getGroupingXrefs())) {
        	throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET);
        }

        final ContentProviderSearchBean searchBean = new ContentProviderSearchBean();
        searchBean.setName(provider.getName());
        searchBean.setDeepCopy(false);
        final List<ContentProvider> cpEntityWithNameList = findBeans(searchBean, 0, Integer.MAX_VALUE);
        if(CollectionUtils.isNotEmpty(cpEntityWithNameList)) {
        	if(StringUtils.isBlank(provider.getId())) {
        		throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_WITH_NAME_EXISTS);
        	} else {
        		for(final ContentProvider test : cpEntityWithNameList) {
        			if(!StringUtils.equals(provider.getId(), test.getId())) {
        				throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_WITH_NAME_EXISTS);
        			}
        		}
        	}
        }

        if(provider.getId()==null){
            // if provider is new, test for unique domain+ssl
            final List<ContentProviderEntity> result = contentProviderService.getProviderByDomainPattern(
                    provider.getDomainPattern(), provider.getIsSSL());
            if(CollectionUtils.isNotEmpty(result)) {
                if(StringUtils.isBlank(provider.getId())) {
                    throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS);
                } else {
                    for(final ContentProviderEntity test : result) {
                        if(!StringUtils.equals(provider.getId(), test.getId())) {
                            throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_DOMAIN_PATTERN_EXISTS);
                        }
                    }
                }
            }
        }
        
        String domainPattern = provider.getDomainPattern();
        if(domainPattern != null) {
        	/* ignore port */
        	if(domainPattern.indexOf(":") > -1) {
        		domainPattern = domainPattern.substring(0, domainPattern.indexOf(":"));
        	}
        	
        	if(!domainPattern.endsWith(provider.getAuthCookieDomain())) {
            	throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_COOKIE_DOMAIN_NOT_SUBSTR_OF_DOMAIN_PATTERN);
            }
        }
        
        if(StringUtils.isBlank(provider.getAuthProviderId())) {
        	throw new  BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
        }
        
        if(authProviderService.getAuthProvider(provider.getAuthProviderId()) == null) {
        	throw new  BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
        }
	}

    @Override
    public Response saveContentProvider(ContentProvider provider) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
        	validate(provider);
            
            final ContentProviderEntity contentProvider = contentProviderDozerConverter.convertToEntity(provider,true);
            contentProviderService.saveContentProvider(contentProvider);
            response.setResponseValue(contentProvider.getId());

        } catch(BasicDataServiceException e) {
            log.info(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public Response deleteContentProvider(String providerId){
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if(StringUtils.isBlank(providerId))
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            contentProviderService.deleteContentProvider(providerId);

        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    @Deprecated
    @Transactional(readOnly = true)
    public List<URIPattern> getUriPatternsForProvider(String providerId, Integer from, Integer size) {
    	final URIPatternSearchBean sb = new URIPatternSearchBean();
    	sb.setContentProviderId(providerId);
        
        final List<URIPatternEntity> entityList = contentProviderService.getUriPatternsList(sb, from, size);
        final List<URIPattern> dtoList = uriPatternDozerConverter.convertToDTOList(entityList, true);
        return dtoList;
    }

    @Override
    @Deprecated
    public Integer getNumOfUriPatternsForProvider(String providerId) {
    	final URIPatternSearchBean sb = new URIPatternSearchBean();
    	sb.setContentProviderId(providerId);

        return contentProviderService.getNumOfUriPatterns(sb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<URIPattern> findUriPatterns(URIPatternSearchBean searchBean, int from, int size) {
        final List<URIPatternEntity> entityList = contentProviderService.getUriPatternsList(searchBean, from, size);
        return uriPatternDozerConverter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false);
    }

    @Override
    public int getNumOfUriPatterns(URIPatternSearchBean searchBean) {
        return contentProviderService.getNumOfUriPatterns(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public URIPattern getURIPattern(String patternId) {
        return uriPatternDozerConverter.convertToDTO(contentProviderService.getURIPattern(patternId), true);
    }
    
    private boolean isValidRedirectURL(final String redirectURL) {
    	return (redirectURL != null && (redirectURL.startsWith("/") || redirectURL.startsWith("http") || redirectURL.startsWith("https")));
    }

    @Override
    public Response saveURIPattern(@WebParam(name = "pattern", targetNamespace = "") URIPattern pattern) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (pattern==null ) {
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (StringUtils.isBlank(pattern.getPattern())) {
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_URI_PATTERN_NOT_SET);
            }
            if (StringUtils.isBlank(pattern.getContentProviderId())) {
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NOT_SET);
            }
            
            final List<URIPatternEntity> entityList = 
            		contentProviderService.getURIPatternsForContentProviderMatchingPattern(pattern.getContentProviderId(), pattern.getPattern());
            if(CollectionUtils.isNotEmpty(entityList)) {
            	if(StringUtils.isBlank(pattern.getId())) {
            		throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_EXISTS);
            	} else {
            		for(final URIPatternEntity test : entityList) {
            			if(!StringUtils.equals(test.getId(), pattern.getId())) {
            				throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_EXISTS);
            			}
            		}
            	}
            }
            
            if(pattern.getMatchMode() == null) {
    			throw new BasicDataServiceException(ResponseCode.PATTERN_MATCH_MODE_REQUIRED);
    		}
            
            if(PatternMatchMode.ANY_PARAMS.equals(pattern.getMatchMode()) || 
               PatternMatchMode.NO_PARAMS.equals(pattern.getMatchMode()) ||
               PatternMatchMode.IGNORE.equals(pattern.getMatchMode())) {
            	pattern.setParams(null);
    		} else {
    			if(CollectionUtils.isEmpty(pattern.getParams())) {
    				throw new BasicDataServiceException(ResponseCode.PATTERN_PARAMS_REQUIRED);
    			}
    		}
            
            if(pattern.isShowOnApplicationPage()) {
            	if(StringUtils.isBlank(pattern.getUrl())) {
            		throw new BasicDataServiceException(ResponseCode.APPLICATION_URL_REQUIRED);
            	}
            	
            	if(StringUtils.isBlank(pattern.getApplicationName())) {
            		throw new BasicDataServiceException(ResponseCode.APPLICATION_NAME_REQUIRED);
            	}
            } else {
            	pattern.setUrl(null);
            	pattern.setApplicationName(null);
            }
            
            if(pattern.isCacheable()) {
            	if(pattern.getCacheTTL() == null) {
            		throw new BasicDataServiceException(ResponseCode.INVALID_CACHE_TTL);
            	} else if(pattern.getCacheTTL().intValue() <= 0) {
            		throw new BasicDataServiceException(ResponseCode.INVALID_CACHE_TTL);
            	}
            } else {
            	pattern.setCacheTTL(null);
            }

            // validate pattern
            try{
            	ContentProviderNode.validate(pattern.getPattern());
            } catch(InvalidPatternException e){
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_INVALID);
            }
            
            if(CollectionUtils.isNotEmpty(pattern.getErrorMappings())) {
            	for(final URIPatternErrorMapping mapping : pattern.getErrorMappings()) {
            		if(!isValidRedirectURL(mapping.getRedirectURL())) {
            			response.addFieldMapping("errorCode", Integer.valueOf(mapping.getErrorCode()).toString());
            			response.addFieldMapping("redirectURL", mapping.getRedirectURL());
            			throw new BasicDataServiceException(ResponseCode.INVALID_ERROR_REDIRECT_URL);
            		}
            	}
            }
            
            if(CollectionUtils.isNotEmpty(pattern.getSubstitutions())) {
            	for(final URIPatternSubstitution substitution : pattern.getSubstitutions()) {
            		if(substitution.getOrder() == null) {
            			throw new BasicDataServiceException(ResponseCode.ORDER_REQUIRED);
            		}
            		
            		if(StringUtils.isBlank(substitution.getQuery())) {
            			throw new BasicDataServiceException(ResponseCode.URI_PATTTERN_SUBSTITUTION_QUERY_REQUIRED);
            		}
            		
            		/*
            		if(StringUtils.isBlank(substitution.getReplaceWith())) {
        				response.addFieldMapping("query", substitution.getQuery());
            			throw new BasicDataServiceException(ResponseCode.URI_PATTTERN_SUBSTITUTION_REPLACE_WITH_REQUIRED);
            		}
            		*/
            	}
            }
            
            if(CollectionUtils.isNotEmpty(pattern.getServers())) {
            	for(final URIPatternServer server : pattern.getServers()) {
            		if(StringUtils.isBlank(server.getServerURL())) {
            			throw new BasicDataServiceException(ResponseCode.SERVER_URL_NOT_SET);
            		}
            	}
            }
            
            if(CollectionUtils.isNotEmpty(pattern.getMetaEntitySet())) {
            	for(final URIPatternMeta meta : pattern.getMetaEntitySet()) {
            		if(StringUtils.isBlank(meta.getName())) {
            			throw new BasicDataServiceException(ResponseCode.URI_PATTERN_META_NAME_NOT_SET);
            		}
            		
            		if(meta.getMetaType()==null || StringUtils.isBlank(meta.getMetaType().getId())) {
        				response.addFieldMapping("metaName", meta.getName());
            			throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_TYPE_NOT_SET);
            		}
            		
            		if(StringUtils.equals(meta.getMetaType().getId(), formPostURIPatternRule)) {
            			if(StringUtils.isEmpty(meta.getContentType())) {
            				response.addFieldMapping("metaName", meta.getName());
                			throw new  BasicDataServiceException(ResponseCode.PATTERN_META_CONTENT_TYPE_MISSING);
            			}
            		}
            		
            		if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
            			for(final URIPatternMetaValue value : meta.getMetaValueSet()) {
            				if (StringUtils.isBlank(value.getName())) {
            					throw new  BasicDataServiceException(ResponseCode.PATTERN_META_NAME_MISSING);
            				}
            				
            				if(value.isEmptyValue()) {
            					value.setGroovyScript(null);
            					value.setAmAttribute(null);
            					value.setStaticValue(null);
            					value.setFetchedValue(null);
            				} else {
            					if(StringUtils.isBlank(value.getGroovyScript()) && 
            					   StringUtils.isBlank(value.getStaticValue()) &&
            					   StringUtils.isBlank(value.getFetchedValue()) && 
            					   (value.getAmAttribute() == null || StringUtils.isBlank(value.getAmAttribute().getId()))) {
            						response.addFieldMapping("uriPatternMetaName", meta.getName());
                    				response.addFieldMapping("uriPatternMetaValueName", value.getName());
            						throw new  BasicDataServiceException(ResponseCode.PATTERN_META_VALUE_MISSING);
            					}
            				}
            			}
            		}
            	}
            }
            
            if(CollectionUtils.isNotEmpty(pattern.getParams())) {
            	for(final URIPatternParameter param : pattern.getParams()) {
            		if(StringUtils.isBlank(param.getName())) {
            			throw new BasicDataServiceException(ResponseCode.PATTERN_URI_PARAM_NAME_REQUIRED);
            		}
            	}
            }
            
            if(CollectionUtils.isNotEmpty(pattern.getMethods())) {
            	final Set<String> methodSet = new HashSet<String>();
            	for(final URIPatternMethod method : pattern.getMethods()) {
            		if(method.getMatchMode() == null) {
            			response.addFieldMapping("method", method.getMethod().toString());
            			throw new BasicDataServiceException(ResponseCode.METHOD_MATCH_MODE_REQUIRED);
            		}
            		
            		if(PatternMatchMode.ANY_PARAMS.equals(method.getMatchMode()) || 
            		   PatternMatchMode.NO_PARAMS.equals(method.getMatchMode()) || 
            		   PatternMatchMode.IGNORE.equals(method.getMatchMode())) {
            			method.setParams(null);
            		} else {
            			if(CollectionUtils.isEmpty(method.getParams())) {
            				response.addFieldMapping("method", method.getMethod().toString());
            				throw new BasicDataServiceException(ResponseCode.METHOD_PARAMS_REQUIRED);
            			}
            		}
            		
            		if(method.getMethod() == null) {
            			throw new BasicDataServiceException(ResponseCode.URI_PATTERN_METHOD_REQUIRED);
            		}
            		
            		if(CollectionUtils.isNotEmpty(method.getParams())) {
            			for(final URIPatternMethodParameter param : method.getParams()) {
            				if(StringUtils.isBlank(param.getName())) {
            					response.addFieldMapping("method", method.getMethod().toString());
            					throw new BasicDataServiceException(ResponseCode.URI_PATTERN_PARAMETER_NAME_REQUIRED);
            				}
            			}
            		}
            		
            		if(CollectionUtils.isNotEmpty(method.getMetaEntitySet())) {
            			for(final URIPatternMethodMeta meta : method.getMetaEntitySet()) {
            				if(StringUtils.isBlank(meta.getName())) {
            					response.addFieldMapping("method", method.getMethod().toString());
            					throw new BasicDataServiceException(ResponseCode.URI_PATTERN_PARAMTER_META_NAME_REQUIRED);
                    		}
            				
            				if(meta.getMetaType()==null || StringUtils.isBlank(meta.getMetaType().getId())) {
            					response.addFieldMapping("method", method.getMethod().toString());
            					response.addFieldMapping("metaName", meta.getName());
            					throw new BasicDataServiceException(ResponseCode.URI_PATTERN_PARAMTER_META_TYPE_REQUIRED);
            				}
            				
            				if(StringUtils.equals(meta.getMetaType().getId(), formPostURIPatternRule)) {
                    			if(StringUtils.isEmpty(meta.getContentType())) {
                    				response.addFieldMapping("method", method.getMethod().toString());
                    				response.addFieldMapping("metaName", meta.getName());
                        			throw new  BasicDataServiceException(ResponseCode.PATTERN_METHOD_META_CONTENT_TYPE_MISSING);
                    			}
                    		}
            				
            				if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
                    			for(final URIPatternMethodMetaValue value : meta.getMetaValueSet()) {
                    				if (StringUtils.isBlank(value.getName())) {
                    					response.addFieldMapping("method", method.getMethod().toString());
                    					throw new  BasicDataServiceException(ResponseCode.PATTERN_METHOD_META_VALUE_NAME_MISSING);
                    				}
                    				
                    				if(value.isEmptyValue()) {
                    					value.setGroovyScript(null);
                    					value.setAmAttribute(null);
                    					value.setStaticValue(null);
                    					value.setFetchedValue(null);
                    				} else {
                    					if(StringUtils.isBlank(value.getGroovyScript()) && 
                    					   StringUtils.isBlank(value.getStaticValue()) &&
                    					   StringUtils.isBlank(value.getFetchedValue()) && 
                    					   (value.getAmAttribute() == null || StringUtils.isBlank(value.getAmAttribute().getId()))) {
                    						response.addFieldMapping("method", method.getMethod().toString());
                        					response.addFieldMapping("metaName", meta.getName());
                        					response.addFieldMapping("metaValueName", value.getName());
                    						throw new BasicDataServiceException(ResponseCode.PATTERN_METHOD_META_VALUE_MISSING);
                    					}
                    				}
                    			}
                    		}
            			}
            		}
            		
            		if(methodSet.contains(getKey(method))) {
            			response.addFieldMapping("method", method.getMethod().toString());
            			throw new BasicDataServiceException(ResponseCode.METHOD_WITH_PARAMS_ALREADY_DEFINED);
            		}
            		methodSet.add(getKey(method));
            	}
            }
            
            if(StringUtils.isNotBlank(pattern.getRedirectTo())) {
            	if(!isValidRedirectURL(pattern.getRedirectTo())) {
            		throw new BasicDataServiceException(ResponseCode.INVALID_PATTERN_REDIRECT_URL);
            	}
            	pattern.setRedirectToGroovyScript(null);
            } else if(StringUtils.isNotBlank(pattern.getRedirectToGroovyScript())) {
            	final String script  = pattern.getRedirectToGroovyScript();
            	boolean validScript = false;
            	if(scriptRunner.scriptExists(script)) {
            		try {
            			if((scriptRunner.instantiateClass(null, script) instanceof AbstractRedirectURLGroovyProcessor)) {
            				validScript = true;
            			}
            		} catch(Throwable e) {
            			log.warn(String.format("Can't instaniate script %s", script), e);
            		}
            	}
            	
            	if(!validScript) {
            		response.addFieldMapping("className", AbstractRedirectURLGroovyProcessor.class.getCanonicalName());
            		throw new BasicDataServiceException(ResponseCode.INVALID_ERROR_REDIRECT_URL_GROOVY_SCRIPT);
            	}
            }

            final URIPatternEntity entity = uriPatternDozerConverter.convertToEntity(pattern,true);
            contentProviderService.saveURIPattern(entity);
            response.setResponseValue(entity.getId());

        } catch(BasicDataServiceException e) {
            log.warn(e.getMessage(), e);
            response.setErrorTokenList(e.getErrorTokenList());
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }
    
    private String getKey(final URIPatternMethod key) {
    	final StringBuilder sb = new StringBuilder(key.getMethod().toString()).append("-").append(key.getMatchMode().toString());
    	if(CollectionUtils.isNotEmpty(key.getParams())) {
    		key.getParams().forEach(param -> {
    			sb.append("-").append(param.getName().toLowerCase());
    			if(CollectionUtils.isNotEmpty(param.getValues())) {
    				final List<String> values = new ArrayList<String>();
    				param.getValues().forEach(val -> {
    					if(val != null) {
    						values.add(val.toLowerCase().trim());
    					}
    				});
    				Collections.sort(values);
    				sb.append("-").append(values);
    			}
    		});
    	}
    	return sb.toString();
    }

    @Override
    public Response deleteProviderPattern(@WebParam(name = "providerId", targetNamespace = "") String providerId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.isBlank(providerId))
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            contentProviderService.deleteProviderPattern(providerId);

        } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
    }

    @Override
    public List<URIPatternMetaType> getAllMetaType() {
        return uriPatternMetaTypeDozerConverter.convertToDTOList(contentProviderService.getAllMetaType(), false);
    }

	@Override
	public Response createDefaultURIPatterns(String providerId) {
		 final Response response = new Response(ResponseStatus.SUCCESS);
		 try {
			 if (StringUtils.isBlank(providerId)) {
				 throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
			 }
			 contentProviderService.createDefaultURIPatterns(providerId);
		 } catch(BasicDataServiceException e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorCode(e.getCode());
        } catch(Throwable e) {
            log.error(e.getMessage(), e);
            response.setStatus(ResponseStatus.FAILURE);
            response.setErrorText(e.getMessage());
        }
        return response;
	}
}
