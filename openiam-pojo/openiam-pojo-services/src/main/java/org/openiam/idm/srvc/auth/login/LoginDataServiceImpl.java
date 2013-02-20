package org.openiam.idm.srvc.auth.login;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.lucene.LoginSearchDAO;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;
import org.openiam.idm.srvc.secdomain.dto.SecurityDomain;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDAO;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service("loginManager")
@WebService(endpointInterface = "org.openiam.idm.srvc.auth.login.LoginDataService", targetNamespace = "urn:idm.openiam.org/srvc/auth/service", serviceName = "LoginWebService")
public class LoginDataServiceImpl implements LoginDataService {

	@Autowired
    protected LoginDAO loginDao;

    @Autowired
    private LoginSearchDAO loginSearchDAO;
    
	@Autowired
	protected LoginAttributeDAO loginAttrDao;
	
	@Autowired
	private SecurityDomainDAO securityDomainDAO;
    
	@Autowired
	protected SecurityDomainDataService secDomainService;
    
	@Autowired
	protected UserDAO userDao;
    
	@Autowired
    private PolicyDataService policyDataService;

	@Autowired
    protected PasswordService passwordManager;
    
	@Autowired
	protected PasswordHistoryDAO passwordHistoryDao;
    
	@Autowired
	protected SysConfiguration sysConfiguration;

	@Autowired
    protected Cryptor cryptor;
    
	@Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    protected LoginDozerConverter loginDozerConverter;

    boolean encrypt = true; // default encryption setting
    private static final Log log = LogFactory
            .getLog(LoginDataServiceImpl.class);

    public void addLogin(LoginEntity login) {
        if (login == null) {
            throw new NullPointerException("Login is null");
        }

        if (login.getCreateDate() == null) {
            login.setCreateDate(new Date(System.currentTimeMillis()));
        }

        loginDao.save(login);
    }

    public LoginEntity getLogin(String secDomainId, String login)
            throws AuthenticationException {
        if (secDomainId == null) {
            throw new NullPointerException("service is null");
        }

        if (login == null) {
            throw new NullPointerException("Login is null");
        }

        SecurityDomainEntity secDomain = securityDomainDAO.findById(secDomainId);
        if (secDomain == null) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_DOMAIN);
        }

        return loginDao.getRecord(login, secDomain.getAuthSysId(), secDomainId);
    }

    public LoginEntity getLoginByManagedSys(String domainId, String login,
            String sysId) {
        if (domainId == null) {
            throw new NullPointerException("domainId is null");
        }
        if (login == null) {
            throw new NullPointerException("Login is null");
        }

        return loginDao.getRecord(login, sysId, domainId);
    }

    public List<LoginEntity> getLoginByManagedSys(String principalName,
            String managedSysId) {
        if (principalName == null) {
            throw new NullPointerException("principalName is null");
        }
        if (managedSysId == null) {
            throw new NullPointerException("manaagedSysId is null");
        }
        return loginDao.findLoginByManagedSys(principalName, managedSysId);
    }

    public String getPassword(String domainId, String login, String sysId)
            throws Exception {

    	LoginEntity lg = getLoginByManagedSys(domainId, login, sysId);
        if (lg != null && lg.getPassword() != null) {
            try {
                return cryptor.decrypt(keyManagementService.getUserKey(
                        lg.getUserId(), KeyName.password.name()), lg
                        .getPassword());
            } catch (EncryptionException e) {
                throw new IllegalArgumentException(
                        "Unable to decrypt the password. ");
            }

        }

        return null;
    }

    /**
     * Checks to see if a login exists for a user - domain - managed system combination
     * @param domainId
     * @param principal
     * @param sysId
     * @return
     */
    public boolean loginExists(String domainId, String principal, String sysId) {
    	LoginEntity lg = getLoginByManagedSys(domainId, principal, sysId);
        if (lg == null) {
            return false;
        }
        return true;
    }

    /**
     * determines if the new passowrd is equal to the current password that is associated with this principal
     * @param domainId
     * @param principal
     * @param sysId
     * @param newPassword
     * @return
     */
    public boolean isPasswordEq(String domainId, String principal,
            String sysId, String newPassword) throws Exception {
        if (domainId == null) {
            throw new NullPointerException("domainId is null");
        }
        if (principal == null) {
            throw new NullPointerException("principal is null");
        }
        if (sysId == null) {
            throw new NullPointerException("sysId is null");
        }
        if (newPassword == null) {
            return false;
        }
        String oldPassword = getPassword(domainId, principal, sysId);
        if (oldPassword != null) {
            if (oldPassword.equals(newPassword)) {
                return true;
            }
        }
        return false;
    }

    public Integer count(LoginSearchBean searchBean){
        return loginSearchDAO.count(searchBean);
    }

    public List<LoginEntity> findBeans(LoginSearchBean searchBean, Integer from, Integer size){
       return loginSearchDAO.find(from, size, null, searchBean);
    }

    /**
     * Sets the password for a login. The password needs to be encrypted externally. this allow for flexiblity in 
     * supporting alternate approaches to encryption.
     * @param domainId
     * @param login
     * @param sysId
     * @param password
     * @return
     */
    public boolean setPassword(String domainId, String login, String sysId,
            String password) {
        Calendar cal = Calendar.getInstance();
        Calendar expCal = Calendar.getInstance();

        // SecurityDomain securityDomain =
        // secDomainService.getSecurityDomain(domainId);
        // Policy plcy =
        // policyDao.findById(securityDomain.getPasswordPolicyId());
        Policy plcy = passwordManager.getPasswordPolicy(domainId, login, sysId);

        String pswdExpValue = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXPIRATION");
        String changePswdOnReset = getPolicyAttribute(
                plcy.getPolicyAttributes(), "CHNG_PSWD_ON_RESET");
        String gracePeriod = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXP_GRACE");

        LoginEntity lg = getLoginByManagedSys(domainId, login, sysId);
        lg.setPassword(password);
        lg.setPwdChanged(cal.getTime());

        // increment the change password count
        if (lg.getPasswordChangeCount() == null) {
            lg.setPasswordChangeCount(new Integer(1));
        } else {
            lg.setPasswordChangeCount(lg.getPasswordChangeCount() + 1);
        }
        // password has been changed - we dont need to force a change password
        // on the next login
        lg.setResetPassword(0);
        lg.setAuthFailCount(0);
        lg.setIsLocked(0);

        // password has been changed the token is no longer valid
        lg.setPswdResetToken(null);
        lg.setPswdResetTokenExp(null);

        // calculate when the password will expire
        if (pswdExpValue != null && !pswdExpValue.isEmpty()) {
            cal.add(Calendar.DATE, Integer.parseInt(pswdExpValue));
            expCal.add(Calendar.DATE, Integer.parseInt(pswdExpValue));
            lg.setPwdExp(expCal.getTime());

            // calc the grace period if there is a policy for it
            if (gracePeriod != null && !gracePeriod.isEmpty()) {
                cal.add(Calendar.DATE, Integer.parseInt(gracePeriod));
                lg.setGracePeriod(cal.getTime());
            }
        }

        loginDao.update(lg);

        if (lg != null) {
        	final PasswordHistoryEntity hist = new PasswordHistoryEntity();
        	hist.setPassword(password);
        	hist.setLoginId(lg.getLoginId());
            passwordHistoryDao.save(hist);
            return true;
        }
        return false;

    }

    public boolean resetPassword(String domainId, String login, String sysId,
            String password) {

        // SecurityDomain securityDomain =
        // secDomainService.getSecurityDomain(domainId);
        // Policy plcy =
        // policyDao.findById(securityDomain.getPasswordPolicyId());

        Policy plcy = passwordManager.getPasswordPolicy(domainId, login, sysId);

        String pswdExpValue = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXPIRATION_ON_RESET");
        // String changePswdOnReset = getPolicyAttribute(
        // plcy.getPolicyAttributes(), "CHNG_PSWD_ON_RESET");
        String gracePeriod = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXP_GRACE");

        LoginEntity lg = getLoginByManagedSys(domainId, login, sysId);
        UserEntity user = userDao.findById(lg.getUserId());
        user.setSecondaryStatus(null);
        userDao.update(user);

        lg.setPassword(password);
        // set the other properties of a password based on policy
        Calendar cal = Calendar.getInstance();

        // reset the authn related flags
        lg.setAuthFailCount(0);
        lg.setIsLocked(0);

        // reset the password change count
        lg.setPasswordChangeCount(0);
        lg.setResetPassword(1);

        lg.setPwdChanged(cal.getTime());

        if (pswdExpValue != null && !pswdExpValue.isEmpty()) {
            cal.add(Calendar.DATE, Integer.parseInt(pswdExpValue));
            lg.setPwdExp(cal.getTime());
        }
        // calc the grace period if there is a policy for it
        if (gracePeriod != null && !gracePeriod.isEmpty()) {
            cal.add(Calendar.DATE, Integer.parseInt(gracePeriod));
            lg.setGracePeriod(cal.getTime());
        }

        loginDao.update(lg);

        if (lg != null) {
            return true;
        }
        return false;
    }

    public String encryptPassword(String userId, String password)
            throws EncryptionException {
        if (password != null) {
            return cryptor.encrypt(
                    keyManagementService.getUserKey(userId,
                            KeyName.password.name()), password);
        }
        return null;
    }

    public String decryptPassword(String userId, String password)
            throws EncryptionException {
        if (password != null) {
            return cryptor.decrypt(
                    keyManagementService.getUserKey(userId,
                            KeyName.password.name()), password);
        }
        return null;
    }

    public List<LoginEntity> getLoginByUser(String userId) {

        log.info("LoginDataService: getLoginByUser userId=" + userId);

        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        List<LoginEntity> loginList = loginDao.findUser(userId);
        if (loginList == null || loginList.size() == 0) {
            return null;
        }
        
        return loginList;
    }

    public List<LoginEntity> getLoginByDomain(String domain) {
        if (domain == null) {
            throw new NullPointerException("domain is null");
        }
        List<LoginEntity> loginList = loginDao.findLoginByDomain(domain);
        if (loginList == null || loginList.size() == 0) {
            return null;
        }
        return loginList;
    }

    public void lockLogin(String domainId, String principal, String sysId) {
        final LoginEntity lg = getLoginByManagedSys(domainId, principal, sysId);
        // get the user object
        UserEntity user = userDao.findById(lg.getUserId());

        lg.setIsLocked(1);
        user.setSecondaryStatus(UserStatusEnum.LOCKED);

        // update
        updateLogin(lg);
        userDao.update(user);

    }

    public void unLockLogin(String domainId, String principal, String sysId) {
    	LoginEntity lg = getLoginByManagedSys(domainId, principal, sysId);
        // get the user object
        UserEntity user = userDao.findById(lg.getUserId());

        lg.setIsLocked(0);
        user.setSecondaryStatus(null);

        // update
        updateLogin(lg);
        userDao.update(user);

    }

    public void removeLogin(String serviceId, String login, String managedSysId) {
        if (login == null) {
            throw new NullPointerException("Login is null");
        }

        LoginEntity loginEntity = loginDao.getRecord(login, managedSysId, serviceId);
        loginDao.delete(loginEntity);
    }

    public int changeIdentityName(String newPrincipalName, String newPassword,
            String userId, String managedSysId, String domainId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        if (managedSysId == null) {
            throw new NullPointerException("managedSysId is null");
        }

        return loginDao.changeIdentity(newPrincipalName, newPassword, userId,
                managedSysId);

    }

    public void updateLogin(LoginEntity login) {
        if (login == null) {
            throw new NullPointerException("Login is null");
        }

        log.debug("Updating Identity" + login);
        loginDao.update(login);
    }

    private String getPolicyAttribute(Set<PolicyAttribute> attr, String name) {
        assert name != null : "Name parameter is null";

        log.debug("Attribute Set size=" + attr.size());

        for (PolicyAttribute policyAtr : attr) {
            if (policyAtr.getName().equalsIgnoreCase(name)) {
                return policyAtr.getValue1();
            }
        }
        return null;

    }

    public void bulkUnLock(UserStatusEnum status) {
        log.info("bulk unlock called.");
        if (status == null) {
            throw new NullPointerException("status is null");
        }
        if (status != UserStatusEnum.LOCKED
                && status != UserStatusEnum.LOCKED_ADMIN) {
            throw new IllegalArgumentException("Invalid status value");
        }
        // since each security domain may have different authn policies, loop
        // through each domain
        final List<SecurityDomain> securityDomainList = secDomainService
                .getAllDomainsWithExclude("IDM");
        for (SecurityDomain secDom : securityDomainList) {
            String authnPolicy = secDom.getAuthnPolicyId();
            if (authnPolicy != null) {
                Policy plcy = policyDataService.getPolicy(authnPolicy);
                String autoUnlockTime = getPolicyAttribute(
                        plcy.getPolicyAttributes(), "AUTO_UNLOCK_TIME");
                if (autoUnlockTime != null) {
                    loginDao.bulkUnlock(secDom.getDomainId(), status,
                            Integer.parseInt(autoUnlockTime));
                }
            }

        }

    }

    public List<LoginEntity> getLockedUserSince(Date lastExecTime) {
        return loginDao.findLockedUsers(lastExecTime);
    }

    public List<LoginEntity> getInactiveUsers(int startDays, int endDays) {
        String primaryManagedSys = sysConfiguration.getDefaultManagedSysId();

        List<LoginEntity> loginList = loginDao.findInactiveUsers(startDays,
                endDays, primaryManagedSys);

        return loginList;
    }

    public List<LoginEntity> getUserNearPswdExpiration(int expDays) {
        List<LoginEntity> loginList = loginDao.findUserNearPswdExp(expDays);
        return loginList;
    }

    public List<LoginEntity> usersWithPasswordExpYesterday() {
        List<LoginEntity> loginList = loginDao.findUserPswdExpYesterday();
        return loginList;
    }

    public LoginEntity getPrimaryIdentity(String userId) {
       
        return getByUserIdManagedSys(userId,
                sysConfiguration.getDefaultManagedSysId());
    }

    @Override
    public LoginEntity getByUserIdManagedSys(String userId, String managedSysId) {
        List<LoginEntity> loginList = getLoginByUser(userId);
        if (loginList != null) {
            for (LoginEntity lg : loginList) {
            	if(lg.getManagedSysId().equals(managedSysId)) {
                    return lg;
                }
            }
        }
        return null;
    }

    public int bulkResetPasswordChangeCount() {
        return loginDao.bulkResetPasswordChangeCount();

    }

    public List<LoginEntity> getAllLoginByManagedSys(String managedSysId) {
        if (managedSysId == null) {
            throw new NullPointerException("managedSysId is null");
        }
        return loginDao.findAllLoginByManagedSys(managedSysId);
    }

    public LoginEntity getPasswordResetToken(String token) {
        return loginDao.findByPasswordResetToken(token);
    }

	@Override
	public LoginEntity getLogin(final String loginId) {
		return loginDao.findById(loginId);
	}
}
