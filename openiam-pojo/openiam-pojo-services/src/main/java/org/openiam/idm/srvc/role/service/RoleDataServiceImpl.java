package org.openiam.idm.srvc.role.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.TreeObjectId;
import org.openiam.base.ws.ResponseCode;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.dozer.converter.RoleAttributeDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.service.LanguageDAO;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceTypeDAO;
import org.openiam.idm.srvc.role.domain.RoleAttributeEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.domain.RoleToRoleMembershipXrefEntity;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.openiam.validator.EntityValidator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("roleDataService")
public class RoleDataServiceImpl implements RoleDataService {

	@Autowired
	private RoleDAO roleDao;
    @Autowired
    private RoleAttributeDAO roleAttributeDAO;
	
	@Autowired
	private ResourceTypeDAO resourceTypeDAO;
	
    @Autowired
    private MetadataElementDAO metadataElementDAO;
	
	@Autowired
	private GroupDAO groupDAO;
	
	@Autowired
	private UserDAO userDAO;

    @Autowired
    private UserDataService userDataService;
	
	@Autowired
	private RoleDozerConverter roleDozerConverter;

    @Autowired
    private RoleAttributeDozerConverter roleAttributeDozerConverter;

	@Autowired
    @Qualifier("entityValidator")
    private EntityValidator entityValidator;
	
    @Autowired
    private ManagedSysDAO managedSysDAO;
	
    @Autowired
    private MetadataTypeDAO typeDAO;

    @Autowired
    protected AuditLogService auditLogService;
    
    @Autowired
    private AccessRightDAO accessRightDAO;

	@Value("${org.openiam.ui.admin.right.id}")
	private String adminRightId;


    private ApplicationContext ac;


    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

    @Autowired
    protected LanguageDAO languageDAO;

    /**
     * Cache for whole roles hierarchy
     * Used when Roles number > 250k records
     */
    private final Map<String, TreeObjectId> rolesTree = new HashMap<String, TreeObjectId>();

	private static final Log log = LogFactory.getLog(RoleDataServiceImpl.class);

	@Deprecated
    @Override
    @Transactional(readOnly = true)
    public RoleEntity getRole(String roleId){
        return  getRole(roleId, null);
    }
    
	@Deprecated
	@Override
    @Transactional(readOnly = true)
	public RoleEntity getRole(String roleId, final String requesterId) {
        if(DelegationFilterHelper.isAllowed(roleId, getDelegationFilter(requesterId))){
            return roleDao.findById(roleId);
        }
        return null;
	}

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public Role getRoleDtoByName(String roleName, String requesterId) {
        RoleEntity roleEntity = getRoleByName(roleName,requesterId);
        return roleDozerConverter.convertToDTO(roleEntity, true);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
	public RoleEntity getRoleLocalized(final String roleId, final String requesterId, final LanguageEntity language) {
		if(DelegationFilterHelper.isAllowed(roleId, getDelegationFilter(requesterId))){
            return roleDao.findById(roleId);
        }
        return null;
	}

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public Role getRoleDtoLocalized(final String roleId, final String requesterId, final LanguageEntity language) {

        //RoleDataService bean = (RoleDataService)ac.getBean("roleDataService");
        RoleEntity roleEntity = this.getProxyService().getRoleLocalized(roleId, requesterId, language);

        if (roleEntity != null) {
            return roleDozerConverter.convertToDTO(roleEntity, true);
        }
        return null;
    }
	
	@Override
	@Transactional
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("roleEntities")
        }
    )
	public void removeRole(final String roleId) {
		if(roleId != null) {
			final RoleEntity roleEntity = roleDao.findById(roleId);
			if(roleEntity != null) {
				//roleAttributeDAO.deleteByRoleId(roleId);
				roleDao.delete(roleEntity);
			}
		}
	}
	
	@Override
    @Transactional
	public void addGroupToRole(final String roleId, 
							   final String groupId, 
							   final Set<String> rightIds,
							   final Date startDate,
							   final Date endDate) {
		if(roleId != null && groupId != null) {
			final RoleEntity role = roleDao.findById(roleId);
			final GroupEntity group = groupDAO.findById(groupId);
			if(role != null && group != null) {
				role.addGroup(group, accessRightDAO.findByIds(rightIds), startDate, endDate);
			}
		}
	}
	
	@Override
    @Transactional
	public void removeGroupFromRole(String roleId, String groupId) {
		if(roleId != null && groupId != null) {
			final RoleEntity role = roleDao.findById(roleId);
			final GroupEntity group = groupDAO.findById(groupId);
			if(role != null && group != null) {
				role.removeGroup(group);
				//roleDao.update(role);
			}
		}

	}
    /**
     * Adds a user to a role using the UserRole object. Similar to addUserToRole, but allows you to update attributes likes start and end date.
     */
   
    @Override
    @Transactional
	public void addUserToRole(final String roleId, 
							  final String userId, 
							  final Set<String> rightIds,
							  final Date startDate,
							  final Date endDate) {
    	final UserEntity user = userDAO.findById(userId);
    	final RoleEntity role = roleDao.findById(roleId);
    	if(user != null && role != null) {
    		user.addRole(role, accessRightDAO.findByIds(rightIds), startDate, endDate);
    	}
	}
	
	@Override
    @Transactional
	public void removeUserFromRole(String roleId, String userId) {
        final UserEntity user = userDAO.findById(userId);
        final RoleEntity role = roleDao.findById(roleId);
        if(user != null && role != null) {
        	user.removeRole(role);
        }
	}

	private void visitChildRoles(final String id, final Set<RoleEntity> visitedSet) {
		if(id != null) {
			if(visitedSet != null) {
				final RoleEntity role = roleDao.findById(id);
				if(role != null) {
					if(!visitedSet.contains(role)) {
						visitedSet.add(role);
						if(CollectionUtils.isNotEmpty(role.getChildRoles())) {
							role.getChildRoles().stream().map(e -> e.getMemberEntity()).forEach(child -> {
								visitChildRoles(child.getId(), visitedSet);
							});
						}
					}
				}
			}
		}
	}

	@Override
    @Transactional
	@CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("roleEntities")
        }
    )
	public void saveRole(final RoleEntity role, final String requestorId) throws BasicDataServiceException {
		if(role != null && entityValidator.isValid(role)) {
			if(role.getManagedSystem() != null && role.getManagedSystem().getId() != null) {
				role.setManagedSystem(managedSysDAO.findById(role.getManagedSystem().getId()));
			} else {
				role.setManagedSystem(null);
			}
			
			if(role.getType() != null && StringUtils.isNotBlank(role.getType().getId())) {
				role.setType(typeDAO.findById(role.getType().getId()));
            } else {
            	role.setType(null);
            }

			if(StringUtils.isBlank(role.getId())) {
				roleDao.save(role);
				role.addApproverAssociation(createDefaultApproverAssociations(role, requestorId));
				role.addUser(userDAO.findById(requestorId), accessRightDAO.findById(adminRightId), null, null);
                addRequiredAttributes(role);
			} else {
				final RoleEntity dbRole = roleDao.findById(role.getId());
				if(dbRole != null) {
					mergeAttributes(role, dbRole, requestorId);
					role.setApproverAssociations(dbRole.getApproverAssociations());
					role.setChildRoles(dbRole.getChildRoles());
					role.setGroups(dbRole.getGroups());
					role.setParentRoles(dbRole.getParentRoles());
					role.setResources(dbRole.getResources());
					role.setUsers(dbRole.getUsers());
				}
			}
			roleDao.merge(role);
		}
	}
    @Override
    @Transactional
    public void addRequiredAttributes(RoleEntity role) {
        if(role!=null && role.getType()!=null && StringUtils.isNotBlank(role.getType().getId())){
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(role.getType().getId());
            List<MetadataElementEntity> elementList = metadataElementDAO.getByExample(sb, -1, -1);
            if(CollectionUtils.isNotEmpty(elementList)){
                for(MetadataElementEntity element: elementList){
                    if(element.isRequired()){
                        roleAttributeDAO.save(AttributeUtil.buildRoleAttribute(role, element));
                    }
                }
            }
        }
    }

    private void mergeAttributes(final RoleEntity bean, final RoleEntity dbObject, final String requestorId) {
		Set<RoleAttributeEntity> beanProps = (bean.getRoleAttributes() != null) ? bean.getRoleAttributes() : new HashSet<RoleAttributeEntity>();
        Set<RoleAttributeEntity> dbProps = (dbObject.getRoleAttributes() != null) ? new HashSet<RoleAttributeEntity>(dbObject.getRoleAttributes()) : new HashSet<RoleAttributeEntity>();

        /* update */
        Iterator<RoleAttributeEntity> dbIteroator = dbProps.iterator();
        while(dbIteroator.hasNext()) {
        	final RoleAttributeEntity dbProp = dbIteroator.next();
        	
        	boolean contains = false;
            for (final RoleAttributeEntity beanProp : beanProps) {
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    dbProp.setValue(beanProp.getValue());
                    dbProp.setMetadataElementId(beanProp.getMetadataElementId());
                    dbProp.setName(beanProp.getName());
                    dbProp.setIsMultivalued(beanProp.getIsMultivalued());
                    contains = true;
                    break;
                }
            }
            
            /* remove */
            if(!contains) {
                auditLogRemoveAttribute(bean,dbProp, requestorId);
            	dbIteroator.remove();
            }
        }

        /* add */
        final Set<RoleAttributeEntity> toAdd = new HashSet<>();
        for (final RoleAttributeEntity beanProp : beanProps) {
            boolean contains = false;
            dbIteroator = dbProps.iterator();
            while(dbIteroator.hasNext()) {
            	final RoleAttributeEntity dbProp = dbIteroator.next();
                if (StringUtils.equals(dbProp.getId(), beanProp.getId())) {
                    contains = true;
                }
            }

            if (!contains) {
                beanProp.setRole(bean);
                beanProp.setMetadataElementId(beanProp.getMetadataElementId());
                auditLogAddAttribute(bean, beanProp, requestorId);
                toAdd.add(beanProp);
            }
        }
        dbProps.addAll(toAdd);
        
        bean.setRoleAttributes(dbProps);
	}

    @Override
    @Transactional(readOnly = true)
    public List<RoleAttribute> getRoleAttributes(String roleId) {
        List<RoleAttributeEntity> attributes = roleAttributeDAO.findByRoleId(roleId);
        return roleAttributeDozerConverter.convertToDTOList(attributes, false);
    }

    private void auditLogRemoveAttribute(final RoleEntity role, final RoleAttributeEntity roleAttr, final String requesterId){
        // Audit Log -----------------------------------------------------------------------------------
    	IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetRole(role.getId(), role.getName());
        auditLog.setTargetRoleAttribute(roleAttr.getId(), roleAttr.getName());
        auditLog.setAction(AuditAction.DELETE_ATTRIBUTE.value());
        auditLog.put(roleAttr.getName(), roleAttr.getValue());
        auditLogService.enqueue(auditLog);
    }

    private void auditLogAddAttribute(final RoleEntity role, final RoleAttributeEntity roleAttr, final String requesterId){
        // Audit Log -----------------------------------------------------------------------------------
    	IdmAuditLogEntity auditLog = new IdmAuditLogEntity();
        auditLog.setRequestorUserId(requesterId);
        auditLog.setTargetRole(role.getId(), role.getName());
        auditLog.setTargetRoleAttribute(roleAttr.getId(), roleAttr.getName());
        auditLog.setAction(AuditAction.ADD_ATTRIBUTE.value());
        auditLog.put(roleAttr.getName(), roleAttr.getValue());
        auditLogService.enqueue(auditLog);
    }

	private ApproverAssociationEntity createDefaultApproverAssociations(final RoleEntity entity, final String requestorId) {
		if(requestorId != null) {
			final ApproverAssociationEntity association = new ApproverAssociationEntity();
			association.setAssociationEntityId(entity.getId());
			association.setAssociationType(AssociationType.ROLE);
			association.setApproverLevel(Integer.valueOf(0));
			association.setApproverEntityId(requestorId);
			association.setApproverEntityType(AssociationType.USER);
			return association;
		} else {
			return null;
		}
	}
	
	/*
	@Override
    @Transactional
	public void saveAttribute(RoleAttributeEntity attribute) {
		if(attribute != null) {
			if(StringUtils.isBlank(attribute.getRoleAttrId())) {
				roleAttributeDAO.save(attribute);
			} else {
				roleAttributeDAO.update(attribute);
			}
		}
	}
	*/

	/*
	@Override
    @Transactional
	public void removeAttribute(final String roleAttributeId) {
		if(roleAttributeId != null) {
			final RoleAttributeEntity entity = roleAttributeDAO.findById(roleAttributeId);
			if(entity != null) {
				roleAttributeDAO.delete(entity);
			}
		}
	}
	*/
/*
    @Override
    @Transactional(readOnly = true)
    public List<Role> getRolesDtoForUser(String userId, String requesterId, int from, int size) {
    	final RoleSearchBean sb = new RoleSearchBean();
    	sb.addUserId(userId);
        final List<RoleEntity> entityList = findBeans(sb, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(entityList, false);
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> getRolesInGroup(final String groupId, final String requesterId, int from, int size) {
        return roleDao.getRolesForGroup(groupId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRolesDtoInGroup(final String groupId, final String requesterId, int from, int size) {
        List<RoleEntity> roleEntityList = roleDao.getRolesForGroup(groupId, getDelegationFilter(requesterId), from, size);
        return roleDozerConverter.convertToDTOList(roleEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRolesDtoForUser(String userId, String requesterId, int from, int size) {
        //final List<RoleEntity> entityList = getRolesForUser(userId, requesterId, from, size);
        final List<RoleEntity> entityList = this.getProxyService().getRolesForUser(userId, requesterId, from, size);
        return roleDozerConverter.convertToDTOList(entityList, false);
    }

    @Override
    @Transactional(readOnly = true)
	public List<Role> getUserRolesAsFlatList(String userId) {
		final UserEntity userEntity = userDAO.findById(userId);
		final Set<RoleEntity> userRoles = (userEntity.getRoles() != null) ? 
				userEntity.getRoles().stream().map(e -> e.getEntity()).collect(Collectors.toSet()) : null;
		
		final Set<RoleEntity> visitedSet = new HashSet<RoleEntity>();

		if(CollectionUtils.isNotEmpty(userRoles)) {
			for(final RoleEntity entity : userRoles) {
				visitChildRoles(entity.getId(), visitedSet);
			}
		}
		
		final List<RoleEntity> resultList = new ArrayList<RoleEntity>(visitedSet);
		return roleDozerConverter.convertToDTOList(resultList, true);
	}

	@Override
    @Transactional(readOnly = true)
    public List<RoleEntity> findBeans(RoleSearchBean searchBean, final String requesterId, int from, int size) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return new ArrayList<RoleEntity>(0);
        }
        return roleDao.getByExample(searchBean, from, size);
	}

    @Override
    @Transactional(readOnly = true)
    public List<Role> findBeansDto(RoleSearchBean searchBean, final String requesterId, int from, int size) {
/*		Set<String> filter = getDelegationFilter(requesterId);
		if(StringUtils.isBlank(searchBean.getKey()))
			searchBean.setKeys(filter);
		else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
			return new ArrayList<Role>(0);
		}
		List<RoleEntity> roleEntityList = roleDao.getByExample(searchBean, from, size);*/

        //RoleDataService bean = (RoleDataService)ac.getBean("roleDataService");
        List<RoleEntity> roleEntityList = this.getProxyService().findBeans(searchBean, requesterId, from, size);

        return roleDozerConverter.convertToDTOList(roleEntityList, false);
    }

	@Override
    @Transactional(readOnly = true)
	public int countBeans(RoleSearchBean searchBean, final String requesterId) {
        Set<String> filter = getDelegationFilter(requesterId);
        if(StringUtils.isBlank(searchBean.getKey()))
            searchBean.setKeys(filter);
        else if(!DelegationFilterHelper.isAllowed(searchBean.getKey(), filter)){
            return 0;
        }
        return roleDao.count(searchBean);
	}

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> findRolesByAttributeValue(String attrName, String attrValue) {
    	final RoleSearchBean searchBean = new RoleSearchBean();
    	searchBean.addAttribute(attrName, attrValue);
        return roleDao.getByExample(searchBean);
    }

/*    @Override
    @Transactional(readOnly = true)
    public List<Role> findRolesDtoByAttributeValue(String attrName, String attrValue) {
        List<RoleEntity> roleEntityList = roleDao.findRolesByAttributeValue(attrName, attrValue);
        return roleDozerConverter.convertToDTOList(roleEntityList, true);
    }*/

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> getRolesForResource(final String resourceId, final String requesterId, final int from, final int size) {
        return roleDao.getRolesForResource(resourceId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getRolesDtoForResource(final String resourceId, final String requesterId, final int from, final int size) {
        List<RoleEntity> roleEntityList = roleDao.getRolesForResource(requesterId, getDelegationFilter(requesterId), from, size);
        return roleDozerConverter.convertToDTOList(roleEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfRolesForResource(final String resourceId, final String requesterId) {
        return roleDao.getNumOfRolesForResource(resourceId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> getChildRoles(final String id, final String requesterId, int from, int size) {
        return roleDao.getChildRoles(id, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getChildRolesDto(final String id, final String requesterId, int from, int size) {
        List<RoleEntity> roleEntityList = roleDao.getChildRoles(id, getDelegationFilter(requesterId), from, size);
        return roleDozerConverter.convertToDTOList(roleEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfChildRoles(final String id, final String requesterId) {
        return roleDao.getNumOfChildRoles(id, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> getParentRoles(final String id, final String requesterId, int from, int size) {
        return roleDao.getParentRoles(id, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getParentRolesDto(final String id, final String requesterId, int from, int size) {
        List<RoleEntity> roleEntityList = roleDao.getParentRoles(id, getDelegationFilter(requesterId), from, size);
        return roleDozerConverter.convertToDTOList(roleEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfParentRoles(final String id, final String requesterId) {
        return roleDao.getNumOfParentRoles(id, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional
	public void addChildRole(final String id, 
							 final String childRoleId, 
							 final Set<String> rights,
							 final Date startDate,
							 final Date endDate) {
		if(id != null && childRoleId != null && !id.equals(childRoleId)) {
			final RoleEntity child = roleDao.findById(childRoleId);
			final RoleEntity parent = roleDao.findById(id);
			if(parent != null && child != null) {
				parent.addChild(child, accessRightDAO.findByIds(rights), startDate, endDate);
			}
			roleDao.update(parent);
		}
	}

	@Override
    @Transactional
	public void removeChildRole(final String id, final String childRoleId) {
		if(id != null && childRoleId != null) {
			final RoleEntity child = roleDao.findById(childRoleId);
			final RoleEntity parent = roleDao.findById(id);
			if(parent != null && child != null) {
				parent.removeChild(child);
			}
			roleDao.update(parent);
		}
	}

	@Deprecated
	@Override
    @Transactional(readOnly = true)
    public int getNumOfRolesForGroup(String groupId, final String requesterId) {
        return roleDao.getNumOfRolesForGroup(groupId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleEntity> getRolesForUser(final String userId, final String requesterId, final int from, final int size) {
        return roleDao.getRolesForUser(userId, getDelegationFilter(requesterId), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfRolesForUser(final String userId, final String requesterId) {
        return roleDao.getNumOfRolesForUser(userId, getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public RoleEntity getRoleByName(String roleName, final String requesterId) {
        final RoleSearchBean searchBean = new RoleSearchBean();
        searchBean.setName(roleName);
        final List<RoleEntity> foundList = this.findBeans(searchBean, requesterId, 0, 1);
		return (CollectionUtils.isNotEmpty(foundList)) ? foundList.get(0) : null;
	}

    protected LanguageEntity getDefaultLanguage() {
        return languageDAO.getDefaultLanguage();
    }

    private Set<String> getDelegationFilter(String requesterId) {
        Set<String> filterData = null;
        if(StringUtils.isNotBlank(requesterId)){
            filterData = new HashSet<String>(
                    DelegationFilterHelper.getRoleFilterFromString( userDataService.getUserAttributesDto(requesterId)));
        }
        return filterData;
    }
    
	@Override
	@Transactional
	public void validateRole2RoleAddition(final String parentId, final String memberId, final Set<String> rights, final Date startDate, final Date endDate) throws BasicDataServiceException {
		final RoleEntity parent = roleDao.findById(parentId);
		final RoleEntity child = roleDao.findById(memberId);
		
		if(parent == null || child == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
		}
		
		if(causesCircularDependency(parent, child, new HashSet<RoleEntity>())) {
			throw new BasicDataServiceException(ResponseCode.CIRCULAR_DEPENDENCY);
		}
		
		if(startDate != null && endDate != null && startDate.after(endDate)) {
        	throw new BasicDataServiceException(ResponseCode.ENTITLEMENTS_DATE_INVALID);
        }
		
		if(StringUtils.equals(parentId, memberId)) {
			throw new BasicDataServiceException(ResponseCode.CANT_ADD_YOURSELF_AS_CHILD);
		}
	}
	
	private boolean causesCircularDependency(final RoleEntity parent, final RoleEntity child, final Set<RoleEntity> visitedSet) {
		boolean retval = false;
		if(parent != null && child != null) {
			if (!visitedSet.contains(child)) {
                visitedSet.add(child);
                if (CollectionUtils.isNotEmpty(parent.getParentRoles())) {
                    for (final RoleToRoleMembershipXrefEntity xref : parent.getParentRoles()) {
                    	final RoleEntity entity = xref.getEntity();
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
	@Transactional(readOnly = true)
	public Role getRoleDTO(String id) {
		return roleDozerConverter.convertToDTO(roleDao.findById(id), true);
	}
	
	@Override
	@Transactional
	public void validateGroup2RoleAddition(String roleId, String groupId)
			throws BasicDataServiceException {
		if(roleId == null || groupId == null) {
			throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "GroupId or RoleId  is null or empty");
		}
		
		final RoleEntity role = roleDao.findById(roleId);
		final GroupEntity group = groupDAO.findById(groupId);
		if(role == null || group == null) {
			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND, "No Group or Role objects  are found");
		}
		
		//if(role.hasGroup(group.getId())) {
		//	throw new BasicDataServiceException(ResponseCode.RELATIONSHIP_EXISTS, String.format("Group %s has already been added to role: %s", groupId, roleId));
		//}
	}


    @Override
    @Transactional
    public void addAttribute(RoleAttributeEntity attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (attribute.getRole() == null || StringUtils.isBlank(attribute.getRole().getId())) {
            throw new NullPointerException("Role has not been associated with this attribute.");
        }

        RoleEntity roleEntity = roleDao.findById(attribute.getRole().getId());
        attribute.setRole(roleEntity);


        roleAttributeDAO.save(attribute);
    }

    @Override
    @Transactional
    public void updateAttribute(RoleAttributeEntity attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (attribute.getRole() == null || StringUtils.isBlank(attribute.getRole().getId())) {
            throw new NullPointerException("Role has not been associated with this attribute.");
        }

        final RoleAttributeEntity roleAttribute = roleAttributeDAO.findById(attribute.getId());
        if (roleAttribute != null) {
            RoleEntity roleEntity = roleDao.findById(attribute.getRole().getId());
            attribute.setRole(roleEntity);
            attribute.setMetadataElementId(roleAttribute.getMetadataElementId());

            roleAttributeDAO.merge(attribute);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreeObjectId> getRolesWithSubRolesIds(List<String> roleIds, String requesterId) {
        return roleDao.findRolesWithSubRolesIds(roleIds,  getDelegationFilter(requesterId));
    }

    @Override
    @Transactional(readOnly = true)
    public void rebuildRoleHierarchyCache() {
        log.info("Role Hierarchy Cache preparation running ....");
        roleDao.rolesHierarchyRebuild();
        log.info("Role Hierarchy Cache preparation done.");
    }

	@Override
	public boolean hasChildEntities(String roleId) {
		final RoleEntity role = roleDao.findById(roleId);
		if(role != null) {
			return CollectionUtils.isNotEmpty(role.getChildRoles()) || CollectionUtils.isNotEmpty(role.getResources());
		} else {
			return false;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public RoleEntity getRoleByNameAndManagedSysId(final String roleName, final String managedSysId) {
		final RoleSearchBean sb = new RoleSearchBean();
		sb.setName(roleName);
		sb.setManagedSysId(managedSysId);
		final List<RoleEntity> roles = roleDao.getByExample(sb);
		if(roles != null) {
			if(roles.size() == 1) {
				return roles.get(0);
			} else if(roles.size() > 1) {
				log.error(String.format("Multiple ROle with name '%s' and managed system %s'", roleName, managedSysId));
				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

    private RoleDataService getProxyService() {
        RoleDataService service = (RoleDataService) ac.getBean("roleDataService");
        return service;
    }

    public List<RoleEntity> getUserRoles(String userId, final String requesterId, int from, int size) {
        return roleDao.getRolesForUser(userId, getDelegationFilter(requesterId), from, size);
    }
}
