package org.openiam.am.srvc.service;

import org.openiam.am.srvc.dao.*;
import org.openiam.am.srvc.domain.*;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ResourceDataService resourceDataService;

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
        if (provider == null)
            throw new NullPointerException("Content provider not set");
        if (provider.getName()==null || provider.getName().trim().isEmpty())
            throw new  IllegalArgumentException("Provider name not set");
        if (provider.getMinAuthLevel()==null || provider.getMinAuthLevel().getId()==null || provider.getMinAuthLevel().getId().trim().isEmpty())
            throw new  IllegalArgumentException("Auth Level not set for provider");

        AuthLevelEntity authLevel = authLevelDao.findById(provider.getMinAuthLevel().getId());
        if(authLevel==null) {
            throw new NullPointerException("Cannot save content provider. Auth LEVEL is not found");
        }

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
            resource.setName(resourceTypeId+"_"+provider.getName());
            resource.setResourceType(resourceType);
            resource.setResourceId(null);
            resource.setIsPublic(true);
            resourceDao.save(resource);

            provider.setId(null);
            provider.setResource(null);
            provider.setResourceId(resource.getResourceId());

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
            resourceDataService.deleteResource(entity.getResourceId());
            // delete servers for given provider
            contentProviderServerDao.deleteByProvider(providerId);
            // delete patterns
            deletePatternByProvider(providerId);
            // delete provider
            contentProviderDao.deleteById(providerId);
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
        if (pattern == null)
            throw new NullPointerException("Invalid agrument ");
        if (pattern.getPattern()==null || pattern.getPattern().trim().isEmpty())
            throw new  IllegalArgumentException("Pattern not set");
        if (pattern.getMinAuthLevel()==null || pattern.getMinAuthLevel().getId()==null || pattern.getMinAuthLevel().getId().trim().isEmpty())
            throw new  IllegalArgumentException("Auth Level not set for url pattern");

        AuthLevelEntity authLevel = authLevelDao.findById(pattern.getMinAuthLevel().getId());
        if(authLevel==null) {
            throw new NullPointerException("Cannot save content provider. Auth LEVEL is not found");
        }

        ContentProviderEntity provider = contentProviderDao.findById(pattern.getContentProvider().getId());
        if(provider==null){
            throw new NullPointerException("Cannot save content provider server. Content Provider is not found");
        }

        pattern.setMinAuthLevel(authLevel);
        pattern.setContentProvider(provider);
        URIPatternEntity entity  = null;
        if(pattern.getId()==null || pattern.getId().trim().isEmpty()){
            // new provider
            // create resources
            ResourceTypeEntity resourceType = resourceTypeDAO.findById(patternResourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for URI pattern. Resource type is not found");
            }

            ResourceEntity resource = new ResourceEntity();
            resource.setName(patternResourceTypeId+"_"+provider.getId()+"_"+pattern.getPattern());
            resource.setResourceType(resourceType);
            resource.setResourceId(null);
            resource.setIsPublic(true);
            resourceDao.add(resource);


            pattern.setId(null);
            pattern.setResource(null);
            pattern.setResourceId(resource.getResourceId());

            uriPatternDao.save(pattern);
            entity = pattern;
        } else{
            // update provider
            entity  = uriPatternDao.findById(pattern.getId());
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
        deleteProviderPattern(entity);
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

        URIPatternMetaTypeEntity metaType = uriPatternMetaTypeDao.findById(uriPatternMetaEntity.getMetaType().getId());
        if(metaType==null){
            throw new NullPointerException("Cannot save Meta data for URI pattern. Meta Type is not found");
        }

        URIPatternEntity pattern = uriPatternDao.findById(uriPatternMetaEntity.getPattern().getId());
        if(pattern==null){
            throw new NullPointerException("Cannot save Meta data for URI pattern. URI pattern is not found");
        }

        Set<URIPatternMetaValueEntity> metaValues =uriPatternMetaEntity.getMetaValueSet();
        uriPatternMetaEntity.setPattern(pattern);
        uriPatternMetaEntity.setMetaType(metaType);
        uriPatternMetaEntity.setMetaValueSet(null);

        URIPatternMetaEntity entity  = null;
        if(uriPatternMetaEntity.getId()==null || uriPatternMetaEntity.getId().trim().isEmpty()){
            // new meta data
            uriPatternMetaEntity.setId(null);

            uriPatternMetaDao.save(uriPatternMetaEntity);
            entity = uriPatternMetaEntity;
        } else{
            // update meta data
            entity  = uriPatternMetaDao.findById(uriPatternMetaEntity.getId());
            entity.setMetaType(uriPatternMetaEntity.getMetaType());
            uriPatternMetaDao.save(entity);
        }
        // sync values
        syncURIPatternMetaValue(entity, metaValues);
        return entity;
    }



    @Override
    @Transactional
    public void deleteMetaDataForPattern(String metaId) {
        if (metaId==null || metaId.trim().isEmpty())
            throw new  IllegalArgumentException("Meta Data Id not set");

        URIPatternMetaEntity entity  = uriPatternMetaDao.findById(metaId);
        deletePatternMeta(entity);
    }


    @Override
    public List<URIPatternMetaTypeEntity> getAllMetaType() {
        return uriPatternMetaTypeDao.findAll();
    }

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
    @Transactional
    private void deleteMetaValue(String id) {
        uriPatternMetaValueDao.deleteById(id);
    }

    @Transactional
    private void saveMetaValue(URIPatternMetaValueEntity value) {
        if (value == null)
            throw new NullPointerException("Meta Value is null");
        if (value.getMetaEntity() == null || value.getMetaEntity().getId()==null || value.getMetaEntity().getId().trim().isEmpty())
            throw new NullPointerException("Meta Data is not set");
        if (value.getName() == null || value.getName().trim().isEmpty())
            throw new NullPointerException("Meta Data Attribute Name is not set");
        if ((value.getAmAttribute() == null
             || value.getAmAttribute().getAmAttributeId()==null
             || value.getAmAttribute().getAmAttributeId().trim().isEmpty())
            &&(value.getStaticValue() == null || value.getStaticValue().trim().isEmpty()))
            throw new NullPointerException("Meta Data Attribute value not set");

        if(value.getAmAttribute() != null
            && value.getAmAttribute().getAmAttributeId()!=null
            && !value.getAmAttribute().getAmAttributeId().trim().isEmpty()){
            value.setStaticValue(null);
            AuthResourceAMAttributeEntity amAttribute = authResourceAMAttributeDao.findById(value.getAmAttribute().getAmAttributeId());
            if(amAttribute==null)
                throw new  NullPointerException("Cannot save Meta data value for URI pattern. Attribute Map is not found");

            value.setAmAttribute(amAttribute);
        } else{
            value.setAmAttribute(null);
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

    @Transactional
    private void deleteProviderPattern(URIPatternEntity entity){
        if(entity!=null){
            // delete resource
            resourceDataService.deleteResource(entity.getResourceId());
            // delete meta
            deleteMetaByPattern(entity.getId());
            // delete pattern
            uriPatternDao.deleteById(entity.getId());
        }
    }


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

    @Transactional
    private void deletePatternMeta(URIPatternMetaEntity entity) {
        // delete all values
        uriPatternMetaValueDao.deleteByMeta(entity.getId());
        //delete meta data
        uriPatternMetaDao.deleteById(entity.getId());
    }
}
