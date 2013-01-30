package org.openiam.idm.srvc.auth.ws;

import org.openiam.base.ws.Response;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Date;
import java.util.List;

/**
 * Web Service Interface to manage the principals that are associated with a user. The login object is largely used for service that use password
 * based authentication.
 *
 * @author Suneet Shah
 * @version 2.1
 */

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/auth/service", name = "LoginDataWebService")
public interface LoginDataWebService {

    /**
     * This method adds a login to the user specified in the Login object.. <br>
     * For example:
     * <p/>
     * <code>
     * Login lv = new Login();<br>
     * lv.setLogin(login);<br>
     * lv.setPassword(password);<br>
     * lv.setService(service);<br>
     * lv.setNewUser(true);<br>
     * loginDataService.addLogin(lv);<br>
     * </code>
     *
     * @param principal
     */
    @WebMethod
    public LoginResponse addLogin(
            @WebParam(name = "principal", targetNamespace = "")
            Login principal);

    @WebMethod
    public Response updateLogin(
            @WebParam(name = "principal", targetNamespace = "")
            Login principal);

    @WebMethod
    public Response removeLogin(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId);

    @WebMethod
    public LoginResponse getLogin(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal) throws AuthenticationException;

    @WebMethod
    public LoginResponse getLoginByManagedSys(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String pricipal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String sysId);

    @WebMethod
    public LoginResponse getPrincipalByManagedSys(
            @WebParam(name = "principal", targetNamespace = "")
            String principalName,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId);

    @WebMethod
    public LoginResponse getPrimaryIdentity(
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    @WebMethod
    public List<Login> findBeans(
            @WebParam(name = "searchBean", targetNamespace = "") LoginSearchBean searchBean, Integer from, Integer size);

    @WebMethod
    public Integer count(@WebParam(name = "searchBean", targetNamespace = "") LoginSearchBean searchBean);

    /**
     * Returns a decrypted password.
     *
     * @param domainId
     * @param principal
     * @param managedSysId
     * @return
     */
    @WebMethod
    public Response getPassword(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId) throws Exception;

    /**
     * Sets the password for a login. The password needs to be encrypted externally. this allow for flexiblity in
     * supporting alternate approaches to encryption.
     *
     * @param domainId
     * @param principal
     * @param managedSysId
     * @param password
     * @return
     */
    @WebMethod
    public Response setPassword(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId,
            @WebParam(name = "password", targetNamespace = "")
            String password);

    /**
     * Sets a new password for the identity and updates the support attributes such as locked account flag.
     *
     * @param domainId
     * @param principal
     * @param managedSysId
     * @param password
     * @return
     */
    @WebMethod
    public Response resetPassword(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId,
            @WebParam(name = "password", targetNamespace = "")
            String password);

    /**
     * Encrypts the password string.
     *
     * @param password
     * @return
     */
    @WebMethod
    public Response encryptPassword( @WebParam(name = "userId", targetNamespace = "")String userId,
            @WebParam(name = "password", targetNamespace = "")
            String password);

    @WebMethod
    public Response decryptPassword( @WebParam(name = "userId", targetNamespace = "")String userId,
            @WebParam(name = "password", targetNamespace = "")
            String password);

    @WebMethod
    public LoginListResponse getLoginByUser(
            @WebParam(name = "userId", targetNamespace = "")
            String userId);

    @WebMethod
    Response lockLogin(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId);

    @WebMethod
    Response unLockLogin(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId);


    @WebMethod
    Response bulkUnLock(
            @WebParam(name = "status", targetNamespace = "")
            UserStatusEnum status);

    @WebMethod
    Response bulkResetPasswordChangeCount();

    @WebMethod
    LoginListResponse getLoginByDomain(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId);

    /**
     * determines if the new passowrd is equal to the current password that is associated with this principal
     *
     * @param domainId
     * @param principal
     * @param managedSysId
     * @param newPassword
     * @return
     */
    @WebMethod
    Response isPasswordEq(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId,
            @WebParam(name = "newPassword", targetNamespace = "")
            String newPassword) throws Exception;

    /**
     * Checks to see if a login exists for a user - domain - managed system combination
     *
     * @param domainId
     * @param principal
     * @param managedSysId
     * @return
     */
    @WebMethod
    Response loginExists(
            @WebParam(name = "domainId", targetNamespace = "")
            String domainId,
            @WebParam(name = "principal", targetNamespace = "")
            String principal,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId);

    @WebMethod
    public LoginListResponse getLockedUserSince(
            @WebParam(name = "lastExecTime", targetNamespace = "")
            Date lastExecTime);

    /**
     * Return the list of users that have not logged in certain number of days.
     *
     * @param startDays
     * @param endDays
     * @return
     */
    @WebMethod
    public LoginListResponse getInactiveUsers(
            @WebParam(name = "startDays", targetNamespace = "")
            int startDays,
            @WebParam(name = "endDays", targetNamespace = "")
            int endDays);

    @WebMethod
    public LoginListResponse getUserNearPswdExpiration(
            @WebParam(name = "expDays", targetNamespace = "")
            int expDays);

    /**
     * Returns a list of Login objects for the managed system specified by the sysId
     *
     * @param managedSysId
     * @return
     */
    @WebMethod
    LoginListResponse getAllLoginByManagedSys(
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId);

    /**
     * Changes the identity of a user
     *
     * @param newPrincipalName
     * @param newPassword
     * @param userId
     * @param managedSysId
     * @return
     */
    @WebMethod
    public Response changeIdentityName(
            @WebParam(name = "newPrincipalName", targetNamespace = "")
            String newPrincipalName,
            @WebParam(name = "newPassword", targetNamespace = "")
            String newPassword,
            @WebParam(name = "userId", targetNamespace = "")
            String userId,
            @WebParam(name = "managedSysId", targetNamespace = "")
            String managedSysId,
            @WebParam(name = "domainId", targetNamespace = "")
            String domain);

}
