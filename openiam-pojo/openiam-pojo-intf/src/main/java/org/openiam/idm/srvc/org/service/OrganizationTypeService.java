package org.openiam.idm.srvc.org.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;


public interface OrganizationTypeService {

	public OrganizationTypeEntity findByName(final String name);
	
	public OrganizationTypeEntity findById(final String id);
	
	public List<OrganizationTypeEntity> findBeans(final OrganizationTypeSearchBean searchBean, final int from, final int size);
	
	public int count(final OrganizationTypeSearchBean searchBean);
	
	public void save(final OrganizationTypeEntity entity);
	
	public void delete(final String id);

	public void addChild(final String id, final String childId);
	
	public void removeChild(final String id, final String childId);
	
	public void validateOrgType2OrgTypeAddition(String parentId, String memberId) throws BasicDataServiceException;
}
