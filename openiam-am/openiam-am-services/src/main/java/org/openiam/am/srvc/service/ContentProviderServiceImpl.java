package org.openiam.am.srvc.service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthLevelAttributeDAO;
import org.openiam.am.srvc.dao.AuthLevelDao;
import org.openiam.am.srvc.dao.AuthLevelGroupingDao;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.AuthResourceAMAttributeDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.dao.URIPatternMetaTypeDao;
import org.openiam.am.srvc.domain.*;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingContentProviderXrefIdEntity;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingURIPatternXrefIdEntity;
import org.openiam.am.srvc.dozer.converter.*;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.groovy.AbstractRedirectURLGroovyProcessor;
import org.openiam.am.srvc.model.MetadataTemplateFieldJSONWrapper;
import org.openiam.am.srvc.model.URIPatternJSONWrapper;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.am.srvc.uriauth.exception.InvalidPatternException;
import org.openiam.am.srvc.uriauth.model.ContentProviderNode;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.SearchParam;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.FieldMappingDataServiceException;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.dto.MetadataFieldTemplateXref;
import org.openiam.idm.srvc.meta.service.MetadataElementTemplateService;
import org.openiam.idm.srvc.meta.service.MetadataTemplateTypeFieldEntityDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.ui.theme.UIThemeDAO;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.script.ScriptIntegration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("contentProviderService")
public class ContentProviderServiceImpl implements  ContentProviderService, InitializingBean {
	private final Log log = LogFactory.getLog(this.getClass());
    private static final String resourceTypeId="CONTENT_PROVIDER";
    private static final String patternResourceTypeId="URL_PATTERN";
    private static final String patternMethodResourceTypeId = "URI_PATTERN_METHOD";

    @Autowired
    private MetadataElementTemplateService templateService;

    @Autowired
    private ContentProviderDozerConverter contentProviderDozerConverter;
    @Autowired
    private URIPatternMetaTypeDozerConverter uriPatternMetaTypeDozerConverter;
    @Autowired
    private AuthLevelAttributeDozerConverter authLevelAttributeDozerConverter;
    @Autowired
    private AuthLevelGroupingDozerConverter authLevelGroupingDozerConverter;
    @Autowired
    private AuthLevelDozerConverter authLevelDozerConverter;
    @Autowired
    private URIPatternDozerConverter uriPatternDozerConverter;

    @Autowired
    private AuthProviderService authProviderService;

    @Autowired
    private ContentProviderDao contentProviderDao;
    @Autowired
    private MetadataTypeDAO metadataTypeDAO;
    
    @Autowired
    private AuthProviderDao authProviderDAO;

    @Autowired
    private URIPatternDao uriPatternDao;
    @Autowired
    private URIPatternMetaTypeDao patternMetaTypeDAO;
    
    @Autowired
    private AuthResourceAMAttributeDao authResourceAMAttributeDao;

    @Autowired
    private ResourceDAO resourceDao;
    @Autowired
    private ResourceTypeDAO resourceTypeDAO;
    
    @Autowired
    private UIThemeDAO uiThemeDAO;
    
    @Autowired
    private ManagedSysDAO managedSysDAO;
    
    @Autowired
    private AuthLevelGroupingDao authLevelGroupingDAO;
    
    @Autowired
    private AuthLevelDao authLevelDAO;
    
    @Autowired
    private AuthLevelAttributeDAO authLevelAttributeDAO;
    
    @Autowired
    private MetadataTypeDAO typeDAO;
    
    @Autowired
    private LanguageDAO languageDAO;
    
    @Autowired
    @Qualifier("defaultPatternResource")
    private Resource defaultPatternResource;
    
    @Autowired
    @Qualifier("defaultTemplateFieldResource")
    private Resource defaultTemplateFieldResource;
    
    @Autowired
    @Qualifier("customJacksonMapper")
    private CustomJacksonMapper mapper;
    
    @Autowired
    private ResourceService resourceService;
    
    @Value("${org.openiam.user.template.id}")
    private String userTemplateId;
    
    @Value("${org.openiam.default.group.page.template}")
    private String defaultGroupPageTemplate;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    @Value("${org.openiam.pattern.meta.type.cookie}")
    private String cookieMetadataType;

    @Value("${org.openiam.auth.provider.type.sms.id}")
    private String smsAuthLevelId;

    @Value("${org.openiam.auth.provider.type.totp.id}")
    private String totpAuthLevelId;
    @Value("${org.openiam.uri.pattern.meta.type.form.post.pattern.rule.id}")
    private String formPostURIPatternRule;

    
    private MetadataTemplateFieldJSONWrapper fieldWrapper;
    private URIPatternJSONWrapper patternWrapper;
    
    @Autowired
    private MetadataTemplateTypeFieldEntityDAO templateTypeEntityDAO;

    @Override
    public List<AuthLevel> getAuthLevelList() {
    	return authLevelDozerConverter.convertToDTOList(authLevelDAO.findAll(), false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuthLevelGrouping> getAuthLevelGroupingList(){
      return authLevelGroupingDozerConverter.convertToDTOList(authLevelGroupingDAO.findAll(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public ContentProvider getContentProvider(String providerId) {
        final ContentProviderEntity entity = contentProviderDao.findById(providerId);
        return (entity != null) ? contentProviderDozerConverter.convertToDTO(entity, true) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfContentProviders(ContentProviderSearchBean cpsb) {
        return contentProviderDao.count(cpsb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentProvider> findBeans(ContentProviderSearchBean cpsb, Integer from, Integer size) {
        List<ContentProviderEntity> contentProviderEntities =  contentProviderDao.getByExample(cpsb, from, size);
        return contentProviderEntities != null ? contentProviderDozerConverter.convertToDTOList(contentProviderEntities, cpsb.isDeepCopy()) : null;
    }

    @Override
    public List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL) {
        return  contentProviderDao.getProviderByDomainPattern(domainPattern, isSSL);
    }

    @Override
    @Transactional
    public String saveContentProvider(final ContentProvider provider) throws BasicDataServiceException{
        validate(provider);
        final ContentProviderEntity contentProvider = contentProviderDozerConverter.convertToEntity(provider,true);

        Map<String, AuthLevelGroupingEntity> levelGroupingMap = authLevelGroupingDAO.findAll().stream().collect(Collectors.toMap(AuthLevelGroupingEntity::getId, c -> c));

    	UIThemeEntity theme = null;
        //final ManagedSysEntity managedSys = managedSysDAO.findById(provider.getManagedSystem().getId());        
        if(contentProvider.getUiTheme() != null) {
        	theme = uiThemeDAO.findById(contentProvider.getUiTheme().getId());
        }
        contentProvider.setUiTheme(theme);
          
        if(contentProvider.getAuthProvider() != null && StringUtils.isNotBlank(contentProvider.getAuthProvider().getId())) {
            contentProvider.setAuthProvider(authProviderDAO.findById(contentProvider.getAuthProvider().getId()));
        } else {
            contentProvider.setAuthProvider(null);
        }
        
        if(StringUtils.isBlank(provider.getLoginURL())) {
            contentProvider.setLoginURL("/idp/login.html");
        }
        
        if(CollectionUtils.isNotEmpty(provider.getServerSet())) {
        	for(final ContentProviderServerEntity server : contentProvider.getServerSet()) {
        		server.setContentProvider(contentProvider);
        	}
        }
        
        final String cpURL = contentProvider.getResource().getURL();
        
        if(StringUtils.isBlank(provider.getPostbackURLParamName())) {
            contentProvider.setPostbackURLParamName("postbackURL");
        }
        
        if(StringUtils.isBlank(provider.getId())) {
            final ResourceTypeEntity resourceType = resourceTypeDAO.findById(resourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for provider. Resource type is not found");
            }

            final ResourceEntity resource = new ResourceEntity();
            resource.setName(resourceTypeId+"_"+provider.getName() + "_" + System.currentTimeMillis());
            resource.setResourceType(resourceType);
            resource.setId(null);
            resource.setIsPublic(false);
            resource.setCoorelatedName(provider.getName());
            resource.setURL(cpURL);
            resourceDao.save(resource);

            contentProvider.setUnavailableResource(generateUnavailableResource(contentProvider, cpURL));
            contentProvider.setResource(resource);
            
            final Set<AuthLevelGroupingContentProviderXrefEntity> incomingXrefs = contentProvider.getGroupingXrefs();
            contentProvider.setGroupingXrefs(null);
            contentProviderDao.save(contentProvider);
            if(CollectionUtils.isNotEmpty(incomingXrefs)) {
            	incomingXrefs.forEach(xref -> {
            		final AuthLevelGroupingEntity grouping = levelGroupingMap.get(xref.getId().getGroupingId());// authLevelGroupingDAO.findById(xref.getId().getGroupingId());
            		xref.setContentProvider(contentProvider);
            		xref.setGrouping(grouping);
            		xref.setId(new AuthLevelGroupingContentProviderXrefIdEntity(grouping.getId(), contentProvider.getId()));
            	});
            }
            contentProvider.setGroupingXrefs(incomingXrefs);
            contentProviderDao.merge(contentProvider);
        } else{
            // update provider
            final ContentProviderEntity dbEntity = contentProviderDao.findById(provider.getId());
        	if(dbEntity != null) {
                contentProvider.setResource(dbEntity.getResource());
                contentProvider.getResource().setURL(cpURL);
                contentProvider.getResource().setCoorelatedName(provider.getName());
                contentProvider.setPatternSet(dbEntity.getPatternSet());

                contentProvider.setUnavailableResource(dbEntity.getUnavailableResource());
        		if(contentProvider.getUnavailableResource() == null) {
                    contentProvider.setUnavailableResource(generateUnavailableResource(contentProvider, cpURL));
        		} else {
                    contentProvider.getUnavailableResource().setURL(cpURL);
                    contentProvider.getUnavailableResource().setCoorelatedName(String.format("%s - Unavailable Resource", provider.getName()));
        		}
        		
        		if(CollectionUtils.isNotEmpty(contentProvider.getGroupingXrefs())) {

                    contentProvider.getGroupingXrefs().forEach(xref -> {
        				xref.setContentProvider(contentProvider);
        				final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
        				xref.setGrouping(grouping);
        				xref.setId(new AuthLevelGroupingContentProviderXrefIdEntity(grouping.getId(), provider.getId()));
        			});
        		}

        		contentProviderDao.merge(contentProvider);
        	}
        }
        return contentProvider.getId();
    }
    
    private ResourceEntity generateUnavailableResource(final ContentProviderEntity provider, final String cpURL) {
    	final ResourceTypeEntity resourceType = resourceTypeDAO.findById(resourceTypeId);
    	final ResourceEntity resource = new ResourceEntity();
        resource.setName(resourceTypeId+"_"+provider.getName() + "_" + System.currentTimeMillis());
        resource.setResourceType(resourceType);
        resource.setId(null);
        resource.setIsPublic(false);
        resource.setCoorelatedName(String.format("%s - Unavailable Resource", provider.getName()));
        resource.setURL(cpURL);
        resourceDao.save(resource);
        return resource;
    }
    
    @Override
    @Transactional
    public void deleteContentProvider(String providerId) throws BasicDataServiceException {
        if (StringUtils.isBlank(providerId))
            throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

        final ContentProviderEntity entity  = contentProviderDao.findById(providerId);
        if(entity!=null){
            contentProviderDao.delete(entity);
        }
    }

    @Override
    public int getNumOfUriPatterns(URIPatternSearchBean searchBean) {
        return uriPatternDao.count(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<URIPattern> getUriPatternsList(URIPatternSearchBean searchBean, int from, int size) {
        return uriPatternDozerConverter.convertToDTOList(uriPatternDao.getByExample(searchBean, from, size), (searchBean != null) ? searchBean.isDeepCopy() : false);
    }

    @Override
    @Transactional(readOnly = true)
    public URIPattern getURIPattern(String patternId) {
        return uriPatternDozerConverter.convertToDTO(uriPatternDao.findById(patternId), true);
    }
    
    private void populateMetaValue(final AbstractMetaValueEntity value) {
    	if(StringUtils.isNotBlank(value.getFetchedValue())) {
    		value.setStaticValue(null);
			value.setAmAttribute(null);
			value.setGroovyScript(null);
			value.setEmptyValue(false);
    	} else if(value.isEmptyValue()) {
			value.setStaticValue(null);
			value.setAmAttribute(null);
			value.setGroovyScript(null);
			value.setFetchedValue(null);
		} else if(value.getAmAttribute() != null && StringUtils.isNotBlank(value.getAmAttribute().getId())) {
			value.setStaticValue(null);
			value.setGroovyScript(null);
			value.setEmptyValue(false);
			value.setFetchedValue(null);
		} else if(StringUtils.isNotBlank(value.getStaticValue())) {
			value.setAmAttribute(null);
			value.setGroovyScript(null);
			value.setEmptyValue(false);
			value.setFetchedValue(null);
		} else if(StringUtils.isNotBlank(value.getGroovyScript())) {
			value.setAmAttribute(null);
			value.setStaticValue(null);
			value.setEmptyValue(false);
			value.setFetchedValue(null);
		}

		/* set am attribute entity, if any */
		if(value.getAmAttribute() != null && StringUtils.isNotBlank(value.getAmAttribute().getId())) {
			final AuthResourceAMAttributeEntity attribute = authResourceAMAttributeDao.findById(value.getAmAttribute().getId());
			value.setAmAttribute(attribute);
		}
    }
    
    private String getCoorelatedName(final URIPatternMethodEntity method) {
    	return String.format("%s - %s - %s", method.getPattern().getContentProvider().getName(),  method.getPattern().getPattern(), method.getMethod());
    }

    @Override
    @Transactional
    public String saveURIPattern(URIPattern uriPattern) throws BasicDataServiceException{
        if (uriPattern==null ) {
            throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }
        if (StringUtils.isBlank(uriPattern.getPattern())) {
            throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_URI_PATTERN_NOT_SET);
        }
        if (StringUtils.isBlank(uriPattern.getContentProviderId())) {
            throw new  BasicDataServiceException(ResponseCode.CONTENT_PROVIDER_NOT_SET);
        }

        final List<URIPatternEntity> entityList =this.getURIPatternsForContentProviderMatchingPattern(uriPattern.getContentProviderId(), uriPattern.getPattern());
        if(CollectionUtils.isNotEmpty(entityList)) {
            if(StringUtils.isBlank(uriPattern.getId())) {
                throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_EXISTS);
            } else {
                for(final URIPatternEntity test : entityList) {
                    if(!StringUtils.equals(test.getId(), uriPattern.getId())) {
                        throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_EXISTS);
                    }
                }
            }
        }

        if(uriPattern.getMatchMode() == null) {
            throw new BasicDataServiceException(ResponseCode.PATTERN_MATCH_MODE_REQUIRED);
        }

        if(PatternMatchMode.ANY_PARAMS.equals(uriPattern.getMatchMode()) ||
           PatternMatchMode.NO_PARAMS.equals(uriPattern.getMatchMode()) ||
           PatternMatchMode.IGNORE.equals(uriPattern.getMatchMode())) {
            uriPattern.setParams(null);
        } else {
            if(CollectionUtils.isEmpty(uriPattern.getParams())) {
                throw new BasicDataServiceException(ResponseCode.PATTERN_PARAMS_REQUIRED);
            }
        }

        if(uriPattern.isShowOnApplicationPage()) {
            if(StringUtils.isBlank(uriPattern.getUrl())) {
                throw new BasicDataServiceException(ResponseCode.APPLICATION_URL_REQUIRED);
            }

            if(StringUtils.isBlank(uriPattern.getApplicationName())) {
                throw new BasicDataServiceException(ResponseCode.APPLICATION_NAME_REQUIRED);
            }
        } else {
            uriPattern.setUrl(null);
            uriPattern.setApplicationName(null);
        }

        if(uriPattern.isCacheable()) {
            if(uriPattern.getCacheTTL() == null) {
                throw new BasicDataServiceException(ResponseCode.INVALID_CACHE_TTL);
            } else if(uriPattern.getCacheTTL().intValue() <= 0) {
                throw new BasicDataServiceException(ResponseCode.INVALID_CACHE_TTL);
            }
        } else {
            uriPattern.setCacheTTL(null);
        }

        // validate pattern
        try{
            ContentProviderNode.validate(uriPattern.getPattern());
        } catch(InvalidPatternException e){
            throw new  BasicDataServiceException(ResponseCode.URI_PATTERN_INVALID);
        }

        if(CollectionUtils.isNotEmpty(uriPattern.getErrorMappings())) {
            for(final URIPatternErrorMapping mapping : uriPattern.getErrorMappings()) {
                if(!isValidRedirectURL(mapping.getRedirectURL())) {
                    FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.INVALID_ERROR_REDIRECT_URL);
                    ex.addFieldMapping("errorCode", Integer.valueOf(mapping.getErrorCode()).toString());
                    ex.addFieldMapping("redirectURL", mapping.getRedirectURL());
                    throw ex;
                }
            }
        }

        if(CollectionUtils.isNotEmpty(uriPattern.getGroupingXrefs())) {
            if(uriPattern.getGroupingXrefs().stream().map(e -> e.getId()).filter(e -> e.getGroupingId().equals(smsAuthLevelId)
                    || e.getGroupingId().equals(totpAuthLevelId)).count() == 2) {
                throw new BasicDataServiceException(ResponseCode.SMS_AND_TOTP_NOT_ALLOWED_SIMULTANEOUSLY);
            }
        }

        if(CollectionUtils.isNotEmpty(uriPattern.getSubstitutions())) {
            for(final URIPatternSubstitution substitution : uriPattern.getSubstitutions()) {
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

        if(CollectionUtils.isNotEmpty(uriPattern.getServers())) {
            for(final URIPatternServer server : uriPattern.getServers()) {
                if(StringUtils.isBlank(server.getServerURL())) {
                    throw new BasicDataServiceException(ResponseCode.SERVER_URL_NOT_SET);
                }
            }
        }

        if(CollectionUtils.isNotEmpty(uriPattern.getMetaEntitySet())) {
            for(final URIPatternMeta meta : uriPattern.getMetaEntitySet()) {
                if(StringUtils.isBlank(meta.getName())) {
                    throw new BasicDataServiceException(ResponseCode.URI_PATTERN_META_NAME_NOT_SET);
                }

                if(meta.getMetaType()==null || StringUtils.isBlank(meta.getMetaType().getId())) {
                    FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.URI_PATTERN_META_TYPE_NOT_SET);
                    ex.addFieldMapping("metaName", meta.getName());
                    throw ex;
                }

                if(StringUtils.equals(meta.getMetaType().getId(), formPostURIPatternRule)) {
                    if(StringUtils.isEmpty(meta.getContentType())) {
                        FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.PATTERN_META_CONTENT_TYPE_MISSING);
                        ex.addFieldMapping("metaName", meta.getName());
                        throw ex;
                    }
                }

                /* cookies require a path */
                if(StringUtils.equals(meta.getMetaType().getId(), cookieMetadataType)) {
                    if(StringUtils.isBlank(meta.getCookiePath())) {
                        FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.COOKIE_PATH_REQUIRED);
                        ex.addFieldMapping("metaName", meta.getName());
                        throw ex;
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
                                FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.PATTERN_META_VALUE_MISSING);
                                ex.addFieldMapping("uriPatternMetaName", meta.getName());
                                ex.addFieldMapping("uriPatternMetaValueName", value.getName());
                                throw ex;
                            }
                        }
                    }
                }
            }
        }

        if(CollectionUtils.isNotEmpty(uriPattern.getParams())) {
            for(final URIPatternParameter param : uriPattern.getParams()) {
                if(StringUtils.isBlank(param.getName())) {
                    throw new BasicDataServiceException(ResponseCode.PATTERN_URI_PARAM_NAME_REQUIRED);
                }
            }
        }

        if(CollectionUtils.isNotEmpty(uriPattern.getMethods())) {
            final Set<String> methodSet = new HashSet<String>();
            for(final URIPatternMethod method : uriPattern.getMethods()) {
                if(method.getMatchMode() == null) {
                    FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.METHOD_MATCH_MODE_REQUIRED);
                    ex.addFieldMapping("method", method.getMethod().toString());
                    throw ex;
                }

                if(PatternMatchMode.ANY_PARAMS.equals(method.getMatchMode()) ||
                   PatternMatchMode.NO_PARAMS.equals(method.getMatchMode()) ||
                   PatternMatchMode.IGNORE.equals(method.getMatchMode())) {
                    method.setParams(null);
                } else {
                    if(CollectionUtils.isEmpty(method.getParams())) {
                        FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.METHOD_PARAMS_REQUIRED);
                        ex.addFieldMapping("method", method.getMethod().toString());
                        throw ex;
                    }
                }

                if(method.getMethod() == null) {
                    throw new BasicDataServiceException(ResponseCode.URI_PATTERN_METHOD_REQUIRED);
                }

                if(CollectionUtils.isNotEmpty(method.getParams())) {
                    for(final URIPatternMethodParameter param : method.getParams()) {
                        if(StringUtils.isBlank(param.getName())) {
                            FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.URI_PATTERN_PARAMETER_NAME_REQUIRED);
                            ex.addFieldMapping("method", method.getMethod().toString());
                            throw ex;
                        }
                    }
                }

                if(CollectionUtils.isNotEmpty(method.getMetaEntitySet())) {
                    for(final URIPatternMethodMeta meta : method.getMetaEntitySet()) {
                        if(StringUtils.isBlank(meta.getName())) {
                            FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.URI_PATTERN_PARAMTER_META_NAME_REQUIRED);
                            ex.addFieldMapping("method", method.getMethod().toString());
                            throw ex;
                        }

                        if(meta.getMetaType()==null || StringUtils.isBlank(meta.getMetaType().getId())) {
                            FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.URI_PATTERN_PARAMTER_META_TYPE_REQUIRED);
                            ex.addFieldMapping("method", method.getMethod().toString());
                            ex.addFieldMapping("metaName", meta.getName());
                            throw ex;
                        }

                        if(StringUtils.equals(meta.getMetaType().getId(), formPostURIPatternRule)) {
                            if(StringUtils.isEmpty(meta.getContentType())) {
                                FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.COOKIE_PATH_REQUIRED_ON_METHOD);
                                ex.addFieldMapping("method", method.getMethod().toString());
                                ex.addFieldMapping("metaName", meta.getName());
                                throw ex;
                            }
                        }

                        /* cookies require a path */
                        if(StringUtils.equals(meta.getMetaType().getId(), cookieMetadataType)) {
                            if(StringUtils.isBlank(meta.getCookiePath())) {
                                FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.COOKIE_PATH_REQUIRED);
                                ex.addFieldMapping("method", method.getMethod().toString());
                                ex.addFieldMapping("metaName", meta.getName());
                                throw ex;
                            }
                        }

                        if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
                            for(final URIPatternMethodMetaValue value : meta.getMetaValueSet()) {
                                if (StringUtils.isBlank(value.getName())) {
                                    FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.PATTERN_METHOD_META_VALUE_NAME_MISSING);
                                    ex.addFieldMapping("method", method.getMethod().toString());
                                    throw ex;
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
                                        FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.PATTERN_METHOD_META_VALUE_MISSING);
                                        ex.addFieldMapping("method", method.getMethod().toString());
                                        ex.addFieldMapping("metaName", meta.getName());
                                        ex.addFieldMapping("metaValueName", value.getName());
                                        throw ex;
                                    }
                                }
                            }
                        }
                    }
                }

                if(methodSet.contains(getKey(method))) {
                    FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.METHOD_WITH_PARAMS_ALREADY_DEFINED);
                    ex.addFieldMapping("method", method.getMethod().toString());
                    throw ex;
                }
                methodSet.add(getKey(method));
            }
        }

        if(StringUtils.isNotBlank(uriPattern.getRedirectTo())) {
            if(!isValidRedirectURL(uriPattern.getRedirectTo())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_PATTERN_REDIRECT_URL);
            }
            uriPattern.setRedirectToGroovyScript(null);
        } else if(StringUtils.isNotBlank(uriPattern.getRedirectToGroovyScript())) {
            final String script  = uriPattern.getRedirectToGroovyScript();
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
                FieldMappingDataServiceException ex = new FieldMappingDataServiceException(ResponseCode.INVALID_ERROR_REDIRECT_URL_GROOVY_SCRIPT);
                ex.addFieldMapping("className", AbstractRedirectURLGroovyProcessor.class.getCanonicalName());
                throw ex;
            }
        }

        final URIPatternEntity entity = uriPatternDozerConverter.convertToEntity(uriPattern,true);
        return saveURIPatternInternal(entity);
    }

    @Transactional
    private String saveURIPatternInternal(URIPatternEntity entity) {
        final UIThemeEntity theme = (entity.getUiTheme() != null) ? uiThemeDAO.findById(entity.getUiTheme().getId()) : null;
        final ContentProviderEntity contentProvider = contentProviderDao.findById(entity.getContentProvider().getId());
        entity.setContentProvider(contentProvider);
        entity.setUiTheme(theme);

        final String applicationURL = (entity.getResource() != null) ? entity.getResource().getURL() : null;

        final ResourceTypeEntity patternMethodResourceType = resourceTypeDAO.findById(patternMethodResourceTypeId);
        if(patternMethodResourceType==null){
            throw new NullPointerException("Cannot create resource for URI pattern. Resource type is not found");
        }

        if(entity.getAuthProvider() != null && StringUtils.isNotBlank(entity.getAuthProvider().getId())) {
            entity.setAuthProvider(authProviderDAO.findById(entity.getAuthProvider().getId()));
        } else {
            entity.setAuthProvider(null);
        }

        if(CollectionUtils.isNotEmpty(entity.getSubstitutions())) {
            for(final URIPatternSubstitutionEntity substitution : entity.getSubstitutions()) {
                substitution.setPattern(entity);
            }
        }

        if(CollectionUtils.isNotEmpty(entity.getXssRules())) {
            entity.getXssRules().forEach(rule -> {
                rule.setPattern(entity);
            });
        }

        if(CollectionUtils.isNotEmpty(entity.getServers())) {
            for(final URIPatternServerEntity server : entity.getServers()) {
                server.setPattern(entity);
            }
        }

        if(CollectionUtils.isNotEmpty(entity.getMethods())) {
            for(final URIPatternMethodEntity patternMethod : entity.getMethods()) {
                patternMethod.setPattern(entity);
                if(CollectionUtils.isNotEmpty(patternMethod.getParams())) {
                    for(final URIPatternMethodParameterEntity parameter : patternMethod.getParams()) {
                        parameter.setPatternMethod(patternMethod);
                    }
                }
                if(CollectionUtils.isNotEmpty(patternMethod.getMetaEntitySet())) {
                    for(final URIPatternMethodMetaEntity meta : patternMethod.getMetaEntitySet()) {
                        meta.setPatternMethod(patternMethod);
                        if(meta.getMetaType() != null && StringUtils.isNotBlank(meta.getMetaType().getId())) {
                            meta.setMetaType(patternMetaTypeDAO.findById(meta.getMetaType().getId()));
                        } else {
                            meta.setMetaType(null);
                        }
                        if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
                            for(final URIPatternMethodMetaValueEntity value : meta.getMetaValueSet()) {
                                value.setMetaEntity(meta);

        						/* satisfy data integrity */
                                populateMetaValue(value);
                            }
                        }
                    }
                }

                if(patternMethod.getId() == null) {
                    final ResourceEntity resource = new ResourceEntity();
                    resource.setName(System.currentTimeMillis() + "_" + entity.getPattern() + "_" + patternMethod.getMethod());
                    resource.setResourceType(patternMethodResourceType);
                    resource.setId(null);
                    resource.setIsPublic(false);
                    resource.setCoorelatedName(getCoorelatedName(patternMethod));
                    resourceDao.add(resource);
                    patternMethod.setResource(resource);
                }
            }
        }

        if(CollectionUtils.isNotEmpty(entity.getMetaEntitySet())) {
            for(final URIPatternMetaEntity meta : entity.getMetaEntitySet()) {
                meta.setPattern(entity);
                if(meta.getMetaType() != null && StringUtils.isNotBlank(meta.getMetaType().getId())) {
                    meta.setMetaType(patternMetaTypeDAO.findById(meta.getMetaType().getId()));
                } else {
                    meta.setMetaType(null);
                }
                if(CollectionUtils.isNotEmpty(meta.getMetaValueSet())) {
                    for(final URIPatternMetaValueEntity value : meta.getMetaValueSet()) {
                        value.setMetaEntity(meta);

						/* satisfy data integrity */
                        populateMetaValue(value);
                    }
                }
            }
        }

        if(CollectionUtils.isNotEmpty(entity.getParams())) {
            for(final URIPatternParameterEntity param : entity.getParams()) {
                param.setPattern(entity);
            }
        }

        if(CollectionUtils.isNotEmpty(entity.getErrorMappings())) {
            for(final URIPatternErrorMappingEntity errorMapping : entity.getErrorMappings()) {
                errorMapping.setPattern(entity);
            }
        }

        if(StringUtils.isBlank(entity.getId())) {

            ResourceTypeEntity resourceType = resourceTypeDAO.findById(patternResourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for URI pattern. Resource type is not found");
            }
            // we need to make corresponded resource as public if it is protected by oauth
            boolean isOAuthProtected = false;
            final Set<AuthLevelGroupingURIPatternXrefEntity> incomingXrefs = entity.getGroupingXrefs();
            if(CollectionUtils.isNotEmpty(incomingXrefs)) {
                for(final AuthLevelGroupingURIPatternXrefEntity xref : incomingXrefs) {
                    if("OAUTH".equals(xref.getId().getGroupingId())){
                        isOAuthProtected = true;
                        break;
                    }
                }
            }

            final ResourceEntity resource = new ResourceEntity();
            resource.setName(System.currentTimeMillis() + "_" + entity.getPattern());
            resource.setResourceType(resourceType);
            resource.setURL(applicationURL);
            resource.setId(null);
            resource.setIsPublic(isOAuthProtected);
            resource.setCoorelatedName(String.format("%s - %s", contentProvider.getName(), entity.getPattern()));
            resourceDao.add(resource);
            entity.setResource(resource);


            entity.setGroupingXrefs(null);
            uriPatternDao.save(entity);
            if(CollectionUtils.isNotEmpty(incomingXrefs)) {
                for(final AuthLevelGroupingURIPatternXrefEntity xref : incomingXrefs) {
                    final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
                    xref.setPattern(entity);
                    xref.setGrouping(grouping);
                    xref.setId(new AuthLevelGroupingURIPatternXrefIdEntity(grouping.getId(), entity.getId()));
                }
            }
            entity.setGroupingXrefs(incomingXrefs);
            uriPatternDao.merge(entity);
        } else{
            final URIPatternEntity dbEntity = uriPatternDao.findById(entity.getId());

            if(dbEntity != null) {
                entity.setResource(dbEntity.getResource());
                if(entity.getResource() != null) {
                    entity.getResource().setURL(applicationURL);
                    entity.getResource().setCoorelatedName(String.format("%s - %s", entity.getContentProvider().getName(), entity.getPattern()));
                }
                if(CollectionUtils.isEmpty(entity.getGroupingXrefs())) {
                    dbEntity.getGroupingXrefs().clear();
                    entity.setGroupingXrefs(dbEntity.getGroupingXrefs());
                } else {
                    for(final AuthLevelGroupingURIPatternXrefEntity xref : entity.getGroupingXrefs()) {
                        final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
                        xref.setGrouping(grouping);
                        xref.setPattern(entity);
                        xref.setId(new AuthLevelGroupingURIPatternXrefIdEntity(grouping.getId(), entity.getId()));
                    }
                }

                if(CollectionUtils.isEmpty(entity.getXssRules())) {
                    dbEntity.getXssRules().clear();
                    entity.setXssRules(dbEntity.getXssRules());
                }

                if(CollectionUtils.isEmpty(entity.getSubstitutions())) {
                    dbEntity.getSubstitutions().clear();
                    entity.setSubstitutions(dbEntity.getSubstitutions());
                }

                if(CollectionUtils.isEmpty(entity.getServers())) {
                    dbEntity.getServers().clear();
                    entity.setServers(dbEntity.getServers());
                }

                if(CollectionUtils.isEmpty(entity.getErrorMappings())) {
                    dbEntity.getErrorMappings().clear();
                    entity.setErrorMappings(dbEntity.getErrorMappings());
                }

                if(CollectionUtils.isEmpty(entity.getMetaEntitySet())) {
                    dbEntity.getMetaEntitySet().clear();
                    entity.setMetaEntitySet(dbEntity.getMetaEntitySet());
                } else {
                    for(final URIPatternMetaEntity meta : entity.getMetaEntitySet()) {
                        if(CollectionUtils.isEmpty(meta.getMetaValueSet())) {
                            if(meta.getId() != null) {
                                final URIPatternMetaEntity dbMeta = dbEntity.getMetaEntity(meta.getId());
                                if(dbMeta != null) {
                                    dbMeta.getMetaValueSet().clear();
                                    meta.setMetaValueSet(dbMeta.getMetaValueSet());
                                }
                            }
                        }
                    }
                }

                if(CollectionUtils.isEmpty(entity.getParams())) {
                    dbEntity.getParams().clear();
                    entity.setParams(dbEntity.getParams());
                }

                if(CollectionUtils.isEmpty(entity.getMethods())) {
                    dbEntity.getMethods().clear();
                    entity.setMethods(dbEntity.getMethods());
                } else {
                    for(final URIPatternMethodEntity patternMethod : entity.getMethods()) {
                        final URIPatternMethodEntity dbMethod = dbEntity.getMethod(patternMethod.getId());
                        if(dbMethod != null) {
                            patternMethod.setResource(dbMethod.getResource());
                            patternMethod.getResource().setCoorelatedName(getCoorelatedName(patternMethod));
                            //set the PK, since the UI could have added/remved the same method, in which case the PK would have been lost
                            //patternMethod.setId(dbMethod.getId());

                            if(CollectionUtils.isEmpty(patternMethod.getParams())) {
                                dbMethod.getParams().clear();
                                patternMethod.setParams(dbMethod.getParams());
                            }
                            if(CollectionUtils.isEmpty(patternMethod.getMetaEntitySet())) {
                                dbMethod.getMetaEntitySet().clear();
                                patternMethod.setMetaEntitySet(dbMethod.getMetaEntitySet());
                            } else {
                                for(final URIPatternMethodMetaEntity meta : patternMethod.getMetaEntitySet()) {
                                    if(meta.getId() != null) {
                                        final URIPatternMethodMetaEntity dbMeta = dbMethod.getMetaEntity(meta.getId());
                                        if(dbMeta != null) {
                                            if(CollectionUtils.isEmpty(meta.getMetaValueSet())) {
                                                dbMeta.getMetaValueSet().clear();
                                                meta.setMetaValueSet(dbMeta.getMetaValueSet());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //find patterns that no longer exist, and remove the resource, as it no longer serves a purpose
                	/*
                	if(CollectionUtils.isNotEmpty(dbEntity.getMethods())) {
                		for(final URIPatternMethodEntity dbMethod : dbEntity.getMethods()) {
                			boolean contains = false;
                			for(final URIPatternMethodEntity patternMethod : pattern.getMethods()) {
                				contains = StringUtils.equals(dbMethod.getId(), patternMethod.getId());
                				if(contains) {
                					break;
                				}
                			}

                			if(!contains) {
                				resourceService.deleteResource(dbMethod.getResource().getId());
                			}
                		}
                	}
                	*/
                }

                entity.setPageTemplates(dbEntity.getPageTemplates());
                //pattern.setMetaEntitySet(dbEntity.getMetaEntitySet());
                uriPatternDao.merge(entity);
            }
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public void deleteProviderPattern(String patternId) throws BasicDataServiceException {
        if (StringUtils.isBlank(patternId))
            throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

        URIPatternEntity entity  = uriPatternDao.findById(patternId);
        if(entity != null) {
            uriPatternDao.delete(entity);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<URIPatternMetaType> getAllMetaType() {
        return uriPatternMetaTypeDozerConverter.convertToDTOList(patternMetaTypeDAO.findAll(), false);
    }

    /*
    @Transactional
    private void syncURIPatternMetaValue(URIPatternMetaEntity metaData, Set<URIPatternMetaValueEntity> newValues){
        if(newValues==null || newValues.isEmpty())
            return;
        for(URIPatternMetaValueEntity value : newValues){
           if(AttributeOperationEnum.DELETE==value.getOperation()){
               deleteMetaValue(value.getId());
           } else {
               value.setMetaEntity(metaData);
               
               saveMetaValue(value);
           }
        }
    }
    */

	@Override
	@Transactional
	public List<URIPatternEntity> getURIPatternsForContentProviderMatchingPattern(final String contentProviderId, final String pattern) {
		return uriPatternDao.getURIPatternsForContentProviderMatchingPattern(contentProviderId, pattern);
	}

	@Override
	@Transactional
	public AuthLevelGrouping getAuthLevelGrouping(String id) {
		return authLevelGroupingDozerConverter.convertToDTO(authLevelGroupingDAO.findById(id), true);
	}
	

	@Override
	@Transactional
	public void deleteAuthLevelGrouping(String id) throws BasicDataServiceException {
        if (StringUtils.isBlank(id))
            throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

		final AuthLevelGroupingEntity entity = authLevelGroupingDAO.findById(id);
		if(entity != null) {
			authLevelGroupingDAO.delete(entity);
		}
	}

	@Override
	@Transactional
	public String saveAuthLevelGrouping(AuthLevelGrouping grouping) throws BasicDataServiceException{
        final AuthLevelGroupingEntity entity = authLevelGroupingDozerConverter.convertToEntity(grouping, true);
        this.validateSaveAuthLevelGrouping(entity);

		if(StringUtils.isBlank(entity.getId())) {
			entity.setAuthLevel(authLevelDAO.findById(entity.getAuthLevel().getId()));
			entity.setAttributes(null);
			entity.setContentProviderXrefs(null);
			entity.setPatternXrefs(null);
			authLevelGroupingDAO.save(entity);
		} else {
			final AuthLevelGroupingEntity dbEntity = authLevelGroupingDAO.findById(entity.getId());
			if(dbEntity != null) {
				dbEntity.setName(entity.getName());
				authLevelGroupingDAO.update(dbEntity);
			}
		}
        return entity.getId();
	}

	@Override
	@Transactional
	public void validateDeleteAuthLevelGrouping(String id)
			throws BasicDataServiceException {
		final AuthLevelGroupingEntity entity = authLevelGroupingDAO.findById(id);
		if(entity != null) {
			if(CollectionUtils.isNotEmpty(entity.getContentProviderXrefs())) {
				throw new BasicDataServiceException(ResponseCode.AUTH_LEVEL_GROUPING_HAS_CONTENT_PROVIDERS);
			}
			
			if(CollectionUtils.isNotEmpty(entity.getPatternXrefs())) {
				throw new BasicDataServiceException(ResponseCode.AUTH_LEVEL_GROUPING_HAS_PATTERNS);
			}
		}
	}

	@Override
	@Transactional
	public void validateSaveAuthLevelGrouping(AuthLevelGroupingEntity entity) throws BasicDataServiceException {
		final AuthLevelGroupingEntity dbEntity = authLevelGroupingDAO.findByName(entity.getName());
		if(dbEntity != null) {
			if(StringUtils.isBlank(entity.getId()) || !(dbEntity.getId().equals(entity.getId()))) {
				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
			}
		}
	}

	@Override
	@Transactional
	public String saveAuthLevelAttibute(AuthLevelAttribute attribute) throws BasicDataServiceException {
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
        if(StringUtils.isBlank(entity.getId())) {
            entity.setId(null);
            if(entity.getGrouping() != null) {
                final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(entity.getGrouping().getId());
                entity.setGrouping(grouping);
            }

            entity.setType(type);
            authLevelAttributeDAO.save(entity);
        } else {
            final AuthLevelAttributeEntity dbEntity = authLevelAttributeDAO.findById(entity.getId());
            if(dbEntity != null) {
                dbEntity.setType(type);
                dbEntity.setValueAsByteArray(entity.getValueAsByteArray());
                dbEntity.setValueAsString(entity.getValueAsString());
                authLevelAttributeDAO.update(dbEntity);
            }
        }
        return entity.getId();
	}

	@Override
	@Transactional
	public void deleteAuthLevelAttribute(String id) throws BasicDataServiceException {
        if (StringUtils.isBlank(id))
            throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);

		final AuthLevelAttributeEntity entity = authLevelAttributeDAO.findById(id);
		if(entity != null) {
			authLevelAttributeDAO.delete(entity);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public AuthLevelAttribute getAuthLevelAttribute(String id) {
		return authLevelAttributeDozerConverter.convertToDTO(authLevelAttributeDAO.findById(id), true);
	}

	@Override
	@Transactional
	public Set<URIPatternEntity> createDefaultURIPatterns(String providerId) throws BasicDataServiceException {
		final Set<URIPatternEntity> retVal = new HashSet<URIPatternEntity>();
        if (StringUtils.isBlank(providerId)) {
             throw new  BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
        }

		if(patternWrapper == null) {
			throw new RuntimeException(String.format("Can't get json metadata.  Check that '%s' is in the classpath", defaultPatternResource));
		}
		
		if(CollectionUtils.isNotEmpty(patternWrapper.getPatterns())) {
			final ContentProviderEntity contentProvider = contentProviderDao.findById(providerId);
			if(contentProvider != null) {
				if(contentProvider.getPatternSet() == null) {
					contentProvider.setPatternSet(new HashSet<URIPatternEntity>());
				}
				
				final Set<String> patternsNotToAdd = new HashSet<>();
				
				/* update existing patterns, if they exist */
				for(final URIPatternEntity pattern : contentProvider.getPatternSet()) {
					for(final URIPatternEntity defaultPattern : patternWrapper.getPatterns()) {
						if(StringUtils.equals(pattern.getPattern(), defaultPattern.getPattern())) {
							pattern.setIsPublic(true);
							pattern.setIgnoreXSS(defaultPattern.isIgnoreXSS());
							if(CollectionUtils.isNotEmpty(defaultPattern.getGroupingXrefs())) {
								for(final AuthLevelGroupingURIPatternXrefEntity defaultGrouping : defaultPattern.getGroupingXrefs()) {
									if(!pattern.hasAuthGrouping(defaultGrouping.getId().getGroupingId())) {
										final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(defaultGrouping.getId().getGroupingId());
										pattern.addGroupingXref(new AuthLevelGroupingURIPatternXrefEntity(pattern, grouping));
									}
								}
							}
							if(CollectionUtils.isNotEmpty(defaultPattern.getXssRules())) {
								for(final URIParamXSSRuleEntity defaultRule : defaultPattern.getXssRules()) {
									if(pattern.hasXssRule(defaultRule.getParamName())) {
										final URIParamXSSRuleEntity dbRule = pattern.getXssRule(defaultRule.getParamName());
										dbRule.setIgnoreXSS(defaultRule.isIgnoreXSS());
									} else {
										final URIParamXSSRuleEntity rule = new URIParamXSSRuleEntity();
										rule.setIgnoreXSS(defaultRule.isIgnoreXSS());
										rule.setParamName(defaultRule.getParamName());
										rule.setPattern(pattern);
										pattern.addXssRule(rule);
									}
								}
							} else {
								if(pattern.getXssRules() == null) {
									pattern.setXssRules(new HashSet<URIParamXSSRuleEntity>());
								}
								pattern.getXssRules().clear();
							}
							//saveURIPattern(pattern);
							uriPatternDao.update(pattern);
							retVal.add(pattern);
						}
					}
					patternsNotToAdd.add(pattern.getPattern());
				}
				
				/* add new patterns */
				for(final URIPatternEntity defaultPattern : patternWrapper.getPatterns()) {
					if(!patternsNotToAdd.contains(defaultPattern.getPattern())) {
						final URIPatternEntity pattern = new URIPatternEntity();
						pattern.setContentProvider(contentProvider);
						pattern.setIsPublic(defaultPattern.getIsPublic());
						pattern.setPattern(defaultPattern.getPattern());
						pattern.setIgnoreXSS(defaultPattern.isIgnoreXSS());
						pattern.setMatchMode(PatternMatchMode.IGNORE);
						if(CollectionUtils.isNotEmpty(defaultPattern.getXssRules())) {
							defaultPattern.getXssRules().forEach(defaultRule -> {
								final URIParamXSSRuleEntity rule = new URIParamXSSRuleEntity();
								rule.setIgnoreXSS(defaultRule.isIgnoreXSS());
								rule.setParamName(defaultRule.getParamName());
								rule.setPattern(pattern);
								pattern.addXssRule(rule);
							});
						}
						pattern.setCacheable(defaultPattern.isCacheable());
						if(pattern.isCacheable()) {
							pattern.setCacheTTL(defaultPattern.getCacheTTL());
						} else {
							pattern.setCacheTTL(null);
						}
						if(pattern.getXssRules() != null) {
							pattern.getXssRules().forEach(e -> { e.setPattern(pattern); });
						}
						if(CollectionUtils.isNotEmpty(defaultPattern.getGroupingXrefs())) {
							final Set<AuthLevelGroupingURIPatternXrefEntity> groupingXrefs = new HashSet<>();
							for(final AuthLevelGroupingURIPatternXrefEntity defaultGrouping : defaultPattern.getGroupingXrefs()) {
								final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(defaultGrouping.getId().getGroupingId());
								groupingXrefs.add(new AuthLevelGroupingURIPatternXrefEntity(pattern, grouping));
							}
							pattern.setGroupingXrefs(groupingXrefs);
						}
                        saveURIPatternInternal(pattern);
						retVal.add(pattern);
					}
				}
			}
		}
		return retVal;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
        InputStream stream = defaultPatternResource.getInputStream();
		patternWrapper = mapper.readValue(stream, URIPatternJSONWrapper.class);
		
		stream = defaultTemplateFieldResource.getInputStream();
		fieldWrapper = mapper.readValue(stream, MetadataTemplateFieldJSONWrapper.class);
	}
	
	private MetadataElementPageTemplateEntity getTemplate(final ContentProvider provider) {
		MetadataElementPageTemplateEntity template = new MetadataElementPageTemplateEntity();
		template.setPublic(true);
		template.setTemplateType(new MetadataTemplateTypeEntity());
		template.getTemplateType().setId(userTemplateId);
		template.setName(String.format("Default Template for %s", provider.getName()));
		return template;
	}

	@Override
	@Transactional
	public String setupApplication(final ContentProvider provider) throws BasicDataServiceException{
		String providerId = saveContentProvider(provider);
		Set<URIPatternEntity> patternSet = createDefaultURIPatterns(providerId);
		
		final Set<URIPatternEntity> userProfilePatterns = patternSet.stream().filter(e -> 
			StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/selfRegistration") ||
			StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/editProfile") ||
			StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/newUser") ||
			StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/editUser")
		).collect(Collectors.toSet());
		
		final Set<URIPatternEntity> groupTemplatePatterns = patternSet.stream().filter(e ->
			StringUtils.startsWithIgnoreCase(e.getPattern(), "/selfservice/editGroup") ||
			StringUtils.startsWithIgnoreCase(e.getPattern(), "/webconsole/editGroup")
		).collect(Collectors.toSet());
		
		MetadataElementPageTemplateEntity template = getTemplate(provider);
		templateService.save(template);
		
		/* crazy bug.  since hte templateService.save() method is basically a huge merge, it will use
		 * the same refernces as the template object.  Consequently, it *may* null out things that we set
		 * in this method, because hibernate may re-set objects that we modify here
		 * 
		 * Therefore, we make a deep copy of that object, so that no reference is the same, and just use that
		 */
		final String id = template.getId();
		template = getTemplate(provider);
		template.setId(id);
		
		for(final MetadataFieldTemplateXref field : fieldWrapper.getFields()) {
			final MetadataFieldTemplateXrefEntity xref = new MetadataFieldTemplateXrefEntity();
			xref.setDisplayOrder(field.getDisplayOrder());
			xref.setEditable(field.isEditable());
			xref.setRequired(field.isRequired());
			//xref.setTemplate(template);
			if(xref.getLanguageMap() == null) {
				xref.setLanguageMap(new HashMap<String, LanguageMappingEntity>());
			}
			field.getLanguageMap().forEach((languageId, text) -> {
				if(StringUtils.isNotBlank(languageId)) {
					final LanguageMappingEntity mapping = new LanguageMappingEntity();
					mapping.setLanguageId(languageId);
					mapping.setValue(text.getValue());
					//mapping.setReferenceId(referenceId);
					xref.getLanguageMap().put(languageId, mapping);
				}
			});
			xref.setField(new MetadataTemplateTypeFieldEntity());
			xref.getField().setId(field.getField().getId());
			xref.setTemplate(template);
			template.addField(xref);
		}
		
		template.setUriPatterns(userProfilePatterns);
		templateService.save(template);
		
		final MetadataElementPageTemplateEntity groupTemplate = templateService.findById(defaultGroupPageTemplate);
		if(groupTemplate != null) {
			groupTemplate.setUriPatterns(groupTemplatePatterns);
			templateService.save(template);
		}
        return providerId;
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
        searchBean.setNameToken(new SearchParam(provider.getName(), MatchType.EXACT));
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

        if(CollectionUtils.isNotEmpty(provider.getGroupingXrefs())) {
            if(provider.getGroupingXrefs().stream().map(e -> e.getId()).filter(e -> e.getGroupingId().equals(smsAuthLevelId)
                    || e.getGroupingId().equals(totpAuthLevelId)).count() == 2) {
                throw new BasicDataServiceException(ResponseCode.SMS_AND_TOTP_NOT_ALLOWED_SIMULTANEOUSLY);
            }
        }

        if(provider.getId()==null){
            // if provider is new, test for unique domain+ssl
            final List<ContentProviderEntity> result = this.getProviderByDomainPattern(provider.getDomainPattern(), provider.getIsSSL());
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

        final AuthProviderEntity authProviderType = authProviderService.getAuthProvider(provider.getAuthProviderId());
        if(authProviderType == null) {
            throw new  BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
        }

        final AuthProviderTypeEntity type = authProviderService.getAuthProviderTypeForProvider(provider.getAuthProviderId());
        if(type == null) {
            log.error(String.format("Type was null for auth provider '%s'", provider.getAuthProviderId()));
            throw new  BasicDataServiceException(ResponseCode.INTERNAL_ERROR);
        }

        if(!type.isLinkableToContentProvider()) {
            throw new  BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_LINKABLE);
        }
    }

    private boolean isValidRedirectURL(final String redirectURL) {
        return (redirectURL != null && (redirectURL.startsWith("/") || redirectURL.startsWith("http") || redirectURL.startsWith("https")));
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
}
