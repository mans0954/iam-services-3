package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.*;

import java.util.List;

public interface ContentProviderService {

    List<AuthLevelEntity> getAuthLevelList();

    ContentProviderEntity getContentProvider(String providerId);

    Integer getNumOfContentProviders(ContentProviderEntity example);

    List<ContentProviderEntity> findBeans(ContentProviderEntity example, Integer from, Integer size);

    ContentProviderEntity saveContentProvider(ContentProviderEntity providerEntity);

    void deleteContentProvider(String providerId);

    List<ContentProviderServerEntity> getProviderServers(ContentProviderServerEntity example, Integer from, Integer size);

    Integer getNumOfProviderServers(ContentProviderServerEntity example);

    void deleteProviderServer(String contentProviderServerId);

    ContentProviderServerEntity saveProviderServer(ContentProviderServerEntity contentProviderServerEntity);

    Integer getNumOfUriPatterns(URIPatternEntity example);

    List<URIPatternEntity> getUriPatternsList(URIPatternEntity example, Integer from, Integer size);

    URIPatternEntity getURIPattern(String patternId);

    URIPatternEntity saveURIPattern(URIPatternEntity uriPatternEntity);

    void deleteProviderPattern(String providerId);

    List<URIPatternMetaEntity> getMetaDataList(URIPatternMetaEntity example, Integer from, Integer size);

    Integer getNumOfMetaData(URIPatternMetaEntity example);

    public URIPatternMetaEntity getURIPatternMeta(String metaId);

    URIPatternMetaEntity saveMetaDataForPattern(URIPatternMetaEntity uriPatternMetaEntity);

    void deleteMetaDataForPattern(String metaId);

    List<URIPatternMetaTypeEntity> getAllMetaType();


    List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, String contextPath, Boolean isSSL);
}
