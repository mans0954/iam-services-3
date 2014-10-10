package org.openiam.am.srvc.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
    private static final String resourceTypeId="CONTENT_PROVIDER";
    private static final String patternResourceTypeId="URL_PATTERN";
    @Autowired
    private ContentProviderDao contentProviderDao;
    @Autowired
    private ContentProviderServerDao contentProviderServerDao;
    
    @Autowired
    private AuthProviderDao authProviderDAO;

    @Autowired
    private URIPatternDao uriPatternDao;
    @Autowired
    private URIPatternMetaDao uriPatternMetaDao;
    @Autowired
    private URIPatternMetaTypeDao uriPatternMetaTypeDao;
    @Autowired
    private URIPatternMetaValueDao uriPatternMetaValueDao;
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
    @Qualifier("defaultPatternResoruce")
    private Resource defaultPatternResoruce;
    
    @Autowired
    @Qualifier("customJacksonMapper")
    private CustomJacksonMapper mapper;
    
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
    public Integer getNumOfContentProviders(ContentProviderEntity example) {
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
    public void saveContentProvider(ContentProviderEntity provider){
       
    	UIThemeEntity theme = null;
        final ManagedSysEntity managedSys = managedSysDAO.findById(provider.getManagedSystem().getId());        
        if(provider.getUiTheme() != null) {
        	theme = uiThemeDAO.findById(provider.getUiTheme().getId());
        }
        
        if(provider.getAuthProvider() != null && StringUtils.isNotBlank(provider.getAuthProvider().getId())) {
        	provider.setAuthProvider(authProviderDAO.findById(provider.getAuthProvider().getId()));
        } else {
        	provider.setAuthProvider(null);
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
            provider.setManagedSystem(managedSys);
            provider.setUiTheme(theme);
            
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
        		dbEntity.setDomainPattern(provider.getDomainPattern());
        		dbEntity.setIsPublic(provider.getIsPublic());
        		dbEntity.setIsSSL(provider.getIsSSL());
        		//dbEntity.setManagedSystem(provider.getManagedSystem());
        		dbEntity.setName(provider.getName());
        		dbEntity.getResource().setURL(cpURL);
        		dbEntity.getResource().setCoorelatedName(provider.getName());
        		dbEntity.setManagedSystem(managedSys);
        		dbEntity.setUiTheme(theme);
        		dbEntity.setAuthProvider(provider.getAuthProvider());
        		dbEntity.setShowOnApplicationPage(provider.isShowOnApplicationPage());
        		if(dbEntity.getGroupingXrefs() == null) {
        			dbEntity.setGroupingXrefs(new HashSet<AuthLevelGroupingContentProviderXrefEntity>());
        		}
        		
        		/* set CP id */
        		if(CollectionUtils.isNotEmpty(provider.getGroupingXrefs())) {
    				for(AuthLevelGroupingContentProviderXrefEntity xref : provider.getGroupingXrefs()) {
    					xref.getId().setContentProviderId(dbEntity.getId());
    				}
        		}
        		
        		/* update and delete */
        		for(final Iterator<AuthLevelGroupingContentProviderXrefEntity> dbIterator = dbEntity.getGroupingXrefs().iterator(); dbIterator.hasNext();) {
        			boolean contains = false;
        			final AuthLevelGroupingContentProviderXrefEntity dbXref = dbIterator.next();
        			if(CollectionUtils.isNotEmpty(provider.getGroupingXrefs())) {
        				for(AuthLevelGroupingContentProviderXrefEntity xref : provider.getGroupingXrefs()) {
        					if(dbXref.getId().equals(xref.getId())) { /* update */
        						dbXref.setOrder(xref.getOrder());
        						contains = true;
        					}
        				}
        			}
        			
        			if(!contains) {
        				dbIterator.remove();
        			}
        		}
        		
        		if(CollectionUtils.isNotEmpty(provider.getGroupingXrefs())) {
        			final Set<AuthLevelGroupingContentProviderXrefEntity> newXrefs = new HashSet<AuthLevelGroupingContentProviderXrefEntity>();
    				for(final AuthLevelGroupingContentProviderXrefEntity xref : provider.getGroupingXrefs()) {
    					boolean contains = false;
    					for(final AuthLevelGroupingContentProviderXrefEntity dbXref : dbEntity.getGroupingXrefs()) {
    						if(xref.getId().equals(dbXref.getId())) {
    							contains = true;
    						}
    					}
    					
    					if(!contains) {
    						final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
    						xref.setContentProvider(dbEntity);
    						xref.setGrouping(grouping);
    						xref.setId(new AuthLevelGroupingContentProviderXrefIdEntity(grouping.getId(), dbEntity.getId()));
    						newXrefs.add(xref);
        				}
    				}
    				dbEntity.getGroupingXrefs().addAll(newXrefs);
        		}
        		
        		contentProviderDao.update(dbEntity);
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
    public List<ContentProviderServerEntity> getProviderServers(ContentProviderServerEntity example, Integer from, Integer size) {
        return contentProviderServerDao.getByExample(example,from, size);
    }

    @Override
    public Integer getNumOfProviderServers(ContentProviderServerEntity example) {
        return contentProviderServerDao.count(example);
    }

    @Override
    @Transactional
    public void deleteProviderServer(String contentProviderServerId) {
    	if(StringUtils.isNotBlank(contentProviderServerId)) {
    		contentProviderServerDao.deleteById(contentProviderServerId);
    	}
    }

    @Override
    @Transactional
    public ContentProviderServerEntity saveProviderServer(ContentProviderServerEntity contentProviderServer) {
        if (contentProviderServer == null) {
            throw new  NullPointerException("Content Provider Server not set");
        }
        if(StringUtils.isBlank(contentProviderServer.getServerURL())) {
            throw new  IllegalArgumentException("Server Url not set");
        }
        if (contentProviderServer.getContentProvider()==null || StringUtils.isBlank(contentProviderServer.getContentProvider().getId())) {
            throw new  IllegalArgumentException("Content Provider not set");
        }

        ContentProviderEntity provider = contentProviderDao.findById(contentProviderServer.getContentProvider().getId());

        if(provider==null){
            throw new NullPointerException("Cannot save content provider server. Content Provider is not found");
        }

        ContentProviderServerEntity entity  = null;
        if(StringUtils.isBlank(contentProviderServer.getId())) {
            // new server

            contentProviderServer.setId(null);
            contentProviderServer.setContentProvider(provider);
            contentProviderServerDao.save(contentProviderServer);
            entity = contentProviderServer;
        } else{
            // update server
            entity  = contentProviderServerDao.findById(contentProviderServer.getId());
            entity.setServerURL(contentProviderServer.getServerURL());
            contentProviderServerDao.save(entity);
        }
        return entity;
    }

    @Override
    public Integer getNumOfUriPatterns(URIPatternEntity example) {
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

    @Override
    @Transactional
    public void saveURIPattern(URIPatternEntity pattern) {
        final UIThemeEntity theme = (pattern.getUiTheme() != null) ? uiThemeDAO.findById(pattern.getUiTheme().getId()) : null;
        
        if(StringUtils.isBlank(pattern.getId())) {
        	final ContentProviderEntity contentProvider = contentProviderDao.findById(pattern.getContentProvider().getId());
        	
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
            pattern.setUiTheme(theme);
            pattern.setContentProvider(contentProvider);
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
        		dbEntity.setIsPublic(pattern.getIsPublic());
        		dbEntity.setPattern(pattern.getPattern());
        		dbEntity.setUiTheme(theme);
        		if(dbEntity.getGroupingXrefs() == null) {
        			dbEntity.setGroupingXrefs(new HashSet<AuthLevelGroupingURIPatternXrefEntity>());
        		}
        		
        		/* set CP id */
        		if(CollectionUtils.isNotEmpty(pattern.getGroupingXrefs())) {
    				for(AuthLevelGroupingURIPatternXrefEntity xref : pattern.getGroupingXrefs()) {
    					xref.getId().setPatternId(dbEntity.getId());
    				}
        		}
        		
        		/* update and delete */
        		for(final Iterator<AuthLevelGroupingURIPatternXrefEntity> dbIterator = dbEntity.getGroupingXrefs().iterator(); dbIterator.hasNext();) {
        			boolean contains = false;
        			final AuthLevelGroupingURIPatternXrefEntity dbXref = dbIterator.next();
        			if(CollectionUtils.isNotEmpty(pattern.getGroupingXrefs())) {
        				for(AuthLevelGroupingURIPatternXrefEntity xref : pattern.getGroupingXrefs()) {
        					if(dbXref.getId().equals(xref.getId())) { /* update */
        						dbXref.setOrder(xref.getOrder());
        						contains = true;
        					}
        				}
        			}
        			
        			if(!contains) {
        				dbIterator.remove();
        			}
        		}
        		
        		if(CollectionUtils.isNotEmpty(pattern.getGroupingXrefs())) {
        			final Set<AuthLevelGroupingURIPatternXrefEntity> newXrefs = new HashSet<AuthLevelGroupingURIPatternXrefEntity>();
    				for(final AuthLevelGroupingURIPatternXrefEntity xref : pattern.getGroupingXrefs()) {
    					boolean contains = false;
    					for(final AuthLevelGroupingURIPatternXrefEntity dbXref : dbEntity.getGroupingXrefs()) {
    						if(xref.getId().equals(dbXref.getId())) {
    							contains = true;
    						}
    					}
    					
    					if(!contains) {
    						final AuthLevelGroupingEntity grouping = authLevelGroupingDAO.findById(xref.getId().getGroupingId());
    						xref.setPattern(dbEntity);
    						xref.setGrouping(grouping);
    						xref.setId(new AuthLevelGroupingURIPatternXrefIdEntity(grouping.getId(), dbEntity.getId()));
    						newXrefs.add(xref);
        				}
    				}
    				dbEntity.getGroupingXrefs().addAll(newXrefs);
        		}
        		
        		if(dbEntity.getResource() != null) {
        			dbEntity.getResource().setCoorelatedName(String.format("%s - %s", pattern.getContentProvider().getName(), pattern.getPattern()));
        		}
        		
        		uriPatternDao.update(dbEntity);
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
    public List<URIPatternMetaEntity> getMetaDataList(URIPatternMetaEntity example, Integer from, Integer size) {
        return uriPatternMetaDao.getByExample(example, from, size);
    }

    @Override
    public Integer getNumOfMetaData(URIPatternMetaEntity example) {
        return uriPatternMetaDao.count(example);
    }

    @Override
    public URIPatternMetaEntity getURIPatternMeta(String metaId) {
        return uriPatternMetaDao.findById(metaId);
    }

    @Override
    @Transactional
    public URIPatternMetaEntity saveMetaDataForPattern(URIPatternMetaEntity uriPatternMetaEntity) {
        if(uriPatternMetaEntity==null) {
            throw new NullPointerException("Invalid argument");
        }
        if(uriPatternMetaEntity.getPattern()==null || StringUtils.isBlank(uriPatternMetaEntity.getPattern().getId())) {
            throw new NullPointerException("URI Pattern not set");
        }
        if(StringUtils.isBlank(uriPatternMetaEntity.getName())) {
            throw new  NullPointerException("URI Pattern Meta name not set");
        }
        if(uriPatternMetaEntity.getMetaType()==null || StringUtils.isBlank(uriPatternMetaEntity.getMetaType().getId())) {
            throw new NullPointerException("Meta Type not set");
        }
        if(CollectionUtils.isNotEmpty(uriPatternMetaEntity.getMetaValueSet())) {
    		for(final URIPatternMetaValueEntity value : uriPatternMetaEntity.getMetaValueSet()) {
    			value.setMetaEntity(uriPatternMetaEntity);
	
    			/* satisfy data integrity */
    			if(value.getAmAttribute() != null && StringUtils.isNotBlank(value.getAmAttribute().getId())) {
    				value.setStaticValue(null);
    				value.setGroovyScript(null);
    			} else if(StringUtils.isNotBlank(value.getStaticValue())) {
    				value.setAmAttribute(null);
    				value.setGroovyScript(null);
    			} else if(StringUtils.isNotBlank(value.getGroovyScript())) {
    				value.setAmAttribute(null);
    				value.setStaticValue(null);
    			}

    			/* set am attribute entity, if any */
    			if(value.getAmAttribute() != null && StringUtils.isNotBlank(value.getAmAttribute().getId())) {
    				final AuthResourceAMAttributeEntity attribute = authResourceAMAttributeDao.findById(value.getAmAttribute().getId());
    				value.setAmAttribute(attribute);
    			}
    		}
    	}
        
        final URIPatternMetaTypeEntity metaType = uriPatternMetaTypeDao.findById(uriPatternMetaEntity.getMetaType().getId());
    	final URIPatternEntity pattern = uriPatternDao.findById(uriPatternMetaEntity.getPattern().getId());
        if(StringUtils.isBlank(uriPatternMetaEntity.getId())) {
        	/* set meta type */
        	uriPatternMetaEntity.setMetaType(metaType);
        	uriPatternMetaEntity.setPattern(pattern);
        	uriPatternMetaDao.save(uriPatternMetaEntity);
        } else {
        	/* do a merge */
        	final URIPatternMetaEntity existing = uriPatternMetaDao.findById(uriPatternMetaEntity.getId());
        	existing.setName(uriPatternMetaEntity.getName());
        	existing.setPattern(pattern);
        	existing.setMetaType(metaType);
        	
        	final Set<URIPatternMetaValueEntity> incomingValues = (uriPatternMetaEntity.getMetaValueSet() != null) ? uriPatternMetaEntity.getMetaValueSet() : new HashSet<URIPatternMetaValueEntity>();
        	final Set<URIPatternMetaValueEntity> existingValues = (existing.getMetaValueSet() != null) ? existing.getMetaValueSet() : new HashSet<URIPatternMetaValueEntity>();
        	for(final Iterator<URIPatternMetaValueEntity> it = existingValues.iterator(); it.hasNext();) {
        		final URIPatternMetaValueEntity existingValue = it.next();
        		boolean exists = false;
        		for(final URIPatternMetaValueEntity incomingValue : incomingValues) {
        			if(StringUtils.equals(incomingValue.getId(), existingValue.getId())) {
        				exists = true;
        				existingValue.setAmAttribute(incomingValue.getAmAttribute());
        				existingValue.setGroovyScript(incomingValue.getGroovyScript());
        				existingValue.setStaticValue(incomingValue.getStaticValue());
        				existingValue.setName(incomingValue.getName());
        				existingValue.setPropagateThroughProxy(incomingValue.isPropagateThroughProxy());
        			}
        		}
        		if(!exists) {
        			it.remove();
        		}
        	}
        	
        	/* find new ones */
        	final List<URIPatternMetaValueEntity> newValues = new LinkedList<URIPatternMetaValueEntity>();
        	for(final URIPatternMetaValueEntity incomingValue : incomingValues) {
        		boolean exists = false;
        		for(final URIPatternMetaValueEntity existingValue : existingValues) {
        			if(StringUtils.equals(incomingValue.getId(), existingValue.getId())) {
        				exists = true;
        			}
        		}
        		
        		if(!exists) {
        			incomingValue.setMetaEntity(existing);
        			newValues.add(incomingValue);
        		}
    		}
        	
        	existingValues.addAll(newValues);
        	
        	uriPatternMetaDao.update(existing);
        	uriPatternMetaEntity = existing;
        }
        return uriPatternMetaEntity;
    }

    


    @Override
    @Transactional
    public void deleteMetaDataForPattern(String metaId) {
        if(metaId != null) {
	        URIPatternMetaEntity entity  = uriPatternMetaDao.findById(metaId);
	        if(entity != null) {
	        	this.uriPatternMetaDao.delete(entity);
	        }
        }
        //deletePatternMeta(entity);
    }


    @Override
    public List<URIPatternMetaTypeEntity> getAllMetaType() {
        return uriPatternMetaTypeDao.findAll();
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
    
    @Transactional
    private void deleteMetaValue(String id) {
        uriPatternMetaValueDao.deleteById(id);
    }

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
			throw new RuntimeException(String.format("Can't get json metadata.  Check that '%s' is in the classpath", defaultPatternResoruce));
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
        InputStream stream = defaultPatternResoruce.getInputStream();
		patternWrapper = mapper.readValue(stream, URIPatternJSONWrapper.class);
	}
}
