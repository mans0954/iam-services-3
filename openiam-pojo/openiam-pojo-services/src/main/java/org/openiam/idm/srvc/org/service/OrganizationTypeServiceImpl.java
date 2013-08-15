package org.openiam.idm.srvc.org.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
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
	public OrganizationTypeEntity findByName(String name) {
		final OrganizationTypeEntity entity = new OrganizationTypeEntity();
		entity.setName(name);
		final List<OrganizationTypeEntity> entityList = organizationTypeDAO.getByExample(entity);
		return (CollectionUtils.isNotEmpty(entityList)) ? entityList.get(0) : null;
	}

	@Override
	public List<OrganizationTypeEntity> findBeans(final OrganizationTypeSearchBean searchBean, int from, int size) {
		return organizationTypeDAO.getByExample(searchBean, from, size);
	}

	@Override
	public int count(final OrganizationTypeSearchBean searchBean) {
		return organizationTypeDAO.count(searchBean);
	}

	@Override
	public void save(OrganizationTypeEntity type) {
		if(type != null) {
			if(StringUtils.isNotBlank(type.getId())) {
				final OrganizationTypeEntity entity = organizationTypeDAO.findById(type.getId());
				if(entity != null) {
					type.setChildTypes(entity.getChildTypes());
					type.setParentTypes(entity.getParentTypes());
					type.setOrganizations(entity.getOrganizations());
					organizationTypeDAO.merge(type);
				}
			} else {
				type.setChildTypes(null);
				type.setParentTypes(null);
				type.setOrganizations(null);
				organizationTypeDAO.save(type);
			}
		}
	}

	@Override
	public void delete(String id) {
		if(StringUtils.isNotBlank(id)) {
			final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
			if(entity != null) {
				organizationTypeDAO.delete(entity);
			}
		}
	}

	@Override
	public void addChild(String id, String childId) {
		if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(childId)) {
			final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
			if(entity != null) {
				final OrganizationTypeEntity child = organizationTypeDAO.findById(childId);
				if(child != null) {
					entity.addChildType(child);
					organizationTypeDAO.update(entity);
				}
			}
		}
	}

	@Override
	public void removeChild(String id, String childId) {
		if(StringUtils.isNotBlank(id) && StringUtils.isNotBlank(childId)) {
			final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
			if(entity != null) {
				entity.removeChildType(childId);
				organizationTypeDAO.update(entity);
			}
		}
	}
}
