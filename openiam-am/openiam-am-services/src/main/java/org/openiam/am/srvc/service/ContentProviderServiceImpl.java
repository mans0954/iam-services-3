package org.openiam.am.srvc.service;

import org.openiam.am.srvc.dao.AuthLevelDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.ContentProviderServerDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderServerEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

            ResourceEntity resource = provider.getResource();
            resource.setName(resourceTypeId+"_"+provider.getName());
            resource.setResourceType(resourceType);
            resource.setResourceId(null);
            resource = resourceDao.add(resource);

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
            entity.setContextPath(provider.getContextPath());
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
            uriPatternDao.deleteByProvider(providerId);
            // delete provider
            contentProviderDao.deleteById(providerId);
        }
    }

    @Override
    public List<ContentProviderServerEntity> getServersForProvider(String providerId, Integer from, Integer size) {
        ContentProviderServerEntity example = new ContentProviderServerEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

        return contentProviderServerDao.getByExample(example,from, size);
    }

    @Override
    public Integer getNumOfServersForProvider(String providerId) {
        ContentProviderServerEntity example = new ContentProviderServerEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
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
    public Integer getNumOfUriPatternsForProvider(String providerId) {
        URIPatternEntity example = new URIPatternEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);
        return uriPatternDao.count(example);
    }

    @Override
    public List<URIPatternEntity> getUriPatternsForProvider(String providerId, Integer from, Integer size) {
        URIPatternEntity example = new URIPatternEntity();
        ContentProviderEntity provider = new ContentProviderEntity();
        provider.setId(providerId);
        example.setContentProvider(provider);

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
        URIPatternEntity entity  = null;
        if(pattern.getId()==null || pattern.getId().trim().isEmpty()){
            // new provider
            // create resources
            ResourceTypeEntity resourceType = resourceTypeDAO.findById(patternResourceTypeId);
            if(resourceType==null){
                throw new NullPointerException("Cannot create resource for URI pattern. Resource type is not found");
            }

            ResourceEntity resource = provider.getResource();
            resource.setName(patternResourceTypeId+"_"+pattern.getPattern());
            resource.setResourceType(resourceType);
            resource.setResourceId(null);
            resource = resourceDao.add(resource);

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
        uriPatternDao.deleteById(patternId);
    }
}
