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

	/**
	 * Get's a user based on his internal ID
	 * @param id - the internal user ID of the user being requested
	 * @param requestorId - the user ID of the person making this call
	 * @param dependants - if set to true, the User is returned with all of his collections
	 * @return - a User object, or null if not found
	 */
    @WebMethod
    public User getUserWithDependent(@WebParam(name = "id", targetNamespace = "") String id,
                                     @WebParam(name = "requestorId", targetNamespace = "") String requestorId,
                                     @WebParam(name = "dependants", targetNamespace = "") boolean dependants);

    /**
     * Find a User based on his principal, security domain, and the managed system
     * @param securityDomain - the security domain ID
     * @param principal - the user's principal (login)
     * @param managedSysId - the ID of the managed system to which the principal belongs to
     * @param dependants - if set to true, the User is returned with all of his collections
     * @return - a User object, or null if not found
     */
    @WebMethod
    public User getUserByPrincipal(@WebParam(name = "securityDomain", targetNamespace = "") String securityDomain,
                                   @WebParam(name = "principal", targetNamespace = "") String principal,
                                   @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId,
                                   @WebParam(name = "dependants", targetNamespace = "") boolean dependants);

    /**
     * Delete a User from the Openiam database
     * @param id - the ID of the user
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeUser(@WebParam(name = "id", targetNamespace = "") String id);

    @WebMethod
    public List<User> findUserByOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId);

    @WebMethod
    List<User> findBeans(@WebParam(name = "searchBean", targetNamespace = "") UserSearchBean userSearchBean,
                         @WebParam(name = "from", targetNamespace = "") int from, @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    int count(UserSearchBean userSearchBean);

    /**
     * Add a UserAttribute to this User
     * @param attribute - the UserAttribute
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addAttribute(@WebParam(name = "attribute", targetNamespace = "") UserAttribute attribute);

    /**
     * Update a UserAttribute to this User
     * @param attribute - the UserAttribute
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response updateAttribute(@WebParam(name = "attribute", targetNamespace = "") UserAttribute attribute);

    /**
     * Get a UserAttribute by the id
     * @param id - the id of this UserAttribute
     * @return a UserAttribute object, or null if not found
     */
    @WebMethod
    public UserAttribute getAttribute(@WebParam(name = "id", targetNamespace = "") String attrId);

    /**
     * Removes a UserAttribute with the specified ID
     * @param id - the id of this UserAttribute
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeAttribute(@WebParam(name = "attr", targetNamespace = "") String attrId);

    /**
     * gets all UserAttributes associated with this User
     * @param userId - the id of this User
     * @return a List of UserAttributes associated with this user
     */
    @WebMethod
    public List<UserAttribute> getUserAttributes(@WebParam(name = "userId", targetNamespace = "") String userId);

    /*
    @WebMethod
    public Response addNote(@WebParam(name = "note", targetNamespace = "") UserNote note);

    @WebMethod
    public Response updateNote(@WebParam(name = "note", targetNamespace = "") UserNote note);

    @WebMethod
    public List<UserNote> getAllNotes(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public UserNote getNote(@WebParam(name = "noteId", targetNamespace = "") java.lang.String noteId);

    @WebMethod
    public Response removeNote(@WebParam(name = "note", targetNamespace = "") String noteId);

    @WebMethod
    public Response removeAllNotes(@WebParam(name = "userId", targetNamespace = "") String userId);
	*/

    /**
     * Add an Address to a User
     * @param address - an Address Object
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addAddress(@WebParam(name = "address", targetNamespace = "") Address address);

    /**
     * Updates an Address for a User
     * @param address - an Address Object
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response updateAddress(@WebParam(name = "address", targetNamespace = "") Address address);

    /**
     * Remove an Address specified by the parameter
     * @param addressId - the ID of the address Object
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeAddress(@WebParam(name = "address", targetNamespace = "") String addressId);


    /**
     * find an Address record by the given id
     * @param addressId - the ID of the Address
     * @return an Address record
     */
    @WebMethod
    public Address getAddressById(@WebParam(name = "addressId", targetNamespace = "") String addressId);

    /**
     * Gets all Address objects associated with the given userId
     * @param userId - the ID of the User
     * @return the Address objects associated with this user
     */
    @WebMethod
    public List<Address> getAddressList(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * returns to Address Objects associated with this user, based on the size and from parameters
     * @param userId - the user ID to which the Address records belong to
     * @param size - the number of records to return
     * @param from - where to start
     * @return the Address objects associated with this user
     */
    @WebMethod
    public List<Address> getAddressListByPage(@WebParam(name = "userId", targetNamespace = "") String userId,
                                              @WebParam(name = "size", targetNamespace = "") Integer size,
                                              @WebParam(name = "from", targetNamespace = "") Integer from);

    /**
     * Add a Phone to this User
     * @param phone - the Phone record
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addPhone(@WebParam(name = "phone", targetNamespace = "") Phone phone);

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

    @WebMethod
    public Supervisor findSupervisor(@WebParam(name = "superiorId", targetNamespace = "") String superiorId,
                                     @WebParam(name = "subordinateId", targetNamespace = "") String subordinateId);

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
    public List<User> getSuperiors(@WebParam(name = "userId", targetNamespace = "") String userId,
                                   @WebParam(name = "from", targetNamespace = "") Integer from,
                                   @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    public int getSuperiorsCount(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<User> getSubordinates(@WebParam(name = "userId", targetNamespace = "") String userId,
                                      @WebParam(name = "from", targetNamespace = "") Integer from,
                                      @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    public int getSubordinatesCount(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<User> findPotentialSupSubs(@WebParam(name = "searchBean", targetNamespace = "") UserSearchBean userSearchBean,
                                             @WebParam(name = "from", targetNamespace = "") Integer from,
                                             @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    public int findPotentialSupSubsCount(@WebParam(name = "searchBean", targetNamespace = "") UserSearchBean userSearchBean);

    @WebMethod
    public Response addSuperior(@WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                           @WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public Response removeSuperior(@WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                              @WebParam(name = "userId", targetNamespace = "") String userId);

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
    public Response acceptITPolicy(@WebParam(name = "userId", targetNamespace = "") final String userId);
}