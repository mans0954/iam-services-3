package org.openiam.idm.srvc.user.service;

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
import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEmbeddableId;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.dao.UserSearchDAO;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginId;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.ContactConstants;
import org.openiam.idm.srvc.continfo.service.AddressDAO;
import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
import org.openiam.idm.srvc.continfo.service.PhoneDAO;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

// Note: as per spec serviceName goes in impl class and name goes in interface
@Service("userManager")
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
    protected UserDozerConverter userDozerConverter;
    @Autowired
    protected UserAttributeDozerConverter userAttributeDozerConverter;
    @Autowired
    protected UserNoteDozerConverter userNoteDozerConverter;
    @Autowired
    protected AddressDozerConverter addressDozerConverter;
    @Autowired
    protected PhoneDozerConverter phoneDozerConverter;
    @Autowired
    protected EmailAddressDozerConverter emailAddressDozerConverter;
    @Autowired
    protected SupervisorDozerConverter supervisorDozerConverter;
    @Autowired
    protected LoginDozerConverter loginDozerConverter;
    
    @Autowired
    @Qualifier("userSearchDAO")
    private UserSearchDAO userSearchDAO;

    private static final Log log = LogFactory.getLog(UserMgr.class);

    // protected UserMsgProducer userMsgProducer;

    /*
      * public UserMgr() {
      *  }
      */

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getUser(java.lang.String)
      */
    public User getUser(String id) {
        UserEntity entity = userDao.findById(id);
        return entity != null ? userDozerConverter.convertToDTO(entity, false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getUser(java.lang.String,
      *      boolean)
      */
    public User getUserWithDependent(String id, boolean dependants) {

        UserEntity usr = userDao.findById(id);
        if (usr == null) {
            return null;
        }

        if (!dependants) {
            return userDozerConverter.convertToDTO(usr, false);
        }

        //	 assemble the various dependant objects
        Hibernate.initialize(usr.getPhones());
        Hibernate.initialize(usr.getEmailAddresses());
        Hibernate.initialize(usr.getAddresses());
        Hibernate.initialize(usr.getUserAttributes());
        User user = userDozerConverter.convertToDTO(usr, true);
        List<LoginEntity> principalList = loginDao.findUser(id);
        if (principalList != null) {
            user.setPrincipalList(loginDozerConverter.convertToDTOList(principalList,true));
        }
        return user;
    }

    public User getUserByPrincipal(String securityDomain, String principal,
                                   String managedSysId, boolean dependants) {
        // get the login
        LoginEmbeddableId loginId = new LoginEmbeddableId(securityDomain, principal, managedSysId);
        LoginEntity login = loginDao.findById(loginId);
        if (login == null) {
            return null;
        }
        return getUserWithDependent(login.getUserId(), dependants);

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addUser(org.openiam.idm.srvc.user.dto.User)
      */
    public User addUser(User user) {
        if (user == null)
            throw new NullPointerException("user object is null");

        if (user.getCreateDate() == null) {
            user.setCreateDate(new Date(System.currentTimeMillis()));
        }
        if (user.getLastUpdate() == null) {
            user.setLastUpdate(new Date(System.currentTimeMillis()));
        }

        validateEmailAddress(user, user.getEmailAddresses());
        UserEntity userEntity = userDozerConverter.convertToEntity(user,true);
        userDao.save(userEntity);

        return  userDozerConverter.convertToDTO(userEntity,true);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addUser(org.openiam.idm.srvc.user.dto.User,
      *      boolean)
      */
    public User addUserWithDependent(User user, boolean dependency) {
        if (user == null)
            throw new NullPointerException("user object is null");

        if (user.getCreateDate() == null) {
            user.setCreateDate(new Date(System.currentTimeMillis()));
        }
        if (user.getLastUpdate() == null) {
            user.setLastUpdate(new Date(System.currentTimeMillis()));
        }

        // if there are dependants, then make user that the parentId has been set

        validateEmailAddress(user, user.getEmailAddresses());

        log.debug("User Object before addUser: " + user);
        UserEntity entity = userDozerConverter.convertToEntity(user,true);
        userDao.save(entity);

        /*if (!dependency)
            return user;*/

        // address
        /*  Map<String, Address> adrMap = user.getAddresses();
        if (adrMap != null && adrMap.size() > 0 ) {
            this.addressDao.saveAddressMap(user.getUserId(),
                        ContactConstants.PARENT_TYPE_USER , adrMap);

       }
       */
        //email
        //  Map<String, EmailAddress> emailMap = user.getEmailAddresses();
        //  if (emailMap != null && emailMap.size() > 0 ) {
        //  this.emailAddressDao.saveEmailAddressMap(user.getUserId(),
        //			  	ContactConstants.PARENT_TYPE_USER , emailMap);
        //  }

        // phone
        /*  Map<String, Phone> phoneMap = user.getPhones();
        if (phoneMap != null && phoneMap.size() > 0 ) {
            this.phoneDao.savePhoneMap(user.getUserId(),
                        ContactConstants.PARENT_TYPE_USER , phoneMap);
        }
        */

        //  this.userMsgProducer.sendMessage(user,"ADD");

        return userDozerConverter.convertToDTO(entity,true);
    }

    private void validateEmailAddress(User user, Set<EmailAddress> emailSet) {

        if (emailSet == null || emailSet.isEmpty())
            return;

        Iterator<EmailAddress> it = emailSet.iterator();

        while (it.hasNext()) {
            EmailAddress emailAdr = it.next();
            if (StringUtils.isEmpty(emailAdr.getParentId())) {
                emailAdr.setParentId(user.getUserId());
                emailAdr.setParentType(ContactConstants.PARENT_TYPE_USER);
            }
        }


    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updateUser(org.openiam.idm.srvc.user.dto.User)
      */
    public void updateUser(User user) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getUserId() == null)
            throw new NullPointerException("user id is null");

        user.setLastUpdate(new Date(System.currentTimeMillis()));

        userDao.update(userDozerConverter.convertToEntity(user,true));

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updateUser(org.openiam.idm.srvc.user.dto.User,
      *      boolean)
      */
    public void updateUserWithDependent(User user, boolean dependency) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getUserId() == null)
            throw new NullPointerException("user id is null");

        user.setLastUpdate(new Date(System.currentTimeMillis()));

        validateEmailAddress(user, user.getEmailAddresses());

        userDao.update(userDozerConverter.convertToEntity(user,true));

        if (!dependency)
            return;

        // address
        /* Map<Address> adrMap = user.getAddresses();
        if (adrMap != null && adrMap.size() > 0 ) {
            this.addressDao.saveAddressMap(user.getUserId(),
                        ContactConstants.PARENT_TYPE_USER , adrMap);
        }
        */
        //email
/*	  Map<String, EmailAddress> emailMap = user.getEmailAddresses();
	  if (emailMap != null && emailMap.size() > 0 ) {
		  this.emailAddressDao.saveEmailAddressMap(user.getUserId(), 
				  	ContactConstants.PARENT_TYPE_USER , emailMap);
	  }
*/
        // phone
/*
		Map<String, Phone> phoneMap = user.getPhones();
	  if (phoneMap != null && phoneMap.size() > 0 ) {
		  this.phoneDao.savePhoneMap(user.getUserId(), 
				  	ContactConstants.PARENT_TYPE_USER , phoneMap);
	  }
*/
        //this.userMsgProducer.sendMessage(user,"UPDATE");

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeUser(java.lang.String)
      */
    public void removeUser(String id) {
        if (id == null)
            throw new NullPointerException("user id is null");

        User user = new User(id);

        // removes all the dependant objects.
        removeAllAttributes(id);
        removeAllPhones(id);
        removeAllAddresses(id);
        removeAllNotes(id);
        removeAllEmailAddresses(id);

        userDao.delete(userDozerConverter.convertToEntity(user,false));

        // / this.userMsgProducer.sendMessage(user.getUserId(),"DELETE");

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

    public User getUserByName(String firstName, String lastName) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setFirstName(firstName);
        searchBean.setLastName(lastName);
        List<User> userList = findBeans(searchBean, 0, 1);
        return (userList != null && !userList.isEmpty()) ? userList.get(0) : null;
    }


    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#findUserByOrganization(java.lang.String)
      */
    public List<User> findUserByOrganization(String orgId) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setOrganizationId(orgId);
        return findBeans(searchBean);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#findUsersByStatus(java.lang.String)
      */
    public List findUsersByStatus(UserStatusEnum status) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setAccountStatus(status.name());
        return findBeans(searchBean);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#search(org.openiam.util.db.Search)
      */
    @Deprecated
    public List<User> search(UserSearch search) {
        List<UserEntity> entityList = userDao.search(search);
        return userDozerConverter.convertToDTOList(entityList,true);
    }
    public List<User> searchByDelegationProperties(DelegationFilterSearch search) {
        List<UserEntity> entityList = userDao.findByDelegationProperties(search);
        return userDozerConverter.convertToDTOList(entityList, true);
    }

    public List<User> findBeans(UserSearchBean searchBean){
        return findBeans(searchBean, -1, -1);
    }

    public List<User> findBeans(UserSearchBean searchBean, int from, int size){
    	List<UserEntity> entityList = null;
    	if(StringUtils.isNotBlank(searchBean.getKey())) {
    		final UserEntity entity = userDao.findById(searchBean.getKey());;
    		if(entity != null) {
    			entityList = new ArrayList<UserEntity>(1);
    			entityList.add(entity);
    		}
    	} else {
    		entityList = (List<UserEntity>)userSearchDAO.find(from, size, null, searchBean);
    	}
    	return userDozerConverter.convertToDTOList(entityList, false);
    }

    public int count(UserSearchBean searchBean){
      return userSearchDAO.count(searchBean);
    }

    /* -------- Methods for Attributes ---------- */

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addAttribute(org.openiam.idm.srvc.user.dto.UserAttribute)
      */
    public UserAttribute addAttribute(UserAttribute attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (StringUtils.isEmpty(attribute.getUserId())) {
            throw new NullPointerException(
                    "User has not been associated with this attribute.");
        }

        UserEntity userEntity = userDao.findById(attribute.getUserId());
        UserAttributeEntity userAttribute = userAttributeDozerConverter.convertToEntity(attribute, false);
        userAttribute.setUser(userEntity);

        userAttributeDao.save(userAttribute);

        // this.userMsgProducer.sendMessage(attribute.getUserId(),"ADD");

        return attribute;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updateAttribute(org.openiam.idm.srvc.user.dto.UserAttribute)
      */
    public void updateAttribute(UserAttribute attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (StringUtils.isEmpty(attribute.getUserId())) {
            throw new NullPointerException(
                    "User has not been associated with this attribute.");
        }
        UserEntity userEntity = userDao.findById(attribute.getUserId());
        UserAttributeEntity userAttribute = userAttributeDozerConverter.convertToEntity(attribute, false);
        userAttribute.setUser(userEntity);

        userAttributeDao.update(userAttribute);
        // this.userMsgProducer.sendMessage(attribute.getUserId(),"UPDATE");

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getAllAttributes(java.lang.String)
      */
    public Map<String, UserAttribute> getAllAttributes(String userId) {
        Map<String, UserAttribute> attrMap = new HashMap<String, UserAttribute>();

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
                attrMap.put(attr.getName(), userAttributeDozerConverter.convertToDTO(attr, false));
            }
        }

        return attrMap;

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getAttribute(java.lang.String)
      */
    public UserAttribute getAttribute(String attrId) {
        if (attrId == null) {
            throw new NullPointerException("attrId is null");
        }
        UserAttributeEntity attributeEntity = userAttributeDao.findById(attrId);

        return attributeEntity != null ? userAttributeDozerConverter.convertToDTO(attributeEntity, false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeAttribute(org.openiam.idm.srvc.user.dto.UserAttribute)
      */
    public void removeAttribute(UserAttribute attr) {
        if (attr == null) {
            throw new NullPointerException("attr is null");
        }
        if (StringUtils.isEmpty(attr.getId())) {
            throw new NullPointerException("attrId is null");
        }
        UserEntity userEntity = userDao.findById(attr.getUserId());

        UserAttributeEntity userAttribute = userAttributeDozerConverter.convertToEntity(attr, false);
        userAttribute.setUser(userEntity);

        userAttributeDao.delete(userAttribute);

        // this.userMsgProducer.sendMessage(attr.getUserId(),"DELETE");

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeAllAttributes(java.lang.String)
      */
    public void removeAllAttributes(String userId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        userAttributeDao.deleteUserAttributes(userId);

        // this.userMsgProducer.sendMessage(userId,"DELETE");

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getUserAsMap(java.lang.String)
      */
    public Map<String, UserAttribute> getUserAsMap(String userId) {
        User usr = getUser(userId);
        if (usr == null) {
            return null;
        }

        Map<String, UserAttribute> attrMap = getAllAttributes(userId);
        if (attrMap == null) {
            attrMap = new HashMap<String, UserAttribute>();
        }
        // assign the predefined properties

        attrMap.put("USER_ID", new UserAttribute(null, userId, null,
                "USER_ID", userId));
        attrMap.put("FIRST_NAME", new UserAttribute(null, userId,
                null, "FIRST_NAME", usr.getFirstName()));
        attrMap.put("LAST_NAME", new UserAttribute(null, userId, null,
                "LAST_NAME", usr.getLastName()));
        attrMap.put("MIDDLE_INIT", new UserAttribute(null, userId,
                null, "MIDDLE_INIT", String.valueOf(usr.getMiddleInit())));
        attrMap.put("TITLE", new UserAttribute(null, userId, null,
                "TITLE", usr.getTitle()));
        attrMap.put("DEPT", new UserAttribute(null, userId, null,
                "DEPT", usr.getDeptCd()));
        attrMap.put("STATUS", new UserAttribute(null, userId, null,
                "STATUS", usr.getStatus().toString()));
        if (usr.getBirthdate() != null) {
            attrMap.put("BIRTHDATE", new UserAttribute(null, userId,
                    null, "BIRTHDATE", usr.getBirthdate().toString()));
        } else {
            attrMap.put("BIRTHDATE", new UserAttribute(null, userId,
                    null, "BIRTHDATE", null));
        }
        attrMap.put("SEX", new UserAttribute(null, userId, null,
                "SEX", String.valueOf(usr.getSex())));
        if (usr.getCreateDate() != null) {
            attrMap.put("CREATE_DATE", new UserAttribute(null, userId,
                    null, "CREATE_DATE", usr.getCreateDate().toString()));
        } else {
            attrMap.put("CREATE_DATE", new UserAttribute(null, userId,
                    null, "CREATE_DATE", null));

        }
        attrMap.put("CREATED_BY", new UserAttribute(null, userId,
                null, "CREATED_BY", usr.getCreatedBy()));
        if (usr.getLastUpdate() != null) {
            attrMap.put("LAST_UPDATE", new UserAttribute(null, userId,
                    null, "LAST_UPDATE", usr.getLastUpdate().toString()));
        } else {
            attrMap.put("LAST_UPDATE", new UserAttribute(null, userId,
                    null, "LAST_UPDATE", null));

        }
        attrMap.put("LAST_UPDATEDBY", new UserAttribute(null, userId,
                null, "LAST_UPDATEDBY", usr.getLastUpdatedBy()));
        attrMap.put("PREFIX", new UserAttribute(null, userId, null,
                "PREFIX", usr.getPrefix()));
        attrMap.put("SUFFIX", new UserAttribute(null, userId, null,
                "SUFFIX", usr.getSuffix()));
        attrMap.put("USER_TYPE_IND", new UserAttribute(null, userId,
                null, "USER_TYPE_IND", usr.getUserTypeInd()));
        attrMap.put("EMPLOYEE_ID", new UserAttribute(null, userId,
                null, "EMPLOYEE_ID", usr.getEmployeeId()));
        attrMap.put("EMPLOYEE_TYPE", new UserAttribute(null, userId,
                null, "EMPLOYEE_TYPE", usr.getEmployeeType()));
        attrMap.put("LOCATION_ID", new UserAttribute(null, userId,
                null, "LOCATION_ID", usr.getLocationCd()));
        attrMap.put("ORGANIZATION_ID", new UserAttribute(null, userId,
                null, "ORGANIZATION_ID", usr.getCompanyId()));
        attrMap.put("COMPANY_OWNER_ID", new UserAttribute(null,
                userId, null, "COMPANY_OWNER_ID", usr.getCompanyOwnerId()));

        attrMap.put("MANAGER_ID", new UserAttribute(null, userId,
                null, "MANAGER_ID", usr.getManagerId()));
        attrMap.put("JOB_CODE", new UserAttribute(null, userId, null,
                "JOB_CODE", usr.getJobCode()));

        return attrMap;
    }

    public List<UserAttribute> getUserAsAttributeList(
            String principalName,
            List<String> attributeList) {

        List<UserAttribute> attrList = new ArrayList<UserAttribute>();


        User u = getUserByPrincipal(sysConfiguration.getDefaultSecurityDomain(), principalName, sysConfiguration.getDefaultManagedSysId(), false);
        if (u == null) {
            return null;
        }
        UserAttribute atr = new UserAttribute("EMAIL", u.getEmail());
        attrList.add(atr);

        return attrList;


    }

    /* -------- Methods for UserNotes ---------- */
    /*
      * Use these methods when you dont want to go through the user object
      */

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addNote(org.openiam.idm.srvc.user.dto.UserNote)
      */
    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addNote(org.openiam.idm.srvc.user.dto.UserNote)
      */
    public UserNote addNote(UserNote note) {
        if (note == null)
            throw new NullPointerException("Note cannot be null");

        if (note.getUserId() == null) {
            throw new NullPointerException(
                    "User is not associated with this note.");
        }
        UserEntity userEntity = StringUtils.isNotEmpty(note.getUserId()) ? userDao.findById(note.getUserId()) : null;


        UserNoteEntity userNote = userNoteDozerConverter.convertToEntity(note, false);
        userNote.setUser(userEntity);

        userNoteDao.save(userNote);

        return userNoteDozerConverter.convertToDTO(userNote, false);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updateNote(org.openiam.idm.srvc.user.dto.UserNote)
      */
    public void updateNote(UserNote note) {
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
        UserNoteEntity userNoteEntity = userNoteDozerConverter.convertToEntity(note, false);
        userNoteEntity.setUser(userEntity);
        userNoteDao.merge(userNoteEntity);

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getAllNotes(java.lang.String)
      */
    public List<UserNote> getAllNotes(String userId) {
        List<UserNote> noteList = new ArrayList<UserNote>();

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        List<UserNoteEntity> noteEntityList = userNoteDao.findUserNotes(userId);
        if (noteEntityList == null || noteEntityList.isEmpty()) {
            return null;
        }

        return userNoteDozerConverter.convertToDTOList(noteEntityList, false);
//        for (UserNoteEntity userNoteEntity : noteEntityList) {
//            noteList.add(new UserNote(userNoteEntity));
//        }
//        return noteList;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getNote(java.lang.String)
      */
    public UserNote getNote(String noteId) {
        if (noteId == null) {
            throw new NullPointerException("attrId is null");
        }
        UserNoteEntity userNoteEntity = userNoteDao.findById(noteId);
        return userNoteEntity != null ? userNoteDozerConverter.convertToDTO(userNoteEntity, false) : null;

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeNote(org.openiam.idm.srvc.user.dto.UserNote)
      */
    public void removeNote(UserNote note) {
        if (note == null) {
            throw new NullPointerException("note is null");
        }
        if (note.getUserNoteId() == null) {
            throw new NullPointerException("noteId is null");
        }
        UserEntity userEntity = StringUtils.isNotEmpty(note.getUserId()) ? userDao.findById(note.getUserId()) : null;
        UserNoteEntity userNoteEntity = userNoteDozerConverter.convertToEntity(note, false);
        userNoteEntity.setUser(userEntity);
        userNoteDao.delete(userNoteEntity);

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeAllNotes(java.lang.String)
      */
    public void removeAllNotes(String userId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        userNoteDao.deleteUserNotes(userId);

    }

    /* ----------- Address Methods ------- */

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addAddress(org.openiam.idm.srvc.continfo.dto.Address)
      */
    public Address addAddress(Address val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (StringUtils.isEmpty(val.getParentId()))
            throw new NullPointerException(
                    "userId for the address is not defined.");

        val.setParentType(ContactConstants.PARENT_TYPE_USER);
        UserEntity parent = userDao.findById(val.getParentId());
        AddressEntity addressEntity =  addressDozerConverter.convertToEntity(val, false);
        addressEntity.setParent(parent);
        addressDao.add(addressEntity);
        return addressDozerConverter.convertToDTO(addressEntity, false);
    }

    public void addAddressSet(Set<Address> adrSet) {
        if (adrSet == null || adrSet.size() == 0)
            return;
        Iterator<Address> it = adrSet.iterator();
        while (it.hasNext()) {
            Address adr = it.next();
            addAddress(adr);
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updateAddress(org.openiam.idm.srvc.continfo.dto.Address)
      */
    public void updateAddress(Address val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getAddressId() == null)
            throw new NullPointerException("AddressId is null");
        if (StringUtils.isEmpty(val.getParentId()))
            throw new NullPointerException(
                    "userId for the address is not defined.");
        if (val.getParentType() == null) {
            throw new NullPointerException(
                    "parentType for the address is not defined.");
        }
        UserEntity parent = userDao.findById(val.getParentId());
        AddressEntity addressEntity = addressDozerConverter.convertToEntity(val,false);
        addressEntity.setParent(parent);
        addressDao.update(addressEntity);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeAddress(org.openiam.idm.srvc.continfo.dto.Address)
      */
    public void removeAddress(Address val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getAddressId() == null)
            throw new NullPointerException("AddressId is null");
        UserEntity parent = userDao.findById(val.getParentId());
        AddressEntity addressEntity = addressDozerConverter.convertToEntity(val,false);
        addressEntity.setParent(parent);
        addressDao.remove(addressEntity);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeAllAddresses(java.lang.String)
      */
    public void removeAllAddresses(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        addressDao.removeByParent(userId, ContactConstants.PARENT_TYPE_USER);

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getAddressById(java.lang.String)
      */
    public Address getAddressById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        AddressEntity addressEntity = addressDao.findById(addressId);
        return addressEntity != null ? addressDozerConverter.convertToDTO(addressEntity, false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getAddressByName(java.lang.String,
      *      java.lang.String)
      */
    public Address getAddressByName(String userId, String addressName) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        if (addressName == null)
            throw new NullPointerException("userId is null");

        AddressEntity addressEntity = addressDao.findByName(addressName, userId,
                ContactConstants.PARENT_TYPE_USER);
        return addressEntity != null ? addressDozerConverter.convertToDTO(addressEntity, false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getDefaultAddress(java.lang.String)
      */
    public Address getDefaultAddress(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        AddressEntity addressEntity = addressDao
                .findDefault(userId, ContactConstants.PARENT_TYPE_USER);

        return addressEntity != null ? addressDozerConverter.convertToDTO(addressEntity, false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getAddressList(java.lang.String)
      */
    public List<Address> getAddressList(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        List<Address> addressList = null;
        List<AddressEntity> addressEntityList = addressDao.findByParentAsList(userId,
                ContactConstants.PARENT_TYPE_USER);

        return addressDozerConverter.convertToDTOList(addressEntityList, false);
//        if(addressEntityList != null) {
//            addressList = new LinkedList<Address>();
//            for (AddressEntity addressEntity : addressEntityList) {
//                addressList.add(new Address(addressEntity));
//            }
//        }
//        return addressList;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getAddressMap(java.lang.String)
      */

    public Map<String, Address> getAddressMap(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        Map<String, Address> addressMap = null;
        Map<String, AddressEntity> addressEntityMap = addressDao.findByParent(userId,
                ContactConstants.PARENT_TYPE_USER);
        if(addressEntityMap != null) {
            addressMap = new HashMap<String, Address>();
            for (Map.Entry<String, AddressEntity> addressEntityEntry : addressEntityMap.entrySet()) {
                addressMap.put(addressEntityEntry.getKey(), new Address(addressEntityEntry.getKey()));
            }
        }
        return addressMap;
    }

    /* ----------- Phone Methods ------- */

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addPhone(org.openiam.idm.srvc.continfo.dto.Phone)
      */
    public Phone addPhone(Phone val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (StringUtils.isEmpty(val.getParentId()))
            throw new NullPointerException(
                    "parentId for the address is not defined.");

        val.setParentType(ContactConstants.PARENT_TYPE_USER);
        UserEntity parent = userDao.findById(val.getParentId());


        PhoneEntity phoneEntity = phoneDozerConverter.convertToEntity(val, false);
        phoneEntity.setParent(parent);

        phoneEntity = phoneDao.add(phoneEntity);
        return phoneDozerConverter.convertToDTO(phoneEntity,false);
    }

    public void addPhoneSet(Set<Phone> phoneSet) {
        if (phoneSet == null || phoneSet.size() == 0)
            return;

        Iterator<Phone> it = phoneSet.iterator();
        while (it.hasNext()) {
            Phone ph = it.next();
            addPhone(ph);
        }

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updatePhone(org.openiam.idm.srvc.continfo.dto.Phone)
      */
    public void updatePhone(Phone val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getPhoneId() == null)
            throw new NullPointerException("PhoneId is null");
        if (StringUtils.isEmpty(val.getParentId()))
            throw new NullPointerException(
                    "parentId for the address is not defined.");
        if (val.getParentType() == null) {
            throw new NullPointerException(
                    "parentType for the address is not defined.");
        }
        UserEntity parent = userDao.findById(val.getParentId());
        PhoneEntity phoneEntity = phoneDozerConverter.convertToEntity(val, false);
        phoneEntity.setParent(parent);

        phoneDao.update(phoneEntity);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removePhone(org.openiam.idm.srvc.continfo.dto.Phone)
      */
    public void removePhone(Phone val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getPhoneId() == null)
            throw new NullPointerException("PhoneId is null");
        UserEntity parent = userDao.findById(val.getParentId());
        PhoneEntity phoneEntity = phoneDozerConverter.convertToEntity(val, false);
        phoneEntity.setParent(parent);

        phoneDao.remove(phoneEntity);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeAllPhones(java.lang.String)
      */
    public void removeAllPhones(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        phoneDao.removeByParent(userId, ContactConstants.PARENT_TYPE_USER);

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getPhoneById(java.lang.String)
      */
    public Phone getPhoneById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        PhoneEntity phoneEntity = phoneDao.findById(addressId);

        return phoneEntity != null ? phoneDozerConverter.convertToDTO(phoneEntity,false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getPhoneByName(java.lang.String,
      *      java.lang.String)
      */
    public Phone getPhoneByName(String userId, String addressName) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        if (addressName == null)
            throw new NullPointerException("userId is null");

        PhoneEntity phoneEntity = phoneDao.findByName(addressName, userId,
                ContactConstants.PARENT_TYPE_USER);
        return phoneEntity != null ?  phoneDozerConverter.convertToDTO(phoneEntity,false): null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getDefaultPhone(java.lang.String)
      */
    public Phone getDefaultPhone(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        PhoneEntity phoneEntity = phoneDao.findDefault(userId, ContactConstants.PARENT_TYPE_USER);
        return phoneEntity != null ?  phoneDozerConverter.convertToDTO(phoneEntity,false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getPhoneList(java.lang.String)
      */
    public List<Phone> getPhoneList(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        List<Phone> phoneList = null;
        List<PhoneEntity> phoneEntityList = phoneDao.findByParentAsList(userId,
                ContactConstants.PARENT_TYPE_USER);

        return phoneDozerConverter.convertToDTOList(phoneEntityList,false);

//        if (phoneEntityList != null) {
//            phoneList = new LinkedList<Phone>();
//            for (PhoneEntity phoneEntity : phoneEntityList) {
//                phoneList.add(new Phone(phoneEntity));
//            }
//        }
//        return phoneList;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getPhoneMap(java.lang.String)
      */
    public Map<String, Phone> getPhoneMap(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        Map<String, Phone> phoneMap = null;
        Map<String, PhoneEntity> phoneEntityMap = phoneDao.findByParent(userId, ContactConstants.PARENT_TYPE_USER);
        if(phoneEntityMap != null) {
            phoneMap = new HashMap<String, Phone>();
            for (Map.Entry<String, PhoneEntity> phoneEntityEntry : phoneEntityMap.entrySet()) {
                phoneMap.put(phoneEntityEntry.getKey(), phoneDozerConverter.convertToDTO(phoneEntityEntry.getValue(),false));
            }
        }
        return phoneMap;
    }

    /* ----------- E-mail Methods ------- */

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addEmailAddress(org.openiam.idm.srvc.continfo.dto.EmailAddress)
      */
    public EmailAddress addEmailAddress(EmailAddress val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (StringUtils.isEmpty(val.getParentId()))
            throw new NullPointerException(
                    "parentId for the address is not defined.");

        val.setParentType(ContactConstants.PARENT_TYPE_USER);
        UserEntity userEntity = userDao.findById(val.getParentId());
        EmailAddressEntity emailAddressEntity = emailAddressDozerConverter.convertToEntity(val, false);
        emailAddressEntity.setParent(userEntity);
        emailAddressEntity = emailAddressDao.add(emailAddressEntity);
        return emailAddressDozerConverter.convertToDTO(emailAddressEntity,false);

    }

    public void addEmailAddressSet(Set<EmailAddress> adrSet) {
        if (adrSet == null || adrSet.size() == 0)
            return;

        Iterator<EmailAddress> it = adrSet.iterator();
        while (it.hasNext()) {
            EmailAddress adr = it.next();
            addEmailAddress(adr);
        }
    }


    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updateEmailAddress(org.openiam.idm.srvc.continfo.dto.EmailAddress)
      */
    public void updateEmailAddress(EmailAddress val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getEmailId() == null)
            throw new NullPointerException("EmailAddressId is null");
        if (StringUtils.isEmpty(val.getParentId()))
            throw new NullPointerException(
                    "parentId for the address is not defined.");
        if (val.getParentType() == null) {
            throw new NullPointerException(
                    "parentType for the address is not defined.");
        }
        UserEntity userEntity = userDao.findById(val.getParentId());
        EmailAddressEntity emailAddressEntity = emailAddressDozerConverter.convertToEntity(val, false);
        emailAddressEntity.setParent(userEntity);

        emailAddressDao.update(emailAddressEntity);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeEmailAddress(org.openiam.idm.srvc.continfo.dto.EmailAddress)
      */
    public void removeEmailAddress(EmailAddress val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getEmailId() == null)
            throw new NullPointerException("EmailAddressId is null");
        UserEntity userEntity = userDao.findById(val.getParentId());
        EmailAddressEntity emailAddressEntity = emailAddressDozerConverter.convertToEntity(val, false);
        emailAddressEntity.setParent(userEntity);

        emailAddressDao.remove(emailAddressEntity);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeAllEmailAddresses(java.lang.String)
      */
    public void removeAllEmailAddresses(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        emailAddressDao.removeByParent(userId,
                ContactConstants.PARENT_TYPE_USER);

    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getEmailAddressById(java.lang.String)
      */
    public EmailAddress getEmailAddressById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        EmailAddressEntity emailAddressEntity = emailAddressDao.findById(addressId);
        return emailAddressEntity != null ? emailAddressDozerConverter.convertToDTO(emailAddressEntity,false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getEmailAddressByName(java.lang.String,
      *      java.lang.String)
      */
    public EmailAddress getEmailAddressByName(String userId, String addressName) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        if (addressName == null)
            throw new NullPointerException("userId is null");

        EmailAddressEntity emailAddressEntity = emailAddressDao.findByName(addressName, userId,
                ContactConstants.PARENT_TYPE_USER);
        return emailAddressEntity != null ? emailAddressDozerConverter.convertToDTO(emailAddressEntity,false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getDefaultEmailAddress(java.lang.String)
      */
    public EmailAddress getDefaultEmailAddress(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        EmailAddressEntity emailAddressEntity = emailAddressDao.findDefault(userId,
                ContactConstants.PARENT_TYPE_USER);
        return emailAddressEntity != null ? emailAddressDozerConverter.convertToDTO(emailAddressEntity,false) : null;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getEmailAddressList(java.lang.String)
      */
    public List<EmailAddress> getEmailAddressList(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        List<EmailAddressEntity> emailAddressEntityList = emailAddressDao.findByParentAsList(userId,
                ContactConstants.PARENT_TYPE_USER);
        return emailAddressDozerConverter.convertToDTOList(emailAddressEntityList,false);
//        List<EmailAddress> emailAddressList = new LinkedList<EmailAddress>();
//        for (EmailAddressEntity emailAddress : emailAddressEntityList) {
//            emailAddressList.add(new EmailAddress(emailAddress));
//        }
//        return emailAddressList;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getEmailAddressMap(java.lang.String)
      */
    public Map<String, EmailAddress> getEmailAddressMap(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        Map<String, EmailAddressEntity> emailAddressEntityMap = emailAddressDao.findByParent(userId,
                ContactConstants.PARENT_TYPE_USER);
        Map<String, EmailAddress> emailAddressMap = new HashMap<String, EmailAddress>();
        for (Map.Entry<String, EmailAddressEntity> addressEntityEntry : emailAddressEntityMap.entrySet()) {
            emailAddressMap.put(addressEntityEntry.getKey(), emailAddressDozerConverter.convertToDTO(addressEntityEntry.getValue(),false));
        }
        return emailAddressMap;
    }

    /* ----------- Supervisor Methods ------- */

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#addSupervisor(org.openiam.idm.srvc.user.dto.Supervisor)
      */
    public Supervisor addSupervisor(Supervisor supervisor) {
        if (supervisor == null)
            throw new NullPointerException("supervisor is null");
        this.supervisorDao.save(supervisorDozerConverter.convertToEntity(supervisor,true));
        return supervisor;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#updateSupervisor(org.openiam.idm.srvc.user.dto.Supervisor)
      */
    public void updateSupervisor(Supervisor supervisor) {
        if (supervisor == null)
            throw new NullPointerException("supervisor is null");
        this.supervisorDao.update(supervisorDozerConverter.convertToEntity(supervisor,true));
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#removeSupervisor(org.openiam.idm.srvc.user.dto.Supervisor)
      */
    public void removeSupervisor(Supervisor supervisor) {
        if (supervisor == null)
            throw new NullPointerException("supervisor is null");
        this.supervisorDao.delete(supervisorDozerConverter.convertToEntity(supervisor,true));
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getSupervisor(java.lang.String)
      */
    public Supervisor getSupervisor(String supervisorObjId) {
        if (supervisorObjId == null)
            throw new NullPointerException("supervisorObjId is null");
        return supervisorDozerConverter.convertToDTO(supervisorDao.findById(supervisorObjId),true);
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getSupervisors(java.lang.String)
      */
    public List<Supervisor> getSupervisors(String employeeId) {
        if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        List<SupervisorEntity> superVisList = supervisorDao.findSupervisors(employeeId);

        return supervisorDozerConverter.convertToDTOList(superVisList, true);

//        List<Supervisor> supervisorList = new LinkedList<Supervisor>();
//        for (SupervisorEntity sup : superVisList) {
//            org.hibernate.Hibernate.initialize(sup.getSupervisor().getPhones());
//            org.hibernate.Hibernate.initialize(sup.getSupervisor().getEmailAddresses());
//            org.hibernate.Hibernate.initialize(sup.getSupervisor().getAddresses());
//            org.hibernate.Hibernate.initialize(sup.getSupervisor().getUserAttributes());
//            supervisorList.add(new Supervisor(sup));
//        }
//        return supervisorList;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getEmployees(java.lang.String)
      */
    public List<Supervisor> getEmployees(String supervisorId) {
        if (supervisorId == null)
            throw new NullPointerException("employeeId is null");
        List<SupervisorEntity> superVisList = supervisorDao.findEmployees(supervisorId);

       return  supervisorDozerConverter.convertToDTOList(superVisList, true);

//        List<Supervisor> supervisorList = new LinkedList<Supervisor>();
//        // initialize the collections dependant objects
//        for (SupervisorEntity sup : superVisList) {
//            org.hibernate.Hibernate.initialize(sup.getEmployee().getPhones());
//            org.hibernate.Hibernate.initialize(sup.getEmployee().getEmailAddresses());
//            org.hibernate.Hibernate.initialize(sup.getEmployee().getAddresses());
//            org.hibernate.Hibernate.initialize(sup.getEmployee().getUserAttributes());
//            supervisorList.add(new Supervisor(sup));
//        }
//
//        return supervisorList;
    }

    /*
      * (non-Javadoc)
      *
      * @see org.openiam.idm.srvc.user.service.UserDataService#getPrimarySupervisor(java.lang.String)
      */
    public Supervisor getPrimarySupervisor(String employeeId) {
        if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        SupervisorEntity entity = supervisorDao.findPrimarySupervisor(employeeId);

        return entity != null ? supervisorDozerConverter.convertToDTO(entity, true) : null;
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
