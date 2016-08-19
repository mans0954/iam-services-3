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
import org.openiam.authmanager.service.AuthorizationManagerService;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseConstants;
import org.openiam.base.OrderConstants;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.SearchMode;
import org.openiam.base.ws.SearchParam;
import org.openiam.base.ws.SortParam;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.core.dao.UserKeyDao;
import org.openiam.core.domain.UserKey;
import org.openiam.dozer.converter.*;
import org.openiam.elasticsearch.dao.EmailElasticSearchRepository;
import org.openiam.elasticsearch.dao.LoginElasticSearchRepository;
import org.openiam.elasticsearch.dao.PhoneElasticSearchRepository;
import org.openiam.elasticsearch.dao.UserElasticSearchRepository;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.access.service.AccessRightDAO;
import org.openiam.idm.srvc.audit.domain.AuditLogTargetEntity;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.continfo.service.AddressDAO;
import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
import org.openiam.idm.srvc.continfo.service.PhoneDAO;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDAO;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.service.MetadataElementDAO;
import org.openiam.idm.srvc.meta.service.MetadataService;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.service.ApproverAssociationDAO;
import org.openiam.idm.srvc.org.service.OrganizationService;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.UserIdentityAnswerDAO;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDAO;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.SupervisorIDEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.*;
import org.openiam.idm.srvc.user.util.DelegationFilterHelper;
import org.openiam.internationalization.LocalizedServiceGet;
import org.openiam.util.AttributeUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service interface that clients will access to gain information about users
 * and related information.
 * 
 * @author Suneet Shah
 * @version 2
 */

@Service("userManager")
public class UserMgr implements UserDataService, ApplicationContextAware {
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
    private UserElasticSearchRepository userRepo;

    @Autowired
    private LoginElasticSearchRepository loginRepo;
    
    @Autowired
    @Qualifier("groupDAO")
    private GroupDAO groupDAO;
    
    @Autowired
    private EmailElasticSearchRepository emailElasticSearchRepo;

    @Autowired
    private ResourceDAO resourceDAO;
    
    @Autowired
    private PhoneElasticSearchRepository phoneRepository;

    @Autowired
    private UserKeyDao userKeyDao;

    @Autowired
    private KeyManagementService keyManagementService;
    @Autowired
    private LoginDataService loginManager;
/*
    @Autowired
    private EmailAddressSearchBeanConverter emailAddressSearchBeanConverter;
    @Autowired
    private AddressSearchBeanConverter addressSearchBeanConverter;
    @Autowired
    private PhoneSearchBeanConverter phoneSearchBeanConverter;
*/

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
    @Autowired
    private ApproverAssociationDAO approverAssociationDAO;
    
    @Autowired
    private AccessRightDAO accessRightDAO;

    @Value("${org.openiam.user.search.max.results}")
    private int MAX_USER_SEARCH_RESULTS;

    @Autowired
    private SupervisorDozerConverter supervisorDozerConverter;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    protected AuditLogService auditLogService;
/*
    @Value("${org.openiam.usersearch.lucene.enabled}")
    private Boolean isLuceneEnabled;*/


    @Autowired
    @Qualifier("authorizationManagerService")
    private AuthorizationManagerService authorizationManagerService;

    private ApplicationContext ac;

    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

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
    public User getUserDto(String id, String requestorId, Boolean isDeep) {
        //UserEntity userEntity = userDao.findByIdDelFlt(id, getDelegationFilterForUserSearch(requestorId));
        UserEntity userEntity = this.getProxyService().getUser(id, requestorId);
        return userDozerConverter.convertToDTO(userEntity, isDeep);
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
    @Transactional(readOnly = true)
    public User getUserDtoByPrincipal(String principal, String managedSysId, boolean dependants) {
        /*LoginEntity login = loginDao.getRecord(principal, managedSysId);
        if (login == null) {
            return null;
        }
        UserEntity userEntity = getUser(login.getUserId(), null);*/
        UserEntity userEntity = this.getProxyService().getUserByPrincipal(principal, managedSysId, dependants);
        return userDozerConverter.convertToDTO(userEntity, dependants);
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
        
        final List<LoginEntity> principalList = user.getPrincipalList();
        if (principalList != null && !principalList.isEmpty()) {
            for (final LoginEntity lg : principalList) {
                if(StringUtils.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId(), lg.getManagedSysId())) {
                	if(StringUtils.isNotBlank(lg.getPassword())) {
                		createInitialPasswordHistoryRecord(lg);
                	}
                }
            }
        }
        
        userDao.save(user);
        if(user.getUserKeys() == null) {
        	user.setUserKeys(new HashSet<UserKey>());
        }
        user.getUserKeys().clear();
        user.getUserKeys().addAll(keyManagementService.generateUserKeys(user));
        //userDao.save(user);
        
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

        for (EmailAddressEntity emailAdr : emailSet) {
            if (emailAdr.getParent() == null) {
                emailAdr.setParent(userDao.findById(user.getId()));
            }
        }

    }

    @Override
    @Transactional
    public void updateUser(UserEntity user) {
        if (user == null) {
            throw new NullPointerException("user object is null");
        }
        if (user.getId() == null) {
            throw new NullPointerException("user id is null");
        }
        user.setLastUpdate(new Date(System.currentTimeMillis()));
        
        /* 
         * IDMAPPS-2700
		 * TOPT_SECRET disappears from phone when updating from UI
         */
        if(CollectionUtils.isNotEmpty(user.getPhones())) {
        	user.getPhones().stream().filter(t -> t.getId() != null).forEach(phone -> {
        		final PhoneEntity dbPhone = phoneDao.findById(phone.getId());
        		phone.setTotpSecret(dbPhone.getTotpSecret());
        	});
        }
        
        final List<LoginEntity> principalList = user.getPrincipalList();
        if (principalList != null && !principalList.isEmpty()) {
            for (final LoginEntity lg : principalList) {
                if(StringUtils.equalsIgnoreCase(sysConfiguration.getDefaultManagedSysId(), lg.getManagedSysId())) {
                	if(StringUtils.isNotBlank(lg.getPassword())) {
                		if(CollectionUtils.isEmpty(lg.getPasswordHistory())) {
                			createInitialPasswordHistoryRecord(lg);
                		}
                	}
                }
            }
        }
        
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
                    EmailAddressEntity entity = emailAddressDao.findById(e.getId());
                    if (entity != null) {
                        userEntity.getEmailAddresses().remove(entity);
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    EmailAddressEntity entity = emailAddressDao.findById(e.getId());
                    if (entity != null) {
                        emailAddressDao.evict(entity);
                    }
                    entity = emailAddressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getEmailAddresses().add(entity);
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    EmailAddressEntity entity = emailAddressDao.findById(e.getId());
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
                    AddressEntity entity = addressDao.findById(e.getId());
                    if (entity != null) {
                        userEntity.getAddresses().remove(entity);
                    }
                } else if (e.getOperation().equals(AttributeOperationEnum.ADD)) {
                    AddressEntity entity = addressDozerConverter.convertToEntity(e, false);
                    entity.setParent(userEntity);
                    userEntity.getAddresses().add(entity);
                } else if (e.getOperation().equals(AttributeOperationEnum.REPLACE)) {
                    AddressEntity entity = addressDao.findById(e.getId());
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
                existingEntity.setMetadataElementId(incomingEntity.getMetadataElementId());
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
        authStateDAO.deleteByUser(id);
        userIdentityAnswerDAO.deleteByUser(id);
        final List<ApproverAssociationEntity> associations = approverAssociationDAO.getByApprover(id, AssociationType.USER);
        if(CollectionUtils.isNotEmpty(associations)) {
        	for(final ApproverAssociationEntity association : associations) {
        		approverAssociationDAO.delete(association);
        	}
        }
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
    public List<User> findUserDtoByOrganization(String orgId) throws BasicDataServiceException {
        UserSearchBean searchBean = new UserSearchBean();
        searchBean.addOrganizationId(orgId);
        List<UserEntity> userEntityList = findBeans(searchBean);
        return userDozerConverter.convertToDTOList(userEntityList, false);
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

            Set<String> orgDelFilter = organizationService.getDelegationFilter(requesterAttributes);

            isOrgFilterSet = CollectionUtils.isNotEmpty(orgDelFilter);//DelegationFilterHelper.isOrgFilterSet(requesterAttributes);
            isGroupFilterSet = DelegationFilterHelper.isGroupFilterSet(requesterAttributes);
            isRoleFilterSet = DelegationFilterHelper.isRoleFilterSet(requesterAttributes);
            isMngReportFilterSet = DelegationFilterHelper.isMngRptFilterSet(requesterAttributes);

//            if (isOrgFilterSet) {
            if (CollectionUtils.isEmpty(searchBean.getOrganizationIdSet())) {
                searchBean.addOrganizationIdList(orgDelFilter);
            }
//            }

            if (CollectionUtils.isEmpty(searchBean.getGroupIdSet()) && isGroupFilterSet) {
                searchBean.setGroupIdSet(new HashSet<String>(DelegationFilterHelper.getGroupFilterFromString(requesterAttributes)));
            }

            if (CollectionUtils.isEmpty(searchBean.getRoleIdSet()) && isRoleFilterSet) {
                searchBean.setRoleIdSet(new HashSet<String>(DelegationFilterHelper.getRoleFilterFromString(requesterAttributes)));
            }

            if(isMngReportFilterSet){
                List<String> subordinariesList = userDao.getSubordinatesIds(searchBean.getRequesterId());
                subordinariesList.add(searchBean.getRequesterId());
                nonEmptyListOfLists.add(subordinariesList);
            }
        }
        List<String> idList = null;
        if(isSearchByPrimaryAttributes(searchBean)) {
            idList = userRepo.findIds(searchBean, new PageRequest(0, Integer.MAX_VALUE));
        }

        if (idList!=null) {
            nonEmptyListOfLists.add( (CollectionUtils.isNotEmpty(idList))? idList: Collections.EMPTY_LIST);
        }

        if (CollectionUtils.isNotEmpty(searchBean.getAttributeList())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForAttributes(searchBean.getAttributeList(), -1, -1));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getRoleIdSet())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForRoles(searchBean.getRoleIdSet(), -1, -1));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForOrganizations(searchBean.getOrganizationIdSet(), -1, -1));
        }

        if (CollectionUtils.isNotEmpty(searchBean.getGroupIdSet())) {
            nonEmptyListOfLists.add(userDao.getUserIdsForGroups(searchBean.getGroupIdSet(), -1, -1));
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
            nonEmptyListOfLists.add(loginRepo.findUserIds(searchBean.getPrincipal(), new PageRequest(0, Integer.MAX_VALUE)).getContent());
        }

        if (searchBean.getEmailAddressMatchToken() != null && searchBean.getEmailAddressMatchToken().isValid()) {
            final EmailSearchBean emailSearchBean = new EmailSearchBean();
            emailSearchBean.setEmailMatchToken(searchBean.getEmailAddressMatchToken());
            final List<String> userIds = emailElasticSearchRepo.findUserIds(emailSearchBean, new PageRequest(0, Integer.MAX_VALUE)).getContent();
            												   //(emailSearchBean.getEmailMatchToken().getValue()).stream().map(e -> e.getId()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(userIds)) {
            	nonEmptyListOfLists.add(userIds);
            }
        }

        if (StringUtils.isNotBlank(searchBean.getPhoneAreaCd()) || StringUtils.isNotBlank(searchBean.getPhoneNbr())) {
            final PhoneSearchBean phoneSearchBean = new PhoneSearchBean();
            phoneSearchBean.setPhoneAreaCd(StringUtils.trimToNull(searchBean.getPhoneAreaCd()));
            phoneSearchBean.setPhoneNbr(StringUtils.trimToNull(searchBean.getPhoneNbr()));
            nonEmptyListOfLists.add(phoneRepository.findUserIds(phoneSearchBean, new PageRequest(0, Integer.MAX_VALUE)).getContent());
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
	
	            if (finalizedIdList==null /*CollectionUtils.isEmpty(finalizedIdList)*/) {
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
            if(finalizedIdList == null) {
                finalizedIdList = new LinkedList<>();
            }
            finalizedIdList.addAll(resultSet);
        }

        return (finalizedIdList != null) ? finalizedIdList : Collections.EMPTY_LIST;
    }

    private boolean isSearchByPrimaryAttributes(UserSearchBean searchBean) {
        boolean result = false;
        if(searchBean!=null){

            result = result || checkSearchParam(searchBean.getFirstNameMatchToken())
                    || checkSearchParam(searchBean.getNickNameMatchToken())
                    || checkSearchParam(searchBean.getLastNameMatchToken())
                    || checkSearchParam(searchBean.getMaidenNameMatchToken())
                    || checkSearchParam(searchBean.getEmployeeIdMatchToken())
                    || checkSearchParam(searchBean.getUserStatus())
                    || checkSearchParam(searchBean.getAccountStatus())
                    || checkSearchParam(searchBean.getJobCode())
                    || checkSearchParam(searchBean.getEmployeeType())
                    || checkSearchParam(searchBean.getUserType());
        }
        return result;
    }

    private boolean checkSearchParam(SearchParam param){
        return param != null && param.isValid();
    }
    private boolean checkSearchParam(String param){
        return StringUtils.isNotBlank(param);
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
            entityList = userDao.findByIds(getUserIds(searchBean), searchBean, from, size);
        }

        if(CollectionUtils.isNotEmpty(entityList)
                && searchBean.getInitDefaulLoginFlag()){
            setDefaultLogin(entityList);
        }

        return entityList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findBeansDto(UserSearchBean searchBean, int from, int size) throws BasicDataServiceException {
        List<UserEntity> entityList = null;
        if (StringUtils.isNotBlank(searchBean.getKey())) {
            final UserEntity entity = userDao.findById(searchBean.getKey());
            if (entity != null) {
                entityList = new ArrayList<UserEntity>(1);
                entityList.add(entity);
            }
        } else {
            entityList = userDao.findByIds(getUserIds(searchBean), searchBean, from, size);
        }

        if (CollectionUtils.isNotEmpty(entityList)
                && searchBean.getInitDefaulLoginFlag()) {
            setDefaultLogin(entityList);
        }

        return userDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
    }

    private void setDefaultLogin(List<UserEntity> entityList) {
        List<String> userIds = new ArrayList<>();
        userIds.add(null);
        for(UserEntity usr: entityList){
            userIds.set(0, usr.getId());
            List<LoginEntity> entities = loginDao.findByUserIds(userIds, sysConfiguration.getDefaultManagedSysId());
            if (CollectionUtils.isNotEmpty(entities)) {
                usr.setDefaultLogin(entities.get(0).getLogin());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int count(UserSearchBean searchBean) throws BasicDataServiceException {
        return userDao.countByIds(getUserIds(searchBean));
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

        attribute.setMetadataElementId(attribute.getMetadataElementId());

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
            attribute.setMetadataElementId(userAttribute.getMetadataElementId());
            userAttributeDao.merge(attribute);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersDtoForResource(String resourceId, String requesterId, int from, int size) {
//        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
//        return userDao.getUsersForResource(resourceId, delegationFilter, from, size);
        UserSearchBean userSearchBean = new UserSearchBean();
        userSearchBean.setRequesterId(requesterId);
        userSearchBean.addResourceId(resourceId);

        List<SortParam> sortParamList = new ArrayList<>();
        sortParamList.add(new SortParam(OrderConstants.ASC, "name"));
        userSearchBean.setSortBy(sortParamList);


        List<UserEntity> userEntityList = getUsersForResource(userSearchBean, from, size);
        return userDozerConverter.convertToDTOList(userEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getUsersForResource(UserSearchBean userSearchBean, int from, int size) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(userSearchBean.getRequesterId());

        String resourceId = userSearchBean.getResourceIdSet().iterator().next();

        return userDao.getUsersForResource(resourceId, delegationFilter, userSearchBean.getSortBy(), from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersDtoForGroup(String groupId, String requesterId, int from, int size) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(groupId, delegationFilter.getGroupIdSet())) {
            List<UserEntity> userEntityList = userDao.getUsersForGroup(groupId, delegationFilter, from, size);
            return userDozerConverter.convertToDTOList(userEntityList, false);
        }
        return new ArrayList<User>(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersDtoForRole(String roleId, String requesterId, int from, int size) {
        DelegationFilterSearchBean delegationFilter = this.getDelegationFilterForUserSearch(requesterId);
        if (DelegationFilterHelper.isAllowed(roleId, delegationFilter.getRoleIdSet())) {
            List<UserEntity> userEntityList = userDao.getUsersForRole(roleId, delegationFilter, from, size);
            return userDozerConverter.convertToDTOList(userEntityList, false);
        }
        return new ArrayList<User>(0);
    }

    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<UserAttribute> getUserAttributeDtoList(String userId, final LanguageEntity language) {
        //List<UserAttributeEntity> userAttributeEntityList =  userAttributeDao.findUserAttributes(userId);
        List<UserAttributeEntity> userAttributeEntityList = this.getProxyService().getUserAttributeList(userId, language);
        return userAttributeDozerConverter.convertToDTOList(userAttributeEntityList, true);
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

        if (val.getType() == null || StringUtils.isBlank(val.getType().getId())) {
            throw new NullPointerException("MetadataType for the address is not defined.");
        }

        final AddressSearchBean sb = new AddressSearchBean();
        sb.setParentId(val.getParent().getId());
        List<AddressEntity> entityList = addressDao.getByExample(sb);
        if (CollectionUtils.isNotEmpty(entityList))
            for (AddressEntity a : entityList) {
                if ((a.getId() != null && !a.getId().equals(val.getId()))
                    && a.getType().getId().equals(val.getType().getId())) {
                    throw new NullPointerException("Address with provided type exists");
                }
            }
        UserEntity parent = userDao.findById(val.getParent().getId());
        val.setParent(parent);

        MetadataTypeEntity type = metadataTypeDAO.findById(val.getType().getId());
        val.setType(type);

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
            if (address.getType() == null || StringUtils.isBlank(address.getType().getId())) {
                throw new NullPointerException("MetadataType for the address is not defined.");
            }
            if (types.contains(address.getType().getId()))
                throw new NullPointerException("Duplicate MetadataType for the address");
            else
                types.add(address.getType().getId());
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
        if (val.getId() == null)
            throw new NullPointerException("AddressId is null");
        if (val.getParent() == null)
            throw new NullPointerException("userId for the address is not defined.");

        final AddressEntity entity = addressDao.findById(val.getId());
        final UserEntity parent = userDao.findById(val.getParent().getId());
        final MetadataTypeEntity metadataType = (val.getType() != null && StringUtils.isNotBlank(val.getType().getId())) ? metadataTypeDAO
                        .findById(val.getType().getId()) : null;

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
            entity.setType(metadataType);

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
	            final AddressSearchBean sb = new AddressSearchBean();
	            sb.setParentId(entity.getParent().getId());
	            List<AddressEntity> addresses = addressDao.getByExample(sb);
	
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
    public Address getAddressDtoById(String addressId) {
        /*if (addressId == null)
            throw new NullPointerException("addressId is null");

        AddressEntity addressEntity = addressDao.findById(addressId);*/
        AddressEntity addressEntity = this.getProxyService().getAddressById(addressId);

        return addressDozerConverter.convertToDTO(addressEntity, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressList(String userId) {
        return this.getAddressList(userId, 0, Integer.MAX_VALUE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Address> getAddressDtoList(String userId, boolean isDeep) {
        return addressDozerConverter.convertToDTOList(getAddressList(userId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressList(String userId, int from, int size) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        AddressSearchBean searchBean = new AddressSearchBean();
        searchBean.setParentId(userId);
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        return getAddressList(searchBean, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Address> getAddressDtoList(String userId, int from, int size) {
        /*if (userId == null)
            throw new NullPointerException("userId is null");

        AddressSearchBean searchBean = new AddressSearchBean();
        searchBean.setParentId(userId);*/
        /* searchBean.setParentType(ContactConstants.PARENT_TYPE_USER); */
        //List<AddressEntity> addressEntityList = getAddressList(searchBean, size, from);
        List<AddressEntity> addressEntityList = this.getProxyService().getAddressList(userId, from, size);
        return addressDozerConverter.convertToDTOList(addressEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressEntity> getAddressList(AddressSearchBean searchBean, int from, int size) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return addressDao.getByExample(searchBean, from ,size);
    }
    
	@Override
	@Transactional
	public void addTOPTTokenToPhone(String phoneId, String secret) {
		final PhoneEntity phone = phoneDao.findById(phoneId);
		if(StringUtils.isNotBlank(secret)) {
        	try {
				final String encrytedPassword = keyManagementService.encrypt(phone.getParent().getId(), KeyName.token, secret);
				phone.setTotpSecret(encrytedPassword);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			phone.setTotpSecret(null);
		}
		phoneDao.update(phone);
	}

    @Override
    @Transactional
    public void addPhone(PhoneEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");

        if (val.getParent() == null)
            throw new NullPointerException("parentId for the phone is not defined.");

        if (val.getType() == null || StringUtils.isBlank(val.getType().getId())) {
            throw new NullPointerException("MetadataType for the phone is not defined.");
        }
        
        if(StringUtils.isNotBlank(val.getTotpSecret())) {
        	try {
				final String encryptedString = keyManagementService.encrypt(val.getParent().getId(), KeyName.token, val.getTotpSecret());
				val.setTotpSecret(encryptedString);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
        }

        PhoneSearchBean sb = new PhoneSearchBean();
        sb.setParentId(val.getParent().getId());

        List<PhoneEntity> entityList = phoneDao.getByExample(sb);
        if (CollectionUtils.isNotEmpty(entityList)) {
            for (PhoneEntity ph : entityList) {
                if ((ph.getId() != null && !ph.getId().equals(val.getId()))
                    && ph.getType().getId().equals(val.getType().getId())) {
                    throw new NullPointerException("Phone with provided type exists");
                }
            }
        }

        MetadataTypeEntity type = metadataTypeDAO.findById(val.getType().getId());
        val.setType(type);

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
            if (phone.getType() == null || StringUtils.isBlank(phone.getType().getId())) {
                throw new NullPointerException("MetadataType for the phone is not defined.");
            }
            if (types.contains(phone.getType().getId()))
                throw new NullPointerException("Duplicate MetadataType for the phone");
            else
                types.add(phone.getType().getId());
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
        final MetadataTypeEntity metadataType = (val.getType() != null && StringUtils.isNotBlank(val.getType().getId())) ? metadataTypeDAO
                .findById(val.getType().getId()) : null;

        if (entity != null && metadataType != null) {
            entity.setCountryCd(val.getCountryCd());
            entity.setAreaCd(val.getAreaCd());
            entity.setName(val.getName());
            entity.setIsActive(val.getIsActive());
            entity.setParent(parent);
            entity.setPhoneExt(val.getPhoneExt());
            entity.setPhoneNbr(val.getPhoneNbr());
            entity.setType(metadataType);

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
	            PhoneSearchBean sb = new PhoneSearchBean();
	            sb.setParentId(entity.getParent().getId());
	            List<PhoneEntity> phones = phoneDao.getByExample(sb);
	
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
    public Phone getPhoneDtoById(String addressId) {
        /*if (addressId == null)
            throw new NullPointerException("addressId is null");
        PhoneEntity phoneEntity = phoneDao.findById(addressId);*/
        PhoneEntity phoneEntity = this.getProxyService().getPhoneById(addressId);
        return phoneDozerConverter.convertToDTO(phoneEntity, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneEntity> getPhoneList(String userId) {
        return this.getPhoneList(userId, 0, Integer.MAX_VALUE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phone> getPhoneDtoList(String userId, boolean isDeep) {
        return phoneDozerConverter.convertToDTOList(getPhoneList(userId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneEntity> getPhoneList(String userId, int from, int size) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        PhoneSearchBean searchBean = new PhoneSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return getPhoneList(searchBean, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phone> getPhoneDtoList(String userId, int from, int size) {
        /*if (userId == null)
            throw new NullPointerException("userId is null");

        PhoneSearchBean searchBean = new PhoneSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        List<PhoneEntity> phoneEntityList = getPhoneList(searchBean, size, from);*/
        List<PhoneEntity> phoneEntityList = this.getProxyService().getPhoneList(userId, from, size);
        return phoneDozerConverter.convertToDTOList(phoneEntityList, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PhoneEntity> getPhoneList(PhoneSearchBean searchBean, int from, int size) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");
        return phoneDao.getByExample(searchBean, from, size);
    }

    @Override
    @Transactional
    public void addEmailAddress(EmailAddressEntity val) {
        if (val == null)
            throw new NullPointerException("val is null");
        if (val.getParent() == null)
            throw new NullPointerException("parentId for the address is not defined.");

        if (val.getType() == null || StringUtils.isBlank(val.getType().getId())) {
            throw new NullPointerException("MetadataType for the email address is not defined.");
        }

        final EmailSearchBean sb = new EmailSearchBean();
        sb.setParentId(val.getParent().getId());

        List<EmailAddressEntity> entityList = emailAddressDao.getByExample(sb);
        if (CollectionUtils.isNotEmpty(entityList))
            for (EmailAddressEntity ea : entityList) {
                if ((ea.getId() != null && !ea.getId().equals(val.getId()))
                    && ea.getType().getId().equals(val.getType().getId())) {
                    throw new NullPointerException("Email Address with provided type exists");
                }
            }

        MetadataTypeEntity type = metadataTypeDAO.findById(val.getType().getId());
        val.setType(type);

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
            if (email.getType() == null || StringUtils.isBlank(email.getType().getId())) {
                throw new NullPointerException("MetadataType for the email is not defined.");
            }
            if (types.contains(email.getType().getId()))
                throw new NullPointerException("Duplicate MetadataType for the email");
            else
                types.add(email.getType().getId());
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
        if (val.getId() == null)
            throw new NullPointerException("EmailAddressId is null");
        if (val.getParent() == null)
            throw new NullPointerException("parentId for the address is not defined.");

        EmailAddressEntity entity = emailAddressDao.findById(val.getId());
        UserEntity parent = userDao.findById(val.getParent().getId());
        final MetadataTypeEntity metadataType = (val.getType() != null && StringUtils.isNotBlank(val.getType().getId())) ? metadataTypeDAO
                        .findById(val.getType().getId()) : null;

        if (entity != null && metadataType != null) {
            entity.setEmailAddress(val.getEmailAddress());
            entity.setName(val.getName());
            entity.setDescription(val.getDescription());
            entity.setParent(parent);
            entity.setIsActive(val.getIsActive());
            entity.setType(metadataType);

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
	            final EmailSearchBean sb = new EmailSearchBean();
	            sb.setParentId(entity.getParent().getId());
	            List<EmailAddressEntity> emailList = emailAddressDao.getByExample(sb);
	
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
    public EmailAddress getEmailAddressDtoById(String addressId) {
        /*if (addressId == null)
            throw new NullPointerException("addressId is null");
        EmailAddressEntity emailAddressEntity = emailAddressDao.findById(addressId);*/
        EmailAddressEntity emailAddressEntity = this.getProxyService().getEmailAddressById(addressId);
        return emailAddressDozerConverter.convertToDTO(emailAddressEntity, false);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAddressEntity> getEmailAddressList(String userId) {
        return this.getEmailAddressList(userId, 0, Integer.MAX_VALUE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAddress> getEmailAddressDtoList(String userId, boolean isDeep) {
        return emailAddressDozerConverter.convertToDTOList(getEmailAddressList(userId), isDeep);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmailAddressEntity> getEmailAddressList(String userId, int from, int size) {
        if (userId == null)
            throw new NullPointerException("userId is null");

        EmailSearchBean searchBean = new EmailSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return getEmailAddressList(searchBean, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<EmailAddressEntity> getEmailAddressList(EmailSearchBean searchBean, int from, int size) {
        if (searchBean == null)
            throw new NullPointerException("searchBean is null");

        return emailAddressDao.getByExample(searchBean, from, size);
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
    public User getPrimarySupervisorDto(String employeeId) {
        /*if (employeeId == null)
            throw new NullPointerException("employeeId is null");
        UserEntity userEntity = userDao.findPrimarySupervisor(employeeId);*/
        UserEntity userEntity = this.getProxyService().getPrimarySupervisor(employeeId);
        return userDozerConverter.convertToDTO(userEntity, false);
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
    public Supervisor findSupervisorDto(String superiorId, String subordinateId) {
        if (superiorId == null)
            throw new NullPointerException("superiorId is null");
        if (superiorId == null)
            throw new NullPointerException("subordinateId is null");
        SupervisorIDEntity id = new SupervisorIDEntity();
        id.setSupervisorId(superiorId);
        id.setEmployeeId(subordinateId);

        SupervisorEntity supervisorEntity = supervisorDao.findById(id);
        return supervisorDozerConverter.convertToDTO(supervisorEntity, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getSuperiors(String userId, int from, int size) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        return userDao.getSuperiors(userId, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getSuperiorsDto(String userId, int from, int size) {
        /*if (userId == null)
            throw new NullPointerException("userId is null");
        List<UserEntity> userEntity = userDao.getSuperiors(userId, from, size);*/
        List<UserEntity> userEntity = this.getProxyService().getSuperiors(userId, from, size);
        return userDozerConverter.convertToDTOList(userEntity, false);
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
    public List<UserEntity> getAllSuperiors(int from, int size) {
        return userDao.getAllSuperiors(from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllSuperiorsDto(int from, int size) {
        //List<UserEntity> userEntityList = userDao.getAllSuperiors(from, size);
        List<UserEntity> userEntityList = this.getProxyService().getAllSuperiors(from, size);
        return userDozerConverter.convertToDTOList(userEntityList, true);
    }

    @Override
    @Transactional(readOnly = true)
    public int getAllSuperiorsCount() {
        return userDao.getAllSuperiorsCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserEntity> getSubordinates(String userId, int from, int size) {
        if (userId == null)
            throw new NullPointerException("userId is null");
        return userDao.getSubordinates(userId, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getSubordinatesDto(String userId, int from, int size) {
        /*if (userId == null)
            throw new NullPointerException("userId is null");
        List<UserEntity> userEntity = userDao.getSubordinates(userId, from, size);*/
        List<UserEntity> userEntity = this.getProxyService().getSubordinates(userId, from, size);
        return userDozerConverter.convertToDTOList(userEntity, false);
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
    public List<UserEntity> findPotentialSupSubs(PotentialSupSubSearchBean searchBean, int from, int size) throws BasicDataServiceException {
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
    public List<User> findPotentialSupSubsDto(PotentialSupSubSearchBean searchBean, int from, int size) throws BasicDataServiceException {
        List<UserEntity> entityList = findAllPotentialSupSubs(searchBean);

        if (entityList != null && entityList.size() >= from) {
            int to = from + size;
            if (to > entityList.size()) {
                to = entityList.size();
            }
            entityList = entityList.subList(from, to);
        }


        return userDozerConverter.convertToDTOList(entityList, true);
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
        if(CollectionUtils.isNotEmpty(userIds)) {
        	/* the userIds list at this point is immutable, so any modifications will throw an exception */
        	userIds = new ArrayList<String>(userIds);
        	userIds.removeAll(userDao.getAllAttachedSupSubIds(searchBean.getTargetUserIds()));
        }
        return userDao.findByIds(userIds);
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
            	if(log.isDebugEnabled()) {
                log.debug("looking to match supervisor ids = " + s.getId() + " " + supervisorId);
            	}                
				if (s.getId().equalsIgnoreCase(supervisorId)) {
                    break;
                }
                // this.removeSupervisor(s.getOrgStructureId());
            }
            if(log.isDebugEnabled()) {
            log.debug("adding supervisor: " + supervisorId);
            }
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
    
    //AM-414
    /* need to set up an initial record for password history */
    private void createInitialPasswordHistoryRecord(final LoginEntity login) {
    	final PasswordHistoryEntity history = new PasswordHistoryEntity();
    	history.setLogin(login);
    	history.setPassword(login.getPassword());
    	login.addHistoryRecord(history);
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
                if(StringUtils.isBlank(lg.getManagedSysId()))
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
		userIdentityAnswerDAO.deleteByUser(userId);
    }

    @Transactional(readOnly = true)
    public int getNumOfEmailsForUser(String userId) {
        EmailSearchBean searchBean = new EmailSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return emailAddressDao.count(searchBean);
    }

    @Transactional(readOnly = true)
    public int getNumOfAddressesForUser(String userId) {
        AddressSearchBean searchBean = new AddressSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return addressDao.count(searchBean);
    }

    @Transactional(readOnly = true)
    public int getNumOfPhonesForUser(String userId) {
        PhoneSearchBean searchBean = new PhoneSearchBean();
        searchBean.setParentId(userId);
        // searchBean.setParentType(ContactConstants.PARENT_TYPE_USER);
        return phoneDao.count(searchBean);
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
        if (newUserEntity.getResetPasswordType() != null) {
        	origUserEntity.setResetPasswordType(newUserEntity.getResetPasswordType());
        }
        setMetadataTypes(origUserEntity);
    }

    @Transactional
    private void updateDefaultFlagForPhone(PhoneEntity targetEntity, boolean newDefaultValue, UserEntity parent) {
        // update default flag
        // 1. get all default phone for user and iterate them
        final PhoneSearchBean sb = new PhoneSearchBean();
        sb.setParentId(parent.getId());
        List<PhoneEntity> phones = phoneDao.getByExample(sb);

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
        final AddressSearchBean sb = new AddressSearchBean();
        sb.setParentId(parent.getId());
        List<AddressEntity> addresses = addressDao.getByExample(sb);

        AddressEntity defaultAddress = getAddressByDefaultFlag(addresses, true);

        if (defaultAddress == null) {
            targetEntity.setIsDefault(true);
        } else {
            if (defaultAddress.getId().equals(targetEntity.getId())) {
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
        final EmailSearchBean sb = new EmailSearchBean();
        sb.setParentId(parent.getId());
        List<EmailAddressEntity> emailList = emailAddressDao.getByExample(sb);

        EmailAddressEntity defaultEmail = getEmailAddressByDefaultFlag(emailList, true);

        if (defaultEmail == null) {
            targetEntity.setIsDefault(true);
        } else {
            if (defaultEmail.getId().equals(targetEntity.getId())) {
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

    @Transactional(readOnly = true)
    public List<UserEntity> getUsersForMSys(String mSysId) {
        return userDao.getUsersForMSys(mSysId);
    }

    @Transactional(readOnly = true)
    public Map<String, UserAttribute> getUserAttributesDto(String userId) {
        List<UserAttribute> userAttributes = getUserAttributesDtoList(userId);
        Map<String, UserAttribute> attributeMap = new HashMap<String, UserAttribute>();
        if(userAttributes != null) {
            for(UserAttribute attr : userAttributes) {
                attributeMap.put(attr.getName(), attr);
            }
        }
        return attributeMap;
    }

    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<UserAttributeEntity> getUserAttributeList(String userId, final LanguageEntity language) {
    	return userAttributeDao.findUserAttributes(userId);
    }

    @Transactional(readOnly = true)
    public List<UserAttribute> getUserAttributesDtoList(String userId) {
        List<UserAttributeEntity> attributeEntities = userAttributeDao.findUserAttributes(userId);
        return userAttributeDozerConverter.convertToDTOList(attributeEntities, false);
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
    public void addUserToGroup(final String userId, final String groupId, final Set<String> rightIds, final Date startDate, final Date endDate) {
        final GroupEntity group = groupDAO.findById(groupId);
        final UserEntity user = userDao.findById(userId);
        if(group != null && user != null) {
        	user.addGroup(group, accessRightDAO.findByIds(rightIds), startDate, endDate);
        }
    }

    @Override
    @Transactional
    public void removeUserFromGroup(String userId, String groupId) {
    	final GroupEntity group = groupDAO.findById(groupId);
    	final UserEntity user = userDao.findById(userId);
    	if(group != null && user != null) {
    		user.removeGroup(group);
    	}
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
    @CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("resources"),
            @CacheKeyEvict("resourceEntities")
        },
        parameterIndex=1
    )
    public void removeUserFromResource(String userId, final String resourceId) {
    	 final ResourceEntity resource = resourceDAO.findById(resourceId);
    	 final UserEntity user = userDao.findById(userId);
    	 if(resource != null && user != null) {
    		 resource.removeUser(user);
    		 resourceDAO.update(resource);
    		 //user.removeResource(resource);
    		 //userDao.update(user); 
    	 }
    }

    @Override
    @Transactional
    @CacheKeyEviction(
    	evictions={
            @CacheKeyEvict("resources"),
            @CacheKeyEvict("resourceEntities")
        },
        parameterIndex=1
    )
    public void addUserToResource(final String userId, final String resourceId, final Set<String> rightIds, final Date startDate, final Date endDate) {
    	final ResourceEntity resource = resourceDAO.findById(resourceId);
    	final UserEntity user = userDao.findById(userId);
    	if(resource != null && user != null) {
    		resource.addUser(user, accessRightDAO.findByIds(rightIds), startDate, endDate);
    		resourceDAO.update(resource);
    		//user.addResource(resource, accessRightDAO.findByIds(rightIds));
    		//userDao.update(user);
    	}
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

            Set<String> orgDelFilter = organizationService.getDelegationFilter(requesterAttributes);

            boolean isOrgFilterSet = CollectionUtils.isNotEmpty(orgDelFilter);// DelegationFilterHelper.isOrgFilterSet(requesterAttributes);
            boolean isGroupFilterSet = DelegationFilterHelper.isGroupFilterSet(requesterAttributes);
            boolean isRoleFilterSet = DelegationFilterHelper.isRoleFilterSet(requesterAttributes);
            Set<String> filterData = null;

            if (isOrgFilterSet) {
                if (CollectionUtils.isNotEmpty(searchBean.getOrganizationIdSet())) {
//                   filterData = new HashSet<String>(DelegationFilterHelper.getOrgIdFilterFromString(requesterAttributes));
                   for(String pk : searchBean.getOrganizationIdSet()) {
                       if(!DelegationFilterHelper.isAllowed(pk, orgDelFilter)){
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

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserDtoByLastDate(Date lastDate) {
        List<UserEntity> userEntityList = userDao.getUserByLastDate(lastDate);
        return userDozerConverter.convertToDTOList(userEntityList, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserDtoBetweenCreateDate(Date fromDate, Date toDate) {
        List<UserEntity> userEntityList = userDao.getUserBetweenCreateDate( fromDate, toDate );
        return userDozerConverter.convertToDTOList(userEntityList, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserDtoBetweenStartDate(Date fromDate, Date toDate) {
        List<UserEntity> userEntityList = userDao.getUserBetweenStartDate(fromDate, toDate);
        return userDozerConverter.convertToDTOList(userEntityList, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUserDtoBetweenLastDate(Date fromDate, Date toDate) {
        List<UserEntity> userEntityList = userDao.getUserBetweenLastDate( fromDate, toDate );

        return userDozerConverter.convertToDTOList(userEntityList, true);
    }

    @Override
	@Transactional(readOnly = true)
    public List<User> getUserDtoBySearchBean(AuditLogSearchBean searchBean) {
        List<IdmAuditLogEntity> auditLogs = auditLogService.findBeans(searchBean, -1, -1);
        Set<String> userIds = new HashSet<String>();
        for (IdmAuditLogEntity log : auditLogs) {
            String userId = null;
            Set<AuditLogTargetEntity> targets = log.getTargets();
            if(targets != null) {
                for (AuditLogTargetEntity target : targets) {
                    if (target.getTargetType().equalsIgnoreCase("user")) {
                        userId = target.getTargetId();
                        break;
                    }
                }
            }
            userIds.add(userId);
        }
        List<UserEntity> userEntityList = userDao.getUserByIds(userIds);

        return userDozerConverter.convertToDTOList(userEntityList, true);
    }

    private UserDataService getProxyService() {
        UserDataService service = (UserDataService)ac.getBean("userManager");
        return service;
    }

    @Override
    public List<Supervisor> findSupervisors(SupervisorSearchBean sb) {
        List<SupervisorEntity> supers = supervisorDao.getByExample(sb);
        return supervisorDozerConverter.convertToDTOList(supers, true);
    }


    @Override
    @Transactional(readOnly = true)
    public UserAttribute getAttributeDto(String attrId) {
        /*if (attrId == null) {
            throw new NullPointerException("attrId is null");
        }
        UserAttributeEntity attributeEntity = userAttributeDao.findById(attrId);*/
        UserAttributeEntity attributeEntity = this.getProxyService().getAttribute(attrId);
        return userAttributeDozerConverter.convertToDTO(attributeEntity, false);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<EmailAddress> getEmailAddressDtoList(EmailSearchBean searchBean, int from, int size) {
        /*if (searchBean == null)
            throw new NullPointerException("searchBean is null");
        List<EmailAddressEntity> emailAddressEntityList = emailAddressDao.getByExample(emailAddressSearchBeanConverter.convert(searchBean), from, size);*/
        List<EmailAddressEntity> emailAddressEntityList = this.getProxyService().getEmailAddressList(searchBean, from, size);
        return emailAddressDozerConverter.convertToDTOList(emailAddressEntityList, searchBean.isDeepCopy());
    }

}
