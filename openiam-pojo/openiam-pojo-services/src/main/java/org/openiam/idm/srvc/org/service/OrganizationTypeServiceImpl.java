package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.org.domain.OrgType2OrgTypeXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service
@Transactional
public class OrganizationTypeServiceImpl implements OrganizationTypeService, InitializingBean, Sweepable {
    private static final Log log = LogFactory.getLog(OrganizationTypeServiceImpl.class);
	@Autowired
	private OrganizationTypeDAO organizationTypeDAO;

    @Autowired
    private UserDataService userDataService;

    @Value("${org.openiam.delegation.filter.organization}")
    private String organizationTypeId;
    @Value("${org.openiam.delegation.filter.division}")
    private String divisionTypeId;
    @Value("${org.openiam.delegation.filter.department}")
    private String departmentTypeId;

    private Map<String, Set<String>> parent2childOrgTypeCached;
    private Map<String, Set<String>> child2parentOrgTypeCached;
    
    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;

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

	@Override
	public void validateOrgType2OrgTypeAddition(String parentId, String memberId)
			throws BasicDataServiceException {
		final OrganizationTypeEntity parent = organizationTypeDAO.findById(parentId);
		final OrganizationTypeEntity child = organizationTypeDAO.findById(memberId);
		
		if(parent == null || child == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		
		if(causesCircularDependency(parent, child, new HashSet<OrganizationTypeEntity>())) {
			throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
		}
		
		if(parent.hasChildType(child.getId())) {
			throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
		}
		
		/*
		if(StringUtils.equals(parentId, memberId)) {
			throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
		}
		*/
	}
	
	private boolean causesCircularDependency(final OrganizationTypeEntity parent, final OrganizationTypeEntity child, final Set<OrganizationTypeEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if(!visitedSet.contains(child)) {
				visitedSet.add(child);
				if(CollectionUtils.isNotEmpty(parent.getParentTypes())) {
					for(final OrganizationTypeEntity entity : parent.getParentTypes()) {
						retval = entity.getId().equals(child.getId());
						if(retval) {
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
    public List<OrganizationTypeEntity> getAllowedParents(String organizationTypeId, String requesterId){
        OrganizationTypeSearchBean searchBean = new OrganizationTypeSearchBean();
        searchBean.setKeySet(getAllowedParentsIds(organizationTypeId, requesterId));
        return findBeans(searchBean, 0, Integer.MAX_VALUE);
    }

    @Override
    public Set<String> getAllowedParentsIds(String organizationTypeId, String requesterId){
//        Set<String> result = new HashSet<>();
//        List<OrgType2OrgTypeXref> xrefList = organizationTypeDAO.getOrgTypeToOrgTypeXrefList();
//
//        for(final OrgType2OrgTypeXref xref : xrefList) {
//            final String orgTypeId = xref.getOrganizationTypeId();
//            final String memberOrgTypeId = xref.getMemberOrganizationTypeId();
//
//            if(StringUtils.equals(memberOrgTypeId, organizationTypeId)) {
//                result.add(orgTypeId);
//            }
//        }
        Map<String, UserAttribute> requesterAttributes = null;
        if (StringUtils.isNotBlank(requesterId)) {
            requesterAttributes = userDataService.getUserAttributesDto(requesterId);
        }
        return getAllowedParentsIds(organizationTypeId, requesterAttributes);
    }
    @Override
    public Set<String> getAllowedParentsIds(String organizationTypeId, Map<String, UserAttribute> requesterAttributes){
        Set<String> result = new HashSet<>();
        Set<String> allowedParentTypesIds = new HashSet<>();
        if(requesterAttributes!=null){
            result = this.findAllowedChildrenByDelegationFilter(requesterAttributes);

            boolean isOrgFilterSet = DelegationFilterHelper.isOrgFilterSet(requesterAttributes);
            boolean isDivFilterSet = DelegationFilterHelper.isDivisionFilterSet(requesterAttributes);
            boolean isDepFilterSet = DelegationFilterHelper.isDeptFilterSet(requesterAttributes);
            boolean isUseOrgInhFilterSet = DelegationFilterHelper.isUseOrgInhFilterSet(requesterAttributes);
            if(isOrgFilterSet){
                result.add(organizationTypeId);
            }
            if(isDivFilterSet
               || (isOrgFilterSet && isUseOrgInhFilterSet)){
                result.add(divisionTypeId);
            }
            if(isDepFilterSet
               || (isDivFilterSet && isUseOrgInhFilterSet)
               || (isOrgFilterSet && isUseOrgInhFilterSet)){
                result.add(departmentTypeId);
            }
        } else {
            result = new HashSet<>(organizationTypeDAO.findAllIds());
        }
        List<OrgType2OrgTypeXrefEntity> xrefList = organizationTypeDAO.getOrgTypeToOrgTypeXrefList();

        for(final OrgType2OrgTypeXrefEntity xref : xrefList) {
            final String orgTypeId = xref.getId().getOrganizationTypeId();
            final String memberOrgTypeId = xref.getId().getMemberOrganizationTypeId();

            if(StringUtils.equals(memberOrgTypeId, organizationTypeId)) {
                allowedParentTypesIds.add(orgTypeId);
            }
        }

        result.retainAll(allowedParentTypesIds);
        return result;
    }
    @Override
    public List<OrganizationTypeEntity> findAllowedChildrenByDelegationFilter(String requesterId){
        Set<String> allowedTypeIds = new HashSet<String>();
        if(StringUtils.isNotBlank(requesterId)){
            Map<String, UserAttribute> userAttributeMap = userDataService.getUserAttributesDto(requesterId);
            allowedTypeIds = findAllowedChildrenByDelegationFilter(userAttributeMap);
        }
        OrganizationTypeSearchBean searchBean = new OrganizationTypeSearchBean();
        searchBean.setKeySet(allowedTypeIds);
        return findBeans(searchBean, 0, Integer.MAX_VALUE);
    }

    @Override
    public Set<String> findAllowedChildrenByDelegationFilter(Map<String, UserAttribute> userAttributeMap){
        Set<String> allowedTypeIds = new HashSet<String>();
        if(userAttributeMap!=null && !userAttributeMap.isEmpty()){
            if(DelegationFilterHelper.isOrgFilterSet(userAttributeMap)){
                allowedTypeIds.addAll(getChildOrgTypeTreeAsFlatList(organizationTypeId, DelegationFilterHelper.isUseOrgInhFilterSet(userAttributeMap)));
            }
            if(DelegationFilterHelper.isDivisionFilterSet(userAttributeMap)){
                allowedTypeIds.addAll(getChildOrgTypeTreeAsFlatList(divisionTypeId, DelegationFilterHelper.isUseOrgInhFilterSet(userAttributeMap)));
            }
            if(DelegationFilterHelper.isDeptFilterSet(userAttributeMap)){
                allowedTypeIds.addAll(getChildOrgTypeTreeAsFlatList(departmentTypeId, DelegationFilterHelper.isUseOrgInhFilterSet(userAttributeMap)));
            }
        }

        if(allowedTypeIds==null || allowedTypeIds.isEmpty()){
            allowedTypeIds = new HashSet<>(organizationTypeDAO.findAllIds());
        }
        return allowedTypeIds;
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

    @Override
    @Transactional
    public void sweep() {
        final StopWatch sw = new StopWatch();
        sw.start();
        final OrgTypeResponse orgTypeResponse = getAllOrgTypeMap();

        synchronized(this) {
            this.parent2childOrgTypeCached = orgTypeResponse.getParentOrg2ChildOrgTypeMap();
            this.child2parentOrgTypeCached = orgTypeResponse.getChild2ParentOrgMap();
        }
        sw.stop();
        log.debug(String.format("Done creating orgs trees. Took: %s ms", sw.getTime()));
    }

    private OrgTypeResponse getAllOrgTypeMap() {
        List<OrgType2OrgTypeXrefEntity> xrefList = organizationTypeDAO.getOrgTypeToOrgTypeXrefList();

        final Map<String, Set<String>> parentOrg2ChildOrgTypeMap = new HashMap<String, Set<String>>();
        final Map<String, Set<String>> child2ParentOrgMap = new HashMap<String, Set<String>>();

        for(final OrgType2OrgTypeXrefEntity xref : xrefList) {
            final String orgTypeId = xref.getId().getOrganizationTypeId();
            final String memberOrgTypeId = xref.getId().getMemberOrganizationTypeId();

            if(!parentOrg2ChildOrgTypeMap.containsKey(orgTypeId)) {
                parentOrg2ChildOrgTypeMap.put(orgTypeId, new HashSet<String>());
            }

            if(!child2ParentOrgMap.containsKey(memberOrgTypeId)) {
                child2ParentOrgMap.put(memberOrgTypeId, new HashSet<String>());
            }

            child2ParentOrgMap.get(memberOrgTypeId).add(orgTypeId);
            parentOrg2ChildOrgTypeMap.get(orgTypeId).add(memberOrgTypeId);
        }
        return new OrgTypeResponse(parentOrg2ChildOrgTypeMap, child2ParentOrgMap);
    }


    private Set<String> getChildOrgTypeTreeAsFlatList(String rootId, boolean isUseOrgInhFlag){
        Set<String> result = new HashSet<String>();
        if(this.parent2childOrgTypeCached.containsKey(rootId)){
            Set<String> childrenTypes = this.parent2childOrgTypeCached.get(rootId);
            if(isUseOrgInhFlag){
                if(CollectionUtils.isNotEmpty(childrenTypes)){
                    for (String rootElementId : childrenTypes){
                        result.addAll(getChildOrgTypeTreeAsFlatList(rootElementId, isUseOrgInhFlag));
                    }
                }
            } else {
                result = childrenTypes;
            }

        }
        return result;
    }

    private class OrgTypeResponse{
        private Map<String, Set<String>> parentOrg2ChildOrgTypeMap;
        private Map<String, Set<String>> child2ParentOrgMap;

        private OrgTypeResponse(Map<String, Set<String>> parentOrg2ChildOrgTypeMap, Map<String, Set<String>> child2ParentOrgMap) {
            this.parentOrg2ChildOrgTypeMap = parentOrg2ChildOrgTypeMap;
            this.child2ParentOrgMap = child2ParentOrgMap;
        }

        public Map<String, Set<String>> getParentOrg2ChildOrgTypeMap() {
            return parentOrg2ChildOrgTypeMap;
        }

        public void setParentOrg2ChildOrgTypeMap(Map<String, Set<String>> parentOrg2ChildOrgTypeMap) {
            this.parentOrg2ChildOrgTypeMap = parentOrg2ChildOrgTypeMap;
        }

        public Map<String, Set<String>> getChild2ParentOrgMap() {
            return child2ParentOrgMap;
        }

        public void setChild2ParentOrgMap(Map<String, Set<String>> child2ParentOrgMap) {
            this.child2ParentOrgMap = child2ParentOrgMap;
        }
    }
}
