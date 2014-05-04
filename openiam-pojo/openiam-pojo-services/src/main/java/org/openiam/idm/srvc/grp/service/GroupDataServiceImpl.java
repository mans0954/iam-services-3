package org.openiam.idm.srvc.grp.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.IdentityDAO;
import org.openiam.idm.srvc.grp.domain.GroupAttributeEntity;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.org.service.OrganizationDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.openiam.internationalization.LocalizedServiceGet;

import java.util.*;

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
    
	@Value("${org.openiam.resource.admin.resource.type.id}")
	private String adminResourceTypeId;
	
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
    @Deprecated
    public GroupEntity getGroupByName(final String groupName, final String requesterId) {
        return getGroupByNameLocalize(groupName, requesterId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public GroupEntity getGroupByNameLocalize(final String groupName, final String requesterId, final LanguageEntity language) {
        final GroupSearchBean searchBean = new GroupSearchBean();
        searchBean.setName(groupName);
        final List<GroupEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
        return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
    }

    @Override
    @Deprecated
    public List<GroupEntity> getChildGroups(final String groupId, final String requesterId, final int from, final int size) {
        return getChildGroupsLocalize(groupId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<GroupEntity> getChildGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language) {
        return groupDao.getChildGroups(groupId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Deprecated
    public List<GroupEntity> getParentGroups(final String groupId, final String requesterId, final int from, final int size) {
        return getParentGroupsLocalize(groupId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<GroupEntity> getParentGroupsLocalize(final String groupId, final String requesterId, final int from, final int size, final LanguageEntity language) {
        return groupDao.getParentGroups(groupId, getDelegationFilter(requesterId), from, size);
    }


    @Override
    @Deprecated
    public List<GroupEntity> findBeans(final GroupSearchBean searchBean, final  String requesterId, int from, int size) {
        return findBeansLocalize(searchBean, requesterId,  from,  size,  getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<GroupEntity> findBeansLocalize(final GroupSearchBean searchBean, final  String requesterId, int from, int size, final LanguageEntity language) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return new ArrayList<GroupEntity>(0);
        }
        return groupDao.getByExample(searchBean, from, size);
    }

    @Override
    @Deprecated
    public List<GroupEntity> getGroupsForUser(final String userId, final String requesterId, int from, int size) {
        return getGroupsForUserLocalize(userId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<GroupEntity> getGroupsForUserLocalize(final String userId, final String requesterId, int from, int size, LanguageEntity language) {
        return groupDao.getGroupsForUser(userId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Deprecated
    public List<GroupEntity> getGroupsForResource(final String resourceId, final String requesterId, final int from, final int size) {
        return getGroupsForResourceLocalize(resourceId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<GroupEntity> getGroupsForResourceLocalize(final String resourceId, final String requesterId, final int from, final int size, LanguageEntity language) {
        return groupDao.getGroupsForResource(resourceId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Deprecated
    public List<GroupEntity> getGroupsForRole(final String roleId, final String requesterId, int from, int size) {
        return getGroupsForRoleLocalize(roleId, requesterId, from, size, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<GroupEntity> getGroupsForRoleLocalize(final String roleId, final String requesterId, int from, int size, LanguageEntity language) {
        return groupDao.getGroupsForRole(roleId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    public int getNumOfGroupsForRole(final String roleId, final String requesterId) {
        return groupDao.getNumOfGroupsForRole(roleId, getDelegationFilter(requesterId));
    }

    @Override
    public int getNumOfGroupsForResource(final String resourceId, final String requesterId) {
        return groupDao.getNumOfGroupsForResource(resourceId, getDelegationFilter(requesterId));
    }

    @Override
    public int getNumOfGroupsForUser(final String userId, final String requesterId) {
        return groupDao.getNumOfGroupsForUser(userId, getDelegationFilter(requesterId));
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
    public int getNumOfChildGroups(final String groupId, final String requesterId) {
        return groupDao.getNumOfChildGroups(groupId, getDelegationFilter(requesterId));
    }

    @Override
    public int getNumOfParentGroups(final String groupId, final String requesterId) {
        return groupDao.getNumOfParentGroups(groupId, getDelegationFilter(requesterId));
    }

    @Override
    @Deprecated
    public List<Group> getCompiledGroupsForUser(final String userId) {
        return getCompiledGroupsForUserLocalize(userId, getDefaultLanguage());
    }

    @Override
    @LocalizedServiceGet
    public List<Group> getCompiledGroupsForUserLocalize(final String userId, final LanguageEntity language) {
        final List<GroupEntity> groupList = this.getGroupsForUser(userId, null, 0, Integer.MAX_VALUE);
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
		if(group != null && entityValidator.isValid(group)) {
			
			if(group.getManagedSystem() != null && group.getManagedSystem().getId() != null) {
				group.setManagedSystem(managedSysDAO.findById(group.getManagedSystem().getId()));
			} else {
				group.setManagedSystem(null);
			}
			
			if(group.getCompany() != null && StringUtils.isNotBlank(group.getCompany().getId())) {
				group.setCompany(organizationDAO.findById(group.getCompany().getId()));
			} else {
				group.setCompany(null);
			}

            if(group.getType() != null && StringUtils.isNotBlank(group.getType().getId())) {
                group.setType(typeDAO.findById(group.getType().getId()));
            } else {
                group.setType(null);
            }

			if(StringUtils.isNotBlank(group.getId())) {
				final GroupEntity dbGroup = groupDao.findById(group.getId());
				if(dbGroup != null) {
					group.setApproverAssociations(dbGroup.getApproverAssociations());
					
					mergeAttribute(group, dbGroup);
					group.setChildGroups(dbGroup.getChildGroups());
					group.setParentGroups(dbGroup.getParentGroups());
					group.setResources(dbGroup.getResources());
					group.setRoles(dbGroup.getRoles());
					group.setUsers(dbGroup.getUsers());
					group.setAdminResource(dbGroup.getAdminResource());
					if(group.getAdminResource() == null) {
						group.setAdminResource(getNewAdminResource(group, requestorId));
					}
				} else {
					return;
				}
			} else {
				group.setAdminResource(getNewAdminResource(group, requestorId));
				groupDao.save(group);

                IdentityEntity groupDefaultEntity = new IdentityEntity(IdentityTypeEnum.GROUP);
                groupDefaultEntity.setCreateDate(new Date());
                groupDefaultEntity.setCreatedBy(requestorId);
                groupDefaultEntity.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
                groupDefaultEntity.setReferredObjectId(group.getId());
                groupDefaultEntity.setStatus(LoginStatusEnum.PENDING_CREATE);
                identityDAO.save(groupDefaultEntity);
				group.addApproverAssociation(createDefaultApproverAssociations(group, requestorId));
			}
			groupDao.merge(group);
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
	
	private ResourceEntity getNewAdminResource(final GroupEntity entity, final String requestorId) {
		final ResourceEntity adminResource = new ResourceEntity();
		adminResource.setName(String.format("GRP_ADMIN_%s_%s", entity.getName(), RandomStringUtils.randomAlphanumeric(2)));
		adminResource.setResourceType(resourceTypeDAO.findById(adminResourceTypeId));
		adminResource.addUser(userDAO.findById(requestorId));
		return adminResource;
	}
	
	private void mergeAttribute(final GroupEntity bean, final GroupEntity dbObject) {
		final Set<GroupAttributeEntity> renewedProperties = new HashSet<GroupAttributeEntity>();
		
		Set<GroupAttributeEntity> beanProps = (bean.getAttributes() != null) ? bean.getAttributes() : new HashSet<GroupAttributeEntity>();
		Set<GroupAttributeEntity> dbProps = (dbObject.getAttributes() != null) ? dbObject.getAttributes() : new HashSet<GroupAttributeEntity>();
		
		/* update */
		for(GroupAttributeEntity dbProp : dbProps) {
			for(final GroupAttributeEntity beanProp : beanProps) {
				if(StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    dbProp.setElement(beanProp.getElement());
					dbProp.setName(beanProp.getName());
					dbProp.setValue(beanProp.getValue());
                    dbProp.setIsMultivalued(beanProp.getIsMultivalued());
                    dbProp.setValues(beanProp.getValues());
					renewedProperties.add(dbProp);
					break;
				}
			}
		}
		
		/* add */
		for(final GroupAttributeEntity beanProp : beanProps) {
			boolean contains = false;
			for(GroupAttributeEntity dbProp : dbProps) {
				if(StringUtils.equals(dbProp.getId(), beanProp.getId())) {
					contains = true;
				}
			}
			
			if(!contains) {
				beanProp.setGroup(bean);
				//dbProps.add(beanProp);
				renewedProperties.add(beanProp);
			}
		}
		for (GroupAttributeEntity prop : renewedProperties) {
            if(prop.getElement()!=null){
                prop.setElement(metadataElementDAO.findById(prop.getElement().getId()));
            } else {
                prop.setElement(null);
            }
        }
		bean.setAttributes(renewedProperties);
		//bean.setResourceProps(renewedProperties);
	}

	@Override
	@Transactional
	public void deleteGroup(String groupId) {
		final GroupEntity entity = groupDao.findById(groupId);
		if(entity != null) {
			groupDao.delete(entity);
            IdentityEntity systemGroupIdentity = identityDAO.findByManagedSysId(groupId, sysConfiguration.getDefaultManagedSysId());
            if(systemGroupIdentity != null) {
                identityDAO.delete(systemGroupIdentity);
            }
		}
	}

	private void visitGroups(final GroupEntity entity, final Set<GroupEntity> visitedSet) {
		if(entity != null) {
			if(!visitedSet.contains(entity)) {
				visitedSet.add(entity);
				final Set<GroupEntity> children = entity.getChildGroups();
				if(CollectionUtils.isNotEmpty(children)) {
					for(final GroupEntity child : children) {
						visitGroups(child, visitedSet);
					}
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
	public void addChildGroup(String groupId, String childGroupId) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity group = groupDao.findById(groupId);
			final GroupEntity child = groupDao.findById(childGroupId);
			if(group != null && child != null) {
				if(!group.hasChildGroup(childGroupId)) {
					group.addChildGroup(child);
					groupDao.update(group);
				}
			}
		}
	}

	@Override
	public void removeChildGroup(String groupId, String childGroupId) {
		if(groupId != null && childGroupId != null) {
			final GroupEntity group = groupDao.findById(groupId);
			if(group != null) {
				group.removeChildGroup(childGroupId);
			}
		}
	}


    private Set<String> getDelegationFilter(String requesterId){
        Set<String> filterData = null;
        if(StringUtils.isNotBlank(requesterId)){
            filterData = new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString( userDataService.getUserAttributesDto(requesterId)));
        }
        return filterData;
    }

	@Override
	@Transactional
	public void validateGroup2GroupAddition(String parentId, String memberId) throws BasicDataServiceException {
		final GroupEntity parent = groupDao.findById(parentId);
		final GroupEntity child = groupDao.findById(memberId);
		
		if(parent == null || child == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		
		if(causesCircularDependency(parent, child, new HashSet<GroupEntity>())) {
			throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
		}
		
		if(parent.hasChildGroup(child.getId())) {
			throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS);
		}
		
		if(StringUtils.equals(parentId, memberId)) {
			throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
		}
	}
	
	private boolean causesCircularDependency(final GroupEntity parent, final GroupEntity child, final Set<GroupEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if(!visitedSet.contains(child)) {
				visitedSet.add(child);
				if(CollectionUtils.isNotEmpty(parent.getParentGroups())) {
					for(final GroupEntity entity : parent.getParentGroups()) {
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
    @Deprecated
	public Group getGroupDTO(String groupId) {
		return getGroupDTOLocalize(groupId, getDefaultLanguage());
	}

    @Override
    @LocalizedServiceGet
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
        return groupDao.findGroupsByAttributeValue(attrName, attrValue);
    }
}
