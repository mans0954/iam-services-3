package org.openiam.idm.srvc.user.ws;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.searchbeans.EmailSearchBean;
import org.openiam.idm.searchbeans.PotentialSupSubSearchBean;
import org.openiam.idm.searchbeans.SupervisorSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.continfo.dto.Address;
import org.openiam.idm.srvc.continfo.dto.EmailAddress;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.dto.SaveTemplateProfileResponse;
import org.openiam.idm.srvc.user.dto.*;

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
     * 
     * @param id
     *            - the internal user ID of the user being requested
     * @param requestorId
     *            - the user ID of the person making this call
     * @param dependants
     *            - if set to true, the User is returned with all of his
     *            collections
     * @return - a User object, or null if not found
     */
    @WebMethod
    public User getUserWithDependent(@WebParam(name = "id", targetNamespace = "") String id,
                                     @WebParam(name = "requestorId", targetNamespace = "") String requestorId,
                                     @WebParam(name = "dependants", targetNamespace = "") boolean dependants);

    /**
     * Find a User based on his principal, security domain, and the managed
     * system
     * 
     * @param principal
     *            - the user's principal (login)
     * @param managedSysId
     *            - the ID of the managed system to which the principal belongs
     *            to
     * @param dependants
     *            - if set to true, the User is returned with all of his
     *            collections
     * @return - a User object, or null if not found
     */
    @WebMethod
    public User getUserByPrincipal(@WebParam(name = "principal", targetNamespace = "") String principal,
                                   @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId,
                                   @WebParam(name = "dependants", targetNamespace = "") boolean dependants);

    /**
     * Delete a User from the Openiam database
     * 
     * @param id
     *            - the ID of the user
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeUser(@WebParam(name = "id", targetNamespace = "") String id);

    @WebMethod
    public List<User> findUserByOrganization(@WebParam(name = "orgId", targetNamespace = "") String orgId);

    /**
     * Call to find users by various criteria
     * @param userSearchBean  
     * @param from - where to start paging
     * @param size - how many results to return
     * @return a List of matched User objects
     */
    @WebMethod
    List<User> findBeans(@WebParam(name = "searchBean", targetNamespace = "") UserSearchBean userSearchBean,
                         @WebParam(name = "from", targetNamespace = "") int from, @WebParam(name = "size", targetNamespace = "") int size);

    @WebMethod
    int count(UserSearchBean userSearchBean);

    /**
     * Add a UserAttribute to this User
     * 
     * @param attribute
     *            - the UserAttribute
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addAttribute(@WebParam(name = "attribute", targetNamespace = "") UserAttribute attribute);

    /**
     * Update a UserAttribute to this User
     * 
     * @param attribute
     *            - the UserAttribute
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response updateAttribute(@WebParam(name = "attribute", targetNamespace = "") UserAttribute attribute);

    /**
     * Get a UserAttribute by the id
     * 
     * @param id
     *            - the id of this UserAttribute
     * @return a UserAttribute object, or null if not found
     */
    @WebMethod
    public UserAttribute getAttribute(@WebParam(name = "id", targetNamespace = "") String attrId);

    /**
     * Removes a UserAttribute with the specified ID
     * 
     * @param id
     *            - the id of this UserAttribute
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeAttribute(@WebParam(name = "attr", targetNamespace = "") String attrId);

    /**
     * gets all UserAttributes associated with this User
     * 
     * @param userId
     *            - the id of this User
     * @return a List of UserAttributes associated with this user
     */
    @WebMethod
    public List<UserAttribute> getUserAttributes(@WebParam(name = "userId", targetNamespace = "") String userId);
    
    @WebMethod
    public List<UserAttribute> getUserAttributesInternationalized(final @WebParam(name = "userId", targetNamespace = "") String userId,
    															  final @WebParam(name = "language", targetNamespace = "") Language language);

    /*
     * @WebMethod public Response addNote(@WebParam(name = "note",
     * targetNamespace = "") UserNote note);
     * 
     * @WebMethod public Response updateNote(@WebParam(name = "note",
     * targetNamespace = "") UserNote note);
     * 
     * @WebMethod public List<UserNote> getAllNotes(@WebParam(name = "userId",
     * targetNamespace = "") String userId);
     * 
     * @WebMethod public UserNote getNote(@WebParam(name = "noteId",
     * targetNamespace = "") java.lang.String noteId);
     * 
     * @WebMethod public Response removeNote(@WebParam(name = "note",
     * targetNamespace = "") String noteId);
     * 
     * @WebMethod public Response removeAllNotes(@WebParam(name = "userId",
     * targetNamespace = "") String userId);
     */

    /**
     * Add an Address to a User
     * 
     * @param address
     *            - an Address Object
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addAddress(@WebParam(name = "address", targetNamespace = "") Address address);

    /**
     * Updates an Address for a User
     * 
     * @param address
     *            - an Address Object
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response updateAddress(@WebParam(name = "address", targetNamespace = "") Address address);

    /**
     * Remove an Address specified by the parameter
     * 
     * @param addressId
     *            - the ID of the address Object
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeAddress(@WebParam(name = "address", targetNamespace = "") String addressId);

    /**
     * find an Address record by the given id
     * 
     * @param addressId
     *            - the ID of the Address
     * @return an Address record
     */
    @WebMethod
    public Address getAddressById(@WebParam(name = "addressId", targetNamespace = "") String addressId);

    /**
     * Gets all Address objects associated with the given userId
     * 
     * @param userId
     *            - the ID of the User
     * @return the Address objects associated with this user
     */
    @WebMethod
    public List<Address> getAddressList(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * returns to Address Objects associated with this user, based on the size
     * and from parameters
     * 
     * @param userId
     *            - the user ID to which the Address records belong to
     * @param size
     *            - the number of records to return
     * @param from
     *            - where to start
     * @return the Address objects associated with this user
     */
    @WebMethod
    public List<Address> getAddressListByPage(@WebParam(name = "userId", targetNamespace = "") String userId,
                                              @WebParam(name = "size", targetNamespace = "") Integer size,
                                              @WebParam(name = "from", targetNamespace = "") Integer from);

    /**
     * Add a Phone to this User
     * 
     * @param phone
     *            - the Phone record
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addPhone(@WebParam(name = "phone", targetNamespace = "") Phone phone);
    
    /**
     * Validate that a phone is valid.
     * 
     * @param phone
     * @return a Response Object, containing the result of this operation 
     */
    @WebMethod
    public Response validatePhone(@WebParam(name = "phone", targetNamespace = "") Phone phone);
    
    @WebMethod
    public Response addTOPTTokenToPhone(@WebParam(name = "phoneId", targetNamespace = "") String phoneId,
    									@WebParam(name = "secret", targetNamespace = "") String secret);

    /**
     * Updates the Phone record
     * 
     * @param phone
     *            - the Phone record
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response updatePhone(@WebParam(name = "phone", targetNamespace = "") Phone phone);

    /**
     * Deletes a phone record
     * 
     * @param phoneId
     *            the ID of the phone
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removePhone(@WebParam(name = "phone", targetNamespace = "") String phoneId);

    /**
     * Get a Phone record by it's ID
     * 
     * @param addressId
     *            - the ID of the phone
     * @return the Phone record, or null if not found
     */
    @WebMethod
    public Phone getPhoneById(@WebParam(name = "phoneId", targetNamespace = "") String phoneId);

    /**
     * Gets all Phones belonging to a user
     * 
     * @param userId
     *            - the user's ID
     * @return a List of Phone records belonging to this user
     */
    @WebMethod
    public List<Phone> getPhoneList(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Gets a paged List of Phones belonging to a User
     * 
     * @param userId
     *            - the user's ID
     * @param size
     *            - how many records to return
     * @param from
     *            - where to start
     * @return a paged List of Phone records belonging to this user
     */
    @WebMethod
    public List<Phone> getPhoneListByPage(@WebParam(name = "userId", targetNamespace = "") String userId,
                                          @WebParam(name = "size", targetNamespace = "") Integer size,
                                          @WebParam(name = "from", targetNamespace = "") Integer from);

    /**
     * Adds an Email Address
     * 
     * @param email
     *            - the Email Address to add
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addEmailAddress(@WebParam(name = "email", targetNamespace = "") EmailAddress email);

    /**
     * Updates an Email Address
     * 
     * @param email
     *            - the Email Address to update
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response updateEmailAddress(@WebParam(name = "email", targetNamespace = "") EmailAddress email);

    /**
     * Remove an Email Address
     * 
     * @param emailId
     *            - the email address ID to delete
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeEmailAddress(@WebParam(name = "email", targetNamespace = "") String emailId);

    /**
     * get an Email Address by ID
     * 
     * @param addressId
     *            - the Email Adddress ID
     * @return an EmailAddress Object, or null if not found
     */
    @WebMethod
    public EmailAddress getEmailAddressById(@WebParam(name = "addressId", targetNamespace = "") String addressId);

    /**
     * Get all EmailAddress records associated with a User
     * 
     * @param userId
     *            - the user ID of interest
     * @return a List of EmailAddress records belonging to this User
     */
    @WebMethod
    public List<EmailAddress> getEmailAddressList(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Gets a paged List of EmailAddress records associated with a User
     * 
     * @param userId
     *            - the user ID of interest
     * @param size
     *            - how many records to fetch
     * @param from
     *            - where to start
     * @return a paged List of EmailAddress records associated with a User
     */
    @WebMethod
    public List<EmailAddress> getEmailAddressListByPage(@WebParam(name = "userId", targetNamespace = "") String userId,
                                                        @WebParam(name = "size", targetNamespace = "") Integer size,
                                                        @WebParam(name = "from", targetNamespace = "") Integer from);

    @WebMethod
    public List<EmailAddress> findEmailBeans(final @WebParam(name="searchBean") EmailSearchBean searchBean,
    										 final @WebParam(name = "size", targetNamespace = "") int size,
    										 final @WebParam(name = "from", targetNamespace = "") int from);
    
    /**
     * Add a Supervisor record
     * 
     * @param supervisor
     *            - the Supervisor record
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addSupervisor(@WebParam(name = "supervisor", targetNamespace = "") Supervisor supervisor);

    /**
     * Update an Supervisor record
     * 
     * @param supervisor
     *            - the Supervisor record
     * @return a Response Object, containing the result of this operation
     */
    // @WebMethod
    // public Response updateSupervisor(@WebParam(name = "supervisor",
    // targetNamespace = "") Supervisor supervisor);

    /**
     * Delete a Supervisor record
     * 
     * @param supervisorId
     *            - the Supervisor ID
     * @param employeeId
     *            - the Employee ID
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeSupervisor(@WebParam(name = "supervisor", targetNamespace = "") String supervisorId,
                                     @WebParam(name = "employeeId", targetNamespace = "") String employeeId);

    /**
     * Get a Supervisor Object by ID
     * 
     * @param id
     *            - the Supervisor ID
     * @return a Supervisor Object, or null if not found
     */
    // @WebMethod
    // public Supervisor getSupervisor(@WebParam(name = "id", targetNamespace =
    // "") String id);

    /**
     * Returns a List of supervisor objects that represents the supervisors for
     * this employee or user.
     * 
     * @param employeeId
     * @return
     */
    // @WebMethod
    // public List<Supervisor> getSupervisors(@WebParam(name = "employeeId",
    // targetNamespace = "") String employeeId);

    /**
     * gets a supervisor Object based on the Superior ID and Subordinate ID
     * 
     * @param superiorId
     *            - the Superior ID
     * @param subordinateId
     *            - the Subordinate ID
     * @return - the Supervisor object
     */
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
    // @WebMethod
    // public List<Supervisor> getEmployees(@WebParam(name = "supervisorId",
    // targetNamespace = "") String supervisorId);

    /**
     * Returns the primary supervisor for this employee. Null if no primary is
     * defined.
     * 
     * @param employeeId
     * @return
     */
    @WebMethod
    public User getPrimarySupervisor(@WebParam(name = "employeeId", targetNamespace = "") String employeeId);

    /**
     * returns a paged List of Supervisors for this User
     * 
     * @param userId
     *            - the User ID
     * @param from
     *            - where to start
     * @param size
     *            - how many objects to return
     * @return a paged List of Supervisors for this User
     */
    @WebMethod
    public List<User> getSuperiors(@WebParam(name = "userId", targetNamespace = "") String userId,
                                   @WebParam(name = "from", targetNamespace = "") Integer from,
                                   @WebParam(name = "size", targetNamespace = "") Integer size);

    /**
     * Get the Number of supervisors for this user
     * 
     * @param userId
     *            - the User ID
     * @return the Number of supervisors for this user
     */
    @WebMethod
    public int getSuperiorsCount(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Gets a Paged List of User Objects, representing the subordinates of this
     * userId
     * 
     * @param userId
     *            - the User ID
     * @param from
     *            - where to start in the list
     * @param size
     *            - how many to return
     * @return a Paged List of User Objects, representing the subordinates of
     *         this userId
     */
    @WebMethod
    public List<User> getSubordinates(@WebParam(name = "userId", targetNamespace = "") String userId,
                                      @WebParam(name = "from", targetNamespace = "") Integer from,
                                      @WebParam(name = "size", targetNamespace = "") Integer size);

    /**
     * Gets the number of subordinates for this User
     * 
     * @param userId
     *            - the User ID
     * @return the number of subordinates for this User
     */
    @WebMethod
    public int getSubordinatesCount(@WebParam(name = "userId", targetNamespace = "") String userId);

    @WebMethod
    public List<User> findPotentialSupSubs(@WebParam(name = "searchBean", targetNamespace = "") PotentialSupSubSearchBean userSearchBean,
                                           @WebParam(name = "from", targetNamespace = "") Integer from,
                                           @WebParam(name = "size", targetNamespace = "") Integer size);

    @WebMethod
    public int findPotentialSupSubsCount(@WebParam(name = "searchBean", targetNamespace = "") PotentialSupSubSearchBean userSearchBean);

    /**
     * Makes the User specified by superiorId a Superior of the User specified
     * by subordinateId
     * 
     * @param superiorId
     *            - the superior's User ID
     * @param suborinateId
     *            - the subordinate's User ID
     * @param requesterId
     *            - ID of the requestor
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response addSuperior(@WebParam(name = "superiorId", targetNamespace = "") String superiorId,
                                @WebParam(name = "suborinateId", targetNamespace = "") String suborinateId,
                                @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Removes the User specified by superiorId from being a Superior of the
     * User specified by subordinateId
     * 
     * @param superiorId
     *            - the superior's User ID
     * @param suborinateId
     *            - the subordinate's User ID
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response removeSuperior(@WebParam(name = "superiorId", targetNamespace = "") String superiorId,
                                   @WebParam(name = "suborinateId", targetNamespace = "") String suborinateId);

    /**
     * Gets a paged List of Users directly entitled to the Resource specified by
     * the resourceId
     * 
     * @param resourceId
     *            - the Resource ID
     * @param requesterId
     *            - ID of the requestor
     * @param from
     *            - where to start in the paged list
     * @param size
     *            - how many to return
     * @return a paged List of Users directly entitled to the Resource specified
     *         by the resourceId
     */
    @WebMethod
    @Deprecated
    public List<User> getUsersForResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
                                          @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                          @WebParam(name = "from", targetNamespace = "") final int from,
                                          @WebParam(name = "size", targetNamespace = "") final int size);

    /**
     * Gets a paged List of Users directly entitled to the Resource specified by
     * the resourceId. This method allows sorting the resultset.
     *
     * @param userSearchBean
     *            - the Resource ID
     * @param from
     *            - where to start in the paged list
     * @param size
     *            - how many to return
     * @return a paged List of Users directly entitled to the Resource specified
     *         by the resourceId
     */
    @WebMethod
    public List<User> getUsersForResourceWithSorting(@WebParam(name = "userSearchBean", targetNamespace = "") final UserSearchBean userSearchBean,
                                                     @WebParam(name = "from", targetNamespace = "")  final int from,
                                                     @WebParam(name = "size", targetNamespace = "") final int size);
    /**
     * Gets the number of Users directly entitled to this Resource
     * 
     * @param resourceId
     *            - the Resource ID
     * @param requesterId
     *            - ID of the requestor
     * @return the number of Users directly entitled to this Resource
     */
    @WebMethod
    @Deprecated
    public int getNumOfUsersForResource(@WebParam(name = "resourceId", targetNamespace = "") final String resourceId,
                                        @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets a paged List of Users that are direct members of this Group
     * 
     * @param groupId
     *            - the Group ID
     * @param requesterId
     *            - the requestor ID
     * @param from
     *            - where to start in the List
     * @param size
     *            - how many to return
     * @return a paged List of Users that are direct members of this Group
     */
    @WebMethod
    @Deprecated
    public List<User> getUsersForGroup(@WebParam(name = "groupId", targetNamespace = "") final String groupId,
                                       @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                       @WebParam(name = "from", targetNamespace = "") final int from,
                                       @WebParam(name = "size", targetNamespace = "") final int size);

    /**
     * Gets the number of Users that are direct members of this Group
     * 
     * @param groupId
     *            - the Group ID
     * @param requesterId
     *            - the requestor ID
     * @return the number of Users that are direct members of this Group
     */
    @WebMethod
    @Deprecated
    public int getNumOfUsersForGroup(@WebParam(name = "groupId", targetNamespace = "") final String groupId,
                                     @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Gets a Paged List of Users that are direct members of this Role
     * 
     * @param roleId
     *            - the Role ID
     * @param requesterId
     *            - ID of the user making this request
     * @param from
     *            - where to start in the list
     * @param size
     *            - how many to return
     * @return a Paged List of Users that are direct members of this Role
     */
    @WebMethod
    @Deprecated
    public List<User> getUsersForRole(@WebParam(name = "roleId", targetNamespace = "") final String roleId,
                                      @WebParam(name = "requesterId", targetNamespace = "") String requesterId,
                                      @WebParam(name = "from", targetNamespace = "") final int from,
                                      @WebParam(name = "size", targetNamespace = "") final int size);

    /**
     * Gets the number of Users that are direct members of this Role
     * 
     * @param roleId
     *            - the Role ID
     * @param requesterId
     *            - ID of the User making this request
     * @return the number of Users that are direct members of this Role
     */
    @WebMethod
    @Deprecated
    public int getNumOfUsersForRole(@WebParam(name = "roleId", targetNamespace = "") final String roleId,
                                    @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Saves a User Object, with an optional Supervisor
     * 
     * @param user
     *            - the User Object
     * @param supervisorId
     *            - the optional Supervisor
     * @return - a UserResponse Object, containing the User object saved
     */
    @WebMethod
    public UserResponse saveUserInfo(@WebParam(name = "user", targetNamespace = "") final User user,
                                     @WebParam(name = "supervisorId", targetNamespace = "") final String supervisorId);

    /**
     * Deletes a User from the databse
     * 
     * @param userId
     *            - the User ID
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response deleteUser(@WebParam(name = "userId", targetNamespace = "") final String userId);

    /**
     * Sets the Secondary Status of the User
     * 
     * @param userId
     *            - the ID of the User
     * @param secondaryStatus
     *            - the Secondary Status
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response setSecondaryStatus(@WebParam(name = "userId", targetNamespace = "") final String userId,
                                       @WebParam(name = "secondaryStatus", targetNamespace = "") final UserStatusEnum secondaryStatus);

    /**
     * Activates a User
     * 
     * @param userId
     *            - the User ID
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response activateUser(@WebParam(name = "userId", targetNamespace = "") final String userId);

    /**
     * Resets User's account
     *
     * @param userId
     *            - the User ID
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response resetUser(@WebParam(name = "userId", targetNamespace = "") final String userId);

    /**
     * Gets the number of Emails for this user
     * 
     * @param userId
     *            - the User ID
     * @return the number of Emails for this user
     */
    @WebMethod
    public int getNumOfEmailsForUser(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Gets the number of Addresses for this user
     * 
     * @param userId
     *            - the User ID
     * @return the number of Addresses for this user
     */
    @WebMethod
    public int getNumOfAddressesForUser(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Gets the number of Phones for this User
     * 
     * @param userId
     *            - the User ID
     * @return the number of Phones for this User
     */
    @WebMethod
    public int getNumOfPhonesForUser(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Saves a User Profile
     * 
     * @param request
     *            - the User Profile to save
     * @return a SaveTemplateProfileResponse, containing the results of this
     *         operation
     */
    @WebMethod
    public SaveTemplateProfileResponse saveUserProfile(@WebParam(name = "request", targetNamespace = "") final UserProfileRequestModel request);

    /**
     * Returns Profile picture by it's ID
     *
     * @param picId
     * @return
     */
    @WebMethod
    ProfilePicture getProfilePictureById(
            @WebParam(name = "picId", targetNamespace = "") String picId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Returns Profile picture for User by his ID
     *
     * @param userId
     * @return
     */
    @WebMethod
    ProfilePicture getProfilePictureByUserId(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Saves Profile picture
     *
     * @param pic
     * @return
     */
    @WebMethod
    Response saveProfilePicture(
            @WebParam(name = "pic", targetNamespace = "") ProfilePicture pic,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Deletes Profile picture by it's ID
     *
     * @param picId
     * @return
     */
    @WebMethod
    Response deleteProfilePictureById(
            @WebParam(name = "picId", targetNamespace = "") String picId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Deletes Profile picture for User by his ID
     * @param userId
     * @return
     */
    @WebMethod
    Response deleteProfilePictureByUserId(
            @WebParam(name = "userId", targetNamespace = "") String userId,
            @WebParam(name = "requesterId", targetNamespace = "") String requesterId);

    /**
     * Accept an IT policy
     * 
     * @param userId
     *            - the User ID who has accepted the IT Policy
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response acceptITPolicy(@WebParam(name = "userId", targetNamespace = "") final String userId);

    /**
     * Validate user search request according to  Delegation filter.
     * @param userSearchBean - UserSearchBean that is represented user search request
     * @return a Response Object, containing the result of this operation
     */
    @WebMethod
    public Response validateUserSearchRequest(@WebParam(name = "userSearchBean", targetNamespace = "") final UserSearchBean userSearchBean);

    @WebMethod
    public List<User> getUserByLastDate(
            @WebParam(name = "lastDate", targetNamespace = "") final Date lastDate);

    @WebMethod
    public List<User> getUserBetweenCreateDate(
            @WebParam(name = "fromDate", targetNamespace = "") final Date fromDate,
            @WebParam(name = "toDate", targetNamespace = "") final Date toDate);

    @WebMethod
    public List<User> getUserBetweenLastDate(
            @WebParam(name = "fromDate", targetNamespace = "") final Date fromDate,
            @WebParam(name = "toDate", targetNamespace = "") final Date toDate);

    @WebMethod
    public List<User> getUserDtoBySearchBean(
            @WebParam(name = "searchBean", targetNamespace = "") final AuditLogSearchBean searchBean);

    /**
     * returns a paged List of all Supervisors in the system
     *
     * @param from
     *            - where to start
     * @param size
     *            - how many objects to return
     * @return a paged List of all Supervisors in the system
     */
    @WebMethod
    public List<User> getAllSuperiors(@WebParam(name = "from", targetNamespace = "") Integer from,
                                      @WebParam(name = "size", targetNamespace = "") Integer size);

    /**
     * Get the Number of all supervisors in the system
     *
     * @return the Number of all supervisors in the system
     */
    @WebMethod
    public int getAllSuperiorsCount();
    public List<User> getUserBetweenStartDate(Date fromDate, Date toDate);

//    @WebMethod
//    public Map<String, UserAttribute> getUserAttributesAsMap(@WebParam(name = "userId", targetNamespace = "") String userId);

    /**
     * Call to find supervisors by various criteria
     * @param supervisorSearchBean
     * @return a List Supervisor objects
     */
    @WebMethod
    public List<Supervisor> findSupervisors(@WebParam(name = "searchBean", targetNamespace = "") SupervisorSearchBean supervisorSearchBean);


}