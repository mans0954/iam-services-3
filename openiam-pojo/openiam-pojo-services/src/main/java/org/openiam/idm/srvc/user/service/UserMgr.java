package org.openiam.idm.srvc.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Sort;
import org.hibernate.Hibernate;
import org.mvel2.optimizers.impl.refl.nodes.ArrayLength;
import org.openiam.base.SysConfiguration;
import org.openiam.core.dao.lucene.AbstractHibernateSearchDao;
import org.openiam.core.dao.lucene.SortType;
import org.openiam.dozer.converter.*;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.role.service.UserRoleDAO;
import org.openiam.idm.srvc.user.dao.UserSearchDAO;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.UserSearch;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.lucene.LoginSearchDAO;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.ContactConstants;
import org.openiam.idm.srvc.continfo.service.AddressDAO;
import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
import org.openiam.idm.srvc.continfo.service.PhoneDAO;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import java.util.*;

/**
 * Service interface that clients will access to gain information about users
 * and related information.
 *
 * @author Suneet Shah
 * @version 2
 */

@Service("userManager")
@Transactional
public class UserMgr implements UserDataService {
    @Autowired
    @Qualifier("userDAO")
    private UserDAO userDao;
    @Autowired
    @Qualifier("userAttributeDAO")
    private UserAttributeDAO userAttributeDao;
    @Autowired
    @Qualifier("userNoteDAO")
    private UserNoteDAO userNoteDao;
    @Autowired
    @Qualifier("supervisorDAO")
    private SupervisorDAO supervisorDao;
    @Autowired
    private AddressDAO addressDao;
    @Autowired
    private EmailAddressDAO emailAddressDao;
    @Autowired
    private PhoneDAO phoneDao;
    @Autowired
    protected LoginDAO loginDao;
    @Autowired
    protected SysConfiguration sysConfiguration;
    @Autowired
    private UserRoleDAO userRoleDAO;
    
    @Autowired
    private UserGroupDAO userGroupDAO;
    
    @Autowired
    private UserSearchDAO userSearchDAO;
    
    @Autowired
    private LoginSearchDAO loginSearchDAO;
    
    @Value("${org.openiam.user.search.max.results}")
    private int MAX_USER_SEARCH_RESULTS;

    private static final Log log = LogFactory.getLog(UserMgr.class);

    @Override
    public UserEntity getUser(String id) {
        return userDao.findById(id);
    }

    @Override
    public UserEntity getUserByPrincipal(String securityDomain, String principal,
                                   String managedSysId, boolean dependants) {
        LoginEntity login = loginDao.getRecord(principal, managedSysId, securityDomain);
        if (login == null) {
            return null;
        }
        return getUser(login.getUserId());

    }

    @Override
    public void addUser(UserEntity user) {
        if (user == null)
            throw new NullPointerException("user object is null");

        if (user.getCreateDate() == null) {
            user.setCreateDate(new Date(System.currentTimeMillis()));
        }
        if (user.getLastUpdate() == null) {
            user.setLastUpdate(new Date(System.currentTimeMillis()));
        }

        validateEmailAddress(user, user.getEmailAddresses());
        userDao.save(user);
    }

    @Override
    public void addUserWithDependent(UserEntity user, boolean dependency) {
        if (user == null)
            throw new NullPointerException("user object is null");

        if (user.getCreateDate() == null) {
            user.setCreateDate(new Date(System.currentTimeMillis()));
        }
        if (user.getLastUpdate() == null) {
            user.setLastUpdate(new Date(System.currentTimeMillis()));
        }
        validateEmailAddress(user, user.getEmailAddresses());
        userDao.save(user);
    }

    private void validateEmailAddress(UserEntity user, Set<EmailAddressEntity> emailSet) {

        if (emailSet == null || emailSet.isEmpty())
            return;

        Iterator<EmailAddressEntity> it = emailSet.iterator();

        while (it.hasNext()) {
            EmailAddressEntity emailAdr = it.next();
            if (emailAdr.getParent() == null) {
                emailAdr.setParent(userDao.findById(user.getUserId()));
                emailAdr.setParentType(ContactConstants.PARENT_TYPE_USER);
            }
        }


    }

    @Override
    public void updateUser(UserEntity user) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getUserId() == null)
            throw new NullPointerException("user id is null");

        user.setLastUpdate(new Date(System.currentTimeMillis()));

        userDao.update(user);

    }

    @Override
    public void updateUserWithDependent(UserEntity user, boolean dependency) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getUserId() == null)
            throw new NullPointerException("user id is null");

        user.setLastUpdate(new Date(System.currentTimeMillis()));

        validateEmailAddress(user, user.getEmailAddresses());

        userDao.update(user);
    }

    @Override
    public void removeUser(String id) {
        if (id == null)
            throw new NullPointerException("user id is null");

        // removes all the dependant objects.
        removeAllAttributes(id);
        removeAllPhones(id);
        removeAllAddresses(id);
        removeAllNotes(id);
        removeAllEmailAddresses(id);

        userDao.delete(userDao.findById(id));
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#findUsersByLastUpdateRange(java.util.Date,
      *      java.util.Date)
      */
    public List findUsersByLastUpdateRange(Date startDate, Date endDate) {
        return userDao.findByLastUpdateRange(startDate, endDate);
    }

    @Override
    public UserEntity getUserByName(String firstName, String lastName) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setFirstName(firstName);
        searchBean.setLastName(lastName);
        List<UserEntity> userList = findBeans(searchBean, 0, 1);
        return (userList != null && !userList.isEmpty()) ? userList.get(0) : null;
    }

    @Override
    public List<UserEntity> findUserByOrganization(String orgId) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setOrganizationId(orgId);
        return findBeans(searchBean);
    }

    @Override
    public List findUsersByStatus(UserStatusEnum status) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setAccountStatus(status.name());
        return findBeans(searchBean);
    }

    @Override
    @Deprecated
    public List<UserEntity> search(UserSearch search) {
        return userDao.search(search);
    }
    
    @Override
    public List<UserEntity> searchByDelegationProperties(DelegationFilterSearch search) {
        return userDao.findByDelegationProperties(search);
    }

    @Override
    public List<UserEntity> findBeans(UserSearchBean searchBean){
        return findBeans(searchBean, -1, -1);
    }
    
    private List<String> getUserIds(final UserSearchBean searchBean) {
    	final List<List<String>> nonEmptyListOfLists = new LinkedList<List<String>>();
		
		nonEmptyListOfLists.add(userSearchDAO.findIds(0, MAX_USER_SEARCH_RESULTS, null, searchBean));
		
		if(StringUtils.isNotBlank(searchBean.getPrincipal())) {
			final LoginSearchBean loginSearchBean = new LoginSearchBean();
			loginSearchBean.setLogin(StringUtils.trimToNull(searchBean.getPrincipal()));
			nonEmptyListOfLists.add(loginSearchDAO.findUserIds(0, MAX_USER_SEARCH_RESULTS, loginSearchBean));
		}
		
		if(CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
			nonEmptyListOfLists.add(userRoleDAO.getUserIdsInRole(searchBean.getRoleIdSet(), 0, MAX_USER_SEARCH_RESULTS));
		}
		
		if(CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
			nonEmptyListOfLists.add(userGroupDAO.getUserIdsInGroup(searchBean.getGroupIdSet(), 0, MAX_USER_SEARCH_RESULTS));
		}
		
		//remove null or empty lists
		for(final Iterator<List<String>> it = nonEmptyListOfLists.iterator(); it.hasNext();) {
			final List<String> list = it.next();
			if(CollectionUtils.isEmpty(list)) {
				it.remove();
			}
		}
		
		List<String> finalizedIdList = null;
		for(final Iterator<List<String>> it = nonEmptyListOfLists.iterator(); it.hasNext();) {
			if(finalizedIdList == null) {
				finalizedIdList = it.next();
			} else {
				finalizedIdList = ListUtils.intersection(finalizedIdList, it.next());
			}
		}
		
		return (finalizedIdList != null) ? finalizedIdList : Collections.EMPTY_LIST;
    }

    @Override
    public List<UserEntity> findBeans(UserSearchBean searchBean, int from, int size){
    	List<UserEntity> entityList = null;
    	if(StringUtils.isNotBlank(searchBean.getKey())) {
    		final UserEntity entity = userDao.findById(searchBean.getKey());;
    		if(entity != null) {
    			entityList = new ArrayList<UserEntity>(1);
    			entityList.add(entity);
    		}
    	} else {
    		List<String> finalizedIdList = getUserIds(searchBean);
    		if(finalizedIdList != null && finalizedIdList.size() >= from) {
    			int to = from + size;
    			if(to > finalizedIdList.size()) {
    				to = finalizedIdList.size();
    			}
    			finalizedIdList = new ArrayList<String>(finalizedIdList.subList(from, to));
    			entityList = userDao.findByIds(finalizedIdList);
    		}
    	}
    	return entityList;
    }

    @Override
    public int count(UserSearchBean searchBean){
    	return getUserIds(searchBean).size();
    }

    @Override
    public void addAttribute(UserAttributeEntity attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (StringUtils.isEmpty(attribute.getUserId())) {
            throw new NullPointerException(
                    "User has not been associated with this attribute.");
        }

        UserEntity userEntity = userDao.findById(attribute.getUserId());
        attribute.setUser(userEntity);

        userAttributeDao.save(attribute);
    }

    @Override
    public void updateAttribute(UserAttributeEntity attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (StringUtils.isEmpty(attribute.getUserId())) {
            throw new NullPointerException(
                    "User has not been associated with this attribute.");
        }
        UserEntity userEntity = userDao.findById(attribute.getUserId());
        attribute.setUser(userEntity);

        userAttributeDao.update(attribute);
    }

    @Override
    public Map<String, UserAttributeEntity> getAllAttributes(String userId) {
        Map<String, UserAttributeEntity> attrMap = new HashMap<String, UserAttributeEntity>();

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }

        UserEntity usrEntity = userDao.findById(userId);

        if (usrEntity == null)
            return null;

        List<UserAttributeEntity> attrList = userAttributeDao
                .findUserAttributes(userId);

        if (attrList == null || attrList.size() == 0)
            return null;

        // migrate to a Map for the User object
        if (attrList != null && !attrList.isEmpty()) {
            int size = attrList.size();
            for (int i = 0; i < size; i++) {
                UserAttributeEntity attr = attrList.get(i);
                attrMap.put(attr.getName(), attr);
            }
        }

        return attrMap;

    }

    @Override
    public UserAttributeEntity getAttribute(String attrId) {
        if (attrId == null) {
            throw new NullPointerException("attrId is null");
        }
        return userAttributeDao.findById(attrId);
    }

    @Override
    public void removeAttribute(final String userAttributeId) {
        final UserAttributeEntity entity = userAttributeDao.findById(userAttributeId);
        userAttributeDao.delete(entity);
    }

    @Override
    public void removeAllAttributes(String userId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        userAttributeDao.deleteUserAttributes(userId);
    }

    @Override
    public void addNote(UserNoteEntity note) {
        if (note == null)
            throw new NullPointerException("Note cannot be null");

        if (note.getUserId() == null) {
            throw new NullPointerException(
                    "User is not associated with this note.");
        }
        UserEntity userEntity = StringUtils.isNotEmpty(note.getUserId()) ? userDao.findById(note.getUserId()) : null;

        note.setUser(userEntity);

        userNoteDao.save(note);
    }

    @Override
    public void updateNote(UserNoteEntity note) {
        if (note == null)
            throw new NullPointerException("Note cannot be null");
        if (StringUtils.isEmpty(note.getUserNoteId())) {
            throw new NullPointerException("noteId is null");
        }
        if (StringUtils.isEmpty(note.getUserId())) {
            throw new NullPointerException(
                    "User is not associated with this note.");
        }
        UserEntity userEntity = StringUtils.isNotEmpty(note.getUserId()) ? userDao.findById(note.getUserId()) : null;
        note.setUser(userEntity);
        userNoteDao.merge(note);
    }

    @Override
    public List<UserNoteEntity> getAllNotes(String userId) {
        List<UserNoteEntity> noteList = new ArrayList<UserNoteEntity>();

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        return userNoteDao.findUserNotes(userId);
    }

    @Override
    public UserNoteEntity getNote(String noteId) {
        if (noteId == null) {
            throw new NullPointerException("attrId is null");
        }
        return userNoteDao.findById(noteId);

    }

    @Override
    public void removeNote(final String userNoteId) {
        if (userNoteId == null) {
            throw new NullPointerException("note is null");
        }
        final UserNoteEntity entity = userNoteDao.findById(userNoteId);
        userNoteDao.delete(entity);

    }

    @Override
    public void removeAllNotes(String userId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        userNoteDao.deleteUserNotes(userId);

    }

    @Override
    public void addAddress(AddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (val.getParent() == null)
            throw new NullPointerException("userId for the address is not defined.");

        val.setParentType(ContactConstants.PARENT_TYPE_USER);
        UserEntity parent = userDao.findById(val.getParent().getUserId());
        val.setParent(parent);
        addressDao.save(val);
    }

    @Override
    public void addAddressSet(Collection<AddressEntity> adrSet) {
        if (adrSet == null || adrSet.size() == 0)
            return;
        Iterator<AddressEntity> it = adrSet.iterator();
        while (it.hasNext()) {
            AddressEntity adr = it.next();
            addAddress(adr);
        }

    }

    @Override
    public void updateAddress(AddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getAddressId() == null)
            throw new NullPointerException("AddressId is null");
        if (val.getParent() == null)
            throw new NullPointerException(
                    "userId for the address is not defined.");
        if (val.getParentType() == null) {
            throw new NullPointerException(
                    "parentType for the address is not defined.");
        }
        UserEntity parent = userDao.findById(val.getParent().getUserId());
        val.setParent(parent);
        addressDao.update(val);
    }

    @Override
    public void removeAddress(final String addressId) {
        final AddressEntity entity = addressDao.findById(addressId);
        addressDao.delete(entity);
    }

    @Override
    public void removeAllAddresses(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        addressDao.removeByParent(userId, ContactConstants.PARENT_TYPE_USER);

    }

    @Override
    public AddressEntity getAddressById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        return addressDao.findById(addressId);
    }

    @Override
    public AddressEntity getAddressByName(String userId, String addressName) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        if (addressName == null)
            throw new NullPointerException("userId is null");

        return addressDao.findByName(addressName, userId,
                ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public AddressEntity getDefaultAddress(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        return addressDao
                .findDefault(userId, ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public List<AddressEntity> getAddressList(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        List<Address> addressList = null;
        return addressDao.findByParentAsList(userId,
                ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public void addPhone(PhoneEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (val.getParent() == null)
            throw new NullPointerException(
                    "parentId for the address is not defined.");

        val.setParentType(ContactConstants.PARENT_TYPE_USER);
        UserEntity parent = userDao.findById(val.getParent().getUserId());
        val.setParent(parent);

        phoneDao.save(val);
    }

    @Override
    public void addPhoneSet(Collection<PhoneEntity> phoneSet) {
        if (phoneSet == null || phoneSet.size() == 0)
            return;

        Iterator<PhoneEntity> it = phoneSet.iterator();
        while (it.hasNext()) {
        	PhoneEntity ph = it.next();
            addPhone(ph);
        }
    }

    @Override
    public void updatePhone(PhoneEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getPhoneId() == null)
            throw new NullPointerException("PhoneId is null");
        if (val.getParent() == null)
            throw new NullPointerException(
                    "parentId for the address is not defined.");
        if (val.getParentType() == null) {
            throw new NullPointerException(
                    "parentType for the address is not defined.");
        }
        UserEntity parent = userDao.findById(val.getParent().getUserId());
        val.setParent(parent);
        phoneDao.update(val);
    }

    @Override
    public void removePhone(final String phoneId) {
    	final PhoneEntity entity = phoneDao.findById(phoneId);
        phoneDao.delete(entity);
    }

    @Override
    public void removeAllPhones(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        phoneDao.removeByParent(userId, ContactConstants.PARENT_TYPE_USER);

    }

    @Override
    public PhoneEntity getPhoneById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        return phoneDao.findById(addressId);
    }

    @Override
    public PhoneEntity getPhoneByName(String userId, String addressName) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        if (addressName == null)
            throw new NullPointerException("userId is null");

        return phoneDao.findByName(addressName, userId,
                ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public PhoneEntity getDefaultPhone(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        return phoneDao.findDefault(userId, ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public List<PhoneEntity> getPhoneList(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        List<Phone> phoneList = null;
        return phoneDao.findByParentAsList(userId,
                ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public void addEmailAddress(EmailAddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getParent() == null)
            throw new NullPointerException(
                    "parentId for the address is not defined.");

        val.setParentType(ContactConstants.PARENT_TYPE_USER);
        UserEntity userEntity = userDao.findById(val.getParent().getUserId());
        val.setParent(userEntity);
        emailAddressDao.save(val);

    }

    @Override
    public void addEmailAddressSet(Collection<EmailAddressEntity> adrSet) {
        if (adrSet == null || adrSet.size() == 0)
            return;

        Iterator<EmailAddressEntity> it = adrSet.iterator();
        while (it.hasNext()) {
        	EmailAddressEntity adr = it.next();
            addEmailAddress(adr);
        }
    }

    @Override
    public void updateEmailAddress(EmailAddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getEmailId() == null)
            throw new NullPointerException("EmailAddressId is null");
        if (val.getParent() == null)
            throw new NullPointerException(
                    "parentId for the address is not defined.");
        if (val.getParentType() == null) {
            throw new NullPointerException(
                    "parentType for the address is not defined.");
        }
        UserEntity userEntity = userDao.findById(val.getParent().getUserId());
        val.setParent(userEntity);
        emailAddressDao.update(val);
    }

    @Override
    public void removeEmailAddress(final String emailAddressId) {
        if (emailAddressId == null)
            throw new NullPointerException("val is null");
        
        final EmailAddressEntity entity = emailAddressDao.findById(emailAddressId);
        emailAddressDao.delete(entity);
    }

    @Override
    public void removeAllEmailAddresses(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        emailAddressDao.removeByParent(userId,
                ContactConstants.PARENT_TYPE_USER);

    }

    @Override
    public EmailAddressEntity getEmailAddressById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        return emailAddressDao.findById(addressId);
    }

    @Override
    public EmailAddressEntity getEmailAddressByName(String userId, String addressName) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        if (addressName == null)
            throw new NullPointerException("userId is null");

        return emailAddressDao.findByName(addressName, userId,
                ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public EmailAddressEntity getDefaultEmailAddress(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        return emailAddressDao.findDefault(userId,
                ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public List<EmailAddressEntity> getEmailAddressList(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        return emailAddressDao.findByParentAsList(userId,
                ContactConstants.PARENT_TYPE_USER);
    }

    @Override
    public void addSupervisor(SupervisorEntity supervisor) {
    	supervisorDao.save(supervisor);
    }

    @Override
    public void updateSupervisor(SupervisorEntity supervisor) {
        if (supervisor == null)
            throw new NullPointerException("supervisor is null");
        supervisorDao.update(supervisor);
    }

    @Override
    public void removeSupervisor(final String supervisorId) {
        if (supervisorId == null)
            throw new NullPointerException("supervisor is null");
        
        final SupervisorEntity entity = supervisorDao.findById(supervisorId);
        supervisorDao.delete(entity);
    }

    @Override
    public SupervisorEntity getSupervisor(String supervisorObjId) {
        if (supervisorObjId == null)
            throw new NullPointerException("supervisorObjId is null");
        return supervisorDao.findById(supervisorObjId);
    }

    @Override
    public List<SupervisorEntity> getSupervisors(String employeeId) {
        if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        return supervisorDao.findSupervisors(employeeId);
    }

    @Override
    public List<SupervisorEntity> getEmployees(String supervisorId) {
        if (supervisorId == null)
            throw new NullPointerException("employeeId is null");
        return supervisorDao.findEmployees(supervisorId);
    }

    @Override
    public SupervisorEntity getPrimarySupervisor(String employeeId) {
        if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        return supervisorDao.findPrimarySupervisor(employeeId);
    }

	@Override
	public List<UserEntity> getUsersForResource(String resourceId, int from, int size) {
		return userDao.getUsersForResource(resourceId, from, size);
	}

	@Override
	public int getNumOfUsersForResource(String resourceId) {
		return userDao.getNumOfUsersForResource(resourceId);
	}

	@Override
	public List<UserEntity> getUsersForGroup(String groupId, int from, int size) {
		return userDao.getUsersForGroup(groupId, from, size);
	}

	@Override
	public int getNumOfUsersForGroup(String groupId) {
		return userDao.getNumOfUsersForGroup(groupId);
	}

	@Override
	public List<UserEntity> getUsersForRole(String roleId, int from, int size) {
		return userDao.getUsersForRole(roleId, from, size);
	}

	@Override
	public int getNumOfUsersForRole(String roleId) {
		return userDao.getNumOfUsersForRole(roleId);
	}
}
