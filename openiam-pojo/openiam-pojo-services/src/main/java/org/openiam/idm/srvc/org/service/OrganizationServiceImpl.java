package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.OrganizationDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Org2OrgXref;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService, InitializingBean,Sweepable {
    private static final Log log = LogFactory.getLog(OrganizationServiceImpl.class);
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
    
    @Autowired
    private MetadataElementDAO metadataElementDAO;
    
	@Value("${org.openiam.resource.admin.resource.type.id}")
	private String adminResourceTypeId;
	
	@Autowired
    private ResourceTypeDAO resourceTypeDao;

    private Map<String, Set<String>> organizationTree;



    @Value("${org.openiam.delegation.filter.organization}")
    private String organizationTypeId;
    @Value("${org.openiam.delegation.filter.division}")
    private String divisionTypeId;
    @Value("${org.openiam.delegation.filter.department}")
    private String departmentTypeId;

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
        final OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setName(name);
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
    public void save(final OrganizationEntity entity, final String requestorId) {
    	
    	if(entity.getOrganizationType() != null) {
    		entity.setOrganizationType(orgTypeDAO.findById(entity.getOrganizationType().getId()));
    	}
    	
        if (StringUtils.isNotBlank(entity.getId())) {
            final OrganizationEntity dbOrg = orgDao.findById(entity.getId());
            if (dbOrg != null) {
            	mergeAttributes(entity, dbOrg);
                entity.setChildOrganizations(dbOrg.getChildOrganizations());
                entity.setParentOrganizations(dbOrg.getParentOrganizations());
                entity.setUsers(dbOrg.getUsers());
                entity.setAdminResource(dbOrg.getAdminResource());
                if(entity.getAdminResource() == null) {
                	entity.setAdminResource(getNewAdminResource(entity, requestorId));
                }
                entity.setApproverAssociations(dbOrg.getApproverAssociations());
            }
        } else {
        	entity.setAdminResource(getNewAdminResource(entity, requestorId));
            orgDao.save(entity);
            entity.addApproverAssociation(createDefaultApproverAssociations(entity, requestorId));
        }
        
        orgDao.merge(entity);
    }
    
    private ResourceEntity getNewAdminResource(final OrganizationEntity entity, final String requestorId) {
		final ResourceEntity adminResource = new ResourceEntity();
		adminResource.setName(String.format("ORG_ADMIN_%s_%s", entity.getName(), RandomStringUtils.randomAlphanumeric(2)));
		adminResource.setResourceType(resourceTypeDao.findById(adminResourceTypeId));
		adminResource.addUser(userDAO.findById(requestorId));
		return adminResource;
	}
    
    private ApproverAssociationEntity createDefaultApproverAssociations(final OrganizationEntity entity, final String requestorId) {
		final ApproverAssociationEntity association = new ApproverAssociationEntity();
		association.setAssociationEntityId(entity.getId());
		association.setAssociationType(AssociationType.ORGANIZATION);
		association.setApproverLevel(Integer.valueOf(0));
		association.setApproverEntityId(requestorId);
		association.setApproverEntityType(AssociationType.USER);
		return association;
	}
    
    private void mergeAttributes(final OrganizationEntity bean, final OrganizationEntity dbObject) {
		
		final Set<OrganizationAttributeEntity> renewedSet = new HashSet<OrganizationAttributeEntity>();
		
		final Set<OrganizationAttributeEntity> beanProps = (bean.getAttributes() != null) ? bean.getAttributes() : new HashSet<OrganizationAttributeEntity>();
		final Set<OrganizationAttributeEntity> dbProps = (dbObject.getAttributes() != null) ? dbObject.getAttributes() : new HashSet<OrganizationAttributeEntity>();
			
		/* update */
		for(final Iterator<OrganizationAttributeEntity> dbIt = dbProps.iterator(); dbIt.hasNext();) {
			final OrganizationAttributeEntity dbProp = dbIt.next();
			for(final Iterator<OrganizationAttributeEntity> it = beanProps.iterator(); it.hasNext();) {
				final OrganizationAttributeEntity beanProp = it.next();
				if(StringUtils.equals(dbProp.getId(), beanProp.getId())) {
					setMetadataTypeOnOrgAttribute(dbProp);
					dbProp.setName(beanProp.getName());
					dbProp.setValue(beanProp.getValue());
					renewedSet.add(dbProp);
					break;
				}
			}
		}
		
		/* add */
		for(final Iterator<OrganizationAttributeEntity> it = beanProps.iterator(); it.hasNext();) {
			boolean contains = false;
			final OrganizationAttributeEntity beanProp = it.next();
			for(final Iterator<OrganizationAttributeEntity> dbIt = dbProps.iterator(); dbIt.hasNext();) {
				final OrganizationAttributeEntity dbProp = dbIt.next();
				if(StringUtils.equals(dbProp.getId(), beanProp.getId())) {
					contains = true;
				}
			}
			
			if(!contains) {
				beanProp.setOrganization(bean);
				setMetadataTypeOnOrgAttribute(beanProp);
				//dbProps.add(beanProp);
				renewedSet.add(beanProp);
			}
		}
		
		bean.setAttributes(renewedSet);
	}
    
    private void setMetadataTypeOnOrgAttribute(final OrganizationAttributeEntity bean) {
    	if(bean.getElement() != null && bean.getElement().getId() != null) {
    		bean.setElement(metadataElementDAO.findById(bean.getElement().getId()));
		} else {
			bean.setElement(null);
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

    @Override
    public Set<String> getDelegationFilter(String requesterId, String organizationTypeId) {
        Set<String> filterData = null;
        if (StringUtils.isNotBlank(requesterId)) {
            Map<String, UserAttribute> requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            filterData = getDelegationFilter(requesterAttributes, organizationTypeId);

        }

        return filterData;
    }

    @Override
    public Set<String> getDelegationFilter(Map<String, UserAttribute> attrMap, String organizationTypeId) {
        Set<String> filterData = new HashSet<String>();
        if(attrMap!=null && !attrMap.isEmpty()){
            boolean isUseOrgInhFlag = DelegationFilterHelper.isUseOrgInhFilterSet(attrMap);

            if (StringUtils.isNotBlank(organizationTypeId)) {
                if(organizationTypeId.equals(this.organizationTypeId)){
                    this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag);
                } else if(organizationTypeId.equals(this.divisionTypeId)){
                    filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag);
                } else if(organizationTypeId.equals(this.departmentTypeId)){
                    filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag);
                } else {
                    filterData = getFullOrgFilterList(attrMap, isUseOrgInhFlag);
                }
            } else {
                filterData = getFullOrgFilterList(attrMap, isUseOrgInhFlag);
            }
        }
        return filterData;
    }

    private Set<String> getFullOrgFilterList(Map<String, UserAttribute> attrMap, boolean isUseOrgInhFlag){
        Set<String> filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag);
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag));
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag));
        return filterData;
    }

	@Override
	public Organization getOrganizationDTO(String orgId) {
		return organizationDozerConverter.convertToDTO(getOrganization(orgId), true);
	}

	@Override
	@Transactional
	public void validateOrg2OrgAddition(String parentId, String memberId)
			throws BasicDataServiceException {
		final OrganizationEntity parent = orgDao.findById(parentId);
		final OrganizationEntity child = orgDao.findById(memberId);
		if (parent == null || child == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if (parent.hasChildOrganization(memberId)) {
            throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
        }

        if (causesCircularDependency(parent, child, new HashSet<OrganizationEntity>())) {
            throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
        }

        if (parentId.equals(memberId)) {
            throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
        }
	}
	
	private boolean causesCircularDependency(final OrganizationEntity parent, final OrganizationEntity child, final Set<OrganizationEntity> visitedSet) {
        boolean retval = false;
        if (parent != null && child != null) {
            if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentOrganizations())) {
                    for (final OrganizationEntity entity : parent.getParentOrganizations()) {
                        retval = entity.getId().equals(child.getId());
                        if (retval) {
                            break;
                        }
                        causesCircularDependency(parent, entity, visitedSet);
                    }
                }
            }
        }
        return retval;
    }

    @Override
    @Transactional
    public void sweep() {
        final StopWatch sw = new StopWatch();
        sw.start();
        final Map<String, Set<String>> tempOrgTreeMap = getAllOrgMap();

        synchronized(this) {
            organizationTree = tempOrgTreeMap;
        }
        sw.stop();
        log.debug(String.format("Done creating orgs trees. Took: %s ms", sw.getTime()));
    }

    private Map<String, Set<String>> getAllOrgMap() {
        List<Org2OrgXref> xrefList = orgDao.getOrgToOrgXrefList();

        final Map<String, Set<String>> parentOrg2ChildOrgMap = new HashMap<String, Set<String>>();
        final Map<String, String> child2ParentOrgMap = new HashMap<String, String>();

        for(final Org2OrgXref xref : xrefList) {
            final String orgId = xref.getOrganizationId();
            final String memberOrgId = xref.getMemberOrganizationId();

            if(!parentOrg2ChildOrgMap.containsKey(orgId)) {
                parentOrg2ChildOrgMap.put(orgId, new HashSet<String>());
            }

            child2ParentOrgMap.put(memberOrgId, orgId);
            parentOrg2ChildOrgMap.get(orgId).add(memberOrgId);
        }

        return parentOrg2ChildOrgMap;
    }

    private Set<String> getOrgTreeFlatList(List<String> rootElementsIdList, boolean isUseOrgInhFlag){
        List<String> result = new ArrayList<String>();
        if(isUseOrgInhFlag){
            if(CollectionUtils.isNotEmpty(rootElementsIdList)){
                for (String rootElementId : rootElementsIdList){
                    result.addAll(getOrgTreeFlatList(rootElementId));
                }
            }
        } else {
            result = rootElementsIdList;
        }
        return new HashSet<String>(result);
    }

    private List<String> getOrgTreeFlatList(String rootId){
        List<String> result = new ArrayList<String>();
        if(StringUtils.isNotBlank(rootId)){
            result.add(rootId);
            for(int i=0; i<result.size();i++){
                String curElem = result.get(i);
                if(this.organizationTree.containsKey(curElem)){
                    result.addAll(this.organizationTree.get(curElem));
                }
            }
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        sweep();
    }
}
