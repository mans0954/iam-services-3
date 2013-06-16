package org.openiam.idm.srvc.user.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.BaseConstants;
import org.openiam.base.SysConfiguration;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.dozer.converter.UserAttributeDozerConverter;
import org.openiam.dozer.converter.UserDozerConverter;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.login.lucene.LoginSearchDAO;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.service.AddressDAO;
import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
import org.openiam.idm.srvc.continfo.service.EmailSearchDAO;
import org.openiam.idm.srvc.continfo.service.PhoneDAO;
import org.openiam.idm.srvc.continfo.service.PhoneSearchDAO;
import org.openiam.idm.srvc.grp.service.UserGroupDAO;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.res.service.ResourceUserDAO;
import org.openiam.idm.srvc.role.service.UserRoleDAO;
import org.openiam.idm.srvc.searchbean.converter.AddressSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.EmailAddressSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.PhoneSearchBeanConverter;
import org.openiam.idm.srvc.user.dao.UserSearchDAO;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface that clients will access to gain information about users
 * and related information.
 * 
 * @author Suneet Shah
 * @version 2
 */

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
    private UserRoleDAO userRoleDAO;

    @Autowired
    private UserGroupDAO userGroupDAO;
    @Autowired
    private ResourceUserDAO resourceUserDAO;

    @Autowired
    private UserSearchDAO userSearchDAO;

    @Autowired
    private LoginSearchDAO loginSearchDAO;

    @Autowired
    private EmailSearchDAO emailSearchDAO;

    @Autowired
    private PhoneSearchDAO phoneSearchDAO;
    @Autowired
    private UserKeyDao userKeyDao;

    @Autowired
    private KeyManagementService keyManagementService;
    @Autowired
    private LoginDataService loginManager;
    @Autowired
    private EmailAddressSearchBeanConverter emailAddressSearchBeanConverter;
    @Autowired
    private AddressSearchBeanConverter addressSearchBeanConverter;
    @Autowired
    private PhoneSearchBeanConverter phoneSearchBeanConverter;

    @Autowired
    private UserAttributeDozerConverter userAttributeDozerConverter;
    @Autowired
    private UserDozerConverter userDozerConverter;
    @Autowired
    private MetadataElementDAO metadataElementDAO;

    @Value("${org.openiam.user.search.max.results}")
    private int MAX_USER_SEARCH_RESULTS;

    private static final Log log = LogFactory.getLog(UserMgr.class);

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(String id) {
        return this.getUser(id, null);
    }
    @Override
    public User getUserDto(String id){
        return userDozerConverter.convertToDTO(this.getUser(id, null), true);
    }
    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(String id, String requestorId) {
        return userDao.findByIdDelFlt(id, getDelegationFilterForUserSearch(requestorId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByPrincipal(String securityDomain, String principal, String managedSysId, boolean dependants) {
        LoginEntity login = loginDao.getRecord(principal, managedSysId, securityDomain);
        if (login == null) {
            return null;
        }
        return getUser(login.getUserId(), null);

    }

    @Override
    @Transactional
    public void addUser(UserEntity user) throws Exception {
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

        keyManagementService.generateUserKeys(user);
    }

    @Override
    @Transactional
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
    @Transactional
    private void validateEmailAddress(UserEntity user, Set<EmailAddressEntity> emailSet) {

        if (emailSet == null || emailSet.isEmpty())
            return;

        Iterator<EmailAddressEntity> it = emailSet.iterator();

        while (it.hasNext()) {
            EmailAddressEntity emailAdr = it.next();
            if (emailAdr.getParent() == null) {
                emailAdr.setParent(userDao.findById(user.getUserId()));
            }
        }

    }

    @Override
    @Transactional
    public void updateUser(UserEntity user) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getUserId() == null)
            throw new NullPointerException("user id is null");

        user.setLastUpdate(new Date(System.currentTimeMillis()));

        userDao.update(user);

    }

    @Override
    @Transactional
    public void updateUserWithDependent(UserEntity user, boolean dependency) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getUserId() == null)
            throw new NullPointerException("user id is null");

        user.setLastUpdate(new Date(System.currentTimeMillis()));

        validateEmailAddress(user, user.getEmailAddresses());
        UserEntity userOrig = userDao.findById(user.getUserId());
        userDao.merge(userOrig);
    }

    @Override
    @Transactional
    public void removeUser(String id) throws Exception {
        if (id == null)
            throw new NullPointerException("user id is null");

        // removes all the dependant objects.
        removeAllAttributes(id);
        removeAllPhones(id);
        removeAllAddresses(id);
        removeAllNotes(id);
        removeAllEmailAddresses(id);

        userGroupDAO.deleteByUserId(id);
        userRoleDAO.deleteByUserId(id);
        resourceUserDAO.deleteAllByUserId(id);
        userKeyDao.deleteByUserId(id);

        userDao.delete(userDao.findById(id));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#findUsersByLastUpdateRange
     * (java.util.Date, java.util.Date)
     */
    @Transactional(readOnly = true)
    public List findUsersByLastUpdateRange(Date startDate, Date endDate) {
        return userDao.findByLastUpdateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByName(String firstName, String lastName) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setFirstName(firstName);
        searchBean.setLastName(lastName);
        List<UserEntity> userList = findBeans(searchBean, 0, 1);
        return (userList != null && !userList.isEmpty()) ? userList.get(0) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findUserByOrganization(String orgId) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.addOrganizationId(orgId);
        return findBeans(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List findUsersByStatus(UserStatusEnum status) {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.setAccountStatus(status.name());
        return findBeans(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> searchByDelegationProperties(DelegationFilterSearch search) {
        return userDao.findByDelegationProperties(search);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findBeans(UserSearchBean searchBean) {
        return findBeans(searchBean, 0, 1);
    }
    @Transactional(readOnly = true)
    private List<String> getUserIds(final UserSearchBean searchBean) {
        final List<List<String>> nonEmptyListOfLists = new LinkedList<List<String>>();

        boolean isOrgFilterSet = false;
        boolean isDeptFilterSet = false;
        boolean isDivisionFilterSet = false;
        boolean isGroupFilterSet = false;
        boolean isRoleFilterSet = false;

        if (StringUtils.isNotBlank(searchBean.getRequesterId())) {
            // check and add delegation filter if necessary
            Map<String, UserAttribute> requesterAttributes = this.getUserAttributesDto(searchBean.getRequesterId());

            isOrgFilterSet = DelegationFilterHelper.isOrgFilterSet(requesterAttributes);
            isDeptFilterSet = DelegationFilterHelper.isDeptFilterSet(requesterAttributes);
            isDivisionFilterSet = DelegationFilterHelper.isDivisionFilterSet(requesterAttributes);
            isGroupFilterSet = DelegationFilterHelper.isGroupFilterSet(requesterAttributes);
            isRoleFilterSet = DelegationFilterHelper.isRoleFilterSet(requesterAttributes);

            if (CollectionUtils.isEmpty(searchBean.getOrganizationIdList()) && isOrgFilterSet) {
                searchBean.setOrganizationIdList(DelegationFilterHelper.getOrgIdFilterFromString(requesterAttributes));
            }

            if (CollectionUtils.isEmpty(searchBean.getGroupIdSet()) && isGroupFilterSet) {
                searchBean.setGroupIdSet(new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(requesterAttributes)));
            }

            if (CollectionUtils.isEmpty(searchBean.getRoleIdSet()) && isRoleFilterSet) {
                searchBean.setRoleIdSet(new HashSet<String>(DelegationFilterHelper.getRoleFilterFromString(requesterAttributes)));
            }

            if (CollectionUtils.isEmpty(searchBean.getDeptIdList()) && isDeptFilterSet) {
                searchBean.setDeptIdList(DelegationFilterHelper.getDeptFilterFromString(requesterAttributes));
            }
            if (CollectionUtils.isEmpty(searchBean.getDivisionIdList()) && isDivisionFilterSet) {
                searchBean.setDivisionIdList(DelegationFilterHelper.getDivisionFilterFromString(requesterAttributes));
            }
        }

        List<String> idList = userSearchDAO.findIds(0, MAX_USER_SEARCH_RESULTS, null, searchBean);

        if (CollectionUtils.isNotEmpty(idList) || (CollectionUtils.isEmpty(idList) && (isOrgFilterSet || isDeptFilterSet || isDivisionFilterSet))) {
            nonEmptyListOfLists.add(idList);
        }

        if (StringUtils.isNotBlank(searchBean.getPrincipal())) {
            final LoginSearchBean loginSearchBean = new LoginSearchBean();
            loginSearchBean.setLogin(StringUtils.trimToNull(searchBean.getPrincipal()));
            nonEmptyListOfLists.add(loginSearchDAO.findUserIds(0, MAX_USER_SEARCH_RESULTS, loginSearchBean));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
            nonEmptyListOfLists.add(userRoleDAO.getUserIdsInRole(searchBean.getRoleIdSet(), 0, MAX_USER_SEARCH_RESULTS));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
            nonEmptyListOfLists.add(userGroupDAO.getUserIdsInGroup(searchBean.getGroupIdSet(), 0, MAX_USER_SEARCH_RESULTS));
        }

        if (StringUtils.isNotBlank(searchBean.getEmailAddress())) {
            final EmailSearchBean emailSearchBean = new EmailSearchBean();
            emailSearchBean.setEmail(searchBean.getEmailAddress());
            nonEmptyListOfLists.add(emailSearchDAO.findUserIds(0, MAX_USER_SEARCH_RESULTS, emailSearchBean));
        }

        if (StringUtils.isNotBlank(searchBean.getPhoneAreaCd()) || StringUtils.isNotBlank(searchBean.getPhoneNbr())) {
            final PhoneSearchBean phoneSearchBean = new PhoneSearchBean();
            phoneSearchBean.setPhoneAreaCd(StringUtils.trimToNull(searchBean.getPhoneAreaCd()));
            phoneSearchBean.setPhoneNbr(StringUtils.trimToNull(searchBean.getPhoneNbr()));
            nonEmptyListOfLists.add(phoneSearchDAO.findUserIds(0, MAX_USER_SEARCH_RESULTS, phoneSearchBean));
        }

        // remove null or empty lists
        // for(final Iterator<List<String>> it = nonEmptyListOfLists.iterator();
        // it.hasNext();) {
        // final List<String> list = it.next();
        // if(CollectionUtils.isEmpty(list)) {
        // it.remove();
        // }
        // }

        List<String> finalizedIdList = null;
        for (final Iterator<List<String>> it = nonEmptyListOfLists.iterator(); it.hasNext();) {
            List<String> nextSubList = it.next();
            if (CollectionUtils.isEmpty(nextSubList))
                nextSubList = Collections.EMPTY_LIST;

            if (finalizedIdList == null) {
                finalizedIdList = nextSubList;
            } else {
                finalizedIdList = ListUtils.intersection(finalizedIdList, nextSubList);
            }
        }

        return (finalizedIdList != null) ? finalizedIdList : Collections.EMPTY_LIST;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findBeans(UserSearchBean searchBean, int from, int size) {
        List<UserEntity> entityList = null;
        if (StringUtils.isNotBlank(searchBean.getKey())) {
            final UserEntity entity = userDao.findById(searchBean.getKey());
            if (entity != null) {
                entityList = new ArrayList<UserEntity>(1);
                entityList.add(entity);
            }
        } else {
            List<String> finalizedIdList = getUserIds(searchBean);
            if (finalizedIdList != null && finalizedIdList.size() >= from) {
                int to = from + size;
                if (to > finalizedIdList.size()) {
                    to = finalizedIdList.size();
                }
                finalizedIdList = new ArrayList<String>(finalizedIdList.subList(from, to));
                entityList = userDao.findByIds(finalizedIdList);
            }
        }
        return entityList;
    }

    @Override
    @Transactional(readOnly = true)
    public int count(UserSearchBean searchBean) {
        return getUserIds(searchBean).size();
    }

    @Override
    @Transactional
    public void addAttribute(UserAttributeEntity attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (StringUtils.isEmpty(attribute.getUserId())) {
            throw new NullPointerException("User has not been associated with this attribute.");
        }

        UserEntity userEntity = userDao.findById(attribute.getUserId());
        attribute.setUser(userEntity);

        MetadataElementEntity element = null;
        if (attribute.getElement() != null && StringUtils.isNotEmpty(attribute.getElement().getId())) {
            element = metadataElementDAO.findById(attribute.getElement().getId());
        }
        attribute.setElement(element);

        userAttributeDao.save(attribute);
    }

    @Override
    @Transactional
    public void updateAttribute(UserAttributeEntity attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (StringUtils.isEmpty(attribute.getUserId())) {
            throw new NullPointerException("User has not been associated with this attribute.");
        }
        final UserAttributeEntity userAttribute = userAttributeDao.findById(attribute.getId());
        if (userAttribute != null) {
            UserEntity userEntity = userDao.findById(attribute.getUserId());
            attribute.setUser(userEntity);
            attribute.setElement(userAttribute.getElement());
            userAttributeDao.merge(attribute);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, UserAttributeEntity> getAllAttributes(String userId) {
        Map<String, UserAttributeEntity> attrMap = new HashMap<String, UserAttributeEntity>();

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }

        UserEntity usrEntity = userDao.findById(userId);

        if (usrEntity == null)
            return null;

        List<UserAttributeEntity> attrList = userAttributeDao.findUserAttributes(userId);

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
    @Transactional(readOnly = true)
    public UserAttributeEntity getAttribute(String attrId) {
        if (attrId == null) {
            throw new NullPointerException("attrId is null");
        }
        return userAttributeDao.findById(attrId);
    }

    @Override
    @Transactional
    public void removeAttribute(final String userAttributeId) {
        final UserAttributeEntity entity = userAttributeDao.findById(userAttributeId);
        userAttributeDao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllAttributes(String userId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        userAttributeDao.deleteUserAttributes(userId);
    }

    @Override
    @Transactional
    public void addNote(UserNoteEntity note) {
        if (note == null)
            throw new NullPointerException("Note cannot be null");

        if (note.getUserId() == null) {
            throw new NullPointerException("User is not associated with this note.");
        }
        UserEntity userEntity = StringUtils.isNotEmpty(note.getUserId()) ? userDao.findById(note.getUserId()) : null;

        note.setUser(userEntity);

        userNoteDao.save(note);
    }

    @Override
    @Transactional
    public void updateNote(UserNoteEntity note) {
        if (note == null)
            throw new NullPointerException("Note cannot be null");
        if (StringUtils.isEmpty(note.getUserNoteId())) {
            throw new NullPointerException("noteId is null");
        }
        if (StringUtils.isEmpty(note.getUserId())) {
            throw new NullPointerException("User is not associated with this note.");
        }
        UserEntity userEntity = StringUtils.isNotEmpty(note.getUserId()) ? userDao.findById(note.getUserId()) : null;
        note.setUser(userEntity);
        userNoteDao.merge(note);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserNoteEntity> getAllNotes(String userId) {
        List<UserNoteEntity> noteList = new ArrayList<UserNoteEntity>();

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        return userNoteDao.findUserNotes(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserNoteEntity getNote(String noteId) {
        if (noteId == null) {
            throw new NullPointerException("attrId is null");
        }
        return userNoteDao.findById(noteId);

    }

    @Override
    @Transactional
    public void removeNote(final String userNoteId) {
        if (userNoteId == null) {
            throw new NullPointerException("note is null");
        }
        final UserNoteEntity entity = userNoteDao.findById(userNoteId);
        userNoteDao.delete(entity);

    }

    @Override
    @Transactional
    public void removeAllNotes(String userId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        userNoteDao.deleteUserNotes(userId);

    }

    @Override
    @Transactional
    public void addAddress(AddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (val.getParent() == null)
            throw new NullPointerException("userId for the address is not defined.");

        UserEntity parent = userDao.findById(val.getParent().getUserId());
        val.setParent(parent);

        updateDefaultFlagForAddress(val, val.getIsDefault(), parent);

        addressDao.save(val);
    }

    @Override
    @Transactional
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
    @Transactional
    public void updateAddress(AddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getAddressId() == null)
            throw new NullPointerException("AddressId is null");
        if (val.getParent() == null)
            throw new NullPointerException("userId for the address is not defined.");

        AddressEntity entity = addressDao.findById(val.getAddressId());
        UserEntity parent = userDao.findById(val.getParent().getUserId());

        if (entity != null) {
            entity.setIsActive(val.getIsActive());
            entity.setBldgNumber(val.getBldgNumber());
            entity.setAddress1(val.getAddress1());
            entity.setAddress2(val.getAddress2());
            entity.setCity(val.getCity());
            entity.setPostalCd(val.getPostalCd());
            entity.setState(val.getState());
            entity.setName(val.getName());
            entity.setParent(parent);

            if (entity.getIsDefault() != val.getIsDefault()) {
                updateDefaultFlagForAddress(entity, val.getIsDefault(), parent);
            }
        }
        addressDao.update(entity);
    }

    @Override
    @Transactional
    public void removeAddress(final String addressId) {
        final AddressEntity entity = addressDao.findById(addressId, "parent");

        if (entity.getIsDefault()) {
            AddressEntity example = new AddressEntity();
            example.setParent(entity.getParent());
            List<AddressEntity> addresses = addressDao.getByExample(example);

            AddressEntity defaultAddress = getAddressByDefaultFlag(addresses, false);
            if (defaultAddress != null) {
                defaultAddress.setIsDefault(true);
                defaultAddress.setParent(entity.getParent());
                addressDao.update(defaultAddress);
            }
        }
        addressDao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllAddresses(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        addressDao.removeByUserId(userId);

    }

    @Override
    @Transactional(readOnly = true)
    public AddressEntity getAddressById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        return addressDao.findById(addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressList(String userId) {
        return this.getAddressList(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressList(String userId, Integer size, Integer from) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        AddressSearchBean searchBean = new AddressSearchBean();
        searchBean.setParentId(userId);
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        return addressDao.getByExample(addressSearchBeanConverter.convert(searchBean), from, size);
    }

    @Override
    @Transactional
    public void addPhone(PhoneEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (val.getParent() == null)
            throw new NullPointerException("parentId for the phone is not defined.");

        UserEntity parent = userDao.findById(val.getParent().getUserId());
        val.setParent(parent);

        updateDefaultFlagForPhone(val, val.getIsDefault(), parent);

        phoneDao.save(val);
    }

    @Override
    @Transactional
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
    @Transactional
    public void updatePhone(PhoneEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getPhoneId() == null)
            throw new NullPointerException("PhoneId is null");
        if (val.getParent() == null)
            throw new NullPointerException("parentId for the address is not defined.");

        PhoneEntity entity = phoneDao.findById(val.getPhoneId());
        UserEntity parent = userDao.findById(val.getParent().getUserId());

        if (entity != null) {
            entity.setAreaCd(val.getAreaCd());
            entity.setName(val.getName());
            entity.setIsActive(val.getIsActive());
            entity.setParent(parent);
            entity.setPhoneExt(val.getPhoneExt());
            entity.setPhoneNbr(val.getPhoneNbr());

            if (entity.getIsDefault() != val.getIsDefault()) {
                updateDefaultFlagForPhone(entity, val.getIsDefault(), parent);
            }
        }
        phoneDao.update(entity);
    }

    @Override
    @Transactional
    public void removePhone(final String phoneId) {
        final PhoneEntity entity = phoneDao.findById(phoneId, "parent");

        if (entity.getIsDefault()) {
            PhoneEntity example = new PhoneEntity();
            example.setParent(entity.getParent());
            List<PhoneEntity> phones = phoneDao.getByExample(example);

            PhoneEntity defaultPhone = getPhoneByDefaultFlag(phones, false);
            if (defaultPhone != null) {
                defaultPhone.setIsDefault(true);
                defaultPhone.setParent(entity.getParent());
                phoneDao.update(defaultPhone);
            }
        }

        phoneDao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllPhones(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        phoneDao.removeByUserId(userId);

    }

    @Override
    @Transactional(readOnly = true)
    public PhoneEntity getPhoneById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        return phoneDao.findById(addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneEntity> getPhoneList(String userId) {
        return this.getPhoneList(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneEntity> getPhoneList(String userId, Integer size, Integer from) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        PhoneSearchBean searchBean = new PhoneSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return phoneDao.getByExample(phoneSearchBeanConverter.convert(searchBean), from, size);
    }

    @Override
    @Transactional
    public void addEmailAddress(EmailAddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getParent() == null)
            throw new NullPointerException("parentId for the address is not defined.");

        UserEntity userEntity = userDao.findById(val.getParent().getUserId());
        val.setParent(userEntity);

        updateDefaultFlagForEmail(val, val.getIsDefault(), userEntity);

        emailAddressDao.save(val);
    }

    @Override
    @Transactional
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
    @Transactional
    public void updateEmailAddress(EmailAddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getEmailId() == null)
            throw new NullPointerException("EmailAddressId is null");
        if (val.getParent() == null)
            throw new NullPointerException("parentId for the address is not defined.");

        EmailAddressEntity entity = emailAddressDao.findById(val.getEmailId());
        UserEntity parent = userDao.findById(val.getParent().getUserId());

        if (entity != null) {
            entity.setEmailAddress(val.getEmailAddress());
            entity.setName(val.getName());
            entity.setDescription(val.getDescription());
            entity.setParent(parent);
            entity.setIsActive(val.getIsActive());

            if (entity.getIsDefault() != val.getIsDefault()) {
                updateDefaultFlagForEmail(entity, val.getIsDefault(), parent);
            }
        }
        emailAddressDao.update(entity);
    }

    @Override
    @Transactional
    public void removeEmailAddress(final String emailAddressId) {
        if (emailAddressId == null)
            throw new NullPointerException("val is null");

        final EmailAddressEntity entity = emailAddressDao.findById(emailAddressId, "parent");

        if (entity.getIsDefault()) {
            EmailAddressEntity example = new EmailAddressEntity();
            example.setParent(entity.getParent());
            List<EmailAddressEntity> emailList = emailAddressDao.getByExample(example);

            EmailAddressEntity defaultEmail = getEmailAddressByDefaultFlag(emailList, false);
            if (defaultEmail != null) {
                defaultEmail.setIsDefault(true);
                defaultEmail.setParent(entity.getParent());
                emailAddressDao.update(defaultEmail);
            }
        }

        emailAddressDao.delete(entity);
    }

    @Override
    @Transactional
    public void removeAllEmailAddresses(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        emailAddressDao.removeByUserId(userId);

    }

    @Override
    @Transactional(readOnly = true)
    public EmailAddressEntity getEmailAddressById(String addressId) {
        if (addressId == null)
            throw new NullPointerException("addressId is null");
        return emailAddressDao.findById(addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAddressEntity> getEmailAddressList(String userId) {
        return this.getEmailAddressList(userId, Integer.MAX_VALUE, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAddressEntity> getEmailAddressList(String userId, Integer size, Integer from) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        EmailSearchBean searchBean = new EmailSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return emailAddressDao.getByExample(emailAddressSearchBeanConverter.convert(searchBean), from, size);
    }

    @Override
    @Transactional
    public void addSupervisor(SupervisorEntity supervisor) {
        supervisorDao.save(supervisor);
    }

    @Override
    @Transactional
    public void updateSupervisor(SupervisorEntity supervisor) {
        if (supervisor == null)
            throw new NullPointerException("supervisor is null");
        supervisorDao.update(supervisor);
    }

    @Override
    @Transactional
    public void removeSupervisor(final String supervisorId) {
        if (supervisorId == null)
            throw new NullPointerException("supervisor is null");

        final SupervisorEntity entity = supervisorDao.findById(supervisorId);
        supervisorDao.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public SupervisorEntity getSupervisor(String supervisorObjId) {
        if (supervisorObjId == null)
            throw new NullPointerException("supervisorObjId is null");
        return supervisorDao.findById(supervisorObjId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisorEntity> getSupervisors(String employeeId) {
        if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        return supervisorDao.findSupervisors(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupervisorEntity> getEmployees(String supervisorId) {
        if (supervisorId == null)
            throw new NullPointerException("employeeId is null");
        return supervisorDao.findEmployees(supervisorId);
    }

    @Override
    @Transactional(readOnly = true)
    public SupervisorEntity getPrimarySupervisor(String employeeId) {
        if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        return supervisorDao.findPrimarySupervisor(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUsersForResource(String resourceId, String requesterId, int from, int size) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        return userDao.getUsersForResource(resourceId, delegationFilter, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfUsersForResource(String resourceId, String requesterId) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        return userDao.getNumOfUsersForResource(resourceId, delegationFilter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUsersForGroup(String groupId, String requesterId, int from, int size) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(groupId, delegationFilter.getGroupIdSet())) {
            return userDao.getUsersForGroup(groupId, delegationFilter, from, size);
        }
        return new ArrayList<UserEntity>(0);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfUsersForGroup(String groupId, String requesterId) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(groupId, delegationFilter.getGroupIdSet())) {
            return userDao.getNumOfUsersForGroup(groupId, delegationFilter);
        }
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUsersForRole(String roleId, String requesterId, int from, int size) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(roleId, delegationFilter.getRoleIdSet())) {
            return userDao.getUsersForRole(roleId, delegationFilter, from, size);
        }
        return new ArrayList<UserEntity>(0);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNumOfUsersForRole(String roleId, String requesterId) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(roleId, delegationFilter.getRoleIdSet())) {
            return userDao.getNumOfUsersForRole(roleId, delegationFilter);
        }
        return 0;
    }

    @Override
    @Transactional
    public String saveUserInfo(UserEntity newUserEntity, SupervisorEntity supervisorEntity) throws Exception {
        String userId = newUserEntity.getUserId();
        if (newUserEntity.getUserId() != null) {
            // update, need to merge user objects
            UserEntity origUser = this.getUser(newUserEntity.getUserId(), null);
            this.mergeUserFields(origUser, newUserEntity);
            userDao.update(origUser);
        } else {
            userId = createNewUser(newUserEntity);
        }
        if (supervisorEntity != null) {
            // update supervisor
            List<SupervisorEntity> supervisorList = this.getSupervisors(newUserEntity.getUserId());
            for (SupervisorEntity s : supervisorList) {
                log.debug("looking to match supervisor ids = " + s.getSupervisor().getUserId() + " " + supervisorEntity.getSupervisor().getUserId());
                if (s.getSupervisor().getUserId().equalsIgnoreCase(supervisorEntity.getSupervisor().getUserId())) {
                    break;
                }
                this.removeSupervisor(s.getOrgStructureId());
            }
            log.debug("adding supervisor: " + supervisorEntity.getSupervisor().getUserId());
            supervisorEntity.setEmployee(newUserEntity);

            this.addSupervisor(supervisorEntity);
        }
        return userId;
    }

    @Transactional
    private String createNewUser(UserEntity newUserEntity) throws Exception {
        List<LoginEntity> principalList = newUserEntity.getPrincipalList();
        Set<EmailAddressEntity> emailAddressList = newUserEntity.getEmailAddresses();

        newUserEntity.setPrincipalList(null);
        // newUserEntity.setEmailAddresses(null);

        this.addUser(newUserEntity);

        if (principalList != null && !principalList.isEmpty()) {
            for (LoginEntity lg : principalList) {
                lg.setDomainId(sysConfiguration.getDefaultSecurityDomain());
                lg.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
                lg.setFirstTimeLogin(1);
                lg.setIsLocked(0);
                lg.setCreateDate(new Date(System.currentTimeMillis()));
                lg.setUserId(newUserEntity.getUserId());
                lg.setStatus("ACTIVE");
                // encrypt the password
                if (lg.getPassword() != null) {
                    String pswd = lg.getPassword();
                    lg.setPassword(loginManager.encryptPassword(newUserEntity.getUserId(), pswd));
                }
                loginDao.save(lg);
            }
        }
        if (emailAddressList != null && !emailAddressList.isEmpty()) {
            for (final EmailAddressEntity email : emailAddressList) {
                email.setParent(newUserEntity);
            }
            this.addEmailAddressSet(emailAddressList);
        }
        return newUserEntity.getUserId();
    }

    @Transactional
    public void deleteUser(String userId) {
        List<LoginEntity> loginList = loginDao.findUser(userId);
        if (loginList == null || loginList.isEmpty()) {
            throw new NullPointerException("Principal Not Found");
        }
        for (LoginEntity login : loginList) {
            // change the status on the identity
            login.setStatus("INACTIVE");
            loginDao.update(login);
        }
        // Turning off the primary identity - change the status on the user
        if (userId != null) {
            UserEntity usr = this.getUser(userId, null);
            usr.setStatus(UserStatusEnum.DELETED);
            userDao.update(usr);
        }
    }
    @Transactional
    public void enableDisableUser(String userId, UserStatusEnum secondaryStatus) {
        UserEntity user = this.getUser(userId, null);
        if (user == null) {
            log.error("UserId " + userId + " not found");
            throw new NullPointerException("UserId " + userId + " not found");
        }
        user.setSecondaryStatus(secondaryStatus);
        userDao.update(user);
    }

    @Transactional
    public void activateUser(String userId) {
        UserEntity user = this.getUser(userId, null);
        if (user == null) {
            log.error("UserId " + userId + " not found");
            throw new NullPointerException("UserId " + userId + " not found");
        }
        List<LoginEntity> loginList = loginDao.findUser(userId);
        if (loginList == null || loginList.isEmpty()) {
            throw new NullPointerException("Principal Not Found");
        }

        for (LoginEntity login : loginList) {
            // change the status on the identity
            login.setStatus(null);
            loginDao.update(login);
        }
        if (userId != null) {
            user.setStatus(UserStatusEnum.ACTIVE);
            userDao.update(user);
        }
    }
    @Transactional(readOnly = true)
    public Integer getNumOfEmailsForUser(String userId) {
        EmailSearchBean searchBean = new EmailSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return emailAddressDao.count(emailAddressSearchBeanConverter.convert(searchBean));
    }
    @Transactional(readOnly = true)
    public Integer getNumOfAddressesForUser(String userId) {
        AddressSearchBean searchBean = new AddressSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return addressDao.count(addressSearchBeanConverter.convert(searchBean));
    }
    @Transactional(readOnly = true)
    public Integer getNumOfPhonesForUser(String userId) {
        PhoneSearchBean searchBean = new PhoneSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return phoneDao.count(phoneSearchBeanConverter.convert(searchBean));
    }

    @Transactional
    public void mergeUserFields(UserEntity origUserEntity, UserEntity newUserEntity) {
        if (newUserEntity.getBirthdate() != null) {
            if (newUserEntity.getBirthdate().equals(BaseConstants.NULL_DATE)) {
                origUserEntity.setBirthdate(null);
            } else {
                origUserEntity.setBirthdate(newUserEntity.getBirthdate());
            }
        }
        if (newUserEntity.getClassification() != null) {
            if (newUserEntity.getClassification().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setClassification(null);
            } else {
                origUserEntity.setClassification(newUserEntity.getClassification());
            }
        }
        if (newUserEntity.getCompanyId() != null) {
            if (newUserEntity.getCompanyId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setCompanyId(null);
            } else {
                origUserEntity.setCompanyId(newUserEntity.getCompanyId());
            }
        }
        if (newUserEntity.getCostCenter() != null) {
            if (newUserEntity.getCostCenter().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setCostCenter(null);
            } else {
                origUserEntity.setCostCenter(newUserEntity.getCostCenter());
            }
        }
        if (newUserEntity.getDeptCd() != null) {
            if (newUserEntity.getDeptCd().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setDeptCd(null);
            } else {
                origUserEntity.setDeptCd(newUserEntity.getDeptCd());
            }
        }
        if (newUserEntity.getDivision() != null) {
            if (newUserEntity.getDivision().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setDivision(null);
            } else {
                origUserEntity.setDivision(newUserEntity.getDivision());
            }
        }

        if (newUserEntity.getEmployeeId() != null) {
            if (newUserEntity.getEmployeeId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setEmployeeId(null);
            } else {
                origUserEntity.setEmployeeId(newUserEntity.getEmployeeId());
            }
        }
        if (newUserEntity.getEmployeeType() != null) {
            if (newUserEntity.getEmployeeType().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setEmployeeType(null);
            } else {
                origUserEntity.setEmployeeType(newUserEntity.getEmployeeType());
            }
        }
        if (newUserEntity.getFirstName() != null) {
            if (newUserEntity.getFirstName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setFirstName(null);
            } else {
                origUserEntity.setFirstName(newUserEntity.getFirstName());
            }
        }
        if (newUserEntity.getJobCode() != null) {
            if (newUserEntity.getJobCode().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setJobCode(null);
            } else {
                origUserEntity.setJobCode(newUserEntity.getJobCode());
            }
        }
        if (newUserEntity.getLastName() != null) {
            if (newUserEntity.getLastName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setLastName(null);
            } else {
                origUserEntity.setLastName(newUserEntity.getLastName());
            }
        }
        if (newUserEntity.getLastDate() != null) {
            if (newUserEntity.getLastDate().equals(BaseConstants.NULL_DATE)) {
                origUserEntity.setLastDate(null);
            } else {
                origUserEntity.setLastDate(newUserEntity.getLastDate());
            }
        }
        if (newUserEntity.getMaidenName() != null) {
            if (newUserEntity.getMaidenName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setMaidenName(null);
            } else {
                origUserEntity.setMaidenName(newUserEntity.getMaidenName());
            }
        }
        if (newUserEntity.getMetadataTypeId() != null) {
            if (newUserEntity.getMetadataTypeId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setMetadataTypeId(null);
            } else {
                origUserEntity.setMetadataTypeId(newUserEntity.getMetadataTypeId());
            }
        }
        if (newUserEntity.getMiddleInit() != null) {
            if (newUserEntity.getMiddleInit().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setMiddleInit(null);
            } else {
                origUserEntity.setMiddleInit(newUserEntity.getMiddleInit());
            }
        }
        if (newUserEntity.getNickname() != null) {
            if (newUserEntity.getNickname().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setNickname(null);
            } else {
                origUserEntity.setNickname(newUserEntity.getNickname());
            }
        }
        if (newUserEntity.getSecondaryStatus() != null) {
            origUserEntity.setSecondaryStatus(newUserEntity.getSecondaryStatus());
        }
        if (newUserEntity.getSex() != null) {
            if (newUserEntity.getSex().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setSex(null);
            } else {
                origUserEntity.setSex(newUserEntity.getSex());
            }
        }
        if (newUserEntity.getStartDate() != null) {
            if (newUserEntity.getStartDate().equals(BaseConstants.NULL_DATE)) {
                origUserEntity.setStartDate(null);
            } else {
                origUserEntity.setStartDate(newUserEntity.getStartDate());
            }
        }

        if (newUserEntity.getStatus() != null) {
            origUserEntity.setStatus(newUserEntity.getStatus());
        }
        if (newUserEntity.getSuffix() != null) {
            if (newUserEntity.getSuffix().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setSuffix(null);
            } else {
                origUserEntity.setSuffix(newUserEntity.getSuffix());
            }
        }
        if (newUserEntity.getShowInSearch() != null) {
            if (newUserEntity.getShowInSearch().equals(BaseConstants.NULL_INTEGER)) {
                origUserEntity.setShowInSearch(0);
            } else {
                origUserEntity.setShowInSearch(newUserEntity.getShowInSearch());
            }
        }
        if (newUserEntity.getTitle() != null) {
            if (newUserEntity.getTitle().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setTitle(null);
            } else {
                origUserEntity.setTitle(newUserEntity.getTitle());
            }
        }
        if (newUserEntity.getUserTypeInd() != null) {
            if (newUserEntity.getUserTypeInd().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setUserTypeInd(null);
            } else {
                origUserEntity.setUserTypeInd(newUserEntity.getUserTypeInd());
            }
        }
        if (newUserEntity.getAlternateContactId() != null) {
            if (newUserEntity.getAlternateContactId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setAlternateContactId(null);
            } else {
                origUserEntity.setAlternateContactId(newUserEntity.getAlternateContactId());
            }
        }

    }

    @Transactional
    private void updateDefaultFlagForPhone(PhoneEntity targetEntity, boolean newDefaultValue, UserEntity parent) {
        // update default flag
        // 1. get all default phone for user and iterate them
        PhoneEntity example = new PhoneEntity();
        example.setParent(parent);
        List<PhoneEntity> phones = phoneDao.getByExample(example);

        PhoneEntity defaultPhone = getPhoneByDefaultFlag(phones, true);

        if (defaultPhone == null) {
            targetEntity.setIsDefault(true);
        } else {
            if (defaultPhone.getPhoneId().equals(targetEntity.getPhoneId())) {
                // the same entity
                // check if default flag is unset
                if (!newDefaultValue) {
                    // need to set new default phone
                    defaultPhone = getPhoneByDefaultFlag(phones, false);
                    if (defaultPhone != null) {
                        defaultPhone.setIsDefault(true);
                        defaultPhone.setParent(parent);
                        phoneDao.update(defaultPhone);
                    }
                }
            } else {
                if (newDefaultValue) {
                    // unset default entity
                    defaultPhone.setIsDefault(false);
                    defaultPhone.setParent(parent);
                    phoneDao.update(defaultPhone);
                }
            }
            targetEntity.setIsDefault(newDefaultValue);
        }
    }

    @Transactional
    private void updateDefaultFlagForAddress(AddressEntity targetEntity, boolean newDefaultValue, UserEntity parent) {
        // update default flag
        // 1. get all default phone for user and iterate them
        AddressEntity example = new AddressEntity();
        example.setParent(parent);
        List<AddressEntity> addresses = addressDao.getByExample(example);

        AddressEntity defaultAddress = getAddressByDefaultFlag(addresses, true);

        if (defaultAddress == null) {
            targetEntity.setIsDefault(true);
        } else {
            if (defaultAddress.getAddressId().equals(targetEntity.getAddressId())) {
                // the same entity
                // check if default flag is unset
                if (!newDefaultValue) {
                    // need to set new default phone
                    defaultAddress = getAddressByDefaultFlag(addresses, false);
                    if (defaultAddress != null) {
                        defaultAddress.setIsDefault(true);
                        defaultAddress.setParent(parent);
                        addressDao.update(defaultAddress);
                    }
                }
            } else {
                if (newDefaultValue) {
                    // unset default entity
                    defaultAddress.setIsDefault(false);
                    defaultAddress.setParent(parent);
                    addressDao.update(defaultAddress);
                }
            }
            targetEntity.setIsDefault(newDefaultValue);
        }
    }

    @Transactional
    private void updateDefaultFlagForEmail(EmailAddressEntity targetEntity, boolean newDefaultValue, UserEntity parent) {
        // update default flag
        // 1. get all default phone for user and iterate them
        EmailAddressEntity example = new EmailAddressEntity();
        example.setParent(parent);
        List<EmailAddressEntity> emailList = emailAddressDao.getByExample(example);

        EmailAddressEntity defaultEmail = getEmailAddressByDefaultFlag(emailList, true);

        if (defaultEmail == null) {
            targetEntity.setIsDefault(true);
        } else {
            if (defaultEmail.getEmailId().equals(targetEntity.getEmailId())) {
                // the same entity
                // check if default flag is unset
                if (!newDefaultValue) {
                    // need to set new default phone
                    defaultEmail = getEmailAddressByDefaultFlag(emailList, false);
                    if (defaultEmail != null) {
                        defaultEmail.setIsDefault(true);
                        defaultEmail.setParent(parent);
                        emailAddressDao.update(defaultEmail);
                    }
                }
            } else {
                if (newDefaultValue) {
                    // unset default entity
                    defaultEmail.setIsDefault(false);
                    defaultEmail.setParent(parent);
                    emailAddressDao.update(defaultEmail);
                }
            }
            targetEntity.setIsDefault(newDefaultValue);
        }
    }

    private PhoneEntity getPhoneByDefaultFlag(List<PhoneEntity> phones, boolean isDefault) {
        if (phones != null && !phones.isEmpty()) {
            for (PhoneEntity p : phones) {
                if (p.getIsDefault() == isDefault)
                    return p;
            }
        }
        return null;
    }

    private AddressEntity getAddressByDefaultFlag(List<AddressEntity> addressEntityList, boolean isDefault) {
        if (addressEntityList != null && !addressEntityList.isEmpty()) {
            for (AddressEntity a : addressEntityList) {
                if (a.getIsDefault() == isDefault)
                    return a;
            }
        }
        return null;
    }

    private EmailAddressEntity getEmailAddressByDefaultFlag(List<EmailAddressEntity> emailList, boolean isDefault) {
        if (emailList != null && !emailList.isEmpty()) {
            for (EmailAddressEntity e : emailList) {
                if (e.getIsDefault() == isDefault)
                    return e;
            }
        }
        return null;
    }

    private DelegationFilterSearchBean getDelegationFilterForUserSearch(String requestorId) {
        DelegationFilterSearchBean filter = new DelegationFilterSearchBean();

        if (StringUtils.isNotBlank(requestorId)) {
            Map<String, UserAttribute> requestorAttributes = this.getUserAttributesDto(requestorId);

            if (DelegationFilterHelper.isOrgFilterSet(requestorAttributes)) {
                filter.setOrganizationIdSet(new HashSet<String>(DelegationFilterHelper.getOrgIdFilterFromString(requestorAttributes)));
            }

            if (DelegationFilterHelper.isGroupFilterSet(requestorAttributes)) {
                filter.setGroupIdSet(new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(requestorAttributes)));
            }

            if (DelegationFilterHelper.isRoleFilterSet(requestorAttributes)) {
                filter.setRoleIdSet(new HashSet<String>(DelegationFilterHelper.getRoleFilterFromString(requestorAttributes)));
            }

            if (DelegationFilterHelper.isDeptFilterSet(requestorAttributes)) {
                filter.setDeptIdSet(new HashSet<String>(DelegationFilterHelper.getDeptFilterFromString(requestorAttributes)));
            }
            if (DelegationFilterHelper.isDivisionFilterSet(requestorAttributes)) {
                filter.setDivisionIdSet(new HashSet<String>(DelegationFilterHelper.getDivisionFilterFromString(requestorAttributes)));
            }
        }
        return filter;
    }
    @Transactional(readOnly = true)
    public List<UserEntity> getUsersForMSys(String mSysId) {
        return userDao.getUsersForMSys(mSysId);
    }

    public Map<String, UserAttribute> getUserAttributesDto(String userId) {
        Map<String, UserAttributeEntity> attributeEntityMap = this.getUserAttributes(userId);
        if (attributeEntityMap != null && !attributeEntityMap.isEmpty()) {
            Map<String, UserAttribute> attributeMap = new HashMap<String, UserAttribute>();
            for (String key : attributeEntityMap.keySet()) {
                UserAttributeEntity entity = attributeEntityMap.get(key);
                if (entity != null) {
                    attributeMap.put(key, userAttributeDozerConverter.convertToDTO(entity, false));
                }
            }
            return attributeMap;
        }
        return null;
    }

    public Map<String, UserAttributeEntity> getUserAttributes(String userId) {
        Map<String, UserAttributeEntity> result = null;
        List<UserAttributeEntity> userAttributes = userAttributeDao.findUserAttributes(userId);
        if (userAttributes != null && !userAttributes.isEmpty()) {
            result = new HashMap<String, UserAttributeEntity>();
            for (UserAttributeEntity entity : userAttributes) {
                result.put(entity.getName(), entity);
            }
        }
        return result;
    }
}
