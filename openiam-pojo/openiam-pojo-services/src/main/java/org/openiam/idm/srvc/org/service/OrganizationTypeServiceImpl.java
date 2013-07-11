package org.openiam.idm.srvc.org.service;

import java.util.List;

import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrganizationTypeServiceImpl implements OrganizationTypeService {

	@Autowired
	private OrganizationTypeDAO organizationTypeDAO;

	@Override
	public OrganizationTypeEntity findById(String id) {
		return organizationTypeDAO.findById(id);
	}

	@Override
	public List<OrganizationTypeEntity> findBeans(OrganizationTypeEntity entity, int from, int size) {
		return organizationTypeDAO.getByExample(entity, from, size);
	}

	@Override
	public int count(OrganizationTypeEntity entity) {
		return organizationTypeDAO.count(entity);
	}

	@Override
	public void save(OrganizationTypeEntity type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<OrganizationTypeEntity> getChildren(String id, int from,
			int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrganizationTypeEntity> getParents(String id, int from, int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OrganizationEntity> getOrganizations(String id, int from,
			int size) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addChild(String id, String childId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeChild(String id, String childId) {
		// TODO Auto-generated method stub
		
	}
}
