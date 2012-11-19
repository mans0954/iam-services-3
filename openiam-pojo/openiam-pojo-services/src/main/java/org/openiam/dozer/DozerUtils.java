package org.openiam.dozer;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyDef;
import org.openiam.idm.srvc.policy.dto.PolicyDefParam;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceUser;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.util.DozerMappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;

@Deprecated
public class DozerUtils {

	private DozerBeanMapper deepMapper;
	private DozerBeanMapper shallowMapper;
	
	@Required
	public void setDeepMapper(final DozerBeanMapper deepMapper) {
		this.deepMapper = deepMapper;
	}
	
	@Required
	public void setShallowMapper(final DozerBeanMapper shallowMapper) {
		this.shallowMapper = shallowMapper;
	}

    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param supervisor
     * @return
     */
    @Deprecated
	public Supervisor getDozerDeepMappedSupervisor(final Supervisor supervisor) {
		Supervisor retVal = null;
		if(supervisor != null) {
			retVal = deepMapper.map(supervisor, Supervisor.class);
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param user
     * @return
     */
    @Deprecated
	public User getDozerDeepMappedUser(final User user) {
		User retVal = null;
		if(user != null) {
			retVal = deepMapper.map(user, User.class);
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param userList
     * @return
     */
    @Deprecated
	public List<User> getDozerDeepMappedUserList(final Collection<User> userList) {
		final List<User> convertedUserList = new LinkedList<User>();
		if(CollectionUtils.isNotEmpty(userList)) {
			for(final User user : userList) {
				final User converted = deepMapper.map(user, User.class);
				convertedUserList.add(converted);
			}
		}
		return convertedUserList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param supervisorList
     * @return
     */
    @Deprecated
	public List<Supervisor> getDozerDeepMappedSupervisorList(final Collection<Supervisor> supervisorList) {
		final List<Supervisor> convertedSupervisorList = new LinkedList<Supervisor>();
		if(CollectionUtils.isNotEmpty(supervisorList)) {
			for(final Supervisor supervisor : supervisorList) {
				convertedSupervisorList.add(deepMapper.map(supervisor, Supervisor.class));
			}
		}
		return convertedSupervisorList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param roleList
     * @return
     */
    @Deprecated
	public List<Role> getDozerDeepMappedRoleList(final Collection<Role> roleList) {
		final List<Role> convertedList = new LinkedList<Role>();
		if(CollectionUtils.isNotEmpty(roleList)) {
			for(final Role role : roleList) {
				convertedList.add(deepMapper.map(role, Role.class));
			}
		}
		return convertedList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param groupArray
     * @return
     */
    @Deprecated
	public GroupEntity[] getDozerDeepMappedGroupArray(final GroupEntity[] groupArray) {
    	GroupEntity[] retVal = null;
		if(groupArray != null) {
			retVal = new GroupEntity[groupArray.length];
			for(int i = 0; i < groupArray.length; i++) {
				retVal[i] = deepMapper.map(groupArray[i], GroupEntity.class);
			}
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param role
     * @return
     */
    @Deprecated
	public Role getDozerDeepMappedRole(final Role role) {
		Role retVal = null;
		if(role != null) {
			retVal = deepMapper.map(role, Role.class);
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param userArray
     * @return
     */
    @Deprecated
	public User[] getDozerDeepMappedUserArray(final User[] userArray) {
		User[] retVal = null;
		if(userArray != null) {
			retVal = new User[userArray.length];
			for(int i = 0; i < userArray.length; i++) {
				retVal[i] = deepMapper.map(userArray[i], User.class);
			}
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param policyDefList
     * @return
     */
    @Deprecated
	public List<PolicyDef> getDozerDeepMappedPolicyDefList(final Collection<PolicyDef> policyDefList) {
		final List<PolicyDef> convertedPolicyDefList = new LinkedList<PolicyDef>();
		if(CollectionUtils.isNotEmpty(policyDefList)) {
			for(final PolicyDef policyDef : policyDefList) {
				convertedPolicyDefList.add(deepMapper.map(policyDef, PolicyDef.class));
			}
		}
		return convertedPolicyDefList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param policyList
     * @return
     */
    @Deprecated
	public List<Policy> getDozerDeepMappedPolicyList(final Collection<Policy> policyList) {
		final List<Policy> convertedPolicyList = new LinkedList<Policy>();
		if(CollectionUtils.isNotEmpty(policyList)) {
			for(final Policy policy : policyList) {
				convertedPolicyList.add(deepMapper.map(policy, Policy.class));
			}
		}
		return convertedPolicyList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param policy
     * @return
     */
    @Deprecated
	public Policy getDozerDeepMappedPolicy(final Policy policy) {
		Policy retVal = null;
		if(policy != null) {
			retVal = deepMapper.map(policy, Policy.class);
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param policyDefParamList
     * @return
     */
    @Deprecated
	public List<PolicyDefParam> getDozerDeepMappedPolicyDefParamList(final Collection<PolicyDefParam> policyDefParamList) {
		final List<PolicyDefParam> convertedList = new LinkedList<PolicyDefParam>();
		if(CollectionUtils.isNotEmpty(policyDefParamList)) {
			for(final PolicyDefParam param : policyDefParamList) {
				convertedList.add(deepMapper.map(param, PolicyDefParam.class));
			}
		}
		return convertedList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param policyDef
     * @return
     */
    @Deprecated
	public PolicyDef getDozerDeepMappedPolicyDef(final PolicyDef policyDef) {
		PolicyDef retVal = null;
		if(policyDef != null) {
			retVal = deepMapper.map(policyDef, PolicyDef.class);
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param resource
     * @return
     */
    @Deprecated
    public Resource getDozerDeepMappedResource(final Resource resource) {
    	Resource retVal = null;
    	if(resource != null) {
    		retVal = deepMapper.map(resource, Resource.class);
    	}
    	return retVal;
    }
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param resourceList
     * @return
     */
    @Deprecated
    public List<Resource> getDozerDeepMappedResourceList(final Collection<Resource> resourceList, final DozerMappingType type) {
    	final List<Resource> convertedList = new LinkedList<Resource>();
    	if(CollectionUtils.isNotEmpty(resourceList)) {
    		for(final Resource resource : resourceList) {
    			if(type == null || DozerMappingType.DEEP.ordinal() == type.ordinal()) {
    				convertedList.add(deepMapper.map(resource, Resource.class));
    			} else {
    				convertedList.add(shallowMapper.map(resource, Resource.class));
    			}
    		}
    	}
    	return convertedList;
    }
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param resourceList
     * @return
     */
    @Deprecated
    public List<Resource> getDozerDeepMappedResourceList(final Collection<Resource> resourceList) {
    	final List<Resource> convertedList = new LinkedList<Resource>();
    	if(CollectionUtils.isNotEmpty(resourceList)) {
    		for(final Resource resource : resourceList) {
    			convertedList.add(deepMapper.map(resource, Resource.class));
    		}
    	}
    	return convertedList;
    }
    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param resourceUser
     * @return
     */
    @Deprecated
    public ResourceUser getDozerDeepMappedResourceUser(final ResourceUser resourceUser) {
		ResourceUser retVal = null;
		if(resourceUser != null) {
			retVal = deepMapper.map(resourceUser, ResourceUser.class);
		}
		return retVal;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param resourceUserList
     * @return
     */
    @Deprecated
    public List<ResourceUser> getDozerDeepMappedResourceUserList(final Collection<ResourceUser> resourceUserList) {
		final List<ResourceUser> convertedList = new LinkedList<ResourceUser>();
		if(CollectionUtils.isNotEmpty(resourceUserList)) {
			for(final ResourceUser resourceUser : resourceUserList) {
				convertedList.add(deepMapper.map(resourceUser, ResourceUser.class));
			}
		}
		return convertedList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedList() instead
     * @param groupList
     * @return
     */
    @Deprecated
	public List<Group> getDozerDeepMappedGroupList(final Collection<Group> groupList) {
		final List<Group> convertedGroupList = new LinkedList<Group>();
		if(CollectionUtils.isNotEmpty(groupList)) {
			for(final Group group : groupList) {
				convertedGroupList.add(deepMapper.map(group, Group.class));
			}
		}
		return convertedGroupList;
	}
    /**
     * This method is deprecated. Please use getDozerMappedObject() instead
     * @param group
     * @return
     */
	@Deprecated
	public Group getDozerDeepMappedGroup(final Group group) {
		Group retVal = null;
		if(group != null) {
			retVal = deepMapper.map(group, Group.class);
		}
		return retVal;
	}



    public <T> T getDozerMappedObject(final T t) {
        return getDozerMappedObject(t, DozerMappingType.DEEP);
    }

    public <T> T getDozerMappedObject(final T t, final DozerMappingType type) {
        T retVal = null;
        if(t != null) {
            Class<T> clazz  = (Class<T>)t.getClass();

            if(type == null || DozerMappingType.DEEP == type) {
                retVal = deepMapper.map(t, clazz);
            } else {
                retVal = shallowMapper.map(t, clazz);
            }
        }
        return retVal;
    }

    public <T> List<T> getDozerMappedList(final Collection<T> list) {
        return getDozerMappedList(list, DozerMappingType.DEEP);
    }

    public <T> List<T> getDozerMappedList(final Collection<T> collection, final DozerMappingType type) {
        final List<T> convertedList = new LinkedList<T>();
        if(CollectionUtils.isNotEmpty(collection)) {
            Class<T> clazz  = (Class<T>)collection.iterator().next().getClass();

            for(final T t : collection) {
                if(type == null || DozerMappingType.DEEP == type) {
                    convertedList.add(deepMapper.map(t, clazz));
                } else {
                    convertedList.add(shallowMapper.map(t, clazz));
                }
            }
        }
        return convertedList;
    }
}
