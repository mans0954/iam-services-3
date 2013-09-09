package org.openiam.idm.srvc.org.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private OrganizationTypeDAO orgTypeDAO;
	
	@Autowired
	private MetadataElementDAO metadataDAO;
	
    @Autowired
    private OrganizationDAO orgDao;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OrganizationAttributeDAO orgAttrDao;

    @Autowired
    private UserDataService userDataService;
    
    @Autowired
    private OrganizationDozerConverter organizationDozerConverter;

    @Override
    public OrganizationEntity getOrganization(String orgId) {
        return getOrganization(orgId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationEntity getOrganization(String orgId, String requesterId) {
        if (DelegationFilterHelper.isAllowed(orgId, getDelegationFilter(requesterId, null))) {
            return orgDao.findById(orgId);
        }
        return null;
    }

    @Override
    public OrganizationEntity getOrganizationByName(final String name, String requesterId) {
        OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setOrganizationName(name);
        final List<OrganizationEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }
    
    @Override
    public int getNumOfOrganizationsForUser(final String userId, final String requesterId) {
    	return orgDao.getNumOfOrganizationsForUser(userId, getDelegationFilter(requesterId, null));
    }

    @Override
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size) {
    	return orgDao.getOrganizationsForUser(userId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    public List<OrganizationEntity> getAllOrganizations(String requesterId) {
        return this.findBeans(new OrganizationSearchBean(), requesterId, -1, -1);
    }

    @Override
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId, searchBean.getOrganizationTypeId());
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return new ArrayList<OrganizationEntity>(0);
        }
        return orgDao.getByExample(searchBean, from, size);
    }

    @Override
    public List<OrganizationEntity> getParentOrganizations(String orgId, String requesterId, int from, int size) {
        return orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    public List<OrganizationEntity> getChildOrganizations(String orgId, String requesterId, int from, int size) {
        return orgDao.getChildOrganizations(orgId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    public int count(final OrganizationSearchBean searchBean, String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId, searchBean.getOrganizationTypeId());
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return 0;
        }

        return orgDao.count(searchBean);
    }

    @Override
    public int getNumOfParentOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfParentOrganizations(orgId, getDelegationFilter(requesterId, null));
    }

    @Override
    public int getNumOfChildOrganizations(String orgId, String requesterId) {
        return orgDao.getNumOfChildOrganizations(orgId, getDelegationFilter(requesterId, null));
    }

    @Override
    @Transactional
    public void addUserToOrg(String orgId, String userId) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        user.getAffiliations().add(organization);
    }

    @Override
    @Transactional
    public void removeUserFromOrg(String orgId, String userId) {
        final OrganizationEntity organization = orgDao.findById(orgId);
        final UserEntity user = userDAO.findById(userId);
        user.getAffiliations().remove(organization);
    }

    @Override
    public void removeAttribute(String attributeId) {
        final OrganizationAttributeEntity entity = orgAttrDao.findById(attributeId);
        if (entity != null) {
            orgAttrDao.delete(entity);
        }
    }

    @Override
    @Transactional
    public void save(final OrganizationEntity entity) {
    	
    	if(entity.getOrganizationType() != null) {
    		entity.setOrganizationType(orgTypeDAO.findById(entity.getOrganizationType().getId()));
    	}
    	
        if (StringUtils.isNotBlank(entity.getId())) {
            final OrganizationEntity dbOrg = orgDao.findById(entity.getId());
            if (dbOrg != null) {
                entity.setAttributes(dbOrg.getAttributes());
                entity.setChildOrganizations(dbOrg.getChildOrganizations());
                entity.setParentOrganizations(dbOrg.getParentOrganizations());
                entity.setUsers(dbOrg.getUsers());
                orgDao.merge(entity);
            }
        } else {
            orgDao.save(entity);
        }
    }

    @Override
    public void save(OrganizationAttributeEntity attribute) {
    	attribute.setElement(metadataDAO.findById(attribute.getElement().getId()));
    	attribute.setOrganization(orgDao.findById(attribute.getOrganization().getId()));
    	
        if (StringUtils.isNotBlank(attribute.getId())) {
            orgAttrDao.merge(attribute);
        } else {
            orgAttrDao.save(attribute);
        }
    }

    @Override
    public void removeChildOrganization(String organizationId, String childOrganizationId) {
        final OrganizationEntity parent = orgDao.findById(organizationId);
        final OrganizationEntity child = orgDao.findById(childOrganizationId);
        if (parent != null && child != null) {
            parent.removeChildOrganization(childOrganizationId);
            orgDao.update(parent);
        }
    }

    @Override
    public void addChildOrganization(String organizationId, String childOrganizationId) {
        final OrganizationEntity parent = orgDao.findById(organizationId);
        final OrganizationEntity child = orgDao.findById(childOrganizationId);
        if (parent != null && child != null) {
            parent.addChildOrganization(child);
            orgDao.update(parent);
        }
    }

    @Override
    public void deleteOrganization(String orgId) {
        final OrganizationEntity entity = orgDao.findById(orgId);
        if (entity != null) {
            orgDao.delete(entity);
        }
    }

    private Set<String> getDelegationFilter(String requesterId, String organizationTypeId) {
        Set<String> filterData = null;
        if (StringUtils.isNotBlank(requesterId)) {
            Map<String, UserAttribute> requesterAttributes = userDataService.getUserAttributesDto(requesterId);

            if (StringUtils.isNotBlank(organizationTypeId)) {
            	filterData = new HashSet<String>(DelegationFilterHelper.getOrgIdFilterFromString(requesterAttributes));
                //classification = OrgClassificationEnum.valueOf(orgClassification);
            	/*
                switch (classification) {
	                case ORGANIZATION:
	                    filterData = new HashSet<String>(DelegationFilterHelper.getOrgIdFilterFromString(requesterAttributes));
	                    break;
	                default:
	                    filterData = getFullOrgFilterList(requesterAttributes);
	                    break;
                }
                */
            } else {
                filterData = getFullOrgFilterList(requesterAttributes);
            }
        }

        return filterData;
    }

    private Set<String> getFullOrgFilterList(Map<String, UserAttribute> attrMap) {
        List<String> filterData = DelegationFilterHelper.getOrgIdFilterFromString(attrMap);
        return new HashSet<String>(filterData);
    }

	@Override
	public Organization getOrganizationDTO(String orgId) {
		return organizationDozerConverter.convertToDTO(getOrganization(orgId), true);
	}
}
