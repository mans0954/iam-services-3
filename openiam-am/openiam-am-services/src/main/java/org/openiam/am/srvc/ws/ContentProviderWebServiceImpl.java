package org.openiam.am.srvc.ws;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.AuthLevelAttributeEntity;
import org.openiam.am.srvc.domain.AuthLevelGroupingEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderServerEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.am.srvc.dozer.converter.*;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbeans.URIPatternSearchBean;
import org.openiam.am.srvc.searchbeans.converter.ContentProviderSearchBeanConverter;
import org.openiam.am.srvc.searchbeans.converter.URIPatternSearchBeanConverter;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.openiam.am.srvc.uriauth.model.URIPatternTree;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

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
    private URIPatternSearchBeanConverter uriPatternSearchBeanConverter;

    @Autowired
    private AuthLevelDozerConverter authLevelDozerConverter;

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
    public List<ContentProvider> findBeans(ContentProviderSearchBean searchBean,Integer from, Integer size) {
        return contentProviderService.findBeans(searchBean, from, size);
    }

    @Override
    public Integer getNumOfContentProviders(ContentProviderSearchBean searchBean) {
        return contentProviderService.getNumOfContentProviders(searchBean);
    }

    @Override
    public Response saveContentProvider(ContentProvider provider) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (provider == null) {
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            if (provider.getName()==null || StringUtils.isBlank(provider.getName())) {
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NAME_NOT_SET);
            }
            if (provider.getDomainPattern()==null || StringUtils.isBlank(provider.getDomainPattern())) {
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET);
            }
            if(StringUtils.isBlank(provider.getManagedSysId())) {
            	throw new  BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
            }
            
            if(CollectionUtils.isEmpty(provider.getGroupingXrefs())) {
            	throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET);
            }

//            UNIQUE KEY `UNIQUE_CP_NAME` (`CONTENT_PROVIDER_NAME`),
//            UNIQUE KEY `UNIQUE_CP_PATTERN` (`DOMAIN_PATTERN`,`IS_SSL`,`CONTEXT_PATH`),

            final ContentProviderSearchBean searchBean = new ContentProviderSearchBean();
            searchBean.setProviderName(provider.getName());
            searchBean.setDeepCopy(false);
            final List<ContentProvider> cpEntityWithNameList = findBeans(searchBean, new Integer(0), Integer.MAX_VALUE);
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
    public List<ContentProviderServer> getServersForProvider(String providerId, Integer from, Integer size) {
        final ContentProviderServerEntity example = new ContentProviderServerEntity();
        final ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);
        
        final List<ContentProviderServerEntity> entities = contentProviderService.getProviderServers(example, from, size);
        final List<ContentProviderServer> servers = contentProviderServerDoserConverter.convertToDTOList(entities, false);
        //log.info(String.format("Content Provider Server Entities: %s, servers: %s", entities, servers));
        return servers;
    }

    @Override
    public Integer getNumOfServersForProvider(String providerId) {
        ContentProviderServerEntity example = new ContentProviderServerEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        return contentProviderService.getNumOfProviderServers(example);
    }

    @Override
    public Response saveProviderServer(final ContentProviderServer contentProviderServer) {
    	log.info(String.format("Incoming server: %s", contentProviderServer));
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (contentProviderServer == null)
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if (StringUtils.isBlank(contentProviderServer.getServerURL()))
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_SERVER_URL_NOT_SET);
            if (StringUtils.isBlank(contentProviderServer.getContentProviderId()))
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NOT_SET);

            final ContentProviderServerEntity example = new ContentProviderServerEntity();
            final ContentProviderEntity provider = new ContentProviderEntity();
            provider.setId(contentProviderServer.getContentProviderId());
            example.setContentProvider(provider);
            example.setServerURL(contentProviderServer.getServerURL());

            final Integer count = contentProviderService.getNumOfProviderServers(example);
            if(count>0){
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_SERVER_EXISTS);
            }
            final ContentProviderServerEntity entity = contentProviderServerDoserConverter.convertToEntity(contentProviderServer, false);
            contentProviderService.saveProviderServer(entity);
            //log.info(String.format("Finalized Server: %s", entity));
            response.setResponseValue(entity.getId());
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
    public Response deleteProviderServer(String contentProviderServerId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.isBlank(contentProviderServerId))
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            contentProviderService.deleteProviderServer(contentProviderServerId);

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
        URIPatternEntity example = new URIPatternEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        final List<URIPatternEntity> entityList = contentProviderService.getUriPatternsList(example, from, size);
        final List<URIPattern> dtoList = uriPatternDozerConverter.convertToDTOList(entityList, true);
        return dtoList;
    }

    @Override
    @Deprecated
    public Integer getNumOfUriPatternsForProvider(String providerId) {
        URIPatternEntity example = new URIPatternEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        return contentProviderService.getNumOfUriPatterns(example);
    }

    @Override
    @Transactional(readOnly = true)
    public List<URIPattern> findUriPatterns(URIPatternSearchBean searchBean, Integer from, Integer size) {
        final List<URIPatternEntity> entityList = contentProviderService.getUriPatternsList(uriPatternSearchBeanConverter.convert(searchBean), from, size);
        return uriPatternDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
    }

    @Override
    public Integer getNumOfUriPatterns(URIPatternSearchBean searchBean) {
        return contentProviderService.getNumOfUriPatterns(uriPatternSearchBeanConverter.convert(searchBean));
    }

    @Override
    @Transactional(readOnly = true)
    public URIPattern getURIPattern(String patternId) {
        return uriPatternDozerConverter.convertToDTO(contentProviderService.getURIPattern(patternId), true);
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
            
            /*
            if(CollectionUtils.isEmpty(pattern.getGroupingXrefs())) {
            	throw new BasicDataServiceException(ResponseCode.URI_PATTERN_AUTH_LEVEL_NOT_SET);
            }
            */
            
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

            // validate pattern
            try{
                new URIPatternTree().addPattern(pattern);
            } catch(InvalidPatternException e){
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_INVALID);
            }

            final URIPatternEntity entity = uriPatternDozerConverter.convertToEntity(pattern,true);
            contentProviderService.saveURIPattern(entity);
            response.setResponseValue(entity.getId());

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
    @Transactional(readOnly = true)
    public List<URIPatternMeta> getMetaDataForPattern(String patternId, Integer from, Integer size) {
        URIPatternMetaEntity example = new URIPatternMetaEntity();
        URIPatternEntity pattern = new URIPatternEntity();
        pattern.setId(patternId);
        example.setPattern(pattern);

        return uriPatternMetaDozerConverter.convertToDTOList(contentProviderService.getMetaDataList(example, from, size), true);
    }

    @Override
    public Integer getNumOfMetaDataForPattern(String patternId) {
        URIPatternMetaEntity example = new URIPatternMetaEntity();
        URIPatternEntity pattern = new URIPatternEntity();
        pattern.setId(patternId);
        example.setPattern(pattern);

        return contentProviderService.getNumOfMetaData(example);
    }
    @Override
    @Transactional(readOnly = true)
    public URIPatternMeta getURIPatternMeta(String metaId){
        return uriPatternMetaDozerConverter.convertToDTO(contentProviderService.getURIPatternMeta(metaId), true);
    }

    @Override
    public Response saveMetaDataForPattern(final URIPatternMeta uriPatternMeta) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (uriPatternMeta==null) {
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            }
            
            if(StringUtils.isBlank(uriPatternMeta.getUriPatternId())) {
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_NOT_SET);
            }

            if(StringUtils.isBlank(uriPatternMeta.getName())) {
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_NAME_NOT_SET);
            }

            if(uriPatternMeta.getMetaType()==null || StringUtils.isBlank(uriPatternMeta.getMetaType().getId())) {
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_TYPE_NOT_SET);
            }
            
            if(CollectionUtils.isNotEmpty(uriPatternMeta.getMetaValueSet())) {
            	for(final URIPatternMetaValue value : uriPatternMeta.getMetaValueSet()) {
            		if (StringUtils.isBlank(value.getName())) {
            			 throw new  BasicDataServiceException(ResponseCode.META_NAME_MISSING);
                    }
            		
            		if(!value.isEmptyValue()) {
            			if(StringUtils.isBlank(value.getGroovyScript()) &&
            			   StringUtils.isBlank(value.getStaticValue()) &&
            			   (value.getAmAttribute() == null || StringUtils.isBlank(value.getAmAttribute().getId()))) {
            				throw new  BasicDataServiceException(ResponseCode.META_VALUE_MISSING);
            			}
            		}
            	}
            }

//            // checjk if meta data already exists
//            URIPatternMetaEntity example = uriPatternMetaDozerConverter.convertToEntity(uriPatternMeta,true);
//            example.setId(null);
//
//            Integer count = contentProviderService.getNumOfMetaData(example);
//            if(count>0)
//                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_EXISTS);


            // check metadata values
            //validateMetaDataValues(uriPatternMeta);

            final URIPatternMetaEntity entity = uriPatternMetaDozerConverter.convertToEntity(uriPatternMeta,true);
            contentProviderService.saveMetaDataForPattern(entity);
            response.setResponseValue(entity.getId());

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

    /*
    private void validateMetaDataValues(URIPatternMeta uriPatternMeta) throws BasicDataServiceException{
        if(CollectionUtils.isNotEmpty(uriPatternMeta.getMetaValueSet())) {
            for (URIPatternMetaValue value: uriPatternMeta.getMetaValueSet()){
                if (StringUtils.isBlank(value.getName())) {
                    throw new BasicDataServiceException(ResponseCode.URL_PATTERN_META_VALUE_NAME_NOT_SET);
                }
                if ((value.getAmAttribute() == null || StringUtils.isBlank(value.getAmAttribute().getId())) &&
                	(StringUtils.isBlank(value.getStaticValue())) &&
                	(StringUtils.isBlank(value.getGroovyScript()))) {
                    throw new BasicDataServiceException(ResponseCode.URL_PATTERN_META_VALUE_MAP_NOT_SET);
                }
            }
        }
    }
    */

    @Override
    public Response deleteMetaDataForPattern(@WebParam(name = "metaId", targetNamespace = "") String metaId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (StringUtils.isBlank(metaId))
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

            contentProviderService.deleteMetaDataForPattern(metaId);

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
