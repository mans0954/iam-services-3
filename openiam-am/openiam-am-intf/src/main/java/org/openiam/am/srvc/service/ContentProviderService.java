package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.AuthLevelEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;

import java.util.List;

public interface ContentProviderService {

    List<AuthLevelEntity> getAuthLevelList();

    ContentProviderEntity getContentProvider(String providerId);

    Integer getNumOfContentProviders(ContentProviderEntity example);

    List<ContentProviderEntity> findBeans(ContentProviderEntity example, Integer from, Integer size);

    ContentProviderEntity saveContentProvider(ContentProviderEntity providerEntity);
}
