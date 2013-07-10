package org.openiam.idm.srvc.org.service;

import java.util.List;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;


public interface OrganizationTypeService {

	public OrganizationTypeEntity findById(final String id);
	
	public List<OrganizationTypeEntity> findBeans(final OrganizationTypeEntity entity, final int from, final int size);
	
	public int count(final OrganizationTypeEntity entity);
	
	public void save(final OrganizationTypeEntity entity);
	
	public void delete(final String id);
	
	public List<OrganizationTypeEntity> getChildren(final String id, final int from, final int size);
	
	public List<OrganizationTypeEntity> getParents(final String id, final int from, final int size);
	
	public List<OrganizationEntity> getOrganizations(final String id, final int from, final int size);
	
	public void addChild(final String id, final String childId);
	
	public void removeChild(final String id, final String childId);
}
