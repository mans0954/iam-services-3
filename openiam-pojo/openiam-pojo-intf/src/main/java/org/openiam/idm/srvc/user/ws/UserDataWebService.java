package org.openiam.idm.srvc.user.ws;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.NewUserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.Supervisor;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserAttribute;
import org.openiam.idm.srvc.user.dto.UserNote;
import org.openiam.idm.srvc.user.dto.UserProfileRequestModel;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

//import org.openiam.idm.srvc.continfo.service.AddressDAO;
//import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
//import org.openiam.idm.srvc.continfo.service.PhoneDAO;

/**
 * WebService interface that clients will access to gain information about users
 * and related information.
 * 
 * @author Suneet Shah
 * @version 2
 */

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/user/service", name = "UserDataService")
public interface UserDataWebService {

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getUser(java.lang.String
     * , boolean)
     */
    @WebMethod
    public User getUserWithDependent(@WebParam(name = "id", targetNamespace = "") String id,
                                     @WebParam(name = "requestorId", targetNamespace = "") String requestorId,
                                     @WebParam(name = "dependants", targetNamespace = "") boolean dependants);

    @WebMethod
    public User getUserByPrincipal(@WebParam(name = "securityDomain", targetNamespace = "") String securityDomain,
                                   @WebParam(name = "principal", targetNamespace = "") String principal,
                                   @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId,
                                   @WebParam(name = "dependants", targetNamespace = "") boolean dependants);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#addUser(org.openiam
     * .idm.srvc.user.dto.User)
     */
    @WebMethod
    public Response addUser(@WebParam(name = "user", targetNamespace = "") User user) throws Exception;


    @WebMethod
    public Response updateUser(User user);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#updateUser(org.openiam
     * .idm.srvc.user.dto.User, boolean)
     */
    @WebMethod
    public Response updateUserWithDependent(@WebParam(name = "user", targetNamespace = "") User user,
                                            @WebParam(name = "dependency", targetNamespace = "") boolean dependency);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeUser(java.lang
     * .String)
     */
    @WebMethod
    public Response removeUser(@WebParam(name = "id", targetNamespace = "") String id);

    @WebMethod
    public User getUserByName(@WebParam(name = "firstName", targetNamespace = "") String firstName,
                              @WebParam(name = "lastName", targetNamespace = "") String lastName);

    @WebMethod
    public List<User> findUsersByLastUpdateRange(@WebParam(name = "startDate", targetNamespace = "") Date startDate,
                                                 @WebParam(name = "endDate", targetNamespace = "") Date endDate);

    @WebMethod
    public List<User> findUserByOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId);

    @WebMethod
    public List<User> findUsersByStatus(@WebParam(name = "status", targetNamespace = "") String status);

    @WebMethod
    public List<User> searchByDelegationProperties(@WebParam(name = "search", targetNamespace = "") DelegationFilterSearch search);

    @WebMethod
    List<User> findBeans(@WebParam(name = "searchBean", targetNamespace = "") UserSearchBean userSearchBean,
                         @WebParam(name = "from", targetNamespace = "") int from, @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    int count(UserSearchBean userSearchBean);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#addAttribute(org.openiam
     * .idm.srvc.user.dto.UserAttribute)
     */
    @WebMethod
    public Response addAttribute(@WebParam(name = "attribute", targetNamespace = "") UserAttribute attribute);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#updateAttribute(org
     * .openiam.idm.srvc.user.dto.UserAttribute)
     */
    @WebMethod
    public Response updateAttribute(@WebParam(name = "attribute", targetNamespace = "") UserAttribute attribute);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getAttribute(java.lang
     * .String)
     */
    @WebMethod
    public UserAttribute getAttribute(@WebParam(name = "id", targetNamespace = "") String attrId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeAttribute(org
     * .openiam.idm.srvc.user.dto.UserAttribute)
     */
    @WebMethod
    public Response removeAttribute(@WebParam(name = "attr", targetNamespace = "") String attrId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeAllAttributes
     * (java.lang.String)
     */
    @WebMethod
    public Response removeAllAttributes(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<UserAttribute> getUserAttributes(@WebParam(name = "userId", targetNamespace = "") String userId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#addNote(org.openiam
     * .idm.srvc.user.dto.UserNote)
     */
    @WebMethod
    public Response addNote(@WebParam(name = "note", targetNamespace = "") UserNote note);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#updateNote(org.openiam
     * .idm.srvc.user.dto.UserNote)
     */
    @WebMethod
    public Response updateNote(@WebParam(name = "note", targetNamespace = "") UserNote note);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getAllNotes(java.lang
     * .String)
     */
    @WebMethod
    public List<UserNote> getAllNotes(@WebParam(name = "userId", targetNamespace = "") String userId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getNote(java.lang.String
     * )
     */
    @WebMethod
    public UserNote getNote(@WebParam(name = "noteId", targetNamespace = "") java.lang.String noteId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeNote(org.openiam
     * .idm.srvc.user.dto.UserNote)
     */
    @WebMethod
    public Response removeNote(@WebParam(name = "note", targetNamespace = "") String noteId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeAllNotes(java
     * .lang.String)
     */
    @WebMethod
    public Response removeAllNotes(@WebParam(name = "userId", targetNamespace = "") String userId);

    /* ----------- Address Methods ------- */
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#addAddress(org.openiam
     * .idm.srvc.continfo.dto.Address)
     */
    @WebMethod
    public Response addAddress(@WebParam(name = "address", targetNamespace = "") Address address);

    @WebMethod
    public Response addAddressSet(@WebParam(name = "addressSet", targetNamespace = "") Set<Address> addressSet);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#updateAddress(org.openiam
     * .idm.srvc.continfo.dto.Address)
     */
    @WebMethod
    public Response updateAddress(@WebParam(name = "address", targetNamespace = "") Address address);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeAddress(org.openiam
     * .idm.srvc.continfo.dto.Address)
     */
    @WebMethod
    public Response removeAddress(@WebParam(name = "address", targetNamespace = "") String addressId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeAllAddresses(
     * java.lang.String)
     */
    @WebMethod
    public Response removeAllAddresses(@WebParam(name = "userId", targetNamespace = "") String userId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getAddressById(java
     * .lang.String)
     */
    @WebMethod
    public Address getAddressById(@WebParam(name = "addressId", targetNamespace = "") String addressId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getAddressList(java
     * .lang.String)
     */
    @WebMethod
    public List<Address> getAddressList(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<Address> getAddressListByPage(@WebParam(name = "userId", targetNamespace = "") String userId,
                                              @WebParam(name = "size", targetNamespace = "") Integer size,
                                              @WebParam(name = "from", targetNamespace = "") Integer from);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#addPhone(org.openiam
     * .idm.srvc.continfo.dto.Phone)
     */
    @WebMethod
    public Response addPhone(@WebParam(name = "phone", targetNamespace = "") Phone phone);

    @WebMethod
    public Response addPhoneSet(Set<Phone> phoneList);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#updatePhone(org.openiam
     * .idm.srvc.continfo.dto.Phone)
     */
    @WebMethod
    public Response updatePhone(@WebParam(name = "phone", targetNamespace = "") Phone phone);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removePhone(org.openiam
     * .idm.srvc.continfo.dto.Phone)
     */
    @WebMethod
    public Response removePhone(@WebParam(name = "phone", targetNamespace = "") String phoneId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeAllPhones(java
     * .lang.String)
     */
    @WebMethod
    public Response removeAllPhones(@WebParam(name = "userId", targetNamespace = "") String userId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getPhoneById(java.lang
     * .String)
     */
    @WebMethod
    public Phone getPhoneById(@WebParam(name = "addressId", targetNamespace = "") String addressId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getPhoneList(java.lang
     * .String)
     */
    @WebMethod
    public List<Phone> getPhoneList(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<Phone> getPhoneListByPage(@WebParam(name = "userId", targetNamespace = "") String userId,
                                          @WebParam(name = "size", targetNamespace = "") Integer size,
                                          @WebParam(name = "from", targetNamespace = "") Integer from);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#addEmailAddress(org
     * .openiam.idm.srvc.continfo.dto.EmailAddress)
     */
    @WebMethod
    public Response addEmailAddress(@WebParam(name = "email", targetNamespace = "") EmailAddress email);

    @WebMethod
    public Response addEmailAddressSet(@WebParam(name = "emailAddressSet", targetNamespace = "") Set<EmailAddress> emailAddressSet);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#updateEmailAddress(
     * org.openiam.idm.srvc.continfo.dto.EmailAddress)
     */
    @WebMethod
    public Response updateEmailAddress(@WebParam(name = "email", targetNamespace = "") EmailAddress email);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeEmailAddress(
     * org.openiam.idm.srvc.continfo.dto.EmailAddress)
     */
    @WebMethod
    public Response removeEmailAddress(@WebParam(name = "email", targetNamespace = "") String emailId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#removeAllEmailAddresses
     * (java.lang.String)
     */
    @WebMethod
    public Response removeAllEmailAddresses(@WebParam(name = "userId", targetNamespace = "") String userId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getEmailAddressById
     * (java.lang.String)
     */
    @WebMethod
    public EmailAddress getEmailAddressById(@WebParam(name = "addressId", targetNamespace = "") String addressId);

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.user.service.UserDataService#getEmailAddressList
     * (java.lang.String)
     */
    @WebMethod
    public List<EmailAddress> getEmailAddressList(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<EmailAddress> getEmailAddressListByPage(@WebParam(name = "userId", targetNamespace = "") String userId,
                                                        @WebParam(name = "size", targetNamespace = "") Integer size,
                                                        @WebParam(name = "from", targetNamespace = "") Integer from);

    @WebMethod
    public Response addSupervisor(@WebParam(name = "supervisor", targetNamespace = "") Supervisor supervisor);

    @WebMethod
    public Response updateSupervisor(@WebParam(name = "supervisor", targetNamespace = "") Supervisor supervisor);

    @WebMethod
    public Response removeSupervisor(@WebParam(name = "supervisor", targetNamespace = "") String supervisorId);

    @WebMethod
    public Supervisor getSupervisor(@WebParam(name = "supervisorObjId", targetNamespace = "") String supervisorObjId);

    /**
     * Returns a List of supervisor objects that represents the supervisors for
     * this employee or user.
     * 
     * @param employeeId
     * @return
     */
    @WebMethod
    public List<Supervisor> getSupervisors(@WebParam(name = "employeeId", targetNamespace = "") String employeeId);

    /**
     * Returns a list of Supervisor objects that represents the employees or
     * users for this supervisor
     * 
     * @param supervisorId
     * @return
     */
    @WebMethod
    public List<Supervisor> getEmployees(@WebParam(name = "supervisorId", targetNamespace = "") String supervisorId);

    /**
     * Returns the primary supervisor for this employee. Null if no primary is
     * defined.
     * 
     * @param employeeId
     * @return
     */
    @WebMethod
    public Supervisor getPrimarySupervisor(@WebParam(name = "employeeId", targetNamespace = "") String employeeId);

    @WebMethod
    public List<User> getUsersForResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
                                          @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                          @WebParam(name = "from", targetNamespace = "") final int from,
                                          @WebParam(name = "size", targetNamespace = "") final int size);

    @WebMethod
    public int getNumOfUsersForResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
                                        @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public List<User> getUsersForGroup(@WebParam(name = "groupId", targetNamespace = "") final String groupId,
                                       @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                       @WebParam(name = "from", targetNamespace = "") final int from,
                                       @WebParam(name = "size", targetNamespace = "") final int size);

    @WebMethod
    public int getNumOfUsersForGroup(@WebParam(name = "groupId", targetNamespace = "") final String groupId,
                                     @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public List<User> getUsersForRole(@WebParam(name = "roleId", targetNamespace = "") final String roleId,
                                      @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                      @WebParam(name = "from", targetNamespace = "") final int from,
                                      @WebParam(name = "size", targetNamespace = "") final int size);

    @WebMethod
    public int getNumOfUsersForRole(@WebParam(name = "roleId", targetNamespace = "") final String roleId,
                                    @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    @WebMethod
    public UserResponse saveUserInfo(@WebParam(name = "user", targetNamespace = "") final User user,
                                     @WebParam(name = "supervisor", targetNamespace = "") final Supervisor supervisor);

    @WebMethod
    public Response deleteUser(@WebParam(name = "userId", targetNamespace = "") final String userId);

    @WebMethod
    public Response enableDisableUser(@WebParam(name = "userId", targetNamespace = "") final String userId,
                                      @WebParam(name = "secondaryStatus", targetNamespace = "") final UserStatusEnum secondaryStatus);

    @WebMethod
    public Response activateUser(@WebParam(name = "userId", targetNamespace = "") final String userId);

    @WebMethod
    public Integer getNumOfEmailsForUser(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public Integer getNumOfAddressesForUser(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public Integer getNumOfPhonesForUser(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public SaveTemplateProfileResponse saveUserProfile(@WebParam(name = "request", targetNamespace = "") final UserProfileRequestModel request);

    /*
    @WebMethod
    public SaveTemplateProfileResponse createNewUserProfile(@WebParam(name = "request", targetNamespace = "") final NewUserProfileRequestModel request);
	*/

    @WebMethod
    public Response sendNewUserEmail(@WebParam(name = "userId", targetNamespace = "") final String userId,
                                     @WebParam(name = "password", targetNamespace = "") final String password,
                                     @WebParam(name = "login", targetNamespace = "") final String login);

    @WebMethod
    public List<User> getByManagedSystem(@WebParam(name = "mSysId", targetNamespace = "") final String mSysId);

    @WebMethod
    public Response approveITPolicy(@WebParam(name = "userId", targetNamespace = "") final String userId);
}