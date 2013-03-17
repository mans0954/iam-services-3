package org.openiam.am.srvc.ws;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderServerEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.domain.URIPatternMetaEntity;
import org.openiam.am.srvc.dozer.converter.*;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbeans.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbeans.converter.ContentProviderSearchBeanConverter;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.openiam.am.srvc.uriauth.model.URIPatternTree;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private AuthLevelDozerConverter authLevelDozerConverter;

    @Autowired
    private ContentProviderServerDoserConverter contentProviderServerDoserConverter;
    @Autowired
    private URIPatternDozerConverter uriPatternDozerConverter;
    @Autowired
    private URIPatternMetaDozerConverter uriPatternMetaDozerConverter;

    @Autowired
    private URIPatternMetaTypeDozerConverter uriPatternMetaTypeDozerConverter;

    @Override
    public List<AuthLevel> getAuthLevelList(){
         return authLevelDozerConverter.convertToDTOList(contentProviderService.getAuthLevelList(), false);
    }

    @Override
    public ContentProvider getContentProvider(String providerId) {
        return contentProviderDozerConverter.convertToDTO(contentProviderService.getContentProvider(providerId),
                                                                 true);
    }

    @Override
    public List<ContentProvider> findBeans(ContentProviderSearchBean searchBean,Integer from, Integer size) {
        List<ContentProviderEntity> result = contentProviderService.findBeans(contentProviderSearchBeanConverter.convert(searchBean), from, size);
        return contentProviderDozerConverter.convertToDTOList(result, searchBean.isDeepCopy());
    }

    @Override
    public Integer getNumOfContentProviders(ContentProviderSearchBean searchBean) {
        return contentProviderService.getNumOfContentProviders(contentProviderSearchBeanConverter.convert(searchBean));
    }

    @Override
    public Response saveContentProvider(ContentProvider provider) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (provider == null)
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if (provider.getName()==null || provider.getName().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NAME_NOT_SET);
            if (provider.getDomainPattern()==null || provider.getDomainPattern().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_DOMAIN_PATERN_NOT_SET);

            if (provider.getAuthLevel()==null || provider.getAuthLevel().getId().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET);

//            UNIQUE KEY `UNIQUE_CP_NAME` (`CONTENT_PROVIDER_NAME`),
//            UNIQUE KEY `UNIQUE_CP_PATTERN` (`DOMAIN_PATTERN`,`IS_SSL`,`CONTEXT_PATH`),

            final ContentProviderSearchBean searchBean = new ContentProviderSearchBean();
            searchBean.setProviderName(provider.getName());
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

            ContentProviderEntity entity = contentProviderService.saveContentProvider(contentProviderDozerConverter.convertToEntity(provider,true));
            response.setResponseValue(contentProviderDozerConverter.convertToDTO(entity, true));

        } catch(BasicDataServiceException e) {
            log.info(e);
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
            if (providerId==null || providerId.trim().isEmpty())
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
        ContentProviderServerEntity example = new ContentProviderServerEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        return contentProviderServerDoserConverter.convertToDTOList(contentProviderService.getProviderServers(example, from, size), false);
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
    public Response saveProviderServer(ContentProviderServer contentProviderServer) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (contentProviderServer == null)
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if (contentProviderServer.getServerURL()==null || contentProviderServer.getServerURL().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_SERVER_URL_NOT_SET);
            if (contentProviderServer.getContentProviderId()==null || contentProviderServer.getContentProviderId().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NOT_SET);

            ContentProviderServerEntity example = new ContentProviderServerEntity();
            ContentProviderEntity provider = new ContentProviderEntity();
            provider.setId(contentProviderServer.getContentProviderId());
            example.setContentProvider(provider);
            example.setServerURL(contentProviderServer.getServerURL());

            Integer count = contentProviderService.getNumOfProviderServers(example);
            if(count>0){
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_SERVER_EXISTS);
            }
            contentProviderService.saveProviderServer(contentProviderServerDoserConverter.convertToEntity(contentProviderServer, false));
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
            if (contentProviderServerId==null || contentProviderServerId.trim().isEmpty())
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
    public List<URIPattern> getUriPatternsForProvider(String providerId, Integer from, Integer size) {
        URIPatternEntity example = new URIPatternEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        return uriPatternDozerConverter.convertToDTOList(contentProviderService.getUriPatternsList(example, from, size), true);
    }

    @Override
    public Integer getNumOfUriPatternsForProvider(String providerId) {
        URIPatternEntity example = new URIPatternEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        return contentProviderService.getNumOfUriPatterns(example);
    }

    @Override
    public URIPattern getURIPattern(String patternId) {
        return uriPatternDozerConverter.convertToDTO(contentProviderService.getURIPattern(patternId), true);
    }

    @Override
    public Response saveURIPattern(@WebParam(name = "pattern", targetNamespace = "") URIPattern pattern) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (pattern==null )
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if (pattern.getPattern()==null || pattern.getPattern().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_URI_PATTERN_NOT_SET);
            if (pattern.getContentProviderId()==null || pattern.getContentProviderId().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NOT_SET);
            if (pattern.getAuthLevel()==null || pattern.getAuthLevel().getId().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_AUTH_LEVEL_NOT_SET);

            URIPatternEntity example = new URIPatternEntity();
            ContentProviderEntity cp = new ContentProviderEntity();
            cp.setId(pattern.getContentProviderId());
            example.setContentProvider(cp);
            example.setPattern(pattern.getPattern());

            final List<URIPatternEntity> entityList = 
            		contentProviderService.getUriPatternsList(example, Integer.valueOf(0), Integer.valueOf(Integer.MAX_VALUE));
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

            URIPatternEntity entity = contentProviderService.saveURIPattern(uriPatternDozerConverter.convertToEntity(pattern,true));
            response.setResponseValue(uriPatternDozerConverter.convertToDTO(entity, true));

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
            if (providerId==null || providerId.trim().isEmpty())
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
    public URIPatternMeta getURIPatternMeta(String metaId){
        return uriPatternMetaDozerConverter.convertToDTO(contentProviderService.getURIPatternMeta(metaId), true);
    }

    @Override
    public Response saveMetaDataForPattern(URIPatternMeta uriPatternMeta) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (uriPatternMeta==null)
                throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
            if(uriPatternMeta.getUriPatternId()==null
                   || uriPatternMeta.getUriPatternId().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_NOT_SET);

            if(uriPatternMeta.getName()==null
               || uriPatternMeta.getName().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_NAME_NOT_SET);

            if(uriPatternMeta.getMetaType()==null
               || uriPatternMeta.getMetaType().getId()==null
               || uriPatternMeta.getMetaType().getId().trim().isEmpty())
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_TYPE_NOT_SET);

//            // checjk if meta data already exists
//            URIPatternMetaEntity example = uriPatternMetaDozerConverter.convertToEntity(uriPatternMeta,true);
//            example.setId(null);
//
//            Integer count = contentProviderService.getNumOfMetaData(example);
//            if(count>0)
//                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_META_EXISTS);


            // check metadata values
            validateMetaDataValues(uriPatternMeta);

            URIPatternMetaEntity entity = contentProviderService.saveMetaDataForPattern(uriPatternMetaDozerConverter.convertToEntity(uriPatternMeta,true));
            response.setResponseValue(uriPatternMetaDozerConverter.convertToDTO(entity, true));

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

    private void validateMetaDataValues(URIPatternMeta uriPatternMeta) throws BasicDataServiceException{
        if(uriPatternMeta.getMetaValueSet()!=null && !uriPatternMeta.getMetaValueSet().isEmpty()){
            for (URIPatternMetaValue value: uriPatternMeta.getMetaValueSet()){
                if (value.getName() == null || value.getName().trim().isEmpty())
                    throw new BasicDataServiceException(ResponseCode.URL_PATTERN_META_VALUE_NAME_NOT_SET);
                if ((value.getAmAttribute() == null
                     || value.getAmAttribute().getAmAttributeId()==null
                     || value.getAmAttribute().getAmAttributeId().trim().isEmpty())
                    &&(value.getStaticValue() == null || value.getStaticValue().trim().isEmpty()))
                    throw new BasicDataServiceException(ResponseCode.URL_PATTERN_META_VALUE_MAP_NOT_SET);
            }
        }
    }

    @Override
    public Response deleteMetaDataForPattern(@WebParam(name = "metaId", targetNamespace = "") String metaId) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            if (metaId==null || metaId.trim().isEmpty())
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
}
