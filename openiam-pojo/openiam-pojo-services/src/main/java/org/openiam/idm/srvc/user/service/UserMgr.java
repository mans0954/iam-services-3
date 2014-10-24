package org.openiam.idm.srvc.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseConstants;
import org.openiam.base.OrderConstants;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.SearchMode;
import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.dozer.converter.*;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.login.lucene.LoginSearchDAO;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.continfo.service.*;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.UserIdentityAnswerDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.searchbean.converter.AddressSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.EmailAddressSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.PhoneSearchBeanConverter;
import org.openiam.idm.srvc.user.dao.UserSearchDAO;
import org.openiam.idm.srvc.user.domain.*;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    private UserSearchDAO userSearchDAO;

    @Autowired
    private LoginSearchDAO loginSearchDAO;

    @Autowired
    @Qualifier("groupDAO")
    private GroupDAO groupDAO;

    @Autowired
    private EmailSearchDAO emailSearchDAO;
    @Autowired
    private ResourceDAO resourceDAO;
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
    private AddressDozerConverter addressDozerConverter;
    @Autowired
    EmailAddressDozerConverter emailAddressDozerConverter;
    @Autowired
    PhoneDozerConverter phoneDozerConverter;

    @Autowired
    private MetadataElementDAO metadataElementDAO;
    @Autowired
    private MetadataTypeDAO metadataTypeDAO;
    @Autowired
    private PasswordHistoryDAO passwordHistoryDAO;
    @Autowired
    private AuthStateDAO authStateDAO;
    @Autowired
    private UserIdentityAnswerDAO userIdentityAnswerDAO;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private RoleDataService roleDataService;

    @Value("${org.openiam.user.search.max.results}")
    private int MAX_USER_SEARCH_RESULTS;

    @Autowired
    @Qualifier("authorizationManagerService")
    private AuthorizationManagerService authorizationManagerService;

    private static final Log log = LogFactory.getLog(UserMgr.class);

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(String id) {
        return this.getUser(id, null);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserDto(String id) {
        return userDozerConverter.convertToDTO(this.getUser(id, null), true);
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUser(String id, String requestorId) {
        return userDao.findByIdDelFlt(id, getDelegationFilterForUserSearch(requestorId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getUserByPrincipal(String principal, String managedSysId, boolean dependants) {
        LoginEntity login = loginDao.getRecord(principal, managedSysId);
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
        setMetadataTypes(user);
        userDao.save(user);
        keyManagementService.generateUserKeys(user);

        addRequiredAttributes(user);
    }

    @Transactional
    public void addRequiredAttributes(UserEntity user) {
        if(user!=null && user.getType()!=null && StringUtils.isNotBlank(user.getType().getId())){
            MetadataElementSearchBean sb = new MetadataElementSearchBean();
            sb.addTypeId(user.getType().getId());
            List<MetadataElementEntity> elementList = metadataElementDAO.getByExample(sb, -1, -1);
            if(CollectionUtils.isNotEmpty(elementList)){
                for(MetadataElementEntity element: elementList){
                    if(element.isRequired()){
                        userAttributeDao.save(AttributeUtil.buildUserAttribute(user, element));
                    }
                }
            }
        }
    }

    @Transactional
    private void validateEmailAddress(UserEntity user, Set<EmailAddressEntity> emailSet) {

        if (emailSet == null || emailSet.isEmpty())
            return;

        Iterator<EmailAddressEntity> it = emailSet.iterator();

        while (it.hasNext()) {
            EmailAddressEntity emailAdr = it.next();
            if (emailAdr.getParent() == null) {
                emailAdr.setParent(userDao.findById(user.getId()));
            }
        }

    }

    @Override
    @Transactional
    public void updateUser(UserEntity user) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getId() == null)
            throw new NullPointerException("user id is null");

        user.setLastUpdate(new Date(System.currentTimeMillis()));
        setMetadataTypes(user);
        userDao.update(user);

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserWithDependent(UserEntity user, boolean dependency) {
        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getId() == null)
            throw new NullPointerException("user id is null");
        user.setLastUpdate(new Date(System.currentTimeMillis()));

        UserEntity userEntity = userDao.findById(user.getId());

        updateUserAttributes(user, userEntity);

        userEntity.updateUser(user);
        setMetadataTypes(user);
        userDao.update(userEntity);
        validateEmailAddress(userEntity, user.getEmailAddresses());

    }

    @Transactional
    public void updateUserFromDto(User user) {

        if (user == null)
            throw new NullPointerException("user object is null");
        if (user.getId() == null)
            throw new NullPointerException("user id is null");
        // Processing emails
        user.setLastUpdate(new Date(System.currentTimeMillis()));

        UserEntity userEntity = userDao.findById(user.getId());
        userEntity.updateUser(userDozerConverter.convertToEntity(user, false));

        // Processing emails
        Set<EmailAddress> emailAddresses = user.getEmailAddresses();
        if (CollectionUtils.isNotEmpty(emailAddresses)) {
            for (EmailAddress e : emailAddresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    EmailAddressEntity entity = emailAddressDao.findById(e.getEmailId());
                    if (entity != null) {
                        userEntity.getEmailAddresses().remove(entity);
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    EmailAddressEntity entity = emailAddressDao.findById(e.getEmailId());
                    if (entity != null) {
                        emailAddressDao.evict(entity);
                    }
                    entity = emailAddressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getEmailAddresses().add(entity);
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    EmailAddressEntity entity = emailAddressDao.findById(e.getEmailId());
                    if (entity != null) {
                        userEntity.getEmailAddresses().remove(entity);
                        emailAddressDao.evict(entity);
                        entity = emailAddressDozerConverter.convertToEntity(e, false);
                        entity.setParent(userEntity);
                        userEntity.getEmailAddresses().add(entity);
                    }
                }
            }
        }

        // Processing addresses
        Set<Address> addresses = user.getAddresses();
        if (CollectionUtils.isNotEmpty(addresses)) {
            for (Address e : addresses) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    AddressEntity entity = addressDao.findById(e.getAddressId());
                    if (entity != null) {
                        userEntity.getAddresses().remove(entity);
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getAddresses().add(entity);
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    AddressEntity entity = addressDao.findById(e.getAddressId());
                    if (entity != null) {
                        userEntity.getAddresses().remove(entity);
                        addressDao.evict(entity);
                        entity = addressDozerConverter.convertToEntity(e, false);
                        entity.setParent(userEntity);
                        userEntity.getAddresses().add(entity);
                    }
                }
            }
        }

        // Processing phones
        Set<Phone> phones = user.getPhones();
        if (CollectionUtils.isNotEmpty(phones)) {
            for (Phone e : phones) {
                if (e.getOperation() == null) {
                    continue;
                }
                if (e.getOperation().equals(AttributeOperationEnum.DELETE)) {
                    PhoneEntity entity = phoneDao.findById(e.getId());
                    if (entity != null) {
                        userEntity.getPhones().remove(entity);
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    PhoneEntity entity = phoneDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getPhones().add(entity);
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    PhoneEntity entity = phoneDao.findById(e.getId());
                    if (entity != null) {
                        userEntity.getPhones().remove(entity);
                        phoneDao.evict(entity);
                        entity = phoneDozerConverter.convertToEntity(e, false);
                        entity.setParent(userEntity);
                        userEntity.getPhones().add(entity);
                    }
                }
            }
        }

        // Processing user attributes
        updateUserAttributes(userDozerConverter.convertToEntity(user, true), userEntity);

        // TODO: Check userRoles and affiliations
        
        setMetadataTypes(userEntity);
        userDao.update(userEntity);

    }

    private void updateUserAttributes(final UserEntity user, final UserEntity userEntity) {
        Map<String, UserAttributeEntity> incomingAttributes = user.getUserAttributes();
        Map<String, UserAttributeEntity> existingAttributes = userEntity.getUserAttributes();
        incomingAttributes = (incomingAttributes != null) ? incomingAttributes : new HashMap<String, UserAttributeEntity>();
        existingAttributes = (existingAttributes != null) ? existingAttributes : new HashMap<String, UserAttributeEntity>();

        final List<UserAttributeEntity> deleteList = new LinkedList<UserAttributeEntity>();
        final List<UserAttributeEntity> editList = new LinkedList<UserAttributeEntity>();
        final List<UserAttributeEntity> newList = new LinkedList<UserAttributeEntity>();

        for (final String incomingKey : incomingAttributes.keySet()) {
            /* new */
            final UserAttributeEntity incomingEntity = incomingAttributes.get(incomingKey);
            final UserAttributeEntity existingEntity = existingAttributes.get(incomingKey);
            if (existingEntity == null) {
                // incomingEntity.setUser(userEntity);
                newList.add(incomingEntity);
            } else { /* exists - modify */
                // existingEntity.setUser(userEntity);
                existingEntity.setElement(incomingEntity.getElement());
                existingEntity.setName(incomingEntity.getName());
                existingEntity.setValue(incomingEntity.getValue());
                existingEntity.setIsMultivalued(incomingEntity.getIsMultivalued());
                existingEntity.setValues(incomingEntity.getValues());
                editList.add(existingEntity);
            }
        }

        for (final String oldKey : existingAttributes.keySet()) {
            if (!incomingAttributes.containsKey(oldKey)) {
                deleteList.add(existingAttributes.get(oldKey));
            }
        }

        for (final UserAttributeEntity entity : newList) {
            userAttributeDao.save(entity);
        }

        for (final UserAttributeEntity entity : editList) {
            userAttributeDao.update(entity);
        }

        for (final UserAttributeEntity entity : deleteList) {
            userAttributeDao.delete(entity);
        }

        user.setUserAttributes(incomingAttributes);
    }

    @Override
    @Transactional
    public void removeUser(String id) throws Exception {
        if (id == null) {
            throw new NullPointerException("user id is null");
        }
        // removes all the dependant objects.
        // removeAllAttributes(id);
       // removeAllPhones(id);
       // removeAllAddresses(id);
       // removeAllNotes(id);
       // removeAllEmailAddresses(id);

        // userKeyDao.deleteByUserId(id);
        //List<UserEntity> supervisors = getSuperiors(id, 0, Integer.MAX_VALUE);
        //for(UserEntity se : supervisors) {
        //   removeSupervisor(se.getId(), id);
        //}

        authStateDAO.deleteByUser(id);
        userIdentityAnswerDAO.deleteByUser(id);
        userDao.delete(userDao.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findUserByOrganization(String orgId) throws BasicDataServiceException {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.addOrganizationId(orgId);
        return findBeans(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> searchByDelegationProperties(DelegationFilterSearch search) {
        return userDao.findByDelegationProperties(search);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findBeans(UserSearchBean searchBean) throws BasicDataServiceException {
        return findBeans(searchBean, 0, 1);
    }

    @Transactional(readOnly = true)
    private List<String> getUserIds(final UserSearchBean searchBean) throws BasicDataServiceException {
        final List<List<String>> nonEmptyListOfLists = new LinkedList<List<String>>();

        boolean isOrgFilterSet = false;
        boolean isGroupFilterSet = false;
        boolean isRoleFilterSet = false;
        boolean isMngReportFilterSet = false;

        if (StringUtils.isNotBlank(searchBean.getRequesterId())) {
            // check and add delegation filter if necessary
            Map<String, UserAttribute> requesterAttributes = this.getUserAttributesDto(searchBean.getRequesterId());

            validateSearchBean(searchBean,  requesterAttributes);

            isOrgFilterSet = DelegationFilterHelper.isOrgFilterSet(requesterAttributes);
            isGroupFilterSet = DelegationFilterHelper.isGroupFilterSet(requesterAttributes);
            isRoleFilterSet = DelegationFilterHelper.isRoleFilterSet(requesterAttributes);
            isMngReportFilterSet = DelegationFilterHelper.isMngRptFilterSet(requesterAttributes);

//            if (isOrgFilterSet) {
            if (CollectionUtils.isEmpty(searchBean.getOrganizationIdSet())) {
                searchBean.addOrganizationIdList(organizationService.getDelegationFilter(requesterAttributes, null));
            }
//            }

            if (CollectionUtils.isEmpty(searchBean.getGroupIdSet()) && isGroupFilterSet) {
                searchBean.setGroupIdSet(new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(requesterAttributes)));
            }

            if (CollectionUtils.isEmpty(searchBean.getRoleIdSet()) && isRoleFilterSet) {
                searchBean.setRoleIdSet(new HashSet<String>(DelegationFilterHelper.getRoleFilterFromString(requesterAttributes)));
            }
        }
        List<String> idList = null;
        if(isMngReportFilterSet){
            idList = userDao.getSubordinatesIds(searchBean.getRequesterId());
            idList.add(searchBean.getRequesterId());
        } else {
            idList = userSearchDAO.findIds(0, MAX_USER_SEARCH_RESULTS, null, searchBean);
        }

        if (CollectionUtils.isNotEmpty(idList) || (CollectionUtils.isEmpty(idList) && (isOrgFilterSet))) {
            nonEmptyListOfLists.add(idList);
        }

        if (CollectionUtils.isNotEmpty(searchBean.getAttributeList())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForAttributes(searchBean.getAttributeList(), 0,
                                                            MAX_USER_SEARCH_RESULTS));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForRoles(searchBean.getRoleIdSet(), 0, MAX_USER_SEARCH_RESULTS));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForOrganizations(searchBean.getOrganizationIdSet(), 0, MAX_USER_SEARCH_RESULTS));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForGroups(searchBean.getGroupIdSet(), 0, MAX_USER_SEARCH_RESULTS));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getResourceIdSet())) {
            // direct entitlements
            List<String> resultUserIdList=new ArrayList<String>();
            List<String> userIds=authorizationManagerService.getUserIdsList();
            if(CollectionUtils.isNotEmpty(userIds)){
                for (String usrId: userIds){
                    for (String resId: searchBean.getResourceIdSet()){
                        if(authorizationManagerService.isEntitled(usrId, resId))
                            resultUserIdList.add(usrId);
                    }
                }
            }
            nonEmptyListOfLists.add(resultUserIdList);
        }

        if (searchBean.getPrincipal() != null) {
            nonEmptyListOfLists.add(loginSearchDAO.findUserIds(0, MAX_USER_SEARCH_RESULTS, searchBean.getPrincipal()));
        }

        if (searchBean.getEmailAddressMatchToken() != null && searchBean.getEmailAddressMatchToken().isValid()) {
            final EmailSearchBean emailSearchBean = new EmailSearchBean();
            emailSearchBean.setEmailMatchToken(searchBean.getEmailAddressMatchToken());
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
        
        if(SearchMode.AND.equals(searchBean.getSearchMode())) {
	        for (final Iterator<List<String>> it = nonEmptyListOfLists.iterator(); it.hasNext();) {
	            List<String> nextSubList = it.next();
	            if (CollectionUtils.isEmpty(nextSubList))
	                nextSubList = Collections.EMPTY_LIST;
	
	            if (CollectionUtils.isEmpty(finalizedIdList)) {
	                finalizedIdList = nextSubList;
	            } else {
	                finalizedIdList = ListUtils.intersection(finalizedIdList, nextSubList);
	            }
	        }
        } else { //OR
        	final Set<String> resultSet = new HashSet<>();
        	for (final Iterator<List<String>> it = nonEmptyListOfLists.iterator(); it.hasNext();) {
	            List<String> nextSubList = it.next();
	            if(CollectionUtils.isNotEmpty(nextSubList)) {
	            	resultSet.addAll(nextSubList);
	            }
        	}
        	
        	for(final String result : resultSet) {
        		if(finalizedIdList == null) {
        			finalizedIdList = new LinkedList<>();
        		}
        		if(finalizedIdList.size() < MAX_USER_SEARCH_RESULTS) {
        			finalizedIdList.add(result);
        		}
        	}
        }

        return (finalizedIdList != null) ? finalizedIdList : Collections.EMPTY_LIST;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findBeans(UserSearchBean searchBean, int from, int size) throws BasicDataServiceException {
        List<UserEntity> entityList = null;
        if (StringUtils.isNotBlank(searchBean.getKey())) {
            final UserEntity entity = userDao.findById(searchBean.getKey());
            if (entity != null) {
                entityList = new ArrayList<UserEntity>(1);
                entityList.add(entity);
            }
        } else {
            List<UserEntity> finalizedIdList = userDao.findByIds(getUserIds(searchBean), searchBean);
            if (from > -1 && size > -1) {
                if (finalizedIdList != null && finalizedIdList.size() >= from) {
                    int to = from + size;
                    if (to > finalizedIdList.size()) {
                        to = finalizedIdList.size();
                    }
                    finalizedIdList = new ArrayList<UserEntity>(finalizedIdList.subList(from, to));
                }
            }
            entityList = finalizedIdList;
        }

        if(searchBean.getInitDefaulLoginFlag()){
            for(UserEntity usr: entityList){
                usr.setDefaultLogin(sysConfiguration.getDefaultManagedSysId());
            }
        }

        return entityList;
    }

    @Override
    @Transactional(readOnly = true)
    public int count(UserSearchBean searchBean) throws BasicDataServiceException {
        return userDao.findByIds(getUserIds(searchBean)).size();
    }

    @Override
    @Transactional
    public void addAttribute(UserAttributeEntity attribute) {
        if (attribute == null)
            throw new NullPointerException("Attribute can not be null");

        if (attribute.getUser() == null || StringUtils.isBlank(attribute.getUser().getId())) {
            throw new NullPointerException("User has not been associated with this attribute.");
        }

        UserEntity userEntity = userDao.findById(attribute.getUser().getId());
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

        if (attribute.getUser() == null || StringUtils.isBlank(attribute.getUser().getId())) {
            throw new NullPointerException("User has not been associated with this attribute.");
        }
        final UserAttributeEntity userAttribute = userAttributeDao.findById(attribute.getId());
        if (userAttribute != null) {
            UserEntity userEntity = userDao.findById(attribute.getUser().getId());
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

        if (val.getMetadataType() == null || StringUtils.isBlank(val.getMetadataType().getId())) {
            throw new NullPointerException("MetadataType for the address is not defined.");
        }

        AddressEntity example = new AddressEntity();
        example.setParent(val.getParent());

        List<AddressEntity> entityList = addressDao.getByExample(example);
        if (CollectionUtils.isNotEmpty(entityList))
            for (AddressEntity a : entityList) {
                if ((a.getAddressId() != null && !a.getAddressId().equals(val.getAddressId()))
                    && a.getMetadataType().getId().equals(val.getMetadataType().getId())) {
                    throw new NullPointerException("Address with provided type exists");
                }
            }
        UserEntity parent = userDao.findById(val.getParent().getId());
        val.setParent(parent);

        MetadataTypeEntity type = metadataTypeDAO.findById(val.getMetadataType().getId());
        val.setMetadataType(type);

        updateDefaultFlagForAddress(val, val.getIsDefault(), parent);

        addressDao.save(val);
    }

    @Override
    @Transactional
    public void addAddressSet(Collection<AddressEntity> adrSet) {
        if (adrSet == null || adrSet.size() == 0)
            return;

        HashSet<String> types = new HashSet<String>();
        for (AddressEntity address : adrSet) {
            if (address.getMetadataType() == null || StringUtils.isBlank(address.getMetadataType().getId())) {
                throw new NullPointerException("MetadataType for the address is not defined.");
            }
            if (types.contains(address.getMetadataType().getId()))
                throw new NullPointerException("Duplicate MetadataType for the address");
            else
                types.add(address.getMetadataType().getId());
        }

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

        final AddressEntity entity = addressDao.findById(val.getAddressId());
        final UserEntity parent = userDao.findById(val.getParent().getId());
        final MetadataTypeEntity metadataType = (val.getMetadataType() != null && StringUtils.isNotBlank(val.getMetadataType().getId())) ? metadataTypeDAO
                        .findById(val.getMetadataType().getId()) : null;

        if (entity != null && metadataType != null) {
            entity.setIsActive(val.getIsActive());
            entity.setBldgNumber(val.getBldgNumber());
            entity.setAddress1(val.getAddress1());
            entity.setAddress2(val.getAddress2());
            entity.setCity(val.getCity());
            entity.setPostalCd(val.getPostalCd());
            entity.setState(val.getState());
            entity.setName(val.getName());
            entity.setParent(parent);
            entity.setMetadataType(metadataType);

            if (entity.getIsDefault() != val.getIsDefault()) {
                updateDefaultFlagForAddress(entity, val.getIsDefault(), parent);
            }
            addressDao.update(entity);
        }
    }

    @Override
    @Transactional
    public void removeAddress(final String addressId) {
        final AddressEntity entity = addressDao.findById(addressId, "parent");

        if(entity != null) {
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
    public List<Address> getAddressDtoList(String userId, boolean isDeep) {
        return addressDozerConverter.convertToDTOList(getAddressList(userId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressList(String userId, Integer size, Integer from) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        AddressSearchBean searchBean = new AddressSearchBean();
        searchBean.setParentId(userId);
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        return getAddressList(searchBean, size, from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressList(AddressSearchBean searchBean, Integer size, Integer from) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return addressDao.getByExample(addressSearchBeanConverter.convert(searchBean), from, size);
    }

    @Override
    @Transactional
    public void addPhone(PhoneEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (val.getParent() == null)
            throw new NullPointerException("parentId for the phone is not defined.");

        if (val.getMetadataType() == null || StringUtils.isBlank(val.getMetadataType().getId())) {
            throw new NullPointerException("MetadataType for the phone is not defined.");
        }

        PhoneEntity example = new PhoneEntity();
        example.setParent(val.getParent());

        List<PhoneEntity> entityList = phoneDao.getByExample(example);
        if (CollectionUtils.isNotEmpty(entityList)) {
            for (PhoneEntity ph : entityList) {
                if ((ph.getId() != null && !ph.getId().equals(val.getId()))
                    && ph.getMetadataType().getId().equals(val.getMetadataType().getId())) {
                    throw new NullPointerException("Phone with provided type exists");
                }
            }
        }

        MetadataTypeEntity type = metadataTypeDAO.findById(val.getMetadataType().getId());
        val.setMetadataType(type);

        UserEntity parent = userDao.findById(val.getParent().getId());
        val.setParent(parent);

        updateDefaultFlagForPhone(val, val.getIsDefault(), parent);

        phoneDao.save(val);
    }

    @Override
    @Transactional
    public void addPhoneSet(Collection<PhoneEntity> phoneSet) {
        if (phoneSet == null || phoneSet.size() == 0)
            return;
        HashSet<String> types = new HashSet<String>();
        for (PhoneEntity phone : phoneSet) {
            if (phone.getMetadataType() == null || StringUtils.isBlank(phone.getMetadataType().getId())) {
                throw new NullPointerException("MetadataType for the phone is not defined.");
            }
            if (types.contains(phone.getMetadataType().getId()))
                throw new NullPointerException("Duplicate MetadataType for the phone");
            else
                types.add(phone.getMetadataType().getId());
        }

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
        if (val.getId() == null)
            throw new NullPointerException("PhoneId is null");
        if (val.getParent() == null)
            throw new NullPointerException("parentId for the address is not defined.");

        final PhoneEntity entity = phoneDao.findById(val.getId());
        final UserEntity parent = userDao.findById(val.getParent().getId());
        final MetadataTypeEntity metadataType = (val.getMetadataType() != null && StringUtils.isNotBlank(val.getMetadataType().getId())) ? metadataTypeDAO
                        .findById(val.getMetadataType().getId()) : null;

        if (entity != null && metadataType != null) {
            entity.setAreaCd(val.getAreaCd());
            entity.setName(val.getName());
            entity.setIsActive(val.getIsActive());
            entity.setParent(parent);
            entity.setPhoneExt(val.getPhoneExt());
            entity.setPhoneNbr(val.getPhoneNbr());
            entity.setMetadataType(metadataType);

            if (entity.getIsDefault() != val.getIsDefault()) {
                updateDefaultFlagForPhone(entity, val.getIsDefault(), parent);
            }
            phoneDao.update(entity);
        }
    }

    @Override
    @Transactional
    public void removePhone(final String phoneId) {
        final PhoneEntity entity = phoneDao.findById(phoneId, "parent");

        if(entity != null) {
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
    public List<Phone> getPhoneDtoList(String userId, boolean isDeep) {
        return phoneDozerConverter.convertToDTOList(getPhoneList(userId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneEntity> getPhoneList(String userId, Integer size, Integer from) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        PhoneSearchBean searchBean = new PhoneSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return getPhoneList(searchBean, size, from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneEntity> getPhoneList(PhoneSearchBean searchBean, Integer size, Integer from) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");
        return phoneDao.getByExample(phoneSearchBeanConverter.convert(searchBean), from, size);
    }

    @Override
    @Transactional
    public void addEmailAddress(EmailAddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getParent() == null)
            throw new NullPointerException("parentId for the address is not defined.");

        if (val.getMetadataType() == null || StringUtils.isBlank(val.getMetadataType().getId())) {
            throw new NullPointerException("MetadataType for the email address is not defined.");
        }

        EmailAddressEntity example = new EmailAddressEntity();
        example.setParent(val.getParent());

        List<EmailAddressEntity> entityList = emailAddressDao.getByExample(example);
        if (CollectionUtils.isNotEmpty(entityList))
            for (EmailAddressEntity ea : entityList) {
                if ((ea.getEmailId() != null && !ea.getEmailId().equals(val.getEmailId()))
                    && ea.getMetadataType().getId().equals(val.getMetadataType().getId())) {
                    throw new NullPointerException("Email Address with provided type exists");
                }
            }

        MetadataTypeEntity type = metadataTypeDAO.findById(val.getMetadataType().getId());
        val.setMetadataType(type);

        UserEntity userEntity = userDao.findById(val.getParent().getId());
        val.setParent(userEntity);

        updateDefaultFlagForEmail(val, val.getIsDefault(), userEntity);

        emailAddressDao.save(val);
    }

    @Override
    @Transactional
    public void addEmailAddressSet(Collection<EmailAddressEntity> adrSet) {
        if (adrSet == null || adrSet.size() == 0)
            return;

        HashSet<String> types = new HashSet<String>();
        for (EmailAddressEntity email : adrSet) {
            if (email.getMetadataType() == null || StringUtils.isBlank(email.getMetadataType().getId())) {
                throw new NullPointerException("MetadataType for the email is not defined.");
            }
            if (types.contains(email.getMetadataType().getId()))
                throw new NullPointerException("Duplicate MetadataType for the email");
            else
                types.add(email.getMetadataType().getId());
        }

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
        UserEntity parent = userDao.findById(val.getParent().getId());
        final MetadataTypeEntity metadataType = (val.getMetadataType() != null && StringUtils.isNotBlank(val.getMetadataType().getId())) ? metadataTypeDAO
                        .findById(val.getMetadataType().getId()) : null;

        if (entity != null && metadataType != null) {
            entity.setEmailAddress(val.getEmailAddress());
            entity.setName(val.getName());
            entity.setDescription(val.getDescription());
            entity.setParent(parent);
            entity.setIsActive(val.getIsActive());
            entity.setMetadataType(metadataType);

            if (entity.getIsDefault() != val.getIsDefault()) {
                updateDefaultFlagForEmail(entity, val.getIsDefault(), parent);
            }
            emailAddressDao.update(entity);
        }
    }

    @Override
    @Transactional
    public void removeEmailAddress(final String emailAddressId) {
        if (emailAddressId == null)
            throw new NullPointerException("val is null");

        final EmailAddressEntity entity = emailAddressDao.findById(emailAddressId, "parent");

        
        if(entity != null) {
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
    public List<EmailAddress> getEmailAddressDtoList(String userId, boolean isDeep) {
        return emailAddressDozerConverter.convertToDTOList(getEmailAddressList(userId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAddressEntity> getEmailAddressList(String userId, Integer size, Integer from) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        EmailSearchBean searchBean = new EmailSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return getEmailAddressList(searchBean, size, from);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAddressEntity> getEmailAddressList(EmailSearchBean searchBean, Integer size, Integer from) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return emailAddressDao.getByExample(emailAddressSearchBeanConverter.convert(searchBean), from, size);
    }

    @Override
    @Transactional
    public void addSupervisor(SupervisorEntity supervisor) {
        if (supervisorDao.findById(supervisor.getId()) != null)
            return;
        if (supervisor.getId() != null && getPrimarySupervisor(supervisor.getId().getEmployeeId()) == null) {
            supervisor.setIsPrimarySuper(true);
        }
        supervisorDao.save(supervisor);
    }

    @Override
    @Transactional
    public void addSuperior(String supervisorId, String subordinateId) {
        SupervisorEntity supervisorEntity = new SupervisorEntity();
        SupervisorIDEntity id = new SupervisorIDEntity();
        id.setSupervisorId(supervisorId);
        id.setEmployeeId(subordinateId);
        supervisorEntity.setId(id);

        addSupervisor(supervisorEntity);
    }

    @Override
    @Transactional
    public void removeSupervisor(final String supervisorId, final String employeeId) {
        if (supervisorId == null)
            throw new NullPointerException("supervisor is null");

        SupervisorIDEntity id = new SupervisorIDEntity();
        id.setSupervisorId(supervisorId);
        id.setEmployeeId(employeeId);

//        final SupervisorEntity entity = supervisorDao.findById(id);
        supervisorDao.deleteById(id);
    }

    // @Override
    // @Transactional(readOnly = true)
    // public SupervisorEntity getSupervisor(String supervisorObjId) {
    // if (supervisorObjId == null)
    // throw new NullPointerException("supervisorObjId is null");
    // return supervisorDao.findById(supervisorObjId);
    // }

    @Override
    @Transactional
    public void evict(Object object) {
        if (object instanceof EmailAddressEntity) {
            emailAddressDao.evict((EmailAddressEntity) object);
        } else if (object instanceof PhoneEntity) {
            phoneDao.evict((PhoneEntity) object);
        } else if (object instanceof AddressEntity) {
            addressDao.evict((AddressEntity) object);
        } else if (object instanceof UserAttributeEntity) {
            userAttributeDao.evict((UserAttributeEntity) object);
        } else {
            throw new IllegalArgumentException("Unsupported type");
        }
    }

    // @Override
    // @Transactional(readOnly = true)
    // public List<UserEntity> getSupervisors(String employeeId) {
    // if (employeeId == null)
    // throw new NullPointerException("employeeId is null");
    // return userDao.findSupervisors(employeeId);
    // }

    // @Override
    // @Transactional(readOnly = true)
    // public List<SupervisorEntity> getEmployees(String supervisorId) {
    // if (supervisorId == null)
    // throw new NullPointerException("employeeId is null");
    // return supervisorDao.findEmployees(supervisorId);
    // }

    @Override
    @Transactional(readOnly = true)
    public UserEntity getPrimarySupervisor(String employeeId) {
        if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        return userDao.findPrimarySupervisor(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public SupervisorEntity findSupervisor(String superiorId, String subordinateId) {
        if (superiorId == null)
            throw new NullPointerException("superiorId is null");
        if (superiorId == null)
            throw new NullPointerException("subordinateId is null");
        SupervisorIDEntity id = new SupervisorIDEntity();
        id.setSupervisorId(superiorId);
        id.setEmployeeId(subordinateId);

        return supervisorDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getSuperiors(String userId, Integer from, Integer size) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        return userDao.getSuperiors(userId, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getSuperiorsCount(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        return userDao.getSuperiorsCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getAllSuperiors(Integer from, Integer size) {
        return userDao.getAllSuperiors(from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getAllSuperiorsCount() {
        return userDao.getAllSuperiorsCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getSubordinates(String userId, Integer from, Integer size) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        return userDao.getSubordinates(userId, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int getSubordinatesCount(String userId) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        return userDao.getSubordinatesCount(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> findPotentialSupSubs(PotentialSupSubSearchBean searchBean, Integer from, Integer size) throws BasicDataServiceException {
        List<UserEntity> entityList = findAllPotentialSupSubs(searchBean);

        if (entityList != null && entityList.size() >= from) {
            int to = from + size;
            if (to > entityList.size()) {
                to = entityList.size();
            }
            entityList = entityList.subList(from, to);
        }

        return entityList;
    }

    @Override
    @Transactional(readOnly = true)
    public int findPotentialSupSubsCount(PotentialSupSubSearchBean searchBean) throws BasicDataServiceException {
        return findAllPotentialSupSubs(searchBean).size();
    }

    @Transactional(readOnly = true)
    private List<UserEntity> findAllPotentialSupSubs(PotentialSupSubSearchBean searchBean) throws BasicDataServiceException {
        List<String> userIds = null;
        if (StringUtils.isNotBlank(searchBean.getKey())) {
            userIds = new ArrayList<String>(1);
            userIds.add(searchBean.getKey());
        } else {
            userIds = getUserIds(searchBean);
        }
        userIds.removeAll(userDao.getAllAttachedSupSubIds(searchBean.getTargetUserIds()));
        return userDao.findByIds(userIds);
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<UserEntity> getUsersForResource(String resourceId, String requesterId, int from, int size) {
//        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
//        return userDao.getUsersForResource(resourceId, delegationFilter, from, size);
        UserSearchBean userSearchBean = new UserSearchBean();
        userSearchBean.setRequesterId(requesterId);
        userSearchBean.addResourceId(resourceId);

        List<SortParam> sortParamList = new ArrayList<>();
        sortParamList.add( new SortParam(OrderConstants.ASC, "name"));
        userSearchBean.setSortBy(sortParamList);


        return getUsersForResource(userSearchBean, from,size);
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUsersForResource(UserSearchBean userSearchBean, int from, int size){
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(userSearchBean.getRequesterId());

        String resourceId = userSearchBean.getResourceIdSet().iterator().next();

        return userDao.getUsersForResource(resourceId, delegationFilter, userSearchBean.getSortBy(), from, size);
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
    @Deprecated
    public int getNumOfUsersForGroup(String groupId, String requesterId) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(groupId, delegationFilter.getGroupIdSet())) {
            return userDao.getNumOfUsersForGroup(groupId, delegationFilter);
        }
        return 0;
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public List<UserEntity> getUsersForRole(String roleId, String requesterId, int from, int size) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(roleId, delegationFilter.getRoleIdSet())) {
            return userDao.getUsersForRole(roleId, delegationFilter, from, size);
        }
        return new ArrayList<UserEntity>(0);
    }

    @Override
    @Transactional(readOnly = true)
    @Deprecated
    public int getNumOfUsersForRole(String roleId, String requesterId) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(roleId, delegationFilter.getRoleIdSet())) {
            return userDao.getNumOfUsersForRole(roleId, delegationFilter);
        }
        return 0;
    }

    @Override
    @Transactional
    public String saveUserInfo(UserEntity newUserEntity, String supervisorId) throws Exception {
        String userId = newUserEntity.getId();
        if (newUserEntity.getId() != null) {
            // update, need to merge user objects
            UserEntity origUser = this.getUser(newUserEntity.getId(), null);
            this.mergeUserFields(origUser, newUserEntity);
            setMetadataTypes(origUser);
            userDao.update(origUser);
        } else {
            userId = createNewUser(newUserEntity);
        }
        if (supervisorId != null) {
            // update supervisor
            List<UserEntity> supervisorList = this.getSuperiors(newUserEntity.getId(), 0, Integer.MAX_VALUE);
            for (UserEntity s : supervisorList) {
                log.debug("looking to match supervisor ids = " + s.getId() + " " + supervisorId);
                if (s.getId().equalsIgnoreCase(supervisorId)) {
                    break;
                }
                // this.removeSupervisor(s.getOrgStructureId());
            }
            log.debug("adding supervisor: " + supervisorId);
            this.addSuperior(supervisorId, newUserEntity.getId());
        }
        return userId;
    }

    private void setMetadataTypes(final UserEntity userEntity) {
    	if(userEntity.getEmployeeType() != null && StringUtils.isNotBlank(userEntity.getEmployeeType().getId())) {
    		userEntity.setEmployeeType(metadataTypeDAO.findById(userEntity.getEmployeeType().getId()));
        } else {
        	userEntity.setEmployeeType(null);
        }
        if(userEntity.getJobCode() != null && StringUtils.isNotBlank(userEntity.getJobCode().getId())) {
        	userEntity.setJobCode(metadataTypeDAO.findById(userEntity.getJobCode().getId()));
        } else {
        	userEntity.setJobCode(null);
        }
        if(userEntity.getType() != null && StringUtils.isNotBlank(userEntity.getType().getId())) {
        	userEntity.setType(metadataTypeDAO.findById(userEntity.getType().getId()));
        } else {
        	userEntity.setType(null);
        }
    }
    
    private String createNewUser(UserEntity newUserEntity) throws Exception {
        List<LoginEntity> principalList = newUserEntity.getPrincipalList();
        Set<EmailAddressEntity> emailAddressList = newUserEntity.getEmailAddresses();
        Set<AddressEntity> addressList = newUserEntity.getAddresses();
        Set<PhoneEntity> phoneList = newUserEntity.getPhones();

        newUserEntity.setPrincipalList(null);
        newUserEntity.setPhones(null);
        newUserEntity.setAddresses(null);
        newUserEntity.setAffiliations(null);
        newUserEntity.setRoles(null);
        // newUserEntity.setEmailAddresses(null);

        this.addUser(newUserEntity);

        if (principalList != null && !principalList.isEmpty()) {
            for (LoginEntity lg : principalList) {
                lg.setManagedSysId(sysConfiguration.getDefaultManagedSysId());
                lg.setFirstTimeLogin(1);
                lg.setIsLocked(0);
                lg.setCreateDate(new Date(System.currentTimeMillis()));
                lg.setUserId(newUserEntity.getId());
                lg.setStatus(LoginStatusEnum.ACTIVE);
                // encrypt the password
                if (lg.getPassword() != null) {
                    String pswd = lg.getPassword();
                    lg.setPassword(loginManager.encryptPassword(newUserEntity.getId(), pswd));
                }
                loginDao.save(lg);
            }
        }
        if (CollectionUtils.isNotEmpty(emailAddressList)) {
            for (final EmailAddressEntity email : emailAddressList) {
                email.setParent(newUserEntity);
            }
            this.addEmailAddressSet(emailAddressList);
        }
        if (CollectionUtils.isNotEmpty(addressList)) {
            for (final AddressEntity address : addressList) {
                address.setParent(newUserEntity);
            }
            this.addAddressSet(addressList);
        }
        if (CollectionUtils.isNotEmpty(phoneList)) {
            for (final PhoneEntity phone : phoneList) {
                phone.setParent(newUserEntity);
            }
            this.addPhoneSet(phoneList);
        }

        /*
         * if(CollectionUtils.isNotEmpty(userRoles)){ for (final UserRoleEntity
         * userRole : userRoles) {
         * userRole.setUserId(newUserEntity.getUserId());
         * roleDataService.assocUserToRole(userRole); } }
         */
        return newUserEntity.getId();
    }

    @Transactional
    public void deleteUser(String userId) {
        List<LoginEntity> loginList = loginDao.findUser(userId);
        if (loginList == null || loginList.isEmpty()) {
            throw new NullPointerException("Principal Not Found");
        }
        for (LoginEntity login : loginList) {
            // change the status on the identity
            login.setStatus(LoginStatusEnum.INACTIVE);
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
    public void setSecondaryStatus(String userId, UserStatusEnum secondaryStatus) {
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

    @Transactional
    public void resetUser(String userId) {
        UserEntity user = this.getUser(userId, null);
        if (user == null) {
            log.error("UserId " + userId + " not found");
            throw new NullPointerException("UserId " + userId + " not found");
        }
        user.setDateITPolicyApproved(null);
        user.setClaimDate(null);
        user.setStatus(UserStatusEnum.PENDING_INITIAL_LOGIN);
        user.setSecondaryStatus(null);
        userDao.update(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRoleInUser(String userId, String roleId) {
        boolean isExists = false;
        UserEntity userEntity = userDao.findById(userId);
        for (RoleEntity r : userEntity.getRoles()) {
            if (r.getId().equals(roleId)) {
                return true;
            }
        }
        return isExists;
    }

    @Transactional(readOnly = true)
    public int getNumOfEmailsForUser(String userId) {
        EmailSearchBean searchBean = new EmailSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return emailAddressDao.count(emailAddressSearchBeanConverter.convert(searchBean));
    }

    @Transactional(readOnly = true)
    public int getNumOfAddressesForUser(String userId) {
        AddressSearchBean searchBean = new AddressSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return addressDao.count(addressSearchBeanConverter.convert(searchBean));
    }

    @Transactional(readOnly = true)
    public int getNumOfPhonesForUser(String userId) {
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
        if (newUserEntity.getCostCenter() != null) {
            if (newUserEntity.getCostCenter().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setCostCenter(null);
            } else {
                origUserEntity.setCostCenter(newUserEntity.getCostCenter());
            }
        }

        if (newUserEntity.getLocationCd() != null) {
            if (newUserEntity.getLocationCd().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setLocationCd(null);
            } else {
                origUserEntity.setLocationCd(newUserEntity.getLocationCd());
            }
        }

        if (newUserEntity.getLocationName() != null) {
            if (newUserEntity.getLocationName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setLocationName(null);
            } else {
                origUserEntity.setLocationName(newUserEntity.getLocationName());
            }
        }

        if (newUserEntity.getMailCode() != null) {
            if (newUserEntity.getMailCode().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setMailCode(null);
            } else {
                origUserEntity.setMailCode(newUserEntity.getMailCode());
            }
        }

        if (newUserEntity.getPrefix() != null) {
            if (newUserEntity.getPrefix().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setPrefix(null);
            } else {
                origUserEntity.setPrefix(newUserEntity.getPrefix());
            }
        }

        if (newUserEntity.getEmployeeId() != null) {
            if (newUserEntity.getEmployeeId().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setEmployeeId(null);
            } else {
                origUserEntity.setEmployeeId(newUserEntity.getEmployeeId());
            }
        }
        if (newUserEntity.getEmployeeType() != null && StringUtils.isNotBlank(newUserEntity.getEmployeeType().getId())) {
           origUserEntity.setEmployeeType(newUserEntity.getEmployeeType());
        } else {
            origUserEntity.setEmployeeType(null);
        }

        if (newUserEntity.getFirstName() != null) {
            if (newUserEntity.getFirstName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setFirstName(null);
            } else {
                origUserEntity.setFirstName(newUserEntity.getFirstName());
            }
        }
        if (newUserEntity.getJobCode() != null && StringUtils.isNotBlank(newUserEntity.getJobCode().getId())) {
            origUserEntity.setJobCode(newUserEntity.getJobCode());
        } else {
            origUserEntity.setJobCode(null);
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
        if (newUserEntity.getClaimDate() != null) {
            if (newUserEntity.getClaimDate().equals(BaseConstants.NULL_DATE)) {
                origUserEntity.setClaimDate(null);
            } else {
                origUserEntity.setClaimDate(newUserEntity.getClaimDate());
            }
        }
        if (newUserEntity.getMaidenName() != null) {
            if (newUserEntity.getMaidenName().equalsIgnoreCase(BaseConstants.NULL_STRING)) {
                origUserEntity.setMaidenName(null);
            } else {
                origUserEntity.setMaidenName(newUserEntity.getMaidenName());
            }
        }
        if (newUserEntity.getType() != null && StringUtils.isNotBlank(newUserEntity.getType().getId())) {
                origUserEntity.setType(newUserEntity.getType());
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
        setMetadataTypes(origUserEntity);
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
            if (defaultPhone.getId().equals(targetEntity.getId())) {
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

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserIdsInRole(String roleId, String requesterId) {
        LinkedList<String> userIds = new LinkedList<String>();
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(roleId, delegationFilter.getRoleIdSet())) {
            List<UserEntity> users = userDao.getUsersForRole(roleId, delegationFilter, 0, Integer.MAX_VALUE);
            for (UserEntity userEntity : users) {
                userIds.add(userEntity.getId());
            }
        }
        return userIds;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserIdsInGroup(String groupId, String requesterId) {
        LinkedList<String> userIds = new LinkedList<String>();
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(groupId, delegationFilter.getRoleIdSet())) {
            List<UserEntity> users = userDao.getUsersForGroup(groupId, delegationFilter, 0, Integer.MAX_VALUE);
            for (UserEntity userEntity : users) {
                userIds.add(userEntity.getId());
            }
        }
        return userIds;
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
        }
        return filter;
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

    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<UserAttributeEntity> getUserAttributeList(String userId, final LanguageEntity language) {
    	return userAttributeDao.findUserAttributes(userId);
    }
    
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getByExample(UserSearchBean searchBean, int start, int size) {
        return userDao.getByExample(searchBean, start, size);
    }

    @Override
    @Transactional
    public void addUserToGroup(String userId, String groupId) {
        final GroupEntity groupEntity = groupDAO.findById(groupId);
        final UserEntity userEntity = userDao.findById(userId);
        userEntity.addGroup(groupEntity);
    }

    @Override
    @Transactional
    public void removeUserFromGroup(String userId, String groupId) {
    	final GroupEntity groupEntity = groupDAO.findById(groupId);
    	final UserEntity userEntity = userDao.findById(userId);
    	userEntity.removeGroup(groupEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHasGroup(String userId, String groupId) {
        // DelegationFilterSearchBean delegationFilter =
        // this.getDelegationFilterForUserSearch(null);
        return userDao.isUserInGroup(userId, groupId);
        // return
        // CollectionUtils.isNotEmpty(userDao.getUsersForGroup(groupId,delegationFilter,
        // 0, Integer.MAX_VALUE));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHasResource(String userId, String resourceId) {
        // DelegationFilterSearchBean delegationFilter =
        // this.getDelegationFilterForUserSearch(null);
        return userDao.isUserEntitledToResoruce(userId, resourceId);
        // return
        // CollectionUtils.isNotEmpty(userDao.getUsersForResource(resourceId,
        // delegationFilter, 0, Integer.MAX_VALUE));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isHasOrganization(String userId, String organizationId) {
        // DelegationFilterSearchBean delegationFilter =
        // this.getDelegationFilterForUserSearch(null);
        // return
        // CollectionUtils.isNotEmpty(userDao.getUsersForOrganization(organizationId,
        // delegationFilter, 0, Integer.MAX_VALUE));
        return userDao.isUserInOrg(userId, organizationId);
    }
    
    @Override
    @Transactional
    public void removeUserFromResource(String userId, String resourceId) {
    	 final ResourceEntity resourceEntity = resourceDAO.findById(resourceId);
    	 final UserEntity userEntity = userDao.findById(userId);
    	 userEntity.removeResource(resourceEntity);
    }

    @Override
    @Transactional
    public void addUserToResource(String userId, String resourceId) {
    	final ResourceEntity resourceEntity = resourceDAO.findById(resourceId);
    	final UserEntity userEntity = userDao.findById(userId);
    	userEntity.addResource(resourceEntity);
    }


    @Override
    @Transactional(readOnly = true)
    public boolean validateSearchBean(UserSearchBean searchBean) throws BasicDataServiceException {
        if (StringUtils.isNotBlank(searchBean.getRequesterId())) {
            Map<String, UserAttribute> requesterAttributes = this.getUserAttributesDto(searchBean.getRequesterId());
            return  validateSearchBean(searchBean, requesterAttributes);
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateSearchBean(UserSearchBean searchBean, Map<String, UserAttribute> requesterAttributes) throws BasicDataServiceException {
        if (requesterAttributes!=null && CollectionUtils.isNotEmpty(requesterAttributes.keySet())) {

            boolean isOrgFilterSet = DelegationFilterHelper.isOrgFilterSet(requesterAttributes);
            boolean isGroupFilterSet = DelegationFilterHelper.isGroupFilterSet(requesterAttributes);
            boolean isRoleFilterSet = DelegationFilterHelper.isRoleFilterSet(requesterAttributes);
            Set<String> filterData = null;

            if (isOrgFilterSet) {
                if (CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
                   filterData = new HashSet<String>(DelegationFilterHelper.getOrgIdFilterFromString(requesterAttributes));
                   for(String pk : searchBean.getOrganizationIdSet()) {
                       if(!DelegationFilterHelper.isAllowed(pk, filterData)){
                           throw new BasicDataServiceException(ResponseCode.NOT_ALLOWED_ORGANIZATION_IN_SEARCH);
                       }
                   }
                }
            }

            if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet()) && isGroupFilterSet) {
                filterData = new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(requesterAttributes));
                for(String pk : searchBean.getGroupIdSet()) {
                    if(!DelegationFilterHelper.isAllowed(pk, filterData)){
                        throw new BasicDataServiceException(ResponseCode.NOT_ALLOWED_GROUP_IN_SEARCH);
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(searchBean.getRoleIdSet()) && isRoleFilterSet) {
                filterData = new HashSet<String>(DelegationFilterHelper.getRoleFilterFromString(requesterAttributes));
                for(String pk : searchBean.getRoleIdSet()) {
                    if(!DelegationFilterHelper.isAllowed(pk, filterData)){
                        throw new BasicDataServiceException(ResponseCode.NOT_ALLOWED_ROLE_IN_SEARCH);
                    }
                }
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUserByLastDate(Date lastDate) {
        return userDao.getUserByLastDate(lastDate);
    }

}
