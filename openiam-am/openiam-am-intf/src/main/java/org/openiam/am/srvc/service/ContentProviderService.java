package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.*;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.exception.BasicDataServiceException;

import java.util.List;

public interface ContentProviderService {
	
	public List<AuthLevelEntity> getAuthLevelList();
	
	public void deleteAuthLevelAttribute(final String id);
	
	public AuthLevelAttributeEntity getAuthLevelAttribute(final String id);
	
	public void saveAuthLevelAttibute(final AuthLevelAttributeEntity entity);
	
	public void deleteAuthLevelGrouping(final String id);
	
	public void saveAuthLevelGrouping(final AuthLevelGroupingEntity entity);
	
	public void validateDeleteAuthLevelGrouping(final String id) throws BasicDataServiceException;
	
	public void validateSaveAuthLevelGrouping(final AuthLevelGroupingEntity entity) throws BasicDataServiceException;
	
	public AuthLevelGroupingEntity getAuthLevelGrouping(final String id);

    List<AuthLevelGroupingEntity> getAuthLevelGroupingList();

    ContentProviderEntity getContentProvider(String providerId);

    Integer getNumOfContentProviders(ContentProviderEntity example);

    List<ContentProviderEntity> findBeans(ContentProviderEntity example, Integer from, Integer size);

    void saveContentProvider(ContentProviderEntity providerEntity);

    void deleteContentProvider(String providerId);

    List<ContentProviderServerEntity> getProviderServers(ContentProviderServerEntity example, Integer from, Integer size);

    Integer getNumOfProviderServers(ContentProviderServerEntity example);

    void deleteProviderServer(String contentProviderServerId);

    ContentProviderServerEntity saveProviderServer(ContentProviderServerEntity contentProviderServerEntity);

    Integer getNumOfUriPatterns(URIPatternEntity example);

    List<URIPatternEntity> getUriPatternsList(URIPatternEntity example, Integer from, Integer size);

    URIPatternEntity getURIPattern(String patternId);

    void saveURIPattern(URIPatternEntity uriPatternEntity);

    void deleteProviderPattern(String providerId);

    List<URIPatternMetaEntity> getMetaDataList(URIPatternMetaEntity example, Integer from, Integer size);

    Integer getNumOfMetaData(URIPatternMetaEntity example);

    public URIPatternMetaEntity getURIPatternMeta(String metaId);

    URIPatternMetaEntity saveMetaDataForPattern(URIPatternMetaEntity uriPatternMetaEntity);

    void deleteMetaDataForPattern(String metaId);

    List<URIPatternMetaTypeEntity> getAllMetaType();


    List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL);
    
    public List<URIPatternEntity> getURIPatternsForContentProviderMatchingPattern(final String contentProviderId, final String pattern);
}
