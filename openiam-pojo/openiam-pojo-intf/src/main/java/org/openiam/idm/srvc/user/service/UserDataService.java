package org.openiam.idm.srvc.user.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

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
    UserEntity getUserByPrincipal(String principal, String managedSysId, boolean dependants);

    void addUser(UserEntity user) throws Exception;

    void addRequiredAttributes(UserEntity user);

    void updateUser(UserEntity user);

    void updateUserWithDependent(UserEntity user, boolean dependency);

    void updateUserFromDto(User user);

    /**
     * Deletes a user from the system. The developer is responsible for deleting
     * associated objects such as the User-Group and User-Role relationship.
     * This has been done on purpose to minimize impact on the system through an
     * erroneous call to the delete operation.
     * 
     * @param id
     */
    void removeUser(String id) throws Exception;

    List<UserEntity> findUserByOrganization(String orgId) throws BasicDataServiceException;

    List<UserEntity> searchByDelegationProperties(DelegationFilterSearch search);

    List<UserEntity> findBeans(UserSearchBean searchBean) throws BasicDataServiceException;

    List<UserEntity> findBeans(UserSearchBean searchBean, int from, int size) throws BasicDataServiceException;

    int count(UserSearchBean searchBean) throws BasicDataServiceException;

    void addAttribute(UserAttributeEntity attribute);

    void updateAttribute(UserAttributeEntity attribute);

    UserAttributeEntity getAttribute(String attrId);

    void removeAttribute(String userAttributeId);

    void removeAllAttributes(String userId);

    void addNote(UserNoteEntity note);

    void updateNote(UserNoteEntity note);

    List<UserNoteEntity> getAllNotes(String userId);

    UserNoteEntity getNote(java.lang.String noteId);

    void removeNote(String userNodeId);

    void removeAllNotes(String userId);

    void addAddress(AddressEntity val);

    void addAddressSet(Collection<AddressEntity> adrList);

    void updateAddress(AddressEntity val);

    void removeAddress(String addressId);

    void removeAllAddresses(String userId);

    AddressEntity getAddressById(String addressId);

    List<AddressEntity> getAddressList(String userId);

    List<Address> getAddressDtoList(String userId, boolean isDeep);

    List<AddressEntity> getAddressList(String userId, Integer size, Integer from);

    List<AddressEntity> getAddressList(AddressSearchBean searchBean, Integer size, Integer from);

    void addTOPTTokenToPhone(String phoneId, String secret);
    
    void addPhone(PhoneEntity val);

    void addPhoneSet(Collection<PhoneEntity> phoneList);

    void updatePhone(PhoneEntity val);

    void removePhone(String phoneId);

    void removeAllPhones(String userId);

    PhoneEntity getPhoneById(String addressId);

    List<PhoneEntity> getPhoneList(String userId);

    List<Phone> getPhoneDtoList(String userId, boolean isDeep);

    List<PhoneEntity> getPhoneList(String userId, Integer size, Integer from);

    List<PhoneEntity> getPhoneList(PhoneSearchBean searchBean, Integer size, Integer from);

    void addEmailAddress(EmailAddressEntity val);

    void addEmailAddressSet(Collection<EmailAddressEntity> adrList);

    void updateEmailAddress(EmailAddressEntity val);

    void removeEmailAddress(String emailAddressId);

    void removeAllEmailAddresses(String userId);

    EmailAddressEntity getEmailAddressById(String addressId);

    List<EmailAddressEntity> getEmailAddressList(String userId);

    List<EmailAddress> getEmailAddressDtoList(String userId, boolean isDeep);

    List<EmailAddressEntity> getEmailAddressList(String userId, Integer size, Integer from);

    List<EmailAddressEntity> getEmailAddressList(EmailSearchBean searchBean, Integer size, Integer from);

    void addSupervisor(SupervisorEntity supervisor);

    void addSuperior(String supervisorId, String subordinateId);

    // public void updateSupervisor(SupervisorEntity supervisor);

    void removeSupervisor(String supervisorId, final String employeeId);

    // public SupervisorEntity getSupervisor(String supervisorObjId);

    void evict(Object object);

    /**
     * Returns a List of supervisor objects that represents the supervisors for
     * this employee or user.
     * 
     * @param superiorId
     * @return
     */
    // public List<UserEntity> getSupervisors(String employeeId);

    SupervisorEntity findSupervisor(String superiorId, String subordinateId);

    List<UserEntity> getSuperiors(String userId, Integer from, Integer size);

    int getSuperiorsCount(String userId);

    List<UserEntity> getAllSuperiors(Integer from, Integer size);

    int getAllSuperiorsCount();

    List<UserEntity> getSubordinates(String userId, Integer from, Integer size);

    int getSubordinatesCount(String userId);

    List<UserEntity> findPotentialSupSubs(PotentialSupSubSearchBean searchBean, Integer from, Integer size) throws BasicDataServiceException;

    int findPotentialSupSubsCount(PotentialSupSubSearchBean searchBean)  throws BasicDataServiceException;

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
    UserEntity getPrimarySupervisor(String employeeId);

    UserEntity getUser(String id);

    User getUserDto(String id);

    UserEntity getUser(String id, String requestorId);

    Map<String, UserAttributeEntity> getAllAttributes(String userId);

    @Deprecated
    List<UserEntity> getUsersForResource(final String resourceId, String requesterId, final int from, final int size);
    List<UserEntity> getUsersForResource(UserSearchBean userSearchBean, int from, int size);

    int getNumOfUsersForResource(final String resourceId, String requesterId);

    List<UserEntity> getUsersForGroup(final String groupId, String requesterId, final int from, final int size);

    @Deprecated
    int getNumOfUsersForGroup(final String groupId, String requesterId);

    @Deprecated
    List<UserEntity> getUsersForRole(final String roleId, String requesterId, final int from, final int size);

    @Deprecated
    int getNumOfUsersForRole(final String roleId, String requesterId);

    String saveUserInfo(UserEntity userEntity, String supervisorId) throws Exception;

    void deleteUser(String userId);

    void setSecondaryStatus(String userId, UserStatusEnum secondaryStatus);

    void activateUser(String userId);

    void resetUser(String userId);

    int getNumOfEmailsForUser(String userId);

    int getNumOfAddressesForUser(String userId);

    int getNumOfPhonesForUser(String userId);

    void mergeUserFields(UserEntity origUserEntity, UserEntity newUserEntity);

    Map<String, UserAttribute> getUserAttributesDto(String userId);
    
    List<UserAttributeEntity> getUserAttributeList(String userId, final LanguageEntity language);

    List<UserAttribute> getUserAttributesDtoList(String userId);

    Map<String, UserAttributeEntity> getUserAttributes(String userId);

    List<UserEntity> getByExample(UserSearchBean searchBean, int start, int size);

    boolean isRoleInUser(String userId, String roleId);

    List<String> getUserIdsInRole(String roleId, String requestrId);

    List<String> getUserIdsInGroup(String groupId, String requestrId);

    void addUserToGroup(String userId, String groupId);

    void removeUserFromGroup(String userId, String groupId);

    boolean isHasGroup(String userId, String groupId);

    void removeUserFromResource(String userId, String resourceId);
    
    void addUserToResource(String userId, String resourceId);

    boolean isHasResource(String userId, String resourceId);

    boolean isHasOrganization(String userId, String organizationId);

    boolean validateSearchBean(UserSearchBean seachBean) throws BasicDataServiceException;

    boolean validateSearchBean(UserSearchBean searchBean, Map<String, UserAttribute> requesterAttributes) throws BasicDataServiceException;

    List<UserEntity> getUserByLastDate(Date lastDate);

}