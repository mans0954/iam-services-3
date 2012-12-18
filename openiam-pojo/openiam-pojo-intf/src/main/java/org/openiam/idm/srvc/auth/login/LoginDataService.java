package org.openiam.idm.srvc.auth.login;

import org.openiam.exception.AuthenticationException;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

import javax.jws.WebService;
import java.util.Date;
import java.util.List;

/**
 * Interface to manage the principal object. The principal object is largely used for service that use password
 * based authentication.
 *
 * @author Suneet Shah
 */

@WebService
public interface LoginDataService {

    public void addLogin(LoginEntity principal);

    public void updateLogin(LoginEntity principal);

    public void removeLogin(String domainId, String principal, String managedSysId);
    
    public LoginEntity getLogin(final String loginId);

    public LoginEntity getLogin(String domainId, String principal) throws AuthenticationException;

    public LoginEntity getLoginByManagedSys(String domainId, String principal, String sysId);

    /**
     * Returns a list of Login objects for the managed system specified by the sysId
     *
     * @param managedSysId
     * @return
     */
    public List<LoginEntity> getAllLoginByManagedSys(String managedSysId);

    /**
     * Returns the primary identity for this user
     *
     * @param userId
     * @return
     */
    public LoginEntity getPrimaryIdentity(String userId);

    /**
     * Returns the identity for this user  and managedSysId
     *
     * @param userId
     * @param managedSysId
     * @return
     */
    public LoginEntity getByUserIdManagedSys(String userId, String managedSysId);

    /**
     * Returns a decrypted password.
     *
     * @param domainId
     * @param principal
     * @param sysId
     * @return
     */
    public String getPassword(String domainId, String principal, String sysId) throws Exception;

    /**
     * determines if the new passowrd is equal to the current password that is associated with this principal
     *
     * @param domainId
     * @param principal
     * @param sysId
     * @param newPassword
     * @return
     */
    public boolean isPasswordEq(String domainId, String principal, String sysId, String newPassword) throws Exception;

    /**
     * Checks to see if a login exists for a user - domain - managed system combination
     *
     * @param domainId
     * @param principal
     * @param sysId
     * @return
     */
    public boolean loginExists(String domainId, String principal, String sysId);

    /**
     * Sets the password for a principal. The password needs to be encrypted externally. this allow for flexiblity in
     * supporting alternate approaches to encryption.
     *
     * @param domainId
     * @param principal
     * @param sysId
     * @param password
     * @return
     */
    public boolean setPassword(String domainId, String principal, String sysId, String password);

    /**
     * Sets a new password for the identity and updates the support attributes such as locked account flag.
     *
     * @param domainId
     * @param principal
     * @param sysId
     * @param password
     * @return
     */
    public boolean resetPassword(String domainId, String principal, String sysId, String password);


    /**
     * Encrypts the password string.
     *
     * @param password
     * @return
     */
    public String encryptPassword(String userId, String password ) throws EncryptionException;

    public String decryptPassword(String userId, String password ) throws EncryptionException;

    public List<LoginEntity> getLoginByUser(String userId);

    void lockLogin(String domainId, String principal, String sysId);

    void unLockLogin(String domainId, String principal, String sysId);


    /**
     * Unlocks all accounts that are in the specified status. Valid status codes include LOCKED AND ADMIN_LOCKED.
     *
     * @param status
     */
    public void bulkUnLock(UserStatusEnum status);

    int bulkResetPasswordChangeCount();

    List<LoginEntity> getLoginByDomain(String domain);

    public List<LoginEntity> getLockedUserSince(Date lastExecTime);

    public List<LoginEntity> getInactiveUsers(int startDays, int endDays);

    public List<LoginEntity> getUserNearPswdExpiration(int expDays);

    /**
     * List of users whose passworss are expiring today
     *
     * @return
     */
    public List<LoginEntity> usersWithPasswordExpYesterday();

    /**
     * Changes the identity of a user
     *
     * @param newPrincipalName
     * @param newPassword
     * @param userId
     * @param managedSysId
     * @return
     */
    public int changeIdentityName(String newPrincipalName, String newPassword,
                                  String userId, String managedSysId, String domainId);

    public List<LoginEntity> getLoginByManagedSys(String principalName, String managedSysId);

    LoginEntity getPasswordResetToken(String token);
}
