package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderServerEntity;

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
}
