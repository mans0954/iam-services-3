package org.openiam.am.srvc.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.*;
import org.openiam.am.srvc.domain.*;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingContentProviderXrefIdEntity;
import org.openiam.am.srvc.domain.pk.AuthLevelGroupingURIPatternXrefIdEntity;
import org.openiam.am.srvc.model.URIPatternJSONWrapper;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceService;
import org.openiam.idm.srvc.res.service.ResourceServiceImpl;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.ui.theme.UIThemeDAO;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.idm.util.CustomJacksonMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.*;

@Service("contentProviderService")
public class ContentProviderServiceImpl implements  ContentProviderService, InitializingBean {
	private final Log log = LogFactory.getLog(this.getClass());
    private static final String resourceTypeId="CONTENT_PROVIDER";
    private static final String patternResourceTypeId="URL_PATTERN";
    private static final String patternMethodResourceTypeId = "URI_PATTERN_METHOD";
    
    @Autowired
    private ContentProviderDao contentProviderDao;
    @Autowired
    private ContentProviderServerDao contentProviderServerDao;
    
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
    @Qualifier("defaultPatternResource")
    private Resource defaultPatternResource;
    
    @Autowired
    @Qualifier("customJacksonMapper")
    private CustomJacksonMapper mapper;
    
    @Autowired
    private ResourceService resourceService;
    
    private URIPatternJSONWrapper patternWrapper;

    @Override
    public List<AuthLevelEntity> getAuthLevelList() {
    	return authLevelDAO.findAll();
    }
    
    @Override
    public List<AuthLevelGroupingEntity> getAuthLevelGroupingList(){
      return authLevelGroupingDAO.findAll();
    }

    @Override
    public ContentProviderEntity getContentProvider(String providerId) {
        return contentProviderDao.findById(providerId);
    }

    @Override
    public int getNumOfContentProviders(ContentProviderEntity example) {
        return contentProviderDao.count(example);
    }

    @Override
    @Transactional
    public List<ContentProviderEntity> findBeans(ContentProviderEntity example, Integer from, Integer size) {
        return contentProviderDao.getByExample(example, from, size);
    }

    @Override
    public List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL) {
        return  contentProviderDao.getProviderByDomainPattern(domainPattern, isSSL);
    }

    @Override
    @Transactional
    public void saveContentProvider(final ContentProviderEntity provider){
       
    	UIThemeEntity theme = null;
        //final ManagedSysEntity managedSys = managedSysDAO.findById(provider.getManagedSystem().getId());        
        if(provider.getUiTheme() != null) {
        	theme = uiThemeDAO.findById(provider.getUiTheme().getId());
        }
        provider.setUiTheme(theme);
        
        if(provider.getAuthProvider() != null && StringUtils.isNotBlank(provider.getAuthProvider().getId())) {
        	provider.setAuthProvider(authProviderDAO.findById(provider.getAuthProvider().getId()));
        } else {
        	provider.setAuthProvider(null);
        }
        
        if(CollectionUtils.isNotEmpty(provider.getServerSet())) {
        	for(final ContentProviderServerEntity server : provider.getServerSet()) {
        		server.setContentProvider(provider);
        	}
        }
        
        final String cpURL = provider.getResource().getURL();
        
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
            
            provider.setResource(resource);
            
            final Set<AuthLevelGroupingContentProviderXrefEntity> incomingXrefs = provider.getGroupingXrefs();
            provider.setGroupingXrefs(null);
            contentProviderDao.save(provider);
            if(CollectionUtils.isNotEmpty(incomingXrefs)) {
            	for(final AuthLevelGroupingContentProviderXrefEntity xref : incomingXrefs) {
            		final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
            		xref.setContentProvider(provider);
            		xref.setGrouping(grouping);
            		xref.setId(new AuthLevelGroupingContentProviderXrefIdEntity(grouping.getId(), provider.getId()));
            	}
            }
            provider.setGroupingXrefs(incomingXrefs);
            contentProviderDao.merge(provider);
        } else{
            // update provider
            final ContentProviderEntity dbEntity = contentProviderDao.findById(provider.getId());
        	if(dbEntity != null) {
        		provider.setResource(dbEntity.getResource());
        		provider.getResource().setURL(cpURL);
        		provider.getResource().setCoorelatedName(provider.getName());
        		provider.setPatternSet(dbEntity.getPatternSet());
        		
        		if(CollectionUtils.isNotEmpty(provider.getGroupingXrefs())) {
        			for(AuthLevelGroupingContentProviderXrefEntity xref : provider.getGroupingXrefs()) {
        				xref.setContentProvider(provider);
        				final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
        				xref.setGrouping(grouping);
        				xref.setId(new AuthLevelGroupingContentProviderXrefIdEntity(grouping.getId(), provider.getId()));
        			}
        		}

        		contentProviderDao.merge(provider);
        	}
        }
    }
    
    @Override
    @Transactional
    public void deleteContentProvider(String providerId) {
    	if(StringUtils.isNotBlank(providerId)) {
    		final ContentProviderEntity entity  = contentProviderDao.findById(providerId);
    		if(entity!=null){
    			contentProviderDao.delete(entity);
    		}
        }
    }

    @Override
    public int getNumOfUriPatterns(URIPatternEntity example) {
        return uriPatternDao.count(example);
    }

    @Override
    public List<URIPatternEntity> getUriPatternsList(URIPatternEntity example, Integer from, Integer size) {
        return uriPatternDao.getByExample(example, from, size);
    }

    @Override
    public URIPatternEntity getURIPattern(String patternId) {
        return uriPatternDao.findById(patternId);
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
    public void saveURIPattern(final URIPatternEntity pattern) {
        final UIThemeEntity theme = (pattern.getUiTheme() != null) ? uiThemeDAO.findById(pattern.getUiTheme().getId()) : null;
        final ContentProviderEntity contentProvider = contentProviderDao.findById(pattern.getContentProvider().getId());
        pattern.setContentProvider(contentProvider);
        pattern.setUiTheme(theme);
        
		final ResourceTypeEntity patternMethodResourceType = resourceTypeDAO.findById(patternMethodResourceTypeId);
        if(patternMethodResourceType==null){
            throw new NullPointerException("Cannot create resource for URI pattern. Resource type is not found");
        }
        
        if(pattern.getAuthProvider() != null && StringUtils.isNotBlank(pattern.getAuthProvider().getId())) {
        	pattern.setAuthProvider(authProviderDAO.findById(pattern.getAuthProvider().getId()));
        } else {
        	pattern.setAuthProvider(null);
        }
        
        if(CollectionUtils.isNotEmpty(pattern.getSubstitutions())) {
        	for(final URIPatternSubstitutionEntity substitution : pattern.getSubstitutions()) {
        		substitution.setPattern(pattern);
        	}
        }
        
        if(CollectionUtils.isNotEmpty(pattern.getServers())) {
        	for(final URIPatternServerEntity server : pattern.getServers()) {
        		server.setPattern(pattern);
        	}
        }
        
        if(CollectionUtils.isNotEmpty(pattern.getMethods())) {
        	for(final URIPatternMethodEntity patternMethod : pattern.getMethods()) {
        		patternMethod.setPattern(pattern);
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
                    resource.setName(System.currentTimeMillis() + "_" + pattern.getPattern() + "_" + patternMethod.getMethod());
                    resource.setResourceType(patternMethodResourceType);
                    resource.setId(null);
                    resource.setIsPublic(false);
                    resource.setCoorelatedName(getCoorelatedName(patternMethod));
                    resourceDao.add(resource);
                    patternMethod.setResource(resource);
        		}
        	}
        }
        
        if(CollectionUtils.isNotEmpty(pattern.getMetaEntitySet())) {
        	for(final URIPatternMetaEntity meta : pattern.getMetaEntitySet()) {
        		meta.setPattern(pattern);
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
        
        if(CollectionUtils.isNotEmpty(pattern.getParams())) {
        	for(final URIPatternParameterEntity param : pattern.getParams()) {
        		param.setPattern(pattern);
        	}
        }
        
        if(CollectionUtils.isNotEmpty(pattern.getErrorMappings())) {
        	for(final URIPatternErrorMappingEntity errorMapping : pattern.getErrorMappings()) {
        		errorMapping.setPattern(pattern);
        	}
        }
        
        if(StringUtils.isBlank(pattern.getId())) {
        	
            ResourceTypeEntity resourceType = resourceTypeDAO.findById(patternResourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for URI pattern. Resource type is not found");
            }

            final ResourceEntity resource = new ResourceEntity();
            resource.setName(System.currentTimeMillis() + "_" + pattern.getPattern());
            resource.setResourceType(resourceType);
            resource.setId(null);
            resource.setIsPublic(false);
            resource.setCoorelatedName(String.format("%s - %s", contentProvider.getName(), pattern.getPattern()));
            resourceDao.add(resource);

            pattern.setResource(resource);
            
            final Set<AuthLevelGroupingURIPatternXrefEntity> incomingXrefs = pattern.getGroupingXrefs();
            pattern.setGroupingXrefs(null);
            uriPatternDao.save(pattern);
            if(CollectionUtils.isNotEmpty(incomingXrefs)) {
            	for(final AuthLevelGroupingURIPatternXrefEntity xref : incomingXrefs) {
            		final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
            		xref.setPattern(pattern);
            		xref.setGrouping(grouping);
            		xref.setId(new AuthLevelGroupingURIPatternXrefIdEntity(grouping.getId(), pattern.getId()));
            	}
            }
            pattern.setGroupingXrefs(incomingXrefs);
            uriPatternDao.merge(pattern);
        } else{
        	final URIPatternEntity dbEntity = uriPatternDao.findById(pattern.getId());
        	
        	if(dbEntity != null) {
        		pattern.setResource(dbEntity.getResource());
        		if(pattern.getResource() != null) {
        			pattern.getResource().setCoorelatedName(String.format("%s - %s", pattern.getContentProvider().getName(), pattern.getPattern()));
        		}
        		if(CollectionUtils.isEmpty(pattern.getGroupingXrefs())) {
        			dbEntity.getGroupingXrefs().clear();
        			pattern.setGroupingXrefs(dbEntity.getGroupingXrefs());
        		} else {
        			for(final AuthLevelGroupingURIPatternXrefEntity xref : pattern.getGroupingXrefs()) {
        				final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
        				xref.setGrouping(grouping);
        				xref.setPattern(pattern);
        				xref.setId(new AuthLevelGroupingURIPatternXrefIdEntity(grouping.getId(), pattern.getId()));
        			}
        		}
        		
        		if(CollectionUtils.isEmpty(pattern.getSubstitutions())) {
        			dbEntity.getSubstitutions().clear();
        			pattern.setSubstitutions(dbEntity.getSubstitutions());
        		}
        		
        		if(CollectionUtils.isEmpty(pattern.getServers())) {
        			dbEntity.getServers().clear();
        			pattern.setServers(dbEntity.getServers());
        		}
        		
        		if(CollectionUtils.isEmpty(pattern.getErrorMappings())) {
                	dbEntity.getErrorMappings().clear();
                	pattern.setErrorMappings(dbEntity.getErrorMappings());
                }
        		
        		if(CollectionUtils.isEmpty(pattern.getMetaEntitySet())) {
        			dbEntity.getMetaEntitySet().clear();
        			pattern.setMetaEntitySet(dbEntity.getMetaEntitySet());
        		} else {
        			for(final URIPatternMetaEntity meta : pattern.getMetaEntitySet()) {
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
        		
        		if(CollectionUtils.isEmpty(pattern.getParams())) {
        			dbEntity.getParams().clear();
        			pattern.setParams(dbEntity.getParams());
        		}
        		
        		if(CollectionUtils.isEmpty(pattern.getMethods())) {
        			 dbEntity.getMethods().clear();
        			 pattern.setMethods(dbEntity.getMethods());
        		} else {
                	for(final URIPatternMethodEntity patternMethod : pattern.getMethods()) {
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
        		
        		pattern.setPageTemplates(dbEntity.getPageTemplates());
        		//pattern.setMetaEntitySet(dbEntity.getMetaEntitySet());
        		uriPatternDao.merge(pattern);
        	}
        }
    }

    @Override
    @Transactional
    public void deleteProviderPattern(String patternId) {
       if(StringUtils.isNotBlank(patternId)) {
    	   URIPatternEntity entity  = uriPatternDao.findById(patternId);
    	   if(entity != null) {
    		   uriPatternDao.delete(entity);
    	   }
       }
    }


    @Override
    public List<URIPatternMetaTypeEntity> getAllMetaType() {
        return patternMetaTypeDAO.findAll();
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
	public AuthLevelGroupingEntity getAuthLevelGrouping(String id) {
		return authLevelGroupingDAO.findById(id);
	}
	

	@Override
	@Transactional
	public void deleteAuthLevelGrouping(String id) {
		final AuthLevelGroupingEntity entity = authLevelGroupingDAO.findById(id);
		if(entity != null) {
			authLevelGroupingDAO.delete(entity);
		}
	}

	@Override
	@Transactional
	public void saveAuthLevelGrouping(AuthLevelGroupingEntity entity) {
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
	public void validateSaveAuthLevelGrouping(AuthLevelGroupingEntity entity)
			throws BasicDataServiceException {
		final AuthLevelGroupingEntity dbEntity = authLevelGroupingDAO.findByName(entity.getName());
		if(dbEntity != null) {
			if(StringUtils.isBlank(entity.getId()) || !(dbEntity.getId().equals(entity.getId()))) {
				throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
			}
		}
	}

	@Override
	@Transactional
	public void saveAuthLevelAttibute(AuthLevelAttributeEntity entity) {
		if(entity != null) {
			MetadataTypeEntity type = null;
			if(entity.getType() != null) {
				type = typeDAO.findById(entity.getType().getId());
			}
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
		}
	}

	@Override
	@Transactional
	public void deleteAuthLevelAttribute(String id) {
		final AuthLevelAttributeEntity entity = authLevelAttributeDAO.findById(id);
		if(entity != null) {
			authLevelAttributeDAO.delete(entity);
		}
	}

	@Override
	@Transactional
	public AuthLevelAttributeEntity getAuthLevelAttribute(String id) {
		return authLevelAttributeDAO.findById(id);
	}

	@Override
	@Transactional
	public void createDefaultURIPatterns(String providerId) {
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
							if(CollectionUtils.isNotEmpty(defaultPattern.getGroupingXrefs())) {
								for(final AuthLevelGroupingURIPatternXrefEntity defaultGrouping : defaultPattern.getGroupingXrefs()) {
									if(!pattern.hasAuthGrouping(defaultGrouping.getId().getGroupingId())) {
										final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(defaultGrouping.getId().getGroupingId());
										pattern.addGroupingXref(new AuthLevelGroupingURIPatternXrefEntity(pattern, grouping));
									}
								}
							}
							saveURIPattern(pattern);
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
						if(CollectionUtils.isNotEmpty(defaultPattern.getGroupingXrefs())) {
							final Set<AuthLevelGroupingURIPatternXrefEntity> groupingXrefs = new HashSet<>();
							for(final AuthLevelGroupingURIPatternXrefEntity defaultGrouping : defaultPattern.getGroupingXrefs()) {
								final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(defaultGrouping.getId().getGroupingId());
								groupingXrefs.add(new AuthLevelGroupingURIPatternXrefEntity(pattern, grouping));
							}
							pattern.setGroupingXrefs(groupingXrefs);
						}
						saveURIPattern(pattern);
					}
				}
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
        InputStream stream = defaultPatternResource.getInputStream();
		patternWrapper = mapper.readValue(stream, URIPatternJSONWrapper.class);
	}
}
