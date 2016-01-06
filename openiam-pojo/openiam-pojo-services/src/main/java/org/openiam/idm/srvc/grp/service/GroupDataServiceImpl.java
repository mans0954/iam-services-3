package org.openiam.idm.srvc.grp.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.common.SetStringResponse;
import org.openiam.authmanager.service.AuthorizationManagerAdminService;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityDAO;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.domain.GroupToGroupMembershipXrefEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.dto.GroupOwner;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.service.RoleDAO;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <code>GroupDataServiceImpl</code> provides a service to manage groups as
 * well as related objects such as Users. Groups are stored in an hierarchical
 * relationship. A user belongs to one or more groups.<br>
 * Groups are often modeled after an organizations structure.
 * 
 * @author Suneet Shah
 * @version 2.0
 */

@Service("groupManager")
@Transactional
public class GroupDataServiceImpl implements GroupDataService {
    @Autowired
    protected SysConfiguration sysConfiguration;

	@Autowired
	private GroupDAO groupDao;

    @Autowired
    private RoleDAO roleDao;

    @Autowired
    private ResourceDAO resourceDao;
	
	@Autowired
	private GroupAttributeDAO groupAttrDao;

    @Autowired
    private UserDataService userDataService;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private GroupDozerConverter groupDozerConverter;

    @Autowired
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;
    
    @Autowired
    private ManagedSysDAO managedSysDAO;
	
	@Autowired
	private ResourceTypeDAO resourceTypeDAO;
	
	@Autowired
	private OrganizationDAO organizationDAO;

    @Autowired
    private MetadataTypeDAO typeDAO;

    @Autowired
    private MetadataElementDAO metadataElementDAO;

    @Autowired
    private IdentityDAO identityDAO;

    @Autowired
    protected LanguageDAO languageDAO;

    @Autowired
    protected AuditLogService auditLogService;
    
    @Autowired
    private AccessRightDAO accessRightDAO;

    @Autowired
    @Qualifier("authorizationManagerAdminService")
    private AuthorizationManagerAdminService authorizationManagerAdminService;
    
	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;

	private static final Log log = LogFactory.getLog(GroupDataServiceImpl.class);

	public GroupDataServiceImpl() {

	}

    protected LanguageEntity getDefaultLanguage() {
        return languageDAO.getDefaultLanguage();
    }


    @Override
    @LocalizedServiceGet
    public GroupEntity getGroupLocalize(String id, LanguageEntity languageEntity) {
        return getGroupLocalize(id, null, languageEntity);
    }

    @Override
    @LocalizedServiceGet
    public GroupEntity getGroupLocalize(String id, String requesterId, LanguageEntity language) {
        if(DelegationFilterHelper.isAllowed(id, getDelegationFilter(requesterId))){
            return groupDao.findById(id);
        }
        return null;
    }

    @Override
    @Deprecated
    public GroupEntity getGroup(final String id) {
        return getGroupLocalize(id, null, getDefaultLanguage());
    }

    @Override
    @Deprecated
	public GroupEntity getGroup(final String id, final String requesterId) {
        return getGroupLocalize(id, requesterId, getDefaultLanguage());
	}
    @Override
    @LocalizedServiceGet
    public GroupEntity getGroupByNameAndManagedSystem(final String groupName, final String managedSystemId, final String requesterId, final LanguageEntity language) {
        final GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.setName(groupName);
        searchBean.setManagedSysId(managedSystemId);
        
        /* can only ever return 1 due to DB constraint */
        final List<GroupEntity> foundList = findBeansLocalize(searchBean, requesterId, 0, Integer.MAX_VALUE, language);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }

    @Override
    @Deprecated
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, final  String requesterId, int from, int size) {
        return getGroupEntities(searchBean, requesterId,  from,  size);
    }

    private List<GroupEntity> getGroupEntities(GroupSearchBean searchBean, String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return new ArrayList<GroupEntity>(0);
        }
        return groupDao.getByExample(searchBean, from, size);
    }

    @Override
    @LocalizedServiceGet
    public List<GroupEntity> findBeansLocalize(final GroupSearchBean searchBean, final  String requesterId, int from, int size, final LanguageEntity language) {
        return getGroupEntities(searchBean, requesterId, from, size);
    }

    @Override
    public List<GroupEntity> findGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId,
                                                int from, int size, LanguageEntity languageEntity){
        List<GroupEntity> finalizedGroups = getGroupListForOwner(searchBean, requesterId, ownerId, getDefaultLanguage());

        if (from > -1 && size > -1) {
            if (finalizedGroups != null && finalizedGroups.size() >= from) {
                int to = from + size;
                if (to > finalizedGroups.size()) {
                    to = finalizedGroups.size();
                }
                finalizedGroups = new ArrayList<GroupEntity>(finalizedGroups.subList(from, to));
            }
        }
        return finalizedGroups;
    }


    @Override
    @Transactional(readOnly = true)
    public List<Group> getGroupsDtoForUser(String userId, String requesterId, int from, int size) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
        final List<GroupEntity> groupEntities = findBeansLocalize(sb, requesterId, from, size, null);
        return groupDozerConverter.convertToDTOList(groupEntities, false);
    }

    @Override
    public Set<String> getGroupIdList(){
        List<String> groupIds = groupDao.getAllIds();
        if(CollectionUtils.isNotEmpty(groupIds))
            return new HashSet<String>(groupIds);
        return Collections.EMPTY_SET;
    }

    @Override
    public int countBeans(final GroupSearchBean searchBean, final String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return 0;
        }
        return groupDao.count(searchBean);
    }

    @Override
    public int countGroupsForOwner(GroupSearchBean searchBean, String requesterId, String ownerId){
        List<GroupEntity> finalizedGroups = getGroupListForOwner(searchBean, requesterId, ownerId, getDefaultLanguage());
        return finalizedGroups.size();
    }

    private List<GroupEntity> getGroupListForOwner(GroupSearchBean searchBean, String requesterId, String ownerId, LanguageEntity languageEntity){
        List<GroupEntity> foundGroups = findBeansLocalize(searchBean, requesterId, -1, -1, languageEntity);
        List<GroupEntity> finalizedGroups = new ArrayList<>();

        Set<String> foundGroupsId = new HashSet<>();
        if(CollectionUtils.isNotEmpty(foundGroups)){
            for (GroupEntity grp: foundGroups){
                foundGroupsId.add(grp.getId());
            }
        }
        HashMap<String, SetStringResponse> ownersMap = authorizationManagerAdminService.getOwnerIdsForGroupSet(foundGroupsId, new Date());
        for (GroupEntity grp: foundGroups){
            SetStringResponse idsResp = ownersMap.get(grp.getId());
            if(idsResp!=null && CollectionUtils.isNotEmpty(idsResp.getSetString()) && idsResp.getSetString().contains(ownerId)){
                finalizedGroups.add(grp);
            }
        }
        return finalizedGroups;
    }

    @Override
    @Deprecated
    public List<Group> getCompiledGroupsForUser(final String userId) {
        return getCompiledGroupsForUserLocalize(userId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<Group> getCompiledGroupsForUserLocalize(final String userId, final LanguageEntity language) {
    	final GroupSearchBean sb = new GroupSearchBean();
    	sb.addUserId(userId);
        final List<GroupEntity> groupList = findBeansLocalize(sb, null, 0, Integer.MAX_VALUE, null);
        final Set<GroupEntity> visitedSet = new HashSet<GroupEntity>();
        if(CollectionUtils.isNotEmpty(groupList)) {
            for(final GroupEntity group : groupList) {
                visitGroups(group, visitedSet);
            }
        }
        return groupDozerConverter.convertToDTOList(new ArrayList<GroupEntity>(visitedSet), true);
    }

	public boolean isUserInCompiledGroupList(String groupId, String userId) {
		if(groupId != null) {
			final List<Group> userGroupList =  getCompiledGroupsForUser(userId);
			if(CollectionUtils.isNotEmpty(userGroupList)) {
				for (Group grp : userGroupList) {
					if (grp.getId().equalsIgnoreCase(groupId)) {
						return true;
					}
				}
			}
		}
		return false;
		
	}

	@Override
	public void saveGroup(final GroupEntity group, final String requestorId) throws BasicDataServiceException {
        saveGroup(group, null, requestorId);
	}
    @Override
    public void saveGroup(final GroupEntity group, final GroupOwner groupOwner, final String requestorId) throws BasicDataServiceException{
        if(group != null && entityValidator.isValid(group)) {

            if(group.getManagedSystem() != null && StringUtils.isNotBlank(group.getManagedSystem().getId())) {
                final ManagedSysEntity mngSys = managedSysDAO.findById(group.getManagedSystem().getId());
                if(mngSys != null) {
                	group.setManagedSystem(managedSysDAO.findById(group.getManagedSystem().getId()));
                	if(mngSys.getResource() != null){
                		group.addResource(mngSys.getResource(), accessRightDAO.findAll(), null, null);
                	}
                }

            } else {
                group.setManagedSystem(null);
            }

            if(CollectionUtils.isNotEmpty(group.getOrganizations())) {
            	group.getOrganizations().forEach(xref -> {
            		xref.setMemberEntity(group);
            		xref.setEntity(organizationDAO.findById(xref.getEntity().getId()));
            		final Set<String> rightIds = (xref.getRights() != null) ? xref.getRights().stream().map(e -> e.getId()).collect(Collectors.toSet()) : null;
            		final List<AccessRightEntity> accessRightList = accessRightDAO.findByIds(rightIds);
            		xref.setRights((accessRightList != null) ? new HashSet<AccessRightEntity>(accessRightList) : null);
            	});
            } else {
                group.setOrganizations(null);
            }

            if(group.getType() != null && StringUtils.isNotBlank(group.getType().getId())) {
                group.setType(typeDAO.findById(group.getType().getId()));
            } else {
                group.setType(null);
            }

            if(group.getClassification() != null && StringUtils.isNotBlank(group.getClassification().getId())) {
                group.setClassification(typeDAO.findById(group.getClassification().getId()));
            } else {
                group.setClassification(null);
            }
            if(group.getAdGroupType() != null && StringUtils.isNotBlank(group.getAdGroupType().getId())) {
                group.setAdGroupType(typeDAO.findById(group.getAdGroupType().getId()));
            } else {
                group.setAdGroupType(null);
            }
            if(group.getAdGroupScope() != null && StringUtils.isNotBlank(group.getAdGroupScope().getId())) {
                group.setAdGroupScope(typeDAO.findById(group.getAdGroupScope().getId()));
            } else {
                group.setAdGroupScope(null);
            }
            if(group.getRisk() != null && StringUtils.isNotBlank(group.getRisk().getId())) {
                group.setRisk(typeDAO.findById(group.getRisk().getId()));
            } else {
                group.setRisk(null);
            }

            if(StringUtils.isNotBlank(group.getId())) {
                final GroupEntity dbGroup = groupDao.findById(group.getId());
                if(dbGroup != null) {
                	
                    group.setApproverAssociations(dbGroup.getApproverAssociations());

                    mergeAttribute(group, dbGroup, requestorId);
                    group.setChildGroups(dbGroup.getChildGroups());
                    group.setParentGroups(dbGroup.getParentGroups());
                    group.setResources(dbGroup.getResources());
                    group.setRoles(dbGroup.getRoles());
                    group.setUsers(dbGroup.getUsers());
                    group.setLastUpdatedBy(requestorId);
                    group.setLastUpdate(Calendar.getInstance().getTime());
                    
                    /* hibernate fails you just null out the PersistentSet.  As of Hibernate 4 */
                    if(CollectionUtils.isEmpty(group.getOrganizations())) {
                    	dbGroup.getOrganizations().clear();
                    } else {
                    	/* basically, we need to remove entries from the DB perstent set tha we don't need */
                    	final Set<String> incomingOrganizationIds = (group.getOrganizations() != null) ? 
                    			group.getOrganizations().stream().map(e -> e.getEntity().getId()).collect(Collectors.toSet()) : null;
                    	group.getOrganizations().forEach(xref -> {
                    		dbGroup.getOrganizations().removeIf(e -> {
                    			return !incomingOrganizationIds.contains(e.getEntity().getId());
                    		});
                    		dbGroup.addOrganization(xref.getEntity(), xref.getRights(), null, null);
                    	});
                    }
                    
                    /* now set the persistent set  on the transient object */
                    group.setOrganizations(dbGroup.getOrganizations());
                    group.getOrganizations().forEach(xref -> {
                    	xref.setMemberEntity(group);
                    	xref.setEntity(organizationDAO.findById(xref.getEntity().getId()));
                    });
                } else {
                    return;
                }
                groupDao.merge(group);

            } else {
            	if(groupOwner != null) {
            		if("user".equals(groupOwner.getType())){
            			group.addUser(userDAO.findById(groupOwner.getId()), accessRightDAO.findById(adminRightId), null, null);
            		} else if("group".equals(groupOwner.getType())){
            			group.addChildGroup(groupDao.findById(groupOwner.getId()), accessRightDAO.findById(adminRightId), null, null);
            		} else {
            			group.addUser(userDAO.findById(requestorId), accessRightDAO.findById(adminRightId), null, null);
            		}
            	}
                group.setCreatedBy(requestorId);
                group.setCreateDate(Calendar.getInstance().getTime());
                groupDao.save(group);

                IdentityEntity groupDefaultEntity = new IdentityEntity(IdentityTypeEnum.GROUP);
                groupDefaultEntity.setCreateDate(new Date());
                groupDefaultEntity.setCreatedBy(requestorId);
                groupDefaultEntity.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
                groupDefaultEntity.setReferredObjectId(group.getId());
                groupDefaultEntity.setStatus(LoginStatusEnum.ACTIVE);
                groupDefaultEntity.setIdentity(group.getName());
                identityDAO.save(groupDefaultEntity);
                group.addApproverAssociation(createDefaultApproverAssociations(group, requestorId));

                addRequiredAttributes(group);
            }
        }
    }
    @Override
    public void addRequiredAttributes(GroupEntity group) {
        if(group!=null && group.getType()!=null && StringUtils.isNotBlank(group.getType().getId())){
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(group.getType().getId());
            List<MetadataElementEntity> elementList = metadataElementDAO.getByExample(sb, -1, -1);
            if(CollectionUtils.isNotEmpty(elementList)){
                for(MetadataElementEntity element: elementList){
                    if(element.isRequired()){
                        groupAttrDao.save(AttributeUtil.buildGroupAttribute(group, element));
                    }
                }
            }
        }
    }

    private ApproverAssociationEntity createDefaultApproverAssociations(final GroupEntity entity, final String requestorId) {
		final ApproverAssociationEntity association = new ApproverAssociationEntity();
		association.setAssociationEntityId(entity.getId());
		association.setAssociationType(AssociationType.GROUP);
		association.setApproverLevel(Integer.valueOf(0));
		association.setApproverEntityId(requestorId);
		association.setApproverEntityType(AssociationType.USER);
		return association;
	}
	
	private void mergeAttribute(final GroupEntity bean, final GroupEntity dbObject, final String requesterId) {
		Set<GroupAttributeEntity> beanProps = (bean.getAttributes() != null) ? bean.getAttributes() : new HashSet<GroupAttributeEntity>();
        Set<GroupAttributeEntity> dbProps = (dbObject.getAttributes() != null) ? new HashSet<GroupAttributeEntity>(dbObject.getAttributes()) : new HashSet<GroupAttributeEntity>();

        /* update */
        Iterator<GroupAttributeEntity> dbIteroator = dbProps.iterator();
        while(dbIteroator.hasNext()) {
        	final GroupAttributeEntity dbProp = dbIteroator.next();
        	
        	boolean contains = false;
            for (final GroupAttributeEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    dbProp.setValue(beanProp.getValue());
                    dbProp.setElement(getEntity(beanProp.getElement()));
                    dbProp.setName(beanProp.getName());
                    dbProp.setIsMultivalued(beanProp.getIsMultivalued());
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if(!contains) {
                auditLogRemoveAttribute(bean, dbProp, requesterId);
            	dbIteroator.remove();
            }
        }

        /* add */
        final Set<GroupAttributeEntity> toAdd = new HashSet<>();
        for (final GroupAttributeEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while(dbIteroator.hasNext()) {
            	final GroupAttributeEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                beanProp.setGroup(bean);
                beanProp.setElement(getEntity(beanProp.getElement()));
                auditLogAddAttribute(bean, beanProp, requesterId);
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);
        
        bean.setAttributes(dbProps);
	}

    private void auditLogRemoveAttribute(final GroupEntity group, final GroupAttributeEntity groupAttr, final String requesterId){
        // Audit Log -----------------------------------------------------------------------------------
    	IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setTargetGroupAttribute(groupAttr.getId(), groupAttr.getName());
        auditLog.setAction(AuditAction.DELETE_ATTRIBUTE.value());
        auditLog.put(groupAttr.getName(), groupAttr.getValue());
        auditLogService.enqueue(auditLog);
    }

    private void auditLogAddAttribute(final GroupEntity group, final GroupAttributeEntity groupAttr, final String requesterId){
        // Audit Log -----------------------------------------------------------------------------------
    	IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetGroup(group.getId(), group.getName());
        auditLog.setTargetGroupAttribute(groupAttr.getId(), groupAttr.getName());
        auditLog.setAction(AuditAction.ADD_ATTRIBUTE.value());
        auditLog.put(groupAttr.getName(), groupAttr.getValue());
        auditLogService.enqueue(auditLog);
    }

	private MetadataElementEntity getEntity(final MetadataElementEntity bean) {
    	if(bean != null && StringUtils.isNotBlank(bean.getId())) {
    		return metadataElementDAO.findById(bean.getId());
    	} else {
    		return null;
    	}
    }

	@Override
	@Transactional
	public void deleteGroup(String groupId) {
		final GroupEntity entity = groupDao.findById(groupId);
		if(entity != null) {
			groupDao.delete(entity);
            List<IdentityEntity> systemGroupIdentityList = identityDAO.findByReferredId(groupId);
            for(IdentityEntity identityEntity : systemGroupIdentityList) {
                identityDAO.delete(identityEntity);
            }
		}
	}

	private void visitGroups(final GroupEntity entity, final Set<GroupEntity> visitedSet) {
		if(entity != null) {
			if(!visitedSet.contains(entity)) {
				visitedSet.add(entity);
				final Set<GroupToGroupMembershipXrefEntity> children = entity.getChildGroups();
				if(CollectionUtils.isNotEmpty(children)) {
					children.stream().map(e -> e.getMemberEntity()).forEach(child -> {
						visitGroups(child, visitedSet);
					});
				}
			}
		}
	}


	public void saveAttribute(final GroupAttributeEntity attribute) {
		if(StringUtils.isNotBlank(attribute.getId())) {
			groupAttrDao.update(attribute);
		} else {
			groupAttrDao.save(attribute);
		}
	}

	@Override
	public void removeAttribute(String attributeId) {
		final GroupAttributeEntity entity = groupAttrDao.findById(attributeId);
		if(entity != null) {
			groupAttrDao.delete(entity);
		}
	}

	@Override
	public void addChildGroup(String groupId, String childGroupId, final Set<String> rights, final Date startDate, final Date endDate) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity group = groupDao.findById(groupId);
			final GroupEntity child = groupDao.findById(childGroupId);
			if(group != null && child != null) {
				group.addChildGroup(child, accessRightDAO.findByIds(rights), startDate, endDate);
			}
		}
	}

	@Override
	public void removeChildGroup(String groupId, String childGroupId) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity child = groupDao.findById(childGroupId);
			final GroupEntity parent = groupDao.findById(groupId);
			if(parent != null && child != null) {
				parent.removeChildGroup(child);
			}
		}
	}

    private Set<String> getDelegationFilter(String requesterId){
        Set<String> filterData = null;
        if(StringUtils.isNotBlank(requesterId)){
            Map<String, UserAttribute> attrbutes =  userDataService.getUserAttributesDto(requesterId);
            filterData = new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(attrbutes));
            List<String> rolesFromDelegation = DelegationFilterHelper.getRoleFilterFromString(attrbutes);
            System.out.println("================================== GroupDataService.getDelegationFilter==== rolesFromDelegation= "+rolesFromDelegation+", requesterId="+requesterId);
            if(rolesFromDelegation != null && rolesFromDelegation.size() > 0){
                GroupSearchBean groupSearchBean = new GroupSearchBean();
                groupSearchBean.setRoleIdSet(new HashSet<String>(rolesFromDelegation));
                List<String> groupIds = groupDao.getIDsByExample(groupSearchBean, 0, Integer.MAX_VALUE);
                System.out.println("================================== GroupDataService.getDelegationFilter==== groupIds= "+groupIds+", requesterId="+requesterId);
                // TODO check: CONJUNCTION
                // IF we can't find any groups by Role Delegation filter we add default skip ID to remove all others groups
                if(groupIds == null || groupIds.size() == 0) {
                    filterData.add("NOTEXIST");
                } else {
                    filterData.addAll(groupIds);
                }
            }
        }
        return filterData;
    }

	@Override
	@Transactional
	public void validateGroup2GroupAddition(String parentId, String memberId, final Set<String> rights, final Date startDate, final Date endDate) throws BasicDataServiceException {
		final GroupEntity parent = groupDao.findById(parentId);
		final GroupEntity child = groupDao.findById(memberId);
		
		if(parent == null || child == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		
		if(causesCircularDependency(parent, child, new HashSet<GroupEntity>())) {
			throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
		}
		
		if(startDate != null && endDate != null && startDate.after(endDate)) {
        	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
        }
		
		/*
		if(parent.hasChildGroup(child.getId())) {
			throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
		}
		*/
		
		if(StringUtils.equals(parentId, memberId)) {
			throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
		}
	}
	
	private boolean causesCircularDependency(final GroupEntity parent, final GroupEntity child, final Set<GroupEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentGroups())) {
                    for (final GroupToGroupMembershipXrefEntity xref : parent.getParentGroups()) {
                    	final GroupEntity entity = xref.getEntity();
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
    @Deprecated
	public Group getGroupDTO(String groupId) {
		return getGroupDTOLocalize(groupId, getDefaultLanguage());
	}

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public Group getGroupDTOLocalize(String groupId, LanguageEntity language) {
        return groupDozerConverter.convertToDTO(groupDao.findById(groupId), true);
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<GroupEntity> findGroupsByAttributeValue(String attrName, String attrValue) {
        return findGroupsByAttributeValueLocalize(attrName, attrValue, getDefaultLanguage());
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<GroupEntity> findGroupsByAttributeValueLocalize(String attrName, String attrValue, LanguageEntity language) {
    	final GroupSearchBean searchBean = new GroupSearchBean();
    	searchBean.addAttribute(attrName, attrValue);
        return groupDao.getByExample(searchBean);
    }

	@Override
	@Transactional(readOnly = true)
	public boolean hasAttachedEntities(String groupId) {
		final GroupEntity group = groupDao.findById(groupId);
		if(group != null) {
			return CollectionUtils.isNotEmpty(group.getChildGroups()) ||
				   CollectionUtils.isNotEmpty(group.getRoles()) ||
				   CollectionUtils.isNotEmpty(group.getResources());
		} else {
			return false;
		}
	}
}
