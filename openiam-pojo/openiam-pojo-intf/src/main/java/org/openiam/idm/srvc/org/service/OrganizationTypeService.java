package org.openiam.idm.srvc.org.service;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface OrganizationTypeService {

	public OrganizationTypeEntity findByName(final String name);
	
	public OrganizationTypeEntity findById(final String id);
	
	public List<OrganizationTypeEntity> findBeans(final OrganizationTypeSearchBean searchBean, final int from, final int size);
	
	public int count(final OrganizationTypeSearchBean searchBean);
	
	public void save(final OrganizationTypeEntity entity);
	
	public void delete(final String id);

	public void addChild(final String id, final String childId);
	
	public void removeChild(final String id, final String childId);

    public List<OrganizationTypeEntity> getAllowedParents(String organizationTypeId, String requesterId);

    public Set<String> getAllowedParentsIds(String organizationTypeId, String requesterId);

    public Set<String> getAllowedParentsIds(String organizationTypeId, Map<String, UserAttribute> requesterAttributes);

    public List<OrganizationTypeEntity> findAllowedChildrenByDelegationFilter(String requesterId);

    public Set<String> findAllowedChildrenByDelegationFilter(Map<String, UserAttribute> userAttributeMap);
	
	public void validateOrgType2OrgTypeAddition(String parentId, String memberId) throws BasicDataServiceException;

    public void fireUpdateOrgTypeMap();
}
