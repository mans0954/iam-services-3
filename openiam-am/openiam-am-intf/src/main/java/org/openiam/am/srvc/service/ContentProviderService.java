package org.openiam.am.srvc.service;

import org.openiam.am.srvc.domain.*;
import org.openiam.am.srvc.dto.*;
import org.openiam.am.srvc.searchbean.ContentProviderSearchBean;
import org.openiam.am.srvc.searchbean.URIPatternSearchBean;
import org.openiam.exception.BasicDataServiceException;

import java.util.List;
import java.util.Set;

public interface ContentProviderService {
	
	List<AuthLevel> getAuthLevelList();
	
	void deleteAuthLevelAttribute(final String id) throws BasicDataServiceException;
	
	AuthLevelAttribute getAuthLevelAttribute(final String id);
	
	String saveAuthLevelAttibute(final AuthLevelAttribute entity) throws BasicDataServiceException;
	
	void deleteAuthLevelGrouping(final String id) throws BasicDataServiceException;
	
	String saveAuthLevelGrouping(final AuthLevelGrouping entity) throws BasicDataServiceException;
	
	void validateDeleteAuthLevelGrouping(final String id) throws BasicDataServiceException;
	
	void validateSaveAuthLevelGrouping(final AuthLevelGroupingEntity entity) throws BasicDataServiceException;
	
	AuthLevelGrouping getAuthLevelGrouping(final String id);

    List<AuthLevelGrouping> getAuthLevelGroupingList();

    ContentProvider getContentProvider(String providerId);

    int getNumOfContentProviders(ContentProviderSearchBean cpsb);

    List<ContentProvider> findBeans(ContentProviderSearchBean cpsb, int from, int size);

    String saveContentProvider(ContentProvider provider) throws BasicDataServiceException;

    void deleteContentProvider(String providerId) throws BasicDataServiceException;

    int getNumOfUriPatterns(URIPatternSearchBean searchBean);

    List<URIPattern> getUriPatternsList(URIPatternSearchBean searchBean, int from, int size);

    URIPattern getURIPattern(String patternId);

    String saveURIPattern(URIPattern uriPattern) throws BasicDataServiceException;

    void deleteProviderPattern(String providerId) throws BasicDataServiceException;

    List<URIPatternMetaType> getAllMetaType();


    List<ContentProviderEntity> getProviderByDomainPattern(String domainPattern, Boolean isSSL);
    
    List<URIPatternEntity> getURIPatternsForContentProviderMatchingPattern(final String contentProviderId, final String pattern);
    
    Set<URIPatternEntity> createDefaultURIPatterns(String providerId) throws BasicDataServiceException;
    
    String setupApplication(final ContentProvider provider) throws BasicDataServiceException;
}
