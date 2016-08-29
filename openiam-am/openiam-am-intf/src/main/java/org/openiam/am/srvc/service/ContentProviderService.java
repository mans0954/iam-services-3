package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.*;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.exception.BasicDataServiceException;

import java.util.List;
import java.util.Set;

public interface ContentProviderService {
	
	List<AuthLevelEntity> getAuthLevelList();
	
	void deleteAuthLevelAttribute(final String id);
	
	AuthLevelAttributeEntity getAuthLevelAttribute(final String id);
	
	void saveAuthLevelAttibute(final AuthLevelAttributeEntity entity);
	
	void deleteAuthLevelGrouping(final String id);
	
	void saveAuthLevelGrouping(final AuthLevelGroupingEntity entity);
	
	void validateDeleteAuthLevelGrouping(final String id) throws BasicDataServiceException;
	
	void validateSaveAuthLevelGrouping(final AuthLevelGroupingEntity entity) throws BasicDataServiceException;
	
	AuthLevelGroupingEntity getAuthLevelGrouping(final String id);

    List<AuthLevelGroupingEntity> getAuthLevelGroupingList();

    ContentProviderEntity getContentProvider(String providerId);

    int getNumOfContentProviders(ContentProviderSearchBean cpsb);

    List<ContentProvider> findBeans(ContentProviderSearchBean cpsb, Integer from, Integer size);

    void saveContentProvider(ContentProviderEntity providerEntity);

    void deleteContentProvider(String providerId);

    int getNumOfUriPatterns(URIPatternSearchBean searchBean);

    List<URIPatternEntity> getUriPatternsList(URIPatternSearchBean searchBean, int from, int size);

    URIPatternEntity getURIPattern(String patternId);

    void saveURIPattern(URIPatternEntity uriPatternEntity);

    void deleteProviderPattern(String providerId);

    List<URIPatternMetaTypeEntity> getAllMetaType();


    List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL);
    
    List<URIPatternEntity> getURIPatternsForContentProviderMatchingPattern(final String contentProviderId, final String pattern);
    
    Set<URIPatternEntity> createDefaultURIPatterns(String providerId);
    
    void setupApplication(final ContentProviderEntity provider);
}
