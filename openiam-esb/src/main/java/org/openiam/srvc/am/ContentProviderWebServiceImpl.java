package org.openiam.srvc.am;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthLevelAttributeEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.dozer.converter.AuthLevelAttributeDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthLevelDozerConverter;
import org.openiam.am.srvc.dozer.converter.AuthLevelGroupingDozerConverter;
import org.openiam.am.srvc.dozer.converter.ContentProviderDozerConverter;
import org.openiam.am.srvc.dozer.converter.ContentProviderServerDoserConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaDozerConverter;
import org.openiam.am.srvc.dozer.converter.URIPatternMetaTypeDozerConverter;
import org.openiam.am.srvc.dto.AuthLevel;
import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.dto.ContentProviderServer;
import org.openiam.am.srvc.dto.PatternMatchMode;
import org.openiam.am.srvc.dto.URIPattern;
import org.openiam.am.srvc.dto.URIPatternErrorMapping;
import org.openiam.am.srvc.dto.URIPatternMeta;
import org.openiam.am.srvc.dto.URIPatternMetaType;
import org.openiam.am.srvc.dto.URIPatternMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethod;
import org.openiam.am.srvc.dto.URIPatternMethodMeta;
import org.openiam.am.srvc.dto.URIPatternMethodMetaValue;
import org.openiam.am.srvc.dto.URIPatternMethodParameter;
import org.openiam.am.srvc.dto.URIPatternParameter;
import org.openiam.am.srvc.dto.URIPatternServer;
import org.openiam.am.srvc.dto.URIPatternSubstitution;
import org.openiam.am.srvc.groovy.AbstractRedirectURLGroovyProcessor;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.am.srvc.service.AuthProviderService;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.*;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.SearchParam;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.script.ScriptIntegration;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("contentProviderWS")
@WebService(endpointInterface = "org.openiam.srvc.am.ContentProviderWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/am/service", portName = "ContentProviderWebServicePort",
            serviceName = "ContentProviderWebService")
public class ContentProviderWebServiceImpl extends AbstractApiService implements ContentProviderWebService{
    private final Log log = LogFactory.getLog(this.getClass());
    @Autowired
    private ContentProviderService contentProviderService;
    @Autowired
    private ContentProviderDozerConverter contentProviderDozerConverter;


    


    @Autowired
    private ContentProviderServerDoserConverter contentProviderServerDoserConverter;

    @Autowired
    private URIPatternMetaDozerConverter uriPatternMetaDozerConverter;


    

    

    

    


    public ContentProviderWebServiceImpl() {
        super(OpenIAMQueue.ContentProviderQueue);
    }


    @Override
//    @Transactional(readOnly = true)
	public AuthLevelAttribute getAuthLevelAttribute(String id) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(id);
        return getValue(ContentProviderAPI.GetAuthLevelAttribute, request, AuthLevelAttributeResponse.class);
//		final AuthLevelAttributeEntity entity = contentProviderService.getAuthLevelAttribute(id);
//		return authLevelAttributeDozerConverter.convertToDTO(entity, true);
	}

	@Override
	public Response saveAuthLevelAttribute(AuthLevelAttribute attribute) {
        return manageGrudApiRequest(ContentProviderAPI.SaveAuthLevelAttribute, attribute);
//        BaseGrudServiceRequest<AuthLevelAttribute> request = new BaseGrudServiceRequest<>(attribute);
//        StringResponse response = this.manageApiRequest(ContentProviderAPI.SaveAuthLevelAttribute, request,StringResponse.class);
//		return response.convertToBase();
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	if(attribute == null) {
//        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
//        	}
//
//        	if(StringUtils.isBlank(attribute.getName())) {
//        		throw new BasicDataServiceException(ResponseCode.NO_NAME);
//        	}
//
//        	if(attribute.getType() == null || StringUtils.isBlank(attribute.getType().getId())) {
//        		throw new BasicDataServiceException(ResponseCode.TYPE_REQUIRED);
//        	}
//
//        	final MetadataTypeEntity type = metadataTypeDAO.findById(attribute.getType().getId());
//        	if(type == null) {
//        		throw new BasicDataServiceException(ResponseCode.TYPE_REQUIRED);
//        	}
//
//        	if(attribute.getGrouping() == null || StringUtils.isBlank(attribute.getGrouping().getId())) {
//        		throw new BasicDataServiceException(ResponseCode.GROUPING_REQUIRED);
//        	}
//
//        	if(type.isBinary()) {
//        		attribute.setValueAsString(null);
//        	} else {
//        		attribute.setValueAsByteArray(null);
//        	}
//
//        	if(StringUtils.isBlank(attribute.getValueAsString()) && ArrayUtils.isEmpty(attribute.getValueAsByteArray())) {
//        		throw new BasicDataServiceException(ResponseCode.VALUE_REQUIRED);
//        	}
//
//        	final AuthLevelAttributeEntity entity = authLevelAttributeDozerConverter.convertToEntity(attribute, true);
//        	contentProviderService.saveAuthLevelAttibute(entity);
//        	response.setResponseValue(entity.getId());
//        } catch(BasicDataServiceException e) {
//            log.info(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
	}

	@Override
	public Response deleteAuthLevelAttribute(String id) {
        return this.manageGrudApiRequest(ContentProviderAPI.DeleteAuthLevelAttribute, id);
//		final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	if(id == null) {
//        		throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
//        	}
//        	contentProviderService.deleteAuthLevelAttribute(id);
//        } catch(BasicDataServiceException e) {
//            log.info(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
	}
    
    @Override
    public Response saveAuthLevelGrouping(final AuthLevelGrouping grouping) {
        return manageGrudApiRequest(ContentProviderAPI.SaveAuthLevelGrouping, grouping);
//    	final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	final AuthLevelGroupingEntity entity = authLevelGroupingDozerConverter.convertToEntity(grouping, true);
//        	contentProviderService.validateSaveAuthLevelGrouping(entity);
//        	contentProviderService.saveAuthLevelGrouping(entity);
//        	response.setResponseValue(entity.getId());
//        } catch(BasicDataServiceException e) {
//            log.info(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }

	@Override
	public Response deleteAuthLevelGrouping(String id) {
        return this.manageGrudApiRequest(ContentProviderAPI.DeleteAuthLevelGrouping, id);

//		final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	contentProviderService.validateDeleteAuthLevelGrouping(id);
//        	contentProviderService.deleteAuthLevelGrouping(id);
//        } catch(BasicDataServiceException e) {
//            log.info(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
	}

	@Override
//    @Transactional(readOnly = true)
	public AuthLevelGrouping getAuthLevelGrouping(String id) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(id);
        return getValue(ContentProviderAPI.GetAuthLevelGrouping, request, AuthLevelGroupingResponse.class);
//		final AuthLevelGroupingEntity entity = contentProviderService.getAuthLevelGrouping(id);
//		return authLevelGroupingDozerConverter.convertToDTO(entity, true);
	}

	@Override
    public List<AuthLevel> getAuthLevelList() {
        return getValueList(ContentProviderAPI.GetAuthLevelList, new BaseServiceRequest(), AuthLevelListResponse.class);
//		return authLevelDozerConverter.convertToDTOList(contentProviderService.getAuthLevelList(), false);
	}
	
    @Override
    @Transactional(readOnly = true)
    public List<AuthLevelGrouping> getAuthLevelGroupingList() {
        return getValueList(ContentProviderAPI.GetAuthLevelGroupingList, new BaseServiceRequest(), AuthLevelGroupingListResponse.class);

//        return (List<AuthLevelGrouping>)this.getValueList(ContentProviderAPI.GetAuthLevelGroupingList, new BaseServiceRequest(), AuthLevelGroupingListResponse.class);
//    	return authLevelGroupingDozerConverter.convertToDTOList(contentProviderService.getAuthLevelGroupingList(), true);
    }

    @Override
    public ContentProvider getContentProvider(String providerId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(providerId);
        return getValue(ContentProviderAPI.GetContentProvider, request, ContentProviderResponse.class);
    }

    @Override
    public List<ContentProvider> findBeans(ContentProviderSearchBean searchBean,int from, int size) {
        BaseSearchServiceRequest<ContentProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
        return getValueList(ContentProviderAPI.FindContentProviderBeans, request, ContentProviderListResponse.class);

//        return (List<ContentProvider>)this.getValueList(ContentProviderAPI.FindContentProviderBeans, request, ContentProviderListResponse.class);
//        return contentProviderService.findBeans(searchBean, from, size);
    }

    @Override
    public int getNumOfContentProviders(ContentProviderSearchBean searchBean) {
        BaseSearchServiceRequest<ContentProviderSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
        return getIntValue(ContentProviderAPI.GetNumOfContentProviders, request);
//        return contentProviderService.getNumOfContentProviders(searchBean);
    }
    

	@Override
	public Response setupApplication(final ContentProvider provider) {
        return manageGrudApiRequest(ContentProviderAPI.SetupApplication, provider);

//		final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	validate(provider);
//        	final ContentProviderEntity contentProvider = contentProviderDozerConverter.convertToEntity(provider,true);
//        	contentProviderService.setupApplication(contentProvider);
//        	response.setResponseValue(contentProvider.getId());
//        } catch(BasicDataServiceException e) {
//            log.info(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//            response.setStacktraceText(ExceptionUtils.getStackTrace(e));
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//            response.setStacktraceText(ExceptionUtils.getStackTrace(e));
//        }
//        return response;
	}
	


    @Override
    public Response saveContentProvider(ContentProvider provider) {
        return manageGrudApiRequest(ContentProviderAPI.SaveContentProvider, provider);
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//        	validate(provider);
//
//            final ContentProviderEntity contentProvider = contentProviderDozerConverter.convertToEntity(provider,true);
//            contentProviderService.saveContentProvider(contentProvider);
//            response.setResponseValue(contentProvider.getId());
//
//        } catch(BasicDataServiceException e) {
//            log.info(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }

    @Override
    public Response deleteContentProvider(String providerId){
        return manageGrudApiRequest(ContentProviderAPI.DeleteContentProvider, providerId);
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if(StringUtils.isBlank(providerId))
//                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//
//            contentProviderService.deleteContentProvider(providerId);
//
//        } catch(BasicDataServiceException e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }

    @Override
    @Deprecated
//    @Transactional(readOnly = true)
    public List<URIPattern> getUriPatternsForProvider(String providerId, Integer from, Integer size) {
        final URIPatternSearchBean sb = new URIPatternSearchBean();
        sb.setContentProviderId(providerId);
        return findUriPatterns(sb, from, size);

//        BaseSearchServiceRequest<URIPatternSearchBean> request = new BaseSearchServiceRequest<>(sb, from, size);
//        return this.getValueList(ContentProviderAPI.GetUriPatternsForProvider, request, URIPatternListResponse.class);
//        final List<URIPatternEntity> entityList = contentProviderService.getUriPatternsList(sb, from, size);
//        final List<URIPattern> dtoList = uriPatternDozerConverter.convertToDTOList(entityList, true);
//        return dtoList;
    }

    @Override
    @Deprecated
    public Integer getNumOfUriPatternsForProvider(String providerId) {
    	final URIPatternSearchBean sb = new URIPatternSearchBean();
    	sb.setContentProviderId(providerId);
        return getNumOfUriPatterns(sb);

//        BaseSearchServiceRequest<URIPatternSearchBean> request = new BaseSearchServiceRequest<>(sb);
//        return getIntValue(ContentProviderAPI.GetNumOfUriPatternsForProvider, request);
//        return contentProviderService.getNumOfUriPatterns(sb);
    }

    @Override
//    @Transactional(readOnly = true)
    public List<URIPattern> findUriPatterns(URIPatternSearchBean searchBean, int from, int size) {
        BaseSearchServiceRequest<URIPatternSearchBean> request = new BaseSearchServiceRequest<>(searchBean, from, size);
        return this.getValueList(ContentProviderAPI.FindUriPatterns, request, URIPatternListResponse.class);
//        final List<URIPatternEntity> entityList = contentProviderService.getUriPatternsList(searchBean, from, size);
//        return uriPatternDozerConverter.convertToDTOList(entityList, (searchBean != null) ? searchBean.isDeepCopy() : false);
    }

    @Override
    public int getNumOfUriPatterns(URIPatternSearchBean searchBean) {
        BaseSearchServiceRequest<URIPatternSearchBean> request = new BaseSearchServiceRequest<>(searchBean);
        return getIntValue(ContentProviderAPI.GetNumOfUriPatterns, request);
//        return contentProviderService.getNumOfUriPatterns(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public URIPattern getURIPattern(String patternId) {
        IdServiceRequest request = new IdServiceRequest();
        request.setId(patternId);
        return getValue(ContentProviderAPI.GetURIPattern, request, URIPatternResponse.class);
//        return uriPatternDozerConverter.convertToDTO(contentProviderService.getURIPattern(patternId), true);
    }
    


    @Override
    public Response saveURIPattern(final @WebParam(name = "pattern", targetNamespace = "") URIPattern pattern) {
        return manageGrudApiRequest(ContentProviderAPI.SaveURIPattern, pattern);
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if (pattern==null ) {
//                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//            }
//            if (StringUtils.isBlank(pattern.getPattern())) {
//                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_URI_PATTERN_NOT_SET);
//            }
//            if (StringUtils.isBlank(pattern.getContentProviderId())) {
//                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NOT_SET);
//            }
//
//            final List<URIPatternEntity> entityList =
//            		contentProviderService.getURIPatternsForContentProviderMatchingPattern(pattern.getContentProviderId(), pattern.getPattern());
//            if(CollectionUtils.isNotEmpty(entityList)) {
//            	if(StringUtils.isBlank(pattern.getId())) {
//            		throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_EXISTS);
//            	} else {
//            		for(final URIPatternEntity test : entityList) {
//            			if(!StringUtils.equals(test.getId(), pattern.getId())) {
//            				throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_EXISTS);
//            			}
//            		}
//            	}
//            }
//
//            if(pattern.getMatchMode() == null) {
//    			throw new BasicDataServiceException(ResponseCode.PATTERN_MATCH_MODE_REQUIRED);
//    		}
//
//            if(PatternMatchMode.ANY_PARAMS.equals(pattern.getMatchMode()) ||
//               PatternMatchMode.NO_PARAMS.equals(pattern.getMatchMode()) ||
//               PatternMatchMode.IGNORE.equals(pattern.getMatchMode())) {
//            	pattern.setParams(null);
//    		} else {
//    			if(CollectionUtils.isEmpty(pattern.getParams())) {
//    				throw new BasicDataServiceException(ResponseCode.PATTERN_PARAMS_REQUIRED);
//    			}
//    		}
//
//            if(pattern.isShowOnApplicationPage()) {
//            	if(StringUtils.isBlank(pattern.getUrl())) {
//            		throw new BasicDataServiceException(ResponseCode.APPLICATION_URL_REQUIRED);
//            	}
//
//            	if(StringUtils.isBlank(pattern.getApplicationName())) {
//            		throw new BasicDataServiceException(ResponseCode.APPLICATION_NAME_REQUIRED);
//            	}
//            } else {
//            	pattern.setUrl(null);
//            	pattern.setApplicationName(null);
//            }
//
//            if(pattern.isCacheable()) {
//            	if(pattern.getCacheTTL() == null) {
//            		throw new BasicDataServiceException(ResponseCode.INVALID_CACHE_TTL);
//            	} else if(pattern.getCacheTTL().intValue() <= 0) {
//            		throw new BasicDataServiceException(ResponseCode.INVALID_CACHE_TTL);
//            	}
//            } else {
//            	pattern.setCacheTTL(null);
//            }
//
//            // validate pattern
//            try{
//            	ContentProviderNode.validate(pattern.getPattern());
//            } catch(InvalidPatternException e){
//                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_INVALID);
//            }
//
//            if(CollectionUtils.isNotEmpty(pattern.getErrorMappings())) {
//            	for(final URIPatternErrorMapping mapping : pattern.getErrorMappings()) {
//            		if(!isValidRedirectURL(mapping.getRedirectURL())) {
//            			response.addFieldMapping("errorCode", Integer.valueOf(mapping.getErrorCode()).toString());
//            			response.addFieldMapping("redirectURL", mapping.getRedirectURL());
//            			throw new BasicDataServiceException(ResponseCode.INVALID_ERROR_REDIRECT_URL);
//            		}
//            	}
//            }
//
//            if(CollectionUtils.isNotEmpty(pattern.getGroupingXrefs())) {
//            	if(pattern.getGroupingXrefs().stream().map(e -> e.getId()).filter(e -> e.getGroupingId().equals(smsAuthLevelId) || e.getGroupingId().equals(totpAuthLevelId)).count() == 2) {
//            		throw new BasicDataServiceException(ResponseCode.SMS_AND_TOTP_NOT_ALLOWED_SIMULTANEOUSLY);
//            	}
//            }
//
//            if(CollectionUtils.isNotEmpty(pattern.getSubstitutions())) {
//            	for(final URIPatternSubstitution substitution : pattern.getSubstitutions()) {
//            		if(substitution.getOrder() == null) {
//            			throw new BasicDataServiceException(ResponseCode.ORDER_REQUIRED);
//            		}
//
//            		if(StringUtils.isBlank(substitution.getQuery())) {
//            			throw new BasicDataServiceException(ResponseCode.URI_PATTTERN_SUBSTITUTION_QUERY_REQUIRED);
//            		}
//
//            		/*
//            		if(StringUtils.isBlank(substitution.getReplaceWith())) {
//        				response.addFieldMapping("query", substitution.getQuery());
//            			throw new BasicDataServiceException(ResponseCode.URI_PATTTERN_SUBSTITUTION_REPLACE_WITH_REQUIRED);
//            		}
//            		*/
//            	}
//            }
//
//            if(CollectionUtils.isNotEmpty(pattern.getServers())) {
//            	for(final URIPatternServer server : pattern.getServers()) {
//            		if(StringUtils.isBlank(server.getServerURL())) {
//            			throw new BasicDataServiceException(ResponseCode.SERVER_URL_NOT_SET);
//            		}
//            	}
//            }
//
//            if(CollectionUtils.isNotEmpty(pattern.getMetaEntitySet())) {
//            	for(final URIPatternMeta meta : pattern.getMetaEntitySet()) {
//            		if(StringUtils.isBlank(meta.getName())) {
//            			throw new BasicDataServiceException(ResponseCode.URI_PATTERN_META_NAME_NOT_SET);
//            		}
//
//            		if(meta.getMetaType()==null || StringUtils.isBlank(meta.getMetaType().getId())) {
//        				response.addFieldMapping("metaName", meta.getName());
//            			throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_TYPE_NOT_SET);
//            		}
//
//            		if(StringUtils.equals(meta.getMetaType().getId(), formPostURIPatternRule)) {
//            			if(StringUtils.isEmpty(meta.getContentType())) {
//            				response.addFieldMapping("metaName", meta.getName());
//                			throw new  BasicDataServiceException(ResponseCode.PATTERN_META_CONTENT_TYPE_MISSING);
//            			}
//            		}
//
//        			/* cookies require a path */
//    				if(StringUtils.equals(meta.getMetaType().getId(), cookieMetadataType)) {
//    					if(StringUtils.isBlank(meta.getCookiePath())) {
//    						response.addFieldMapping("metaName", meta.getName());
//    						throw new BasicDataServiceException(ResponseCode.COOKIE_PATH_REQUIRED);
//    					}
//    				}
//
//            		if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
//            			for(final URIPatternMetaValue value : meta.getMetaValueSet()) {
//            				if (StringUtils.isBlank(value.getName())) {
//            					throw new  BasicDataServiceException(ResponseCode.PATTERN_META_NAME_MISSING);
//            				}
//
//            				if(value.isEmptyValue()) {
//            					value.setGroovyScript(null);
//            					value.setAmAttribute(null);
//            					value.setStaticValue(null);
//            					value.setFetchedValue(null);
//            				} else {
//            					if(StringUtils.isBlank(value.getGroovyScript()) &&
//            					   StringUtils.isBlank(value.getStaticValue()) &&
//            					   StringUtils.isBlank(value.getFetchedValue()) &&
//            					   (value.getAmAttribute() == null || StringUtils.isBlank(value.getAmAttribute().getId()))) {
//            						response.addFieldMapping("uriPatternMetaName", meta.getName());
//                    				response.addFieldMapping("uriPatternMetaValueName", value.getName());
//            						throw new  BasicDataServiceException(ResponseCode.PATTERN_META_VALUE_MISSING);
//            					}
//            				}
//            			}
//            		}
//            	}
//            }
//
//            if(CollectionUtils.isNotEmpty(pattern.getParams())) {
//            	for(final URIPatternParameter param : pattern.getParams()) {
//            		if(StringUtils.isBlank(param.getName())) {
//            			throw new BasicDataServiceException(ResponseCode.PATTERN_URI_PARAM_NAME_REQUIRED);
//            		}
//            	}
//            }
//
//            if(CollectionUtils.isNotEmpty(pattern.getMethods())) {
//            	final Set<String> methodSet = new HashSet<String>();
//            	for(final URIPatternMethod method : pattern.getMethods()) {
//            		if(method.getMatchMode() == null) {
//            			response.addFieldMapping("method", method.getMethod().toString());
//            			throw new BasicDataServiceException(ResponseCode.METHOD_MATCH_MODE_REQUIRED);
//            		}
//
//            		if(PatternMatchMode.ANY_PARAMS.equals(method.getMatchMode()) ||
//            		   PatternMatchMode.NO_PARAMS.equals(method.getMatchMode()) ||
//            		   PatternMatchMode.IGNORE.equals(method.getMatchMode())) {
//            			method.setParams(null);
//            		} else {
//            			if(CollectionUtils.isEmpty(method.getParams())) {
//            				response.addFieldMapping("method", method.getMethod().toString());
//            				throw new BasicDataServiceException(ResponseCode.METHOD_PARAMS_REQUIRED);
//            			}
//            		}
//
//            		if(method.getMethod() == null) {
//            			throw new BasicDataServiceException(ResponseCode.URI_PATTERN_METHOD_REQUIRED);
//            		}
//
//            		if(CollectionUtils.isNotEmpty(method.getParams())) {
//            			for(final URIPatternMethodParameter param : method.getParams()) {
//            				if(StringUtils.isBlank(param.getName())) {
//            					response.addFieldMapping("method", method.getMethod().toString());
//            					throw new BasicDataServiceException(ResponseCode.URI_PATTERN_PARAMETER_NAME_REQUIRED);
//            				}
//            			}
//            		}
//
//            		if(CollectionUtils.isNotEmpty(method.getMetaEntitySet())) {
//            			for(final URIPatternMethodMeta meta : method.getMetaEntitySet()) {
//            				if(StringUtils.isBlank(meta.getName())) {
//            					response.addFieldMapping("method", method.getMethod().toString());
//            					throw new BasicDataServiceException(ResponseCode.URI_PATTERN_PARAMTER_META_NAME_REQUIRED);
//                    		}
//
//            				if(meta.getMetaType()==null || StringUtils.isBlank(meta.getMetaType().getId())) {
//            					response.addFieldMapping("method", method.getMethod().toString());
//            					response.addFieldMapping("metaName", meta.getName());
//            					throw new BasicDataServiceException(ResponseCode.URI_PATTERN_PARAMTER_META_TYPE_REQUIRED);
//            				}
//
//            				if(StringUtils.equals(meta.getMetaType().getId(), formPostURIPatternRule)) {
//                    			if(StringUtils.isEmpty(meta.getContentType())) {
//                    				response.addFieldMapping("method", method.getMethod().toString());
//                    				response.addFieldMapping("metaName", meta.getName());
//                        			throw new  BasicDataServiceException(ResponseCode.COOKIE_PATH_REQUIRED_ON_METHOD);
//                    			}
//                    		}
//
//            				/* cookies require a path */
//            				if(StringUtils.equals(meta.getMetaType().getId(), cookieMetadataType)) {
//            					if(StringUtils.isBlank(meta.getCookiePath())) {
//            						response.addFieldMapping("method", method.getMethod().toString());
//                    				response.addFieldMapping("metaName", meta.getName());
//            						throw new BasicDataServiceException(ResponseCode.COOKIE_PATH_REQUIRED);
//            					}
//            				}
//
//            				if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
//                    			for(final URIPatternMethodMetaValue value : meta.getMetaValueSet()) {
//                    				if (StringUtils.isBlank(value.getName())) {
//                    					response.addFieldMapping("method", method.getMethod().toString());
//                    					throw new  BasicDataServiceException(ResponseCode.PATTERN_METHOD_META_VALUE_NAME_MISSING);
//                    				}
//
//                    				if(value.isEmptyValue()) {
//                    					value.setGroovyScript(null);
//                    					value.setAmAttribute(null);
//                    					value.setStaticValue(null);
//                    					value.setFetchedValue(null);
//                    				} else {
//                    					if(StringUtils.isBlank(value.getGroovyScript()) &&
//                    					   StringUtils.isBlank(value.getStaticValue()) &&
//                    					   StringUtils.isBlank(value.getFetchedValue()) &&
//                    					   (value.getAmAttribute() == null || StringUtils.isBlank(value.getAmAttribute().getId()))) {
//                    						response.addFieldMapping("method", method.getMethod().toString());
//                        					response.addFieldMapping("metaName", meta.getName());
//                        					response.addFieldMapping("metaValueName", value.getName());
//                    						throw new BasicDataServiceException(ResponseCode.PATTERN_METHOD_META_VALUE_MISSING);
//                    					}
//                    				}
//                    			}
//                    		}
//            			}
//            		}
//
//            		if(methodSet.contains(getKey(method))) {
//            			response.addFieldMapping("method", method.getMethod().toString());
//            			throw new BasicDataServiceException(ResponseCode.METHOD_WITH_PARAMS_ALREADY_DEFINED);
//            		}
//            		methodSet.add(getKey(method));
//            	}
//            }
//
//            if(StringUtils.isNotBlank(pattern.getRedirectTo())) {
//            	if(!isValidRedirectURL(pattern.getRedirectTo())) {
//            		throw new BasicDataServiceException(ResponseCode.INVALID_PATTERN_REDIRECT_URL);
//            	}
//            	pattern.setRedirectToGroovyScript(null);
//            } else if(StringUtils.isNotBlank(pattern.getRedirectToGroovyScript())) {
//            	final String script  = pattern.getRedirectToGroovyScript();
//            	boolean validScript = false;
//            	if(scriptRunner.scriptExists(script)) {
//            		try {
//            			if((scriptRunner.instantiateClass(null, script) instanceof AbstractRedirectURLGroovyProcessor)) {
//            				validScript = true;
//            			}
//            		} catch(Throwable e) {
//            			log.warn(String.format("Can't instaniate script %s", script), e);
//            		}
//            	}
//
//            	if(!validScript) {
//            		response.addFieldMapping("className", AbstractRedirectURLGroovyProcessor.class.getCanonicalName());
//            		throw new BasicDataServiceException(ResponseCode.INVALID_ERROR_REDIRECT_URL_GROOVY_SCRIPT);
//            	}
//            }
//
//            final URIPatternEntity entity = uriPatternDozerConverter.convertToEntity(pattern,true);
//            contentProviderService.saveURIPattern(entity);
//            response.setResponseValue(entity.getId());
//
//        } catch(BasicDataServiceException e) {
//            log.warn(e.getMessage(), e);
//            response.setErrorTokenList(e.getErrorTokenList());
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//            response.setStacktraceText(ExceptionUtils.getFullStackTrace(e));
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//            response.setStacktraceText(ExceptionUtils.getFullStackTrace(e));
//        }
//        return response;
    }
    


    @Override
    public Response deleteProviderPattern(@WebParam(name = "providerId", targetNamespace = "") String providerId) {
        return this.manageGrudApiRequest(ContentProviderAPI.DeleteProviderPattern, providerId);
//        final Response response = new Response(ResponseStatus.SUCCESS);
//        try {
//            if (StringUtils.isBlank(providerId))
//                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//
//            contentProviderService.deleteProviderPattern(providerId);
//
//        } catch(BasicDataServiceException e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
    }

    @Override
    public List<URIPatternMetaType> getAllMetaType() {
        return getValueList(ContentProviderAPI.GetAllMetaType, new BaseServiceRequest(), URIPatternMetaTypeListResponse.class);
//        return uriPatternMetaTypeDozerConverter.convertToDTOList(contentProviderService.getAllMetaType(), false);
    }

	@Override
	public Response createDefaultURIPatterns(String providerId) {
        return this.manageGrudApiRequest(ContentProviderAPI.CreateDefaultURIPatterns, providerId);

//		 final Response response = new Response(ResponseStatus.SUCCESS);
//		 try {
//			 if (StringUtils.isBlank(providerId)) {
//				 throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
//			 }
//			 contentProviderService.createDefaultURIPatterns(providerId);
//		 } catch(BasicDataServiceException e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorCode(e.getCode());
//        } catch(Throwable e) {
//            log.error(e.getMessage(), e);
//            response.setStatus(ResponseStatus.FAILURE);
//            response.setErrorText(e.getMessage());
//        }
//        return response;
	}
}
