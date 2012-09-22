package org.openiam.dozer;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.dozer.DozerBeanMapper;
import org.openiam.idm.srvc.grp.dto.Group;
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
	
	public Supervisor getDozerDeepMappedSupervisor(final Supervisor supervisor) {
		Supervisor retVal = null;
		if(supervisor != null) {
			retVal = deepMapper.map(supervisor, Supervisor.class);
		}
		return retVal;
	}
	
	public User getDozerDeepMappedUser(final User user) {
		User retVal = null;
		if(user != null) {
			retVal = deepMapper.map(user, User.class);
		}
		return retVal;
	}
	
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
	
	public List<Supervisor> getDozerDeepMappedSupervisorList(final Collection<Supervisor> supervisorList) {
		final List<Supervisor> convertedSupervisorList = new LinkedList<Supervisor>();
		if(CollectionUtils.isNotEmpty(supervisorList)) {
			for(final Supervisor supervisor : supervisorList) {
				convertedSupervisorList.add(deepMapper.map(supervisor, Supervisor.class));
			}
		}
		return convertedSupervisorList;
	}
	
	public List<Role> getDozerDeepMappedRoleList(final Collection<Role> roleList) {
		final List<Role> convertedList = new LinkedList<Role>();
		if(CollectionUtils.isNotEmpty(roleList)) {
			for(final Role role : roleList) {
				convertedList.add(deepMapper.map(role, Role.class));
			}
		}
		return convertedList;
	}
	
	public Group[] getDozerDeepMappedGroupArray(final Group[] groupArray) {
		Group[] retVal = null;
		if(groupArray != null) {
			retVal = new Group[groupArray.length];
			for(int i = 0; i < groupArray.length; i++) {
				retVal[i] = deepMapper.map(groupArray[i], Group.class);
			}
		}
		return retVal;
	}
	
	public Role getDozerDeepMappedRole(final Role role) {
		Role retVal = null;
		if(role != null) {
			retVal = deepMapper.map(role, Role.class);
		}
		return retVal;
	}
	
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
	
	public List<PolicyDef> getDozerDeepMappedPolicyDefList(final Collection<PolicyDef> policyDefList) {
		final List<PolicyDef> convertedPolicyDefList = new LinkedList<PolicyDef>();
		if(CollectionUtils.isNotEmpty(policyDefList)) {
			for(final PolicyDef policyDef : policyDefList) {
				convertedPolicyDefList.add(deepMapper.map(policyDef, PolicyDef.class));
			}
		}
		return convertedPolicyDefList;
	}
	
	public List<Policy> getDozerDeepMappedPolicyList(final Collection<Policy> policyList) {
		final List<Policy> convertedPolicyList = new LinkedList<Policy>();
		if(CollectionUtils.isNotEmpty(policyList)) {
			for(final Policy policy : policyList) {
				convertedPolicyList.add(deepMapper.map(policy, Policy.class));
			}
		}
		return convertedPolicyList;
	}
	
	public Policy getDozerDeepMappedPolicy(final Policy policy) {
		Policy retVal = null;
		if(policy != null) {
			retVal = deepMapper.map(policy, Policy.class);
		}
		return retVal;
	}
	
	public List<PolicyDefParam> getDozerDeepMappedPolicyDefParamList(final Collection<PolicyDefParam> policyDefParamList) {
		final List<PolicyDefParam> convertedList = new LinkedList<PolicyDefParam>();
		if(CollectionUtils.isNotEmpty(policyDefParamList)) {
			for(final PolicyDefParam param : policyDefParamList) {
				convertedList.add(deepMapper.map(param, PolicyDefParam.class));
			}
		}
		return convertedList;
	}
	
	public PolicyDef getDozerDeepMappedPolicyDef(final PolicyDef policyDef) {
		PolicyDef retVal = null;
		if(policyDef != null) {
			retVal = deepMapper.map(policyDef, PolicyDef.class);
		}
		return retVal;
	}
	
    public Resource getDozerDeepMappedResource(final Resource resource) {
    	Resource retVal = null;
    	if(resource != null) {
    		retVal = deepMapper.map(resource, Resource.class);
    	}
    	return retVal;
    }
    
    public List<Resource> getDozerDeepMappedResourceList(final Collection<Resource> resourceList) {
    	final List<Resource> convertedList = new LinkedList<Resource>();
    	if(CollectionUtils.isNotEmpty(resourceList)) {
    		for(final Resource resource : resourceList) {
    			convertedList.add(deepMapper.map(resource, Resource.class));
    		}
    	}
    	return convertedList;
    }
	
    public ResourceUser getDozerDeepMappedResourceUser(final ResourceUser resourceUser) {
		ResourceUser retVal = null;
		if(resourceUser != null) {
			retVal = deepMapper.map(resourceUser, ResourceUser.class);
		}
		return retVal;
	}
	
    public List<ResourceUser> getDozerDeepMappedResourceUserList(final Collection<ResourceUser> resourceUserList) {
		final List<ResourceUser> convertedList = new LinkedList<ResourceUser>();
		if(CollectionUtils.isNotEmpty(resourceUserList)) {
			for(final ResourceUser resourceUser : resourceUserList) {
				convertedList.add(deepMapper.map(resourceUser, ResourceUser.class));
			}
		}
		return convertedList;
	}
    
	public List<Group> getDozerDeepMappedGroupList(final Collection<Group> groupList) {
		final List<Group> convertedGroupList = new LinkedList<Group>();
		if(CollectionUtils.isNotEmpty(groupList)) {
			for(final Group group : groupList) {
				convertedGroupList.add(deepMapper.map(group, Group.class));
			}
		}
		return convertedGroupList;
	}
	
	public Group getDozerDeepMappedGroup(final Group group) {
		Group retVal = null;
		if(group != null) {
			retVal = deepMapper.map(group, Group.class);
		}
		return retVal;
	}
}
