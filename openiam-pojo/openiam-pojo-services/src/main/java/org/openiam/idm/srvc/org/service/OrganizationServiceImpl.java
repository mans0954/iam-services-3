package org.openiam.idm.srvc.org.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	
	@Autowired
    private OrganizationDAO orgDao;
    
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OrganizationAttributeDAO orgAttrDao;
    
    @Autowired
    private UserAffiliationDAO userAffiliationDAO;
    
    @Override
	public List<OrganizationEntity> getTopLevelOrganizations() {
		return orgDao.findRootOrganizations();
	}

	@Override
	public OrganizationEntity getOrganization(String orgId) {
		return orgDao.findById(orgId);
	}

	@Override
	public List<OrganizationEntity> getOrganizationsForUser(String userId) {
		return userAffiliationDAO.findOrgAffiliationsByUser(userId);
	}

	@Override
	public void addUserToOrg(String orgId, String userId) {
		final OrganizationEntity organization = orgDao.findById(orgId);
		final UserEntity user = userDAO.findById(userId);
		
		final UserAffiliationEntity entity = new UserAffiliationEntity();
		entity.setOrganization(organization);
		entity.setUser(user);
		
		userAffiliationDAO.save(entity);
	}

	@Override
	public void removeUserFromOrg(String orgId, String userId) {
		final UserAffiliationEntity entity = userAffiliationDAO.getRecord(userId, orgId);
		if(entity != null) {
			userAffiliationDAO.delete(entity);
		}
	}

	@Override
	public List<OrganizationEntity> getAllOrganizations() {
		return orgDao.findAllOrganization();
	}

	@Override
	public List<OrganizationEntity> findBeans(final OrganizationEntity searchBean, int from, int size) {
		return orgDao.getByExample(searchBean, from, size);
	}

	@Override
	public int count(final OrganizationEntity searchBean) {
		return orgDao.count(searchBean);
	}

	@Override
	public void removeAttribute(String attributeId) {
		final OrganizationAttributeEntity entity = orgAttrDao.findById(attributeId);
		if(entity != null) {
			orgAttrDao.delete(entity);
		}
	}

	@Override
	public void save(final OrganizationEntity entity) {
		if(StringUtils.isNotBlank(entity.getOrgId())) {
			final OrganizationEntity dbOrg = orgDao.findById(entity.getOrgId());
			if(dbOrg != null) {
				entity.setAttributes(dbOrg.getAttributes());
				entity.setChildOrganizations(dbOrg.getChildOrganizations());
				entity.setParentOrganizations(dbOrg.getParentOrganizations());
				orgDao.merge(entity);
			}
		} else {
			orgDao.save(entity);
		}
	}

	@Override
	public void save(OrganizationAttributeEntity attribute) {
		if(StringUtils.isNotBlank(attribute.getAttrId())) {
			orgAttrDao.update(attribute);
		} else {
			orgAttrDao.save(attribute);
		}
	}

	@Override
	public void removeChildOrganization(String organizationId,
			String childOrganizationId) {
		final OrganizationEntity parent = orgDao.findById(organizationId);
		final OrganizationEntity child = orgDao.findById(childOrganizationId);
		if(parent != null && child != null) {
			parent.removeChildOrganization(childOrganizationId);
			orgDao.update(parent);
		}
	}

	@Override
	public void addChildOrganization(String organizationId,
			String childOrganizationId) {
		final OrganizationEntity parent = orgDao.findById(organizationId);
		final OrganizationEntity child = orgDao.findById(childOrganizationId);
		if(parent != null && child != null) {
			parent.addChildOrganization(child);
			orgDao.update(parent);
		}
	}

	@Override
	public void deleteOrganization(String orgId) {
		final OrganizationEntity entity = orgDao.findById(orgId);
		if(entity != null) {
			userDAO.disassociateUsersFromOrganization(orgId);
			userAffiliationDAO.deleteByOrganizationId(orgId);
			orgAttrDao.deleteByOrganizationId(orgId);
			orgDao.delete(entity);
		}
	}

	@Override
	public List<OrganizationEntity> getParentOrganizations(
			String organizationId, int from, int size) {
		return orgDao.getParentOrganizations(organizationId, from, size);
	}

	@Override
	public List<OrganizationEntity> getChildOrganizations(
			String organizationId, int from, int size) {
		return orgDao.getChildOrganizations(organizationId, from, size);
	}

	@Override
	public int getNumOfParentOrganizations(String organizationId) {
		return orgDao.getNumOfParentOrganizations(organizationId);
	}

	@Override
	public int getNumOfChildOrganizations(String organizationId) {
		return orgDao.getNumOfChildOrganizations(organizationId);
	}

	@Override
	public UserAffiliationEntity getAffiliation(String userId,
			String organizationId) {
		return userAffiliationDAO.getRecord(userId, organizationId);
	}
}
