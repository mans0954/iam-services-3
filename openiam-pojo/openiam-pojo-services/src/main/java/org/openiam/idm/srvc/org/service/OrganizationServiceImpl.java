package org.openiam.idm.srvc.org.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.UserAffiliationEntity;
import org.openiam.idm.srvc.org.dto.OrgClassificationEnum;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sun.xml.internal.ws.util.xml.MetadataDocument;

@Service
@Transactional
public class OrganizationServiceImpl implements OrganizationService {

	@Autowired
	private MetadataElementDAO metadataDAO;
	
    @Autowired
    private OrganizationDAO orgDao;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private OrganizationAttributeDAO orgAttrDao;

    @Autowired
    private UserAffiliationDAO userAffiliationDAO;

    @Autowired
    private UserDataService userDataService;

    @Override
    public OrganizationEntity getOrganization(String orgId) {
        return getOrganization(orgId, null);
    }

    @Override
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
    public List<OrganizationEntity> getOrganizationsForUser(String userId) {
        return this.getOrganizationsForUser(userId, null);
    }

    @Override
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId) {
        return userAffiliationDAO.findOrgAffiliationsByUser(userId, getDelegationFilter(requesterId, null));
    }

    @Override
    public List<OrganizationEntity> getAllOrganizations(String requesterId) {
        return this.findBeans(new OrganizationSearchBean(), requesterId, -1, -1);
    }

    @Override
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId, searchBean.getClassification());
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return new ArrayList<OrganizationEntity>(0);
        }
        return orgDao.getByExample(searchBean, from, size);
    }

    @Override
    public List<OrganizationEntity> getParentOrganizations(String orgId, String parentClassification, String requesterId, int from, int size) {
        return orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId, parentClassification), from, size);
    }

    @Override
    public List<OrganizationEntity> getChildOrganizations(String orgId, String childClassification, String requesterId, int from, int size) {
        return orgDao.getChildOrganizations(orgId, getDelegationFilter(requesterId, childClassification), from, size);
    }

    @Override
    public int count(final OrganizationSearchBean searchBean, String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId, searchBean.getClassification());
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return 0;
        }

        return orgDao.count(searchBean);
    }

    @Override
    public int getNumOfParentOrganizations(String orgId, String parentClassification, String requesterId) {
        return orgDao.getNumOfParentOrganizations(orgId, getDelegationFilter(requesterId, parentClassification));
    }

    @Override
    public int getNumOfChildOrganizations(String orgId, String childClassification, String requesterId) {
        return orgDao.getNumOfChildOrganizations(orgId, getDelegationFilter(requesterId, childClassification));
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
        if (entity != null) {
            userAffiliationDAO.delete(entity);
        }
    }

    @Override
    public void removeAttribute(String attributeId) {
        final OrganizationAttributeEntity entity = orgAttrDao.findById(attributeId);
        if (entity != null) {
            orgAttrDao.delete(entity);
        }
    }

    @Override
    public void save(final OrganizationEntity entity) {
        if (StringUtils.isNotBlank(entity.getId())) {
            final OrganizationEntity dbOrg = orgDao.findById(entity.getId());
            if (dbOrg != null) {
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
            userDAO.disassociateUsersFromOrganization(orgId);
            userAffiliationDAO.deleteByOrganizationId(orgId);
            //orgAttrDao.deleteByOrganizationId(orgId);
            orgDao.delete(entity);
        }
    }

    @Override
    public UserAffiliationEntity getAffiliation(String userId, String organizationId) {
        return userAffiliationDAO.getRecord(userId, organizationId);
    }

    private Set<String> getDelegationFilter(String requesterId, String orgClassification) {
        OrgClassificationEnum classification = null;
        Set<String> filterData = null;
        if (StringUtils.isNotBlank(requesterId)) {
            Map<String, UserAttribute> requesterAttributes = userDataService.getUserAttributesDto(requesterId);

            if (orgClassification != null) {
                classification = OrgClassificationEnum.valueOf(orgClassification);
                switch (classification) {
                case DIVISION:
                    filterData = new HashSet<String>(DelegationFilterHelper.getDivisionFilterFromString(requesterAttributes));
                    break;
                case DEPARTMENT:
                    filterData = new HashSet<String>(DelegationFilterHelper.getDeptFilterFromString(requesterAttributes));
                    break;
                case ORGANIZATION:
                    filterData = new HashSet<String>(DelegationFilterHelper.getOrgIdFilterFromString(requesterAttributes));
                    break;
                default:
                    filterData = getFullOrgFilterList(requesterAttributes);
                    break;
                }
            } else {
                filterData = getFullOrgFilterList(requesterAttributes);
            }
        }

        return filterData;
    }

    private Set<String> getFullOrgFilterList(Map<String, UserAttribute> attrMap) {
        List<String> filterData = DelegationFilterHelper.getOrgIdFilterFromString(attrMap);
        filterData.addAll(DelegationFilterHelper.getDeptFilterFromString(attrMap));
        filterData.addAll(DelegationFilterHelper.getDivisionFilterFromString(attrMap));
        return new HashSet<String>(filterData);
    }
}
