package org.openiam.idm.srvc.user.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.*;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.*;

/**
 * Service interface that clients will access to gain information about users
 * and related information.
 * 
 * @author Suneet Shah
 * @version 2
 */

public interface UserDataService {

    /**
     * Returns a User object that is associated with the principal. ManagedSysId
     * refers to a principal that is associated with a particular target system.
     * User 0 to use the default principal ID.
     * 
     * @param principal
     * @param managedSysId
     * @return
     */
    public UserEntity getUserByPrincipal(String principal, String managedSysId, boolean dependants);

    public User getUserDtoByPrincipal(String principal, String managedSysId, boolean dependants);

    public void addUser(UserEntity user) throws Exception;

    public void addRequiredAttributes(UserEntity user);

    public void updateUser(UserEntity user);

    public void updateUserWithDependent(UserEntity user, boolean dependency);

    //public void updateUserFromDto(User user);

    /**
     * Deletes a user from the system. The developer is responsible for deleting
     * associated objects such as the User-Group and User-Role relationship.
     * This has been done on purpose to minimize impact on the system through an
     * erroneous call to the delete operation.
     * 
     * @param id
     */
    public void removeUser(String id) throws Exception;

    public List<UserEntity> findUserByOrganization(String orgId) throws BasicDataServiceException;

    public List<User> findUserDtoByOrganization(String orgId) throws BasicDataServiceException;

    public List<UserEntity> searchByDelegationProperties(DelegationFilterSearch search);

    public List<UserEntity> findBeans(UserSearchBean searchBean) throws BasicDataServiceException;

    public List<UserEntity> findBeans(UserSearchBean searchBean, int from, int size) throws BasicDataServiceException;

    public List<User> findBeansDto(UserSearchBean searchBean, int from, int size) throws BasicDataServiceException;

    int count(UserSearchBean searchBean) throws BasicDataServiceException;

    public void addAttribute(UserAttributeEntity attribute);

    public void updateAttribute(UserAttributeEntity attribute);

    public UserAttributeEntity getAttribute(String attrId);

    public UserAttribute getAttributeDto(String attrId);

    public void removeAttribute(String userAttributeId);

    public void removeAllAttributes(String userId);

    public void addNote(UserNoteEntity note);

    public void updateNote(UserNoteEntity note);

    public List<UserNoteEntity> getAllNotes(String userId);

    public UserNoteEntity getNote(java.lang.String noteId);

    public void removeNote(String userNodeId);

    public void removeAllNotes(String userId);

    public void addAddress(AddressEntity val);

    public void addAddressSet(Collection<AddressEntity> adrList);

    public void updateAddress(AddressEntity val);

    public void removeAddress(String addressId);

    public void removeAllAddresses(String userId);

    public AddressEntity getAddressById(String addressId);

    public Address getAddressDtoById(String addressId);

    public List<AddressEntity> getAddressList(AddressSearchBean searchBean, int from, int size);
    
    public List<Address> getAddressDtoList(AddressSearchBean searchBean, int from, int size);

    public void addTOPTTokenToPhone(String phoneId, String secret);
    
    public void addPhone(PhoneEntity val);

    public void addPhoneSet(Collection<PhoneEntity> phoneList);

    public void updatePhone(PhoneEntity val);

    public void removePhone(String phoneId);

    public void removeAllPhones(String userId);

    public PhoneEntity getPhoneById(String addressId);

    public Phone getPhoneDtoById(String addressId);

    public List<PhoneEntity> getPhoneList(PhoneSearchBean searchBean, int from, int size);
    
    public List<Phone> getPhoneDTOList(PhoneSearchBean searchBean, int from, int size);

    public void addEmailAddress(EmailAddressEntity val);

    public void addEmailAddressSet(Collection<EmailAddressEntity> adrList);

    public void updateEmailAddress(EmailAddressEntity val);

    public void removeEmailAddress(String emailAddressId);

    public void removeAllEmailAddresses(String userId);

    public EmailAddressEntity getEmailAddressById(String addressId);

    public EmailAddress getEmailAddressDtoById(String addressId);

    public List<EmailAddressEntity> getEmailAddressList(String userId);

    public List<EmailAddress> getEmailAddressDtoList(String userId, boolean isDeep);

    public List<EmailAddressEntity> getEmailAddressList(String userId, int from, int size);


    public List<EmailAddressEntity> getEmailAddressList(EmailSearchBean searchBean, int from, int size);

    public List<EmailAddress> getEmailAddressDtoList(EmailSearchBean searchBean, int from, int size);

    public void addSupervisor(SupervisorEntity supervisor);

    public void addSuperior(String supervisorId, String subordinateId);

    // public void updateSupervisor(SupervisorEntity supervisor);

    public void removeSupervisor(String supervisorId, final String employeeId);

    // public SupervisorEntity getSupervisor(String supervisorObjId);

    public void evict(Object object);

    /**
     * Returns a List of supervisor objects that represents the supervisors for
     * this employee or user.
     * 
     * @param superiorId
     * @return
     */
    // public List<UserEntity> getSupervisors(String employeeId);

    public SupervisorEntity findSupervisor(String superiorId, String subordinateId);

    public Supervisor findSupervisorDto(String superiorId, String subordinateId);

    public List<UserEntity> getSuperiors(String userId, int from, int size);

    public List<User> getSuperiorsDto(String userId, int from, int size);

    public int getSuperiorsCount(String userId);

    public List<UserEntity> getAllSuperiors(int from, int size);

    public List<User> getAllSuperiorsDto(int from, int size);

    public int getAllSuperiorsCount();

    public List<UserEntity> getSubordinates(String userId, int from, int size);

    public List<User> getSubordinatesDto(String userId, int from, int size);

    public int getSubordinatesCount(String userId);

    public List<UserEntity> findPotentialSupSubs(PotentialSupSubSearchBean searchBean, int from, int size) throws BasicDataServiceException;

    public List<User> findPotentialSupSubsDto(PotentialSupSubSearchBean searchBean, int from, int size) throws BasicDataServiceException;

    public int findPotentialSupSubsCount(PotentialSupSubSearchBean searchBean)  throws BasicDataServiceException;

    /**
     * Returns a list of Supervisor objects that represents the employees or
     * users for this supervisor
     * 
     * @param supervisorId
     * @return
     */
    // public List<SupervisorEntity> getEmployees(String supervisorId);

    /**
     * Returns the primary supervisor for this employee. Null if no primary is
     * defined.
     * 
     * @param employeeId
     * @return
     */
    public UserEntity getPrimarySupervisor(String employeeId);

    public User getPrimarySupervisorDto(String employeeId);

    public UserEntity getUser(String id);

    public User getUserDto(String id);

    public User getUserDto(String id, Boolean isDeep);

    public Map<String, UserAttributeEntity> getAllAttributes(String userId);

    //public List<UserEntity> getUsersForResource(final String resourceId, String requesterId, final int from, final int size);

    public List<User> getUsersDtoForResource(String resourceId, int from, int size);

    public List<UserEntity> getUsersForResource(UserSearchBean userSearchBean, int from, int size);

    //public List<User> getUsersDtoForResource(UserSearchBean userSearchBean, int from, int size);

    //public int getNumOfUsersForResource(final String resourceId, String requesterId);

    //public List<UserEntity> getUsersForGroup(final String groupId, String requesterId, final int from, final int size);

    public List<User> getUsersDtoForGroup(String groupId, int from, int size);

    public int getNumOfUsersForGroup(final String groupId);

    //public List<UserEntity> getUsersForRole(final String roleId, String requesterId, final int from, final int size);

    public List<User> getUsersDtoForRole(String roleId, int from, int size);

    public int getNumOfUsersForRole(final String roleId);

    public String saveUserInfo(UserEntity userEntity, String supervisorId) throws Exception;

    public void deleteUser(String userId);

    public void setSecondaryStatus(String userId, UserStatusEnum secondaryStatus);

    public void activateUser(String userId);

    public void resetUser(String userId);

    public int getNumOfEmailsForUser(String userId);

    public int getNumOfAddressesForUser(String userId);

    public int getNumOfPhonesForUser(String userId);

    public void mergeUserFields(UserEntity origUserEntity, UserEntity newUserEntity);

    List<UserEntity> getUsersForMSys(String mSysId);

    public Map<String, UserAttribute> getUserAttributesDto(String userId);
    
    public List<UserAttributeEntity> getUserAttributeList(String userId, final LanguageEntity language);

    public List<UserAttribute> getUserAttributeDtoList(String userId, final LanguageEntity language);

    public List<UserAttribute> getUserAttributesDtoList(String userId);

    public Map<String, UserAttributeEntity> getUserAttributes(String userId);

    List<UserEntity> getByExample(UserSearchBean searchBean, int start, int size);

    //boolean isRoleInUser(String userId, String roleId);

    List<String> getUserIdsInRole(String roleId);

    List<String> getUserIdsInGroup(String groupId);

    void addUserToGroup(String userId, String groupId, Set<String> rightIds, final Date startDate, final Date endDate);

    void removeUserFromGroup(String userId, String groupId);


    boolean isHasGroup(String userId, String groupId);

    void removeUserFromResource(String userId, String resourceId);
    
    void addUserToResource(String userId, String resourceId, Set<String> rightIds, final Date startDate, final Date endDate);

    boolean isHasResource(String userId, String resourceId);

    boolean isHasOrganization(String userId, String organizationId);

    boolean validateSearchBean(UserSearchBean seachBean) throws BasicDataServiceException;

    public boolean validateSearchBean(UserSearchBean searchBean, Map<String, UserAttribute> requesterAttributes) throws BasicDataServiceException;

    List<UserEntity> getUserByLastDate(Date lastDate);

    public List<User> getUserDtoByLastDate(Date lastDate);

    public List<User> getUserDtoBetweenCreateDate(Date fromDate, Date toDate);

    public List<User> getUserDtoBetweenLastDate(Date fromDate, Date toDate);

    public List<User> getUserDtoBySearchBean(AuditLogSearchBean searchBean);
    public List<User> getUserDtoBetweenStartDate(Date fromDate, Date toDate);

    public List<Supervisor> findSupervisors(SupervisorSearchBean sb);
    
    public boolean isIndexed(final String id);
}