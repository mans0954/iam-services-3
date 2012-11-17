package org.openiam.idm.srvc.role.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.dozer.converter.UserRoleDozerConverter;
import org.openiam.exception.data.ObjectNotFoundException;
import org.openiam.idm.srvc.grp.dto.Group;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.res.service.ResourceRoleDAO;
import org.openiam.idm.srvc.role.dto.Role;
import org.openiam.idm.srvc.role.dto.RoleAttribute;
import org.openiam.idm.srvc.role.dto.RoleConstant;
import org.openiam.idm.srvc.role.dto.RolePolicy;
import org.openiam.idm.srvc.role.dto.RoleSearch;
import org.openiam.idm.srvc.role.dto.UserRole;
import org.openiam.idm.srvc.user.dto.User;

import org.openiam.idm.srvc.user.dto.UserConstant;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

//Note: as per spec serviceName goes in impl class and name goes in interface
		

public class RoleDataServiceImpl implements RoleDataService {

	private RoleDAO roleDao;
	private RoleAttributeDAO roleAttributeDAO;
	private UserDataService userManager;
	private UserRoleDAO userRoleDao;
	private UserGroupDAO userGroupDao;
	private RolePolicyDAO rolePolicyDao;
    private ResourceRoleDAO resRoleDao;
    @Autowired
    private UserDozerConverter userDozerConverter;
    @Autowired
    private UserRoleDozerConverter userRoleDozerConverter;

	private static final Log log = LogFactory.getLog(RoleDataServiceImpl.class);

	public RoleDAO getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(RoleDAO roleDao) {
		this.roleDao = roleDao;
	}

	public Role addRole(Role role) {
		if (role == null)
			throw new IllegalArgumentException("role object is null");

		roleDao.add(role);
		return role;
	}

	public Role getRole(String roleId) {
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");

		Role rl = roleDao.findById(roleId);
		
		//if (!org.hibernate.Hibernate.isInitialized(rl.getUsers())) {
//		if (rl != null) {
//			org.hibernate.Hibernate.initialize(rl.getUsers());
//			org.hibernate.Hibernate.initialize(rl.getGroups());	
//		}
		return rl;

	}
	

	public void updateRole(Role role) {
		if (role == null)
			throw new IllegalArgumentException("role object is null");

		roleDao.update(role);

	}

	/**
	 * Returns a list of all Roles regardless of service The list is sorted by
	 * ServiceId, Role
	 * 
	 * @return
	 */
	public List<Role> getAllRoles() {

		return roleDao.findAllRoles();

	}

	public int removeRole(String roleId) {
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");

		Role rl = new Role(roleId);

        try {
            this.roleAttributeDAO.deleteRoleAttributes(roleId);
            this.userRoleDao.removeAllUsersInRole(roleId);
            this.resRoleDao.deleteByRoleId(roleId);
            this.roleDao.remove(rl);
        }catch (Exception e) {
            log.error(e.toString());
            return 0;
        }
        return 1;
	}

	public List<Role> getRolesInDomain(String domainId) {
		long start = System.currentTimeMillis();
		
		List<Role> rlList = roleDao.findRolesInService(domainId);
		
		long end = System.currentTimeMillis();
		log.debug("findRolesInService: " + (end-start));
		
		if (rlList == null || rlList.size() == 0)
			return null;
		return rlList;
		

	}

	/* ---------------------- RoleAttribute Methods --------------- */

	public RoleAttribute addAttribute(RoleAttribute attribute) {
		if (attribute == null)
			throw new IllegalArgumentException("Attribute can not be null");

		if (attribute.getRoleId() == null) {
			throw new IllegalArgumentException(
					"Role has not been associated with this attribute.");
		}

		roleAttributeDAO.add(attribute);
		return attribute;
	}

	public RoleAttribute[] getAllAttributes(String roleId) {

		if (roleId == null) {
			throw new IllegalArgumentException("groupId is null");
		}

		Role role = roleDao.findById(roleId);
		Set attrSet = role.getRoleAttributes();
		if (attrSet != null && attrSet.isEmpty())
			return null;
		return this.roleAttrSetToArray(attrSet);
	}

	public RoleAttribute getAttribute(String attrId) {
		if (attrId == null) {
			throw new IllegalArgumentException("attrId is null");
		}
		return roleAttributeDAO.findById(attrId);
	}

	public void removeAllAttributes(String roleId) {
		if (roleId == null) {
			throw new IllegalArgumentException("roleId is null");
		}
		this.roleAttributeDAO.deleteRoleAttributes(roleId);

	}

	public void removeAttribute(RoleAttribute attr) {
		if (attr == null) {
			throw new IllegalArgumentException("attr is null");
		}
		if (attr.getRoleAttrId() == null) {
			throw new IllegalArgumentException("attrId is null");
		}

		roleAttributeDAO.remove(attr);

	}

	public void updateAttribute(RoleAttribute attribute) {
		if (attribute == null)
			throw new IllegalArgumentException("Attribute can not be null");
		if (attribute.getRoleAttrId() == null) {
			throw new IllegalArgumentException("Attribute id is null");
		}
		if (attribute.getRoleId() == null) {
			throw new IllegalArgumentException(
					"Role has not been associated with this attribute.");
		}

		roleAttributeDAO.update(attribute);
	}




	/* ------------- Group to Role Methods --------------------------------- */

	public void addGroupToRole(String roleId, String groupId) {
		// TODO Auto-generated method stub
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		if (groupId == null)
			throw new IllegalArgumentException("groupId is null");

		roleDao.addGroupToRole(roleId, groupId);

	}

	public Group[] getGroupsInRole(String roleId) {
		Role rl = roleDao.findById(roleId);
		if (rl == null) {
			log.error("Role not found for roleId =" + roleId);
			throw new ObjectNotFoundException();
		}
		//org.hibernate.Hibernate.initialize(rl.getGroups());
		Set<Group> grpSet = rl.getGroups();
		if (grpSet == null || grpSet.isEmpty()) {
			return null;
		}
		return this.groupSetToArray(grpSet);

	}

	public boolean isGroupInRole(String roleId, String groupId) {

		Role rl = roleDao.findById(roleId);
		if (rl == null) {
			log.error("Role not found for roleId =" + roleId);
			throw new ObjectNotFoundException();
		}
		//org.hibernate.Hibernate.initialize(rl.getGroups());
		Set<Group> grpSet = rl.getGroups();
		if (grpSet == null || grpSet.isEmpty()) {
			return false;
		}
		Iterator<Group> it = grpSet.iterator();
		while (it.hasNext()) {
			Group g = it.next();
			if (g.getGrpId().equalsIgnoreCase(groupId)) {
				return true;
			}
		}
		return false;
	}

	public void removeGroupFromRole(String roleId, String groupId) {
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		if (groupId == null)
			throw new IllegalArgumentException("groupId object is null");

		this.roleDao.removeGroupFromRole(roleId, groupId);

	}

	public void removeAllGroupsFromRole(String roleId) {
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		roleDao.removeAllGroupsFromRole(roleId);
	}

	public List<Role> getRolesInGroup(String groupId) {
		// TODO Auto-generated method stub

		if (groupId == null)
			throw new IllegalArgumentException("groupid is null");

		List<Role> roleList = roleDao.findRolesInGroup(groupId);
		if (roleList == null || roleList.isEmpty())
			return null;
		return roleList;


	}


	
	/* ------------- User to Role Methods --------------------------------- */
	
	/**
	 * Adds a user to a role using the UserRole object. Similar to addUserToRole, but allows you to update attributes likes start and end date.
	 */
	public void assocUserToRole(UserRole ur) {
		if (ur.getRoleId() == null)
			throw new IllegalArgumentException("roleId is null");
		if (ur.getUserId() == null)
			throw new IllegalArgumentException("userId object is null");	
		
		ur.setUserRoleId(null);
		userRoleDao.save(userRoleDozerConverter.convertToEntity(ur,false));
	}
	
	/**
	 * Updates the attributes in the user role object.
	 * @param ur
	 */
	public void updateUserRoleAssoc(UserRole ur) {
		if (ur.getRoleId() == null)
			throw new IllegalArgumentException("roleId is null");
		if (ur.getUserId() == null)
			throw new IllegalArgumentException("userId object is null");		
		userRoleDao.update(userRoleDozerConverter.convertToEntity(ur,false));
	}
	
	public UserRole getUserRoleById(String userRoleId ) {
		if (userRoleId == null) {
			throw new IllegalArgumentException("userRoleId is null");
		}
		return userRoleDozerConverter.convertToDTO(userRoleDao.findById(userRoleId),false);
		
	}
	
	public List<UserRole> getUserRolesForUser(String userId) {
		if (userId == null) {
			throw new IllegalArgumentException("userId is null");
		}
		return userRoleDozerConverter.convertToDTOList(userRoleDao.findUserRoleByUser(userId),false);
	}
	
	
	public void addUserToRole(String roleId, String userId) {

		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		if (userId == null)
			throw new IllegalArgumentException("userId object is null");
		
		final UserRole ur = new UserRole();
		ur.setUserId(userId);
		ur.setRoleId(roleId);
		
		userRoleDao.save(userRoleDozerConverter.convertToEntity(ur,false));

	}
	
	public boolean isUserInRole(String roleId, String userId) {
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		if (userId == null)
			throw new IllegalArgumentException("userIdId object is null");
	
		List<Role> userRoleList = this.getUserRolesAsFlatList(userId);
		if (userRoleList == null) {
			return false;
		}
		for (Role rl : userRoleList) {
			if (rl.getRoleId().equalsIgnoreCase(roleId)) {
				return true;
			}
		}
		return false;
	}

	public void removeUserFromRole(String roleId, String userId) {
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		if (userId == null)
			throw new IllegalArgumentException("userId object is null");

		userRoleDao.removeUserFromRole(roleId, userId);
	}

	/**
	 * Returns the roles that are directly associated with a user; ie. Does not take into
	 * account roles that may be associated with a user becuase of a group relationship.
	 * @param userId
	 * @return
	 */
	public List<Role> getUserRolesDirect(String userId) {
		if (userId == null)
			throw new IllegalArgumentException("userIdId is null");

		List<Role> roleList = roleDao.findUserRoles(userId);
		if (roleList == null || roleList.size() == 0)
			return null;
		
		Set<Role> newRoleSet = new HashSet();
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.DIRECT, newRoleSet);
		}
		if (roleList == null || roleList.size() == 0) {
			return null;
		}
		return roleList;
		
	}
	
	
	/**
	 * Returns an array of roles that a user belongs to.
	 */
/*	public List<Role> getUserRoles(String userId) {
		if (userId == null)
			throw new IllegalArgumentException("userIdId is null");

		List<Role> roleList = roleDao.findUserRoles(userId);
		
		log.debug("getUserRoles for userId=" + userId);
		log.debug(" - findUserRoles = " + roleList);
		
		
		Set<Role> newRoleSet = new HashSet();
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.DIRECT, newRoleSet);
		}
		
		roleList =  roleDao.findIndirectUserRoles(userId);
		
		log.debug(" - findIndirectUserRoles = " + roleList);
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.INDIRECT, newRoleSet);
		}
		if (newRoleSet == null || newRoleSet.size() == 0) {
			return null;
		}
		List<Role> newRoles = new ArrayList<Role>(newRoleSet);
		return newRoles;
		

	}
*/

	/**
	 * Returns an array of Role objects that indicate the Roles a user is
	 * associated to.
	 * 
	 * @param userId
	 * @return
	 */
	public List<Role> getUserRoles(String userId) {
		if (userId == null)
			throw new IllegalArgumentException("userIdId is null");

		List<Role> roleList = roleDao.findUserRoles(userId);
		
		log.debug("getUserRoles for userId=" + userId);
		log.debug(" - findUserRoles = " + roleList);
		
		
		Set<Role> newRoleSet = new HashSet();
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.DIRECT, newRoleSet);
		}
		
		roleList =  roleDao.findIndirectUserRoles(userId);
		
		log.debug(" - findIndirectUserRoles = " + roleList);
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.INDIRECT, newRoleSet);
		}
		if (newRoleSet == null || newRoleSet.size() == 0) {
			return null;
		}
		List<Role> newRoles = new ArrayList<Role>(newRoleSet);
		// for each of these roles, figure out if there are roles above it in the hierarchy
		
		Set<Role> compiledSet = new LinkedHashSet<Role>();
		for (Role rl : newRoleSet ) {
			visitParentRoles(rl.getRoleId(), compiledSet);
		}
		
		
		return new ArrayList<Role>(compiledSet);

		
		//return newRoles;

	}
	
	private void visitChildRoles(final String roleId, final Set<Role> visitedSet) {
		if(roleId != null) {
			if(visitedSet != null) {
				final Role role = roleDao.findById(roleId);
				if(role != null) {
					if(!visitedSet.contains(role)) {
						visitedSet.add(role);
						if(CollectionUtils.isNotEmpty(role.getChildRoles())) {
							for(final Role child : role.getChildRoles()) {
								visitChildRoles(child.getRoleId(), visitedSet);
							}
						}
					}
				}
			}
		}
	}
	
	private void visitParentRoles(final String roleId, final Set<Role> visitedSet) {
		if(roleId != null) {
			if(visitedSet != null) {
				final Role role = roleDao.findById(roleId);
				if(role != null) {
					if(!visitedSet.contains(role)) {
						visitedSet.add(role);
						if(CollectionUtils.isNotEmpty(role.getParentRoles())) {
							for(final Role child : role.getParentRoles()) {
								visitParentRoles(child.getRoleId(), visitedSet);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Returns a list of roles that a user belongs to. Roles can be hierarchical and this operation traverses the tree to roles that are in the 
	 * hierarchy.
	 * @param userId
	 * @return
	 */
	public List<Role> getUserRolesAsFlatList(String userId) {
		if (userId == null)
			throw new IllegalArgumentException("userIdId is null");

		List<Role> roleList = roleDao.findUserRoles(userId);
		
		log.debug("getUserRoles for userId=" + userId);
		log.debug(" - findUserRoles = " + roleList);
		
		
		Set<Role> newRoleSet = new HashSet();
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.DIRECT, newRoleSet);
		}
		
		roleList =  roleDao.findIndirectUserRoles(userId);
		
		log.debug(" - findIndirectUserRoles = " + roleList);
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.INDIRECT, newRoleSet);
		}
		if (newRoleSet == null || newRoleSet.size() == 0) {
			return null;
		}
		List<Role> newRoles = new ArrayList<Role>(newRoleSet);
		// for each of these roles, figure out if there are roles above it in the hierarchy

        // store the roles in sorted order
        Set<Role> roleSet = new TreeSet<Role>();
		for (Role rl : newRoles ) {
			visitParentRoles(rl.getRoleId(), roleSet);
		}

        List<Role> newRoleList = new ArrayList<Role>(roleSet);
		return newRoleList;		
	}
	
	
	public List<Role> getUserRolesByDomain(String domainId,  String userId) {
		if (userId == null)
			throw new IllegalArgumentException("userIdId is null");

		List<Role> roleList = roleDao.findUserRolesByService(domainId,userId);
		if (roleList == null || roleList.size() == 0)
			return null;
		
		Set<Role> newRoleSet = new HashSet();
		
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.DIRECT, newRoleSet);
		}
		
		roleList =  roleDao.findIndirectUserRoles(userId);
		if (roleList != null && !roleList.isEmpty()) {
			updateRoleAssociation(roleList, RoleConstant.INDIRECT, newRoleSet);
		}
		if (roleList == null || roleList.size() == 0) {
			return null;
		}
		return roleList;

	}
	


	public User[] getUsersInRole(String roleId) {
		if (roleId == null)
			throw new IllegalArgumentException("roleId is null");
		
		/* Get the users that are directly associated */
		Role rl = getRole(roleId);
		
		
		//System.out.println("in getUsersInRole: rl=" + rl);
		//System.out.println("in getUsersInRole: users =" + rl.getUsers());
		
		List<User> userList = userDozerConverter.convertToDTOList(userRoleDao.findUserByRole(roleId),true);

        // No direct association, continue with indirect
		if (userList == null || userList.isEmpty())
            userList = new ArrayList<User>();

		Set<User> newUserSet = updateUserRoleAssociation(userList, UserConstant.DIRECT);

		/* Get the users that are linked through a group */
	 	Set<Group> groupSet = rl.getGroups();
	 	// ensure that we have a unique set of users.
	 	// iterate through the groups
	 	if (groupSet != null && !groupSet.isEmpty()) {
	 		Iterator<Group> it = groupSet.iterator();
	 		while (it.hasNext()) {
	 			Group grp = it.next();
	 			List<User> userLst = userDozerConverter.convertToDTOList(userGroupDao.findUserByGroup(grp.getGrpId()), true);
	 			//Set<User> grpUsers = grp.getUsers();
	 			userSetToNewUserSet(userLst, UserConstant.INDIRECT, newUserSet);
	 		}
	 	}
	 	int size = newUserSet.size();
        // no users found, return null
        if(size == 0)
            return null;
	 	User[] userAry = new User[size];
	 	return newUserSet.toArray(userAry);
	 	
		
	}
	
	
	
	/** **************** Helper Methods ***************************** */

	/**
	 * Converts a list of Role objects into an Array
	 * 
	 * @param roleList
	 * @return
	 */
	private Role[] roleListToArray(List<Role> roleList) {

		if (roleList == null || roleList.size() == 0)
			return null;

		int size = roleList.size();
		Role[] roleAry = new Role[size];
		for (int ctr = 0; ctr < size; ctr++) {
			Role rl = roleList.get(ctr);
			roleAry[ctr] = rl;
		}
		return roleAry;

	}

	private RoleAttribute[] roleAttrSetToArray(Set<RoleAttribute> attrSet) {

		int size = attrSet.size();
		RoleAttribute[] roleAttrAry = new RoleAttribute[size];
		Iterator<RoleAttribute> it = attrSet.iterator();
		int ctr = 0;
		while (it.hasNext()) {
			RoleAttribute ra = it.next();
			roleAttrAry[ctr++] = ra;
		}
		return roleAttrAry;

	}

	private Group[] groupSetToArray(Set<Group> groupSet) {

		int size = groupSet.size();
		Group[] groupAry = new Group[size];
		Iterator<Group> it = groupSet.iterator();
		int ctr = 0;
		while (it.hasNext()) {
			Group ra = it.next();
			groupAry[ctr++] = ra;
		}
		return groupAry;

	}

	private User[] userSetToArray(Set<User> userSet) {

		int size = userSet.size();
		User[] userAry = new User[size];
		Iterator<User> it = userSet.iterator();
		int ctr = 0;
		while (it.hasNext()) {
			User u = it.next();
			userAry[ctr++] = u;
		}
		return userAry;

	}
	private User[] userCollectionToArray(Collection<User> userCol) {

		int size = userCol.size();
		User[] userAry = new User[size];
		return  userCol.toArray(userAry);
		


	}
	
	private Set<User> updateUserRoleAssociation(List<User> userList, int roleAssociationMethod) {

		Set<User> newUserSet = new HashSet();
		
		for ( User u :userList) {
			newUserSet.add(u);
		}

		return newUserSet;


	}
	
	private void updateRoleAssociation(List<Role> roleList, int associationMethod, Set<Role> newRoleSet) {
		int size = roleList.size();
		for (int i=0; i<size; i++) {
			Role rl = roleList.get(i);
			rl.setUserAssociationMethod(RoleConstant.DIRECT);
			newRoleSet.add(rl);
		}
		//return newRoleSet;		
	}

	private void userSetToNewUserSet(List<User> userList, int roleAssociationMethod, Set<User> newUserSet) {

		for ( User u : userList) {
			newUserSet.add(u);
		}

	}	
	
	public List<Role> search(RoleSearch search) {
		if (search == null) {
			throw new IllegalArgumentException("Search parameter is null");
		}
		List<Role> roleList = roleDao.search(search);
		if (roleList == null || roleList.isEmpty()) {
			return null;
		}
		if (roleList == null || roleList.size() == 0) {
			return null;
		}
		return roleList;
		
	}
	
	public UserDataService getUserManager() {
		return userManager;
	}

	public void setUserManager(UserDataService userManager) {
		this.userManager = userManager;
	}

	public RoleAttributeDAO getRoleAttributeDAO() {
		return roleAttributeDAO;
	}

	public void setRoleAttributeDAO(RoleAttributeDAO roleAttributeDAO) {
		this.roleAttributeDAO = roleAttributeDAO;
	}

	public UserRoleDAO getUserRoleDao() {
		return userRoleDao;
	}

	public void setUserRoleDao(UserRoleDAO userRoleDao) {
		this.userRoleDao = userRoleDao;
	}

	public UserGroupDAO getUserGroupDao() {
		return userGroupDao;
	}

	public void setUserGroupDao(UserGroupDAO userGroupDao) {
		this.userGroupDao = userGroupDao;
	}

	/* Role Policies */
	public RolePolicy addRolePolicy(RolePolicy rPolicy) {
		if (rPolicy == null) {
			throw new NullPointerException("rPolicy is null");
		}
		rolePolicyDao.add(rPolicy);
		return rPolicy;
	}

	public RolePolicy updateRolePolicy(RolePolicy rPolicy) {
		if (rPolicy == null) {
			throw new NullPointerException("rPolicy is null");
		}
		return rolePolicyDao.update(rPolicy);

	}
	
	public List<RolePolicy> getAllRolePolicies(String roleId) {
		if (roleId == null) {
			throw new NullPointerException("roleId is null");
		}
		return rolePolicyDao.findRolePolicies(roleId);
	}

	public RolePolicy getRolePolicy(String rolePolicyId) {
		if (rolePolicyId == null) {
			throw new NullPointerException("rolePolicyId is null");
		}
		return rolePolicyDao.findById(rolePolicyId);
	}

	public void removeRolePolicy(RolePolicy rPolicy) {
		if (rPolicy == null) {
			throw new NullPointerException("rPolicy is null");
		}
		rolePolicyDao.remove(rPolicy);
	}
	
	@Override
	public Role getRoleByName(String roleName) {
		return roleDao.getRoleByName(roleName);
	}

	public RolePolicyDAO getRolePolicyDao() {
		return rolePolicyDao;
	}

	public void setRolePolicyDao(RolePolicyDAO rolePolicyDao) {
		this.rolePolicyDao = rolePolicyDao;
	}

    public ResourceRoleDAO getResRoleDao() {
        return resRoleDao;
    }

    public void setResRoleDao(ResourceRoleDAO resRoleDao) {
        this.resRoleDao = resRoleDao;
    }
}
