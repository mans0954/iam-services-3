package org.openiam.idm.srvc.org.service;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface OrganizationTypeService {

	OrganizationTypeEntity findByName(final String name);
	
	OrganizationTypeEntity findById(final String id);
	
	List<OrganizationTypeEntity> findBeans(final OrganizationTypeSearchBean searchBean, final int from, final int size);
	
	int count(final OrganizationTypeSearchBean searchBean);
	
	void save(final OrganizationTypeEntity entity);
	
	void delete(final String id);

	void addChild(final String id, final String childId);
	
	void removeChild(final String id, final String childId);

    List<OrganizationTypeEntity> getAllowedParents(String organizationTypeId, String requesterId);

    Set<String> getAllowedParentsIds(String organizationTypeId, String requesterId);

    Set<String> getAllowedParentsIds(String organizationTypeId, Map<String, UserAttribute> requesterAttributes);

    List<OrganizationTypeEntity> findAllowedChildrenByDelegationFilter(String requesterId);

    Set<String> findAllowedChildrenByDelegationFilter(Map<String, UserAttribute> userAttributeMap);
	
	void validateOrgType2OrgTypeAddition(String parentId, String memberId) throws BasicDataServiceException;

    void fireUpdateOrgTypeMap();
}
