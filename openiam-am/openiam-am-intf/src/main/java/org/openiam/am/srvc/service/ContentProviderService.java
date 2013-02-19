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

    List<ContentProviderServerEntity> getServersForProvider(String providerId, Integer from, Integer size);

    Integer getNumOfServersForProvider(String providerId);

    void deleteProviderServer(String contentProviderServerId);

    ContentProviderServerEntity saveProviderServer(ContentProviderServerEntity contentProviderServerEntity);

    Integer getNumOfUriPatternsForProvider(String providerId);

    List<URIPatternEntity> getUriPatternsForProvider(String providerId, Integer from, Integer size);

    URIPatternEntity getURIPattern(String patternId);

    URIPatternEntity saveURIPattern(URIPatternEntity uriPatternEntity);

    void deleteProviderPattern(String providerId);

    List<URIPatternMetaEntity> getMetaDataForPattern(String patternId, Integer from, Integer size);

    Integer getNumOfMetaDataForPattern(String patternId);

    public URIPatternMetaEntity getURIPatternMeta(String metaId);

    URIPatternMetaEntity saveMetaDataForPattern(URIPatternMetaEntity uriPatternMetaEntity);

    void deleteMetaDataForPattern(String metaId);
}
