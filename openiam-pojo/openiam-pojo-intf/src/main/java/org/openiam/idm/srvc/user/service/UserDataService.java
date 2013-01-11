package org.openiam.idm.srvc.user.service;

import org.openiam.idm.searchbeans.OrganizationSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.continfo.domain.EmailAddressEntity;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.org.dto.Organization;
import org.openiam.idm.srvc.user.domain.SupervisorEntity;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.UserSearch;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service interface that clients will access to gain information about users and related information.
 *
 * @author Suneet Shah
 * @version 2
 */

public interface UserDataService {

    /**
     * Returns a User object that is associated with the principal.
     * ManagedSysId refers to a principal that is associated with a particular target system. User 0 to
     * use the default principal ID.
     *
     * @param securityDomain
     * @param principal
     * @param managedSysId
     * @return
     */
    public UserEntity getUserByPrincipal(String securityDomain, String principal, String managedSysId, boolean dependants);

    public void addUser(UserEntity user) throws Exception;

    public void addUserWithDependent(UserEntity user, boolean dependency);

    public void updateUser(UserEntity user);

    public void updateUserWithDependent(UserEntity user, boolean dependency);


    /**
     * Deletes a user from the system. The developer is responsible for deleting associated objects such as the User-Group and User-Role relationship.
     * This has been done on purpose to minimize impact on the system through an erroneous call to the delete operation.
     *
     * @param id
     */
    public void removeUser(String id);


    public UserEntity getUserByName(String firstName, String lastName);

    public List<UserEntity> findUsersByLastUpdateRange(Date startDate, Date endDate);

    public List<UserEntity> findUserByOrganization(String orgId);

    public List<UserEntity> findUsersByStatus(UserStatusEnum status);
    @Deprecated
    public List<UserEntity> search(UserSearch search);
    public List<UserEntity> searchByDelegationProperties(DelegationFilterSearch search);

    public List<UserEntity> findBeans(UserSearchBean searchBean);

    public List<UserEntity> findBeans(UserSearchBean searchBean, int from, int size);

    int count(UserSearchBean searchBean);

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

    public AddressEntity getAddressByName(String userId, String addressName);

    public AddressEntity getDefaultAddress(String userId);

    public List<AddressEntity> getAddressList(String userId);

    public void addPhone(PhoneEntity val);

    public void addPhoneSet(Collection<PhoneEntity> phoneList);

    public void updatePhone(PhoneEntity val);

    public void removePhone(String phoneId);

    public void removeAllPhones(String userId);

    public PhoneEntity getPhoneById(String addressId);

    public PhoneEntity getPhoneByName(String userId, String addressName);

    public PhoneEntity getDefaultPhone(String userId);

    public List<PhoneEntity> getPhoneList(String userId);

    public void addEmailAddress(EmailAddressEntity val);

    public void addEmailAddressSet(Collection<EmailAddressEntity> adrList);

    public void updateEmailAddress(EmailAddressEntity val);

    public void removeEmailAddress(String emailAddressId);

    public void removeAllEmailAddresses(String userId);

    public EmailAddressEntity getEmailAddressById(String addressId);

    public EmailAddressEntity getEmailAddressByName(String userId,
                                              String addressName);

    public EmailAddressEntity getDefaultEmailAddress(String userId);

    public List<EmailAddressEntity> getEmailAddressList(String userId);
    public List<EmailAddressEntity> getEmailAddressList(String userId, Integer size, Integer from);

    public void addSupervisor(SupervisorEntity supervisor);

    public void updateSupervisor(SupervisorEntity supervisor);

    public void removeSupervisor(String supervisorId);

    public SupervisorEntity getSupervisor(String supervisorObjId);

    /**
     * Returns a List of supervisor objects that represents the supervisors for this employee or user.
     *
     * @param employeeId
     * @return
     */
    public List<SupervisorEntity> getSupervisors(String employeeId);

    /**
     * Returns a list of Supervisor objects that represents the employees or users for this supervisor
     *
     * @param supervisorId
     * @return
     */
    public List<SupervisorEntity> getEmployees(String supervisorId);

    /**
     * Returns the primary supervisor for this employee. Null if no primary is defined.
     *
     * @param employeeId
     * @return
     */
    public SupervisorEntity getPrimarySupervisor(String employeeId);

    public UserEntity getUser(String id);

    public Map<String, UserAttributeEntity> getAllAttributes(String userId);

    public List<UserEntity> getUsersForResource(final String resourceId, final int from, final int size);
    public int getNumOfUsersForResource(final String resourceId);
    
    public List<UserEntity> getUsersForGroup(final String groupId, final int from, final int size);
    public int getNumOfUsersForGroup(final String groupId);
    
    public List<UserEntity> getUsersForRole(final String roleId, final int from, final int size);
    public int getNumOfUsersForRole(final String roleId);

    public String saveUserInfo(UserEntity userEntity, SupervisorEntity supervisorEntity) throws Exception;

    public void deleteUser(String userId);

    public void enableDisableUser(String userId, UserStatusEnum secondaryStatus);

    public void activateUser(String userId);

    public Integer getNumOfEmailsForUser( String userId);
}