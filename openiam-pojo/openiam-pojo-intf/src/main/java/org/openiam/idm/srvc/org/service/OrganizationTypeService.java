package org.openiam.idm.srvc.org.service;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface OrganizationTypeService {

	OrganizationTypeEntity findByName(final String name);
	
	OrganizationType findById(final String id, final Language language);
	
	List<OrganizationType> findBeans(final OrganizationTypeSearchBean searchBean, final int from, final int size, final Language language);
	
	int count(final OrganizationTypeSearchBean searchBean);
	
	String save(final OrganizationType entity) throws BasicDataServiceException;
	
	void delete(final String id) throws BasicDataServiceException;

	void addChild(final String id, final String childId) throws BasicDataServiceException;
	
	void removeChild(final String id, final String childId) throws BasicDataServiceException;

    List<OrganizationType> getAllowedParents(String organizationTypeId, final Language language);

    Set<String> getAllowedParentsIds(String organizationTypeId);

    Set<String> getAllowedParentsIds(String organizationTypeId, Map<String, UserAttribute> requesterAttributes);

    List<OrganizationType> findAllowedChildrenByDelegationFilter(String requesterId, final Language language);

    Set<String> findAllowedChildrenByDelegationFilter(Map<String, UserAttribute> userAttributeMap);
	
	void validateOrgType2OrgTypeAddition(String parentId, String memberId) throws BasicDataServiceException;

    void fireUpdateOrgTypeMap();
}
