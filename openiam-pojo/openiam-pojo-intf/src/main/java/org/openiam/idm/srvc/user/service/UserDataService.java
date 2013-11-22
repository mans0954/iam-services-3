package org.openiam.idm.srvc.user.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
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
     * @param securityDomain
     * @param principal
     * @param managedSysId
     * @return
     */
    public UserEntity getUserByPrincipal(String securityDomain, String principal, String managedSysId, boolean dependants);

    public void addUser(UserEntity user) throws Exception;

    public void updateUser(UserEntity user);

    public void updateUserWithDependent(UserEntity user, boolean dependency);

    public void updateUserFromDto(User user);

    /**
     * Deletes a user from the system. The developer is responsible for deleting
     * associated objects such as the User-Group and User-Role relationship.
     * This has been done on purpose to minimize impact on the system through an
     * erroneous call to the delete operation.
     * 
     * @param id
     */
    public void removeUser(String id) throws Exception;

    public UserEntity getUserByName(String firstName, String lastName) throws BasicDataServiceException;

    public List<UserEntity> findUsersByLastUpdateRange(Date startDate, Date endDate);

    public List<UserEntity> findUserByOrganization(String orgId) throws BasicDataServiceException;

    public List<UserEntity> findUsersByStatus(UserStatusEnum status) throws BasicDataServiceException;

    public List<UserEntity> searchByDelegationProperties(DelegationFilterSearch search);

    public List<UserEntity> findBeans(UserSearchBean searchBean) throws BasicDataServiceException;

    public List<UserEntity> findBeans(UserSearchBean searchBean, int from, int size) throws BasicDataServiceException;

    int count(UserSearchBean searchBean) throws BasicDataServiceException;

    public void addAttribute(UserAttributeEntity attribute);

    public void updateAttribute(UserAttributeEntity attribute);

    public UserAttributeEntity getAttribute(String attrId);

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

    public List<AddressEntity> getAddressList(String userId);

    public List<Address> getAddressDtoList(String userId, boolean isDeep);

    public List<AddressEntity> getAddressList(String userId, Integer size, Integer from);

    public List<AddressEntity> getAddressList(AddressSearchBean searchBean, Integer size, Integer from);

    public void addPhone(PhoneEntity val);

    public void addPhoneSet(Collection<PhoneEntity> phoneList);

    public void updatePhone(PhoneEntity val);

    public void removePhone(String phoneId);

    public void removeAllPhones(String userId);

    public PhoneEntity getPhoneById(String addressId);

    public List<PhoneEntity> getPhoneList(String userId);

    public List<Phone> getPhoneDtoList(String userId, boolean isDeep);

    public List<PhoneEntity> getPhoneList(String userId, Integer size, Integer from);

    public List<PhoneEntity> getPhoneList(PhoneSearchBean searchBean, Integer size, Integer from);

    public void addEmailAddress(EmailAddressEntity val);

    public void addEmailAddressSet(Collection<EmailAddressEntity> adrList);

    public void updateEmailAddress(EmailAddressEntity val);

    public void removeEmailAddress(String emailAddressId);

    public void removeAllEmailAddresses(String userId);

    public EmailAddressEntity getEmailAddressById(String addressId);

    public List<EmailAddressEntity> getEmailAddressList(String userId);

    public List<EmailAddress> getEmailAddressDtoList(String userId, boolean isDeep);

    public List<EmailAddressEntity> getEmailAddressList(String userId, Integer size, Integer from);

    public List<EmailAddressEntity> getEmailAddressList(EmailSearchBean searchBean, Integer size, Integer from);

    public void addSupervisor(SupervisorEntity supervisor);

    public void addSuperior(String supervisorId, String subordinateId);

    public void updateSupervisor(SupervisorEntity supervisor);

    public void removeSupervisor(String supervisorId);

    public SupervisorEntity getSupervisor(String supervisorObjId);

    public void evict(Object object);

    /**
     * Returns a List of supervisor objects that represents the supervisors for
     * this employee or user.
     * 
     * @param employeeId
     * @return
     */
    public List<SupervisorEntity> getSupervisors(String employeeId);

    public SupervisorEntity findSupervisor(String superiorId, String subordinateId);

    public List<UserEntity> getSuperiors(String userId, Integer from, Integer size);

    public int getSuperiorsCount(String userId);

    public List<UserEntity> getSubordinates(String userId, Integer from, Integer size);

    public int getSubordinatesCount(String userId);

    public List<UserEntity> findPotentialSupSubs(UserSearchBean searchBean, Integer from, Integer size) throws BasicDataServiceException;

    public int findPotentialSupSubsCount(UserSearchBean searchBean) throws BasicDataServiceException;

    /**
     * Returns a list of Supervisor objects that represents the employees or
     * users for this supervisor
     * 
     * @param supervisorId
     * @return
     */
    public List<SupervisorEntity> getEmployees(String supervisorId);

    /**
     * Returns the primary supervisor for this employee. Null if no primary is
     * defined.
     * 
     * @param employeeId
     * @return
     */
    public SupervisorEntity getPrimarySupervisor(String employeeId);

    public UserEntity getUser(String id);

    public User getUserDto(String id);

    public UserEntity getUser(String id, String requestorId);

    public Map<String, UserAttributeEntity> getAllAttributes(String userId);

    public List<UserEntity> getUsersForResource(final String resourceId, String requesterId, final int from, final int size);

    public int getNumOfUsersForResource(final String resourceId, String requesterId);

    public List<UserEntity> getUsersForGroup(final String groupId, String requesterId, final int from, final int size);

    public int getNumOfUsersForGroup(final String groupId, String requesterId);

    public List<UserEntity> getUsersForRole(final String roleId, String requesterId, final int from, final int size);

    public int getNumOfUsersForRole(final String roleId, String requesterId);

    public String saveUserInfo(UserEntity userEntity, SupervisorEntity supervisorEntity) throws Exception;

    public void deleteUser(String userId);

    public void setSecondaryStatus(String userId, UserStatusEnum secondaryStatus);

    public void activateUser(String userId);

    public int getNumOfEmailsForUser(String userId);

    public int getNumOfAddressesForUser(String userId);

    public int getNumOfPhonesForUser(String userId);

    public void mergeUserFields(UserEntity origUserEntity, UserEntity newUserEntity);

    List<UserEntity> getUsersForMSys(String mSysId);

    public Map<String, UserAttribute> getUserAttributesDto(String userId);

    public Map<String, UserAttributeEntity> getUserAttributes(String userId);

    List<UserEntity> getByExample(UserSearchBean searchBean, int start, int size);

    boolean isRoleInUser(String userId, String roleId);

    List<String> getUserIdsInRole(String roleId, String requestrId);

    List<String> getUserIdsInGroup(String groupId, String requestrId);

    void addUserToGroup(String userId, String groupId);

    void removeUserFromGroup(String userId, String groupId);

    boolean isHasGroup(String userId, String groupId);

    void addUserToResource(String userId, String resourceId);

    boolean isHasResource(String userId, String resourceId);

    boolean isHasOrganization(String userId, String organizationId);

    boolean validateSearchBean(UserSearchBean seachBean) throws BasicDataServiceException;

    public boolean validateSearchBean(UserSearchBean searchBean, Map<String, UserAttribute> requesterAttributes) throws BasicDataServiceException;

}