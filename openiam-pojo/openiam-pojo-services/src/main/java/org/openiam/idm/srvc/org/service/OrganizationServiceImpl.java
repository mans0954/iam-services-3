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
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.org.domain.Org2OrgXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationAttributeEntity;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service("organizationService")
@Transactional
public class OrganizationServiceImpl implements OrganizationService, InitializingBean, Sweepable {
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
    private OrganizationTypeService organizationTypeService;
    
    @Autowired
    private OrganizationDozerConverter organizationDozerConverter;
    
    @Autowired
    private MetadataElementDAO metadataElementDAO;
    
	@Value("${org.openiam.resource.admin.resource.type.id}")
	private String adminResourceTypeId;
	
	@Autowired
    private ResourceTypeDAO resourceTypeDao;
	
    @Autowired
    private MetadataTypeDAO typeDAO;
    
    @Autowired
    private GroupDAO groupDAO;

    private Map<String, Set<String>> organizationTree;


    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;


    @Value("${org.openiam.organization.type.id}")
    private String organizationTypeId;
    @Value("${org.openiam.division.type.id}")
    private String divisionTypeId;
    @Value("${org.openiam.department.type.id}")
    private String departmentTypeId;

    @Override
    @LocalizedServiceGet
    public OrganizationEntity getOrganizationLocalized(String orgId, final LanguageEntity langauge) {
        return getOrganizationLocalized(orgId, null, langauge);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public OrganizationEntity getOrganizationLocalized(String orgId, String requesterId, final LanguageEntity langauge) {
        if (DelegationFilterHelper.isAllowed(orgId, getDelegationFilter(requesterId, null))) {
            return orgDao.findById(orgId);
        }
        return null;
    }

    @Override
    @LocalizedServiceGet
    public OrganizationEntity getOrganizationByName(final String name, String requesterId, final LanguageEntity langauge) {
        final OrganizationSearchBean searchBean = new OrganizationSearchBean();
        searchBean.setName(name);
        final List<OrganizationEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1, null);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }
    
    @Override
    public int getNumOfOrganizationsForUser(final String userId, final String requesterId) {
    	return orgDao.getNumOfOrganizationsForUser(userId, getDelegationFilter(requesterId, null));
    }

    @Override
    @LocalizedServiceGet
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size, final LanguageEntity langauge) {
    	return orgDao.getOrganizationsForUser(userId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    @LocalizedServiceGet
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, int from, int size, final LanguageEntity langauge) {
        Set<String> filter = getDelegationFilter(requesterId, null);
        if (StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if (!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)) {
            return new ArrayList<OrganizationEntity>(0);
        }
        return orgDao.getByExample(searchBean, from, size);
    }

    @Override
    @LocalizedServiceGet
    public List<OrganizationEntity> getParentOrganizations(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
        return orgDao.getParentOrganizations(orgId, getDelegationFilter(requesterId, null), from, size);
    }

    @Override
    @LocalizedServiceGet
    public List<OrganizationEntity> getChildOrganizations(String orgId, String requesterId, int from, int size, final LanguageEntity langauge) {
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
    	
    	if(entity.getType() != null && StringUtils.isNotBlank(entity.getType().getId())) {
    		entity.setType(typeDAO.findById(entity.getType().getId()));
        } else {
        	entity.setType(null);
        }
    	
        if (StringUtils.isNotBlank(entity.getId())) {
            final OrganizationEntity dbOrg = orgDao.findById(entity.getId());
            if (dbOrg != null) {
            	mergeAttributes(entity, dbOrg);
                mergeParents(entity, dbOrg);
                entity.setChildOrganizations(dbOrg.getChildOrganizations());
                entity.setParentOrganizations(dbOrg.getParentOrganizations());
                entity.setUsers(dbOrg.getUsers());
                entity.setAdminResource(dbOrg.getAdminResource());
                if(entity.getAdminResource() == null) {
                	entity.setAdminResource(getNewAdminResource(entity, requestorId));
                }
                entity.getAdminResource().setCoorelatedName(entity.getName());
                entity.setApproverAssociations(dbOrg.getApproverAssociations());
                entity.setLstUpdate(Calendar.getInstance().getTime());
                entity.setLstUpdatedBy(requestorId);
            }
        } else {
        	entity.setAdminResource(getNewAdminResource(entity, requestorId));
            mergeParents(entity, null);
            entity.setCreateDate(Calendar.getInstance().getTime());
            entity.setCreatedBy(requestorId);
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
		adminResource.setCoorelatedName(entity.getName());
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

    private void mergeParents(final OrganizationEntity bean, final OrganizationEntity dbObject) {
        final Set<OrganizationEntity> renewedSet = new HashSet<OrganizationEntity>();

        final Set<OrganizationEntity> beanParents = (bean.getParentOrganizations() != null) ? bean.getParentOrganizations() : new HashSet<OrganizationEntity>();
        final Set<OrganizationEntity> dbParents = (dbObject!=null && dbObject.getParentOrganizations() != null) ? dbObject.getParentOrganizations() : new HashSet<OrganizationEntity>();

        /* add */
        for(final Iterator<OrganizationEntity> it = beanParents.iterator(); it.hasNext();) {
            boolean contains = false;
            final OrganizationEntity beanParent = it.next();
            for(final Iterator<OrganizationEntity> dbIt = dbParents.iterator(); dbIt.hasNext();) {
                final OrganizationEntity dbParent = dbIt.next();
                if(StringUtils.equals(dbParent.getId(), beanParent.getId())) {
                    contains = true;
                    renewedSet.add(dbParent);
                }
            }

            if(!contains) {
                final OrganizationEntity dbParOrg = orgDao.findById(beanParent.getId());
//                dbParOrg.getChildOrganizations().add(bean);
                renewedSet.add(dbParOrg);
            }
        }
        bean.setParentOrganizations(renewedSet);
    }

    private void mergeAttributes(final OrganizationEntity bean, final OrganizationEntity dbObject) {
		
    	Set<OrganizationAttributeEntity> beanProps = (bean.getAttributes() != null) ? bean.getAttributes() : new HashSet<OrganizationAttributeEntity>();
        Set<OrganizationAttributeEntity> dbProps = (dbObject.getAttributes() != null) ? new HashSet<OrganizationAttributeEntity>(dbObject.getAttributes()) : new HashSet<OrganizationAttributeEntity>();

        /* update */
        Iterator<OrganizationAttributeEntity> dbIteroator = dbProps.iterator();
        while(dbIteroator.hasNext()) {
        	final OrganizationAttributeEntity dbProp = dbIteroator.next();
        	
        	boolean contains = false;
            for (final OrganizationAttributeEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    dbProp.setValue(beanProp.getValue());
                    dbProp.setElement(getEntity(beanProp.getElement()));
                    dbProp.setName(beanProp.getName());
                    dbProp.setIsMultivalued(beanProp.getIsMultivalued());
                    dbProp.setValues(beanProp.getValues());
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if(!contains) {
            	dbIteroator.remove();
            }
        }

        /* add */
        final Set<OrganizationAttributeEntity> toAdd = new HashSet<>();
        for (final OrganizationAttributeEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while(dbIteroator.hasNext()) {
            	final OrganizationAttributeEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                beanProp.setOrganization(bean);
                beanProp.setElement(getEntity(beanProp.getElement()));
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);
        
        bean.setAttributes(dbProps);
	}
    
    private MetadataElementEntity getEntity(final MetadataElementEntity bean) {
    	if(bean != null && StringUtils.isNotBlank(bean.getId())) {
    		return metadataElementDAO.findById(bean.getId());
    	} else {
    		return null;
    	}
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
        	final GroupEntity example = new GroupEntity();
        	example.setCompany(entity);
        	final List<GroupEntity> groups = groupDAO.getByExample(example);
        	if(groups != null) {
        		for(final GroupEntity group : groups) {
        			group.setCompany(null);
        			groupDAO.update(group);
        		}
        	}
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

            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag));
            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag));
            filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag));

//            if (StringUtils.isNotBlank(organizationTypeId)) {
//                if(organizationTypeId.equals(this.organizationTypeId)){
//                    filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag);
//                } else if(organizationTypeId.equals(this.divisionTypeId)){
//                    filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag);
//                } else if(organizationTypeId.equals(this.departmentTypeId)){
//                    filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag);
//                } else {
//                    filterData = getFullOrgFilterList(attrMap, isUseOrgInhFlag);
//                }
//            } else {
//                filterData = getFullOrgFilterList(attrMap, isUseOrgInhFlag);
//            }
        }
        return filterData;
    }

    @Override
    @LocalizedServiceGet
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId, final LanguageEntity langauge){
        Set<String> filterData = null;
        Set<String> allowedOrgTypes = null;
        Map<String, UserAttribute> requesterAttributes = null;
        if (StringUtils.isNotBlank(requesterId)) {
            requesterAttributes = userDataService.getUserAttributesDto(requesterId);
            filterData = getDelegationFilter(requesterAttributes, organizationTypeId);
//            allowedOrgTypes = organizationTypeService.findAllowedChildrenByDelegationFilter(requesterAttributes);
//
//            boolean isOrgFilterSet = DelegationFilterHelper.isOrgFilterSet(requesterAttributes);
//            boolean isDivFilterSet = DelegationFilterHelper.isDivisionFilterSet(requesterAttributes);
//            boolean isDepFilterSet = DelegationFilterHelper.isDeptFilterSet(requesterAttributes);
//            boolean isUseOrgInhFilterSet = DelegationFilterHelper.isUseOrgInhFilterSet(requesterAttributes);
//            if(isOrgFilterSet){
//                allowedOrgTypes.add(organizationTypeId);
//            }
//            if(isDivFilterSet
//                    || (isOrgFilterSet && isUseOrgInhFilterSet)){
//                allowedOrgTypes.add(divisionTypeId);
//            }
//            if(isDepFilterSet
//                    || (isDivFilterSet && isUseOrgInhFilterSet)
//                    || (isOrgFilterSet && isUseOrgInhFilterSet)){
//                allowedOrgTypes.add(departmentTypeId);
//            }
        }
        allowedOrgTypes = organizationTypeService.getAllowedParentsIds(orgTypeId, requesterAttributes);
//        allowedOrgTypes.retainAll(allowedParentTypesIds);

        return orgDao.findAllByTypesAndIds(allowedOrgTypes, filterData);
    }

    private Set<String> getFullOrgFilterList(Map<String, UserAttribute> attrMap, boolean isUseOrgInhFlag){
        Set<String> filterData = this.getOrgTreeFlatList(DelegationFilterHelper.getOrgIdFilterFromString(attrMap), isUseOrgInhFlag);
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDeptFilterFromString(attrMap), isUseOrgInhFlag));
        filterData.addAll(this.getOrgTreeFlatList(DelegationFilterHelper.getDivisionFilterFromString(attrMap), isUseOrgInhFlag));
        return filterData;
    }

	@Override
	@LocalizedServiceGet
	public Organization getOrganizationDTO(String orgId, final LanguageEntity langauge) {
		return organizationDozerConverter.convertToDTO(getOrganizationLocalized(orgId, langauge), true);
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

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue, final LanguageEntity langauge) {
    	final OrganizationSearchBean searchBean = new OrganizationSearchBean();
    	searchBean.addAttribute(attrName, attrValue);
        return orgDao.getByExample(searchBean);
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
        List<Org2OrgXrefEntity> xrefList = orgDao.getOrgToOrgXrefList();

        final Map<String, Set<String>> parentOrg2ChildOrgMap = new HashMap<String, Set<String>>();
        final Map<String, String> child2ParentOrgMap = new HashMap<String, String>();

        for(final Org2OrgXrefEntity xref : xrefList) {
            final String orgId = xref.getId().getOrganizationId();
            final String memberOrgId = xref.getId().getMemberOrganizationId();

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
    	transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus status) {
				sweep();
	    		return null;
			}
    		
		});
    }


    @Deprecated
    public Organization getOrganizationDTO(final String orgId){
        return this.getOrganizationDTO(orgId, getDefaultLanguage());
    }
    @Deprecated
    public OrganizationEntity getOrganization(String orgId){
        return this.getOrganization(orgId, null);
    }
    @Deprecated
    public OrganizationEntity getOrganization(final String orgId, String requesterId){
        return this.getOrganizationLocalized(orgId, requesterId, getDefaultLanguage());
    }
    @Deprecated
    public OrganizationEntity getOrganizationByName(final String name, String requesterId){
        return this.getOrganizationByName(name, requesterId, getDefaultLanguage());
    }
    @Deprecated
    public List<OrganizationEntity> getOrganizationsForUser(String userId, String requesterId, final int from, final int size){
        return this.getOrganizationsForUser(userId, requesterId, from, size, getDefaultLanguage());
    }
    @Deprecated
    public List<OrganizationEntity> getParentOrganizations(final String orgId, String requesterId, final int from, final int size){
        return this.getParentOrganizations(orgId, requesterId, from, size, getDefaultLanguage());
    }
    @Deprecated
    public List<OrganizationEntity> getChildOrganizations(final String orgId, String requesterId, final int from, final int size){
        return this.getChildOrganizations(orgId, requesterId, from, size, getDefaultLanguage());
    }
    @Deprecated
    public List<OrganizationEntity> findBeans(final OrganizationSearchBean searchBean, String requesterId, final int from, final int size){
        return this.findBeans(searchBean, requesterId, from, size, getDefaultLanguage());
    }
    @Deprecated
    public List<OrganizationEntity> getAllowedParentOrganizationsForType(final String orgTypeId, String requesterId){
        return this.getAllowedParentOrganizationsForType(orgTypeId, requesterId, getDefaultLanguage());
    }
    @Deprecated
    public List<OrganizationEntity> findOrganizationsByAttributeValue(final String attrName, String attrValue){
        return this.findOrganizationsByAttributeValue(attrName, attrValue, getDefaultLanguage());
    }

    private LanguageEntity getDefaultLanguage(){
        LanguageEntity lang = new LanguageEntity();
        lang.setId("1");
        return lang;
    }
}
