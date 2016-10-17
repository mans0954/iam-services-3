package org.openiam.idm.srvc.org.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.SearchParam;
import org.openiam.dozer.converter.OrganizationTypeDozerBeanConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.org.domain.OrgType2OrgTypeXrefEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.thread.Sweepable;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

@Service
@Transactional
public class OrganizationTypeServiceImpl extends AbstractBaseService implements OrganizationTypeService, InitializingBean, Sweepable {
    private static final Log log = LogFactory.getLog(OrganizationTypeServiceImpl.class);
	@Autowired
	private OrganizationTypeDAO organizationTypeDAO;

    @Autowired
    private UserDataService userDataService;

    @Autowired
    private OrganizationTypeDozerBeanConverter dozerConverter;
    
    private Map<String, Set<String>> parent2childOrgTypeCached;
    private Map<String, Set<String>> child2parentOrgTypeCached;
    
    @Autowired
    @Qualifier("transactionTemplate")
    private TransactionTemplate transactionTemplate;

	@Override
    @Transactional(readOnly = true)
	public OrganizationType findById(String id, final Language language) {
        OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
        return (entity != null) ? dozerConverter.convertToDTO(entity, true) : null;
	}
	
	@Override
	public OrganizationTypeEntity findByName(String name) {
		final OrganizationTypeSearchBean sb = new OrganizationTypeSearchBean();
		sb.setNameToken(new SearchParam(name, MatchType.EXACT));
		final List<OrganizationTypeEntity> entityList = organizationTypeDAO.getByExample(sb);
		return (CollectionUtils.isNotEmpty(entityList)) ? entityList.get(0) : null;
	}

	@Override
    @LocalizedServiceGet
	public List<OrganizationType> findBeans(final OrganizationTypeSearchBean searchBean, int from, int size, final Language language) {
//        return organizationTypeDAO.getByExample(searchBean, from, size);
        return dozerConverter.convertToDTOList(organizationTypeDAO.getByExample(searchBean, from, size), (searchBean != null) ? searchBean.isDeepCopy() : false);
	}

    @Override
	public int count(final OrganizationTypeSearchBean searchBean) {
		return organizationTypeDAO.count(searchBean);
	}

	@Override
	public String save(OrganizationType typeDto) throws BasicDataServiceException{
        if(typeDto == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if(StringUtils.isBlank(typeDto.getName())) {
            throw new BasicDataServiceException(ResponseCode.NO_NAME);
        }

        final OrganizationTypeEntity duplicate = this.findByName(typeDto.getName());
        if(duplicate != null) {
            if(!StringUtils.equals(duplicate.getId(), typeDto.getId())) {
                throw new BasicDataServiceException(ResponseCode.NAME_TAKEN);
            }
        }

        final OrganizationTypeEntity entity = dozerConverter.convertToEntity(typeDto, true);
		if(entity != null) {
			if(StringUtils.isNotBlank(entity.getId())) {
				final OrganizationTypeEntity dbEntity = organizationTypeDAO.findById(entity.getId());
				if(dbEntity != null) {
                    entity.setChildTypes(dbEntity.getChildTypes());
                    entity.setParentTypes(dbEntity.getParentTypes());
                    entity.setOrganizations(dbEntity.getOrganizations());
					organizationTypeDAO.merge(entity);
				}
			} else {
                entity.setChildTypes(null);
                entity.setParentTypes(null);
                entity.setOrganizations(null);
				organizationTypeDAO.save(entity);
			}
		}
        return entity.getId();
	}

	@Override
	public void delete(String id) throws BasicDataServiceException {
        if(StringUtils.isBlank(id)) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
        if(entity == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }
        if(CollectionUtils.isNotEmpty(entity.getChildTypes())) {
            throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_CHILDREN_EXIST);
        }

        if(CollectionUtils.isNotEmpty(entity.getParentTypes())) {
            throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_PARENTS_EXIST);
        }

        if(CollectionUtils.isNotEmpty(entity.getOrganizations())) {
            throw new BasicDataServiceException(ResponseCode.ORGANIZATION_TYPE_TIED_TO_ORGANIZATION);
        }

        organizationTypeDAO.delete(entity);
	}

	@Override
	public void addChild(String id, String childId) throws BasicDataServiceException{
        if(StringUtils.isBlank(id) || StringUtils.isBlank(childId)) {
            throw new BasicDataServiceException(ResponseCode.MISSING_REQUIRED_ATTRIBUTE);
        }

        final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
        if(entity != null) {
            final OrganizationTypeEntity child = organizationTypeDAO.findById(childId);
            if(child != null) {
                entity.addChildType(child);
                organizationTypeDAO.update(entity);
            }
        }
	}

	@Override
	public void removeChild(String id, String childId) throws BasicDataServiceException{
        if(StringUtils.isBlank(id) || StringUtils.isBlank(childId)) {
            throw new BasicDataServiceException(ResponseCode.MISSING_REQUIRED_ATTRIBUTE);
        }

        final OrganizationTypeEntity entity = organizationTypeDAO.findById(id);
        if(entity != null) {
            entity.removeChildType(childId);
            organizationTypeDAO.update(entity);
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
    @LocalizedServiceGet
    public List<OrganizationType> getAllowedParents(String organizationTypeId, String requesterId, final Language language){
        OrganizationTypeSearchBean searchBean = new OrganizationTypeSearchBean();
        searchBean.setKeySet(getAllowedParentsIds(organizationTypeId, requesterId));
        return findBeans(searchBean, 0, Integer.MAX_VALUE,language);
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
                result.add(propertyValueSweeper.getString("org.openiam.division.type.id"));
            }
            if(isDepFilterSet
               || (isDivFilterSet && isUseOrgInhFilterSet)
               || (isOrgFilterSet && isUseOrgInhFilterSet)){
                result.add(propertyValueSweeper.getString("org.openiam.department.type.id"));
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
    @LocalizedServiceGet
    public List<OrganizationType> findAllowedChildrenByDelegationFilter(String requesterId, final Language language){
        Set<String> allowedTypeIds = new HashSet<String>();
        if(StringUtils.isNotBlank(requesterId)){
            Map<String, UserAttribute> userAttributeMap = userDataService.getUserAttributesDto(requesterId);
            allowedTypeIds = findAllowedChildrenByDelegationFilter(userAttributeMap);
        }
        OrganizationTypeSearchBean searchBean = new OrganizationTypeSearchBean();
        searchBean.setKeySet(allowedTypeIds);
        return findBeans(searchBean, 0, Integer.MAX_VALUE,language);
    }

    @Override
    public Set<String> findAllowedChildrenByDelegationFilter(Map<String, UserAttribute> userAttributeMap){
        Set<String> allowedTypeIds = new HashSet<String>();
        if(userAttributeMap!=null && !userAttributeMap.isEmpty()){
            if(DelegationFilterHelper.isOrgFilterSet(userAttributeMap)){
                allowedTypeIds.addAll(getChildOrgTypeTreeAsFlatList(propertyValueSweeper.getString("org.openiam.organization.type.id"), DelegationFilterHelper.isUseOrgInhFilterSet(userAttributeMap)));
            }
            if(DelegationFilterHelper.isDivisionFilterSet(userAttributeMap)){
                allowedTypeIds.addAll(getChildOrgTypeTreeAsFlatList(propertyValueSweeper.getString("org.openiam.division.type.id"), DelegationFilterHelper.isUseOrgInhFilterSet(userAttributeMap)));
            }
            if(DelegationFilterHelper.isDeptFilterSet(userAttributeMap)){
                allowedTypeIds.addAll(getChildOrgTypeTreeAsFlatList(propertyValueSweeper.getString("org.openiam.department.type.id"), DelegationFilterHelper.isUseOrgInhFilterSet(userAttributeMap)));
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
    @Scheduled(fixedRateString="${org.openiam.org.manager.threadsweep}", initialDelayString="${org.openiam.org.manager.threadsweep}")
    public void sweep() {
        final StopWatch sw = new StopWatch();
        sw.start();

        fireUpdateOrgTypeMap();
        sw.stop();
        log.debug(String.format("Done creating orgs trees. Took: %s ms", sw.getTime()));
    }

    @Transactional(readOnly = true)
    public void fireUpdateOrgTypeMap() {
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
        this.parent2childOrgTypeCached =parentOrg2ChildOrgTypeMap;
        this.child2parentOrgTypeCached = child2ParentOrgMap;
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
