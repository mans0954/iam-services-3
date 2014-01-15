package org.openiam.am.srvc.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.cxf.interceptor.URIMappingInterceptor;
import org.openiam.am.srvc.constants.AmAttributes;
import org.openiam.am.srvc.dao.*;
import org.openiam.am.srvc.domain.*;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.ui.theme.UIThemeDAO;
import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service("contentProviderService")
public class ContentProviderServiceImpl implements  ContentProviderService{
    private static final String resourceTypeId="CONTENT_PROVIDER";
    private static final String patternResourceTypeId="URL_PATTERN";
    @Autowired
    private AuthLevelDao authLevelDao;
    @Autowired
    private ContentProviderDao contentProviderDao;
    @Autowired
    private ContentProviderServerDao contentProviderServerDao;

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

    @Override
    public List<AuthLevelEntity> getAuthLevelList(){
      return  authLevelDao.findAll();
    }

    @Override
    public ContentProviderEntity getContentProvider(String providerId) {
        if(providerId==null || providerId.trim().isEmpty())
            throw new NullPointerException("Content Provider Id not set");
        return contentProviderDao.findById(providerId);
    }

    @Override
    public Integer getNumOfContentProviders(ContentProviderEntity example) {
        return contentProviderDao.count(example);
    }

    @Override
    public List<ContentProviderEntity> findBeans(ContentProviderEntity example, Integer from, Integer size) {
        return contentProviderDao.getByExample(example, from, size);
    }

    @Override
    public List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL) {
        return  contentProviderDao.getProviderByDomainPattern(domainPattern, isSSL);
    }

    @Override
    @Transactional
    public ContentProviderEntity saveContentProvider(ContentProviderEntity provider){
        if (provider == null) {
            throw new NullPointerException("Content provider not set");
        }
        if (StringUtils.isBlank(provider.getName())) {
            throw new  IllegalArgumentException("Provider name not set");
        }
        if (provider.getMinAuthLevel()==null || StringUtils.isBlank(provider.getMinAuthLevel().getId())) {
            throw new  IllegalArgumentException("Auth Level not set for provider");
        }
        if(provider.getManagedSystem() == null || StringUtils.isBlank(provider.getManagedSystem().getManagedSysId())) {
        	throw new  IllegalArgumentException("Managed System not set for provider");
        }

        final AuthLevelEntity authLevel = authLevelDao.findById(provider.getMinAuthLevel().getId());
        if(authLevel==null) {
            throw new NullPointerException("Cannot save content provider. Auth LEVEL is not found");
        }
        
        final ManagedSysEntity managedSys = managedSysDAO.findById(provider.getManagedSystem().getManagedSysId());
        if(managedSys == null) {
        	throw new NullPointerException("Cannot save content provider. Managed System is not found");
        }
        
        if(provider.getUiTheme() != null) {
        	final UIThemeEntity theme = uiThemeDAO.findById(provider.getUiTheme().getId());
        	provider.setUiTheme(theme);
        }
        
        final String cpURL = provider.getResource().getURL();

        provider.setManagedSystem(managedSys);
        provider.setMinAuthLevel(authLevel);
        ContentProviderEntity entity  = null;
        if(provider.getId()==null || provider.getId().trim().isEmpty()){
            // new provider
            // create resources
            ResourceTypeEntity resourceType = resourceTypeDAO.findById(resourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for provider. Resource type is not found");
            }

            ResourceEntity resource = new ResourceEntity();
            resource.setName(resourceTypeId+"_"+provider.getName() + "_" + System.currentTimeMillis());
            resource.setResourceType(resourceType);
            resource.setId(null);
            resource.setIsPublic(false);
            resource.setURL(cpURL);
            resourceDao.save(resource);

            provider.setId(null);
            provider.setResource(resource);
            //provider.setResourceId(resource.getResourceId());

            contentProviderDao.save(provider);
            entity = provider;
        } else{
            // update provider
            entity  = contentProviderDao.findById(provider.getId());
            entity.setDomainPattern(provider.getDomainPattern());
            entity.setName(provider.getName());
            entity.setMinAuthLevel(authLevel);
            entity.setIsPublic(provider.getIsPublic());
            entity.setIsSSL(provider.getIsSSL());
            entity.setUiTheme(provider.getUiTheme());
            entity.getResource().setURL(cpURL);
            /*entity.setContextPath(provider.getContextPath());*/
            entity.setPatternSet(null);
            entity.setServerSet(null);

            contentProviderDao.save(entity);
        }
        return entity;
    }

    @Override
    @Transactional
    public void deleteContentProvider(String providerId) {
        if (providerId==null || providerId.trim().isEmpty())
            throw new  IllegalArgumentException("Provider Id name not set");

        ContentProviderEntity entity  = contentProviderDao.findById(providerId);

        if(entity!=null){
            // delete resource
            //resourceDataService.deleteResource(entity.getResource().getResourceId());
            /*
            // delete servers for given provider
            contentProviderServerDao.deleteByProvider(providerId);
            // delete patterns
            deletePatternByProvider(providerId);
            // delete provider
            contentProviderDao.deleteById(providerId);
            */
            contentProviderDao.delete(entity);
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
        if (contentProviderServerId==null || contentProviderServerId.trim().isEmpty())
            throw new  IllegalArgumentException("Content Provider Server Id name not set");

        contentProviderServerDao.deleteById(contentProviderServerId);
    }

    @Override
    @Transactional
    public ContentProviderServerEntity saveProviderServer(ContentProviderServerEntity contentProviderServer) {
        if (contentProviderServer == null)
            throw new  NullPointerException("Content Provider Server not set");
        if (contentProviderServer.getServerURL()==null || contentProviderServer.getServerURL().trim().isEmpty())
            throw new  IllegalArgumentException("Server Url not set");
        if (contentProviderServer.getContentProvider()==null
                || contentProviderServer.getContentProvider().getId()==null
                || contentProviderServer.getContentProvider().getId().trim().isEmpty())
            throw new  IllegalArgumentException("Content Provider not set");

        ContentProviderEntity provider = contentProviderDao.findById(contentProviderServer.getContentProvider().getId());

        if(provider==null){
            throw new NullPointerException("Cannot save content provider server. Content Provider is not found");
        }

        ContentProviderServerEntity entity  = null;
        if(contentProviderServer.getId()==null || contentProviderServer.getId().trim().isEmpty()){
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
    public URIPatternEntity saveURIPattern(URIPatternEntity pattern) {
        if (pattern == null) {
            throw new NullPointerException("Invalid agrument ");
        }
        if (StringUtils.isBlank(pattern.getPattern())) {
            throw new  IllegalArgumentException("Pattern not set");
        }
        if (pattern.getMinAuthLevel()==null || StringUtils.isBlank(pattern.getMinAuthLevel().getId())) {
            throw new  IllegalArgumentException("Auth Level not set for url pattern");
        }

        AuthLevelEntity authLevel = authLevelDao.findById(pattern.getMinAuthLevel().getId());
        if(authLevel==null) {
            throw new NullPointerException("Cannot save content provider. Auth LEVEL is not found");
        }

        if(pattern.getUiTheme() != null) {
        	final UIThemeEntity theme = uiThemeDAO.findById(pattern.getUiTheme().getId());
        	pattern.setUiTheme(theme);
        }
        
        ContentProviderEntity provider = contentProviderDao.findById(pattern.getContentProvider().getId());
        if(provider==null){
            throw new NullPointerException("Cannot save content provider server. Content Provider is not found");
        }

        pattern.setMinAuthLevel(authLevel);
        pattern.setContentProvider(provider);
        URIPatternEntity entity  = null;
        if(StringUtils.isBlank(pattern.getId())) {
            // new provider
            // create resources
            ResourceTypeEntity resourceType = resourceTypeDAO.findById(patternResourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for URI pattern. Resource type is not found");
            }

            ResourceEntity resource = new ResourceEntity();
            resource.setName(System.currentTimeMillis() + "_" + pattern.getPattern());
            resource.setResourceType(resourceType);
            resource.setId(null);
            resource.setIsPublic(false);
            resourceDao.add(resource);


            pattern.setId(null);
            pattern.setResource(resource);
            //pattern.setResourceId(resource.getResourceId());

            uriPatternDao.save(pattern);
            entity = pattern;
        } else{
            // update provider
            entity  = uriPatternDao.findById(pattern.getId());
            entity.setUiTheme(pattern.getUiTheme());
            entity.setPattern(pattern.getPattern());
            entity.setMinAuthLevel(authLevel);
            entity.setIsPublic(pattern.getIsPublic());
            entity.setMetaEntitySet(null);

            uriPatternDao.save(entity);
        }
        return entity;
    }

    @Override
    @Transactional
    public void deleteProviderPattern(String patternId) {
        if (patternId==null || patternId.trim().isEmpty())
            throw new  IllegalArgumentException("URI Pattern Id not set");

        URIPatternEntity entity  = uriPatternDao.findById(patternId);
        if(entity != null) {
        	uriPatternDao.delete(entity);
        }
        //deleteProviderPattern(entity);
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
        if(uriPatternMetaEntity==null)
            throw new NullPointerException("Invalid argument");
        if(uriPatternMetaEntity.getPattern()==null
           || uriPatternMetaEntity.getPattern().getId()==null
           || uriPatternMetaEntity.getPattern().getId().trim().isEmpty())
            throw new NullPointerException("URI Pattern not set");
        if(uriPatternMetaEntity.getName()==null
           || uriPatternMetaEntity.getName().trim().isEmpty())
            throw new  NullPointerException("URI Pattern Meta name not set");
        if(uriPatternMetaEntity.getMetaType()==null
           || uriPatternMetaEntity.getMetaType().getId()==null
           || uriPatternMetaEntity.getMetaType().getId().trim().isEmpty())
            throw new NullPointerException("Meta Type not set");
        
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
        if (metaId==null || metaId.trim().isEmpty())
            throw new  IllegalArgumentException("Meta Data Id not set");

        URIPatternMetaEntity entity  = uriPatternMetaDao.findById(metaId);
        if(entity != null) {
        	this.uriPatternMetaDao.delete(entity);
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
	public List<URIPatternEntity> getURIPatternsForContentProviderMatchingPattern(final String contentProviderId, final String pattern) {
		return uriPatternDao.getURIPatternsForContentProviderMatchingPattern(contentProviderId, pattern);
	}

    /*
    @Transactional
    private void saveMetaValue(URIPatternMetaValueEntity value) {
        if (value == null) {
            throw new NullPointerException("Meta Value is null");
        }
        if (value.getMetaEntity() == null || StringUtils.isBlank(value.getMetaEntity().getId())) {
            throw new NullPointerException("Meta Data is not set");
        }
        if (StringUtils.isBlank(value.getName())) {
            throw new NullPointerException("Meta Data Attribute Name is not set");
        }
        if ((value.getAmAttribute() == null || StringUtils.isBlank(value.getAmAttribute().getId())) &&
        	(StringUtils.isBlank(value.getStaticValue())) && 
        	(StringUtils.isBlank(value.getGroovyScript()))) {
            throw new NullPointerException("Meta Data Attribute value not set");
        }

        if(value.getAmAttribute() != null && StringUtils.isNotBlank(value.getAmAttribute().getId())) {
            value.setStaticValue(null);
            value.setGroovyScript(null);
            AuthResourceAMAttributeEntity amAttribute = authResourceAMAttributeDao.findById(value.getAmAttribute().getId());
            if(amAttribute==null) {
                throw new  NullPointerException("Cannot save Meta data value for URI pattern. Attribute Map is not found");
            }
            value.setAmAttribute(amAttribute);
        } else if(StringUtils.isNotBlank(value.getGroovyScript())) {
        	value.setStaticValue(null);
            value.setAmAttribute(null);
        } else {
        	value.setAmAttribute(null);
        	value.setGroovyScript(null);
        }

        URIPatternMetaValueEntity entity = null;
        if(value.getId()==null || value.getId().trim().isEmpty()){
            // new meta data value
            value.setId(null);

            uriPatternMetaValueDao.save(value);
            entity = value;
        } else{
            // update meta data value
            entity  = uriPatternMetaValueDao.findById(value.getId());
            entity.setName(value.getName());
            entity.setStaticValue(value.getStaticValue());
            entity.setAmAttribute(value.getAmAttribute());
            uriPatternMetaValueDao.save(entity);
        }
    }
    */

    /*
    @Transactional
    private void deleteProviderPattern(URIPatternEntity entity){
        if(entity!=null){
            // delete resource
            //resourceDataService.deleteResource(entity.getResource().getResourceId());
            // delete meta
            deleteMetaByPattern(entity.getId());
            // delete pattern
            
            uriPatternDao.deleteById(entity.getId());
        }
    }
    */

    /*
    @Transactional
    private void deletePatternByProvider(String providerId){
        URIPatternEntity example = new URIPatternEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        List<URIPatternEntity> patternList = getUriPatternsList(example, 0, Integer.MAX_VALUE);
        if(patternList!=null && !patternList.isEmpty()){
            for (URIPatternEntity pattern: patternList){
                deleteProviderPattern(pattern);
            }
        }
    }
    */

    /*
    @Transactional
    private void deleteMetaByPattern(String patternId){
        URIPatternMetaEntity example = new URIPatternMetaEntity();
        URIPatternEntity pattern = new URIPatternEntity();
        pattern.setId(patternId);
        example.setPattern(pattern);

        List<URIPatternMetaEntity> metaList = getMetaDataList(example, 0, Integer.MAX_VALUE);
        if(metaList!=null && !metaList.isEmpty()){
            for (URIPatternMetaEntity meta: metaList){
                deletePatternMeta(meta);
            }
        }
    }
    */
    /*
    @Transactional
    private void deletePatternMeta(URIPatternMetaEntity entity) {
        // delete all values
        //uriPatternMetaValueDao.deleteByMeta(entity.getId());
        //delete meta data
        uriPatternMetaDao.deleteById(entity.getId());
    }
    */
}
