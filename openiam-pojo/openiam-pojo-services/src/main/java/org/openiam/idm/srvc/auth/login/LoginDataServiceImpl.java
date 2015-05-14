package org.openiam.idm.srvc.auth.login;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.LoginDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.searchbeans.LoginSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginStatusEnum;
import org.openiam.idm.srvc.auth.login.lucene.LoginSearchDAO;
import org.openiam.idm.srvc.continfo.service.EmailAddressDAO;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.msg.dto.NotificationParam;
import org.openiam.idm.srvc.msg.dto.NotificationRequest;
import org.openiam.idm.srvc.msg.service.MailService;
import org.openiam.idm.srvc.msg.service.MailTemplateParameters;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.service.PasswordHistoryDAO;
import org.openiam.idm.srvc.pswd.service.PasswordPolicyProvider;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("loginManager")
public class LoginDataServiceImpl implements LoginDataService {

    @Autowired
    protected LoginDAO loginDao;

    @Autowired
    private LoginSearchDAO loginSearchDAO;

    @Autowired
    protected LoginAttributeDAO loginAttrDao;

    @Autowired
    protected UserDAO userDao;

    @Autowired
    private PolicyDataService policyDataService;

    @Autowired
    protected PasswordPolicyProvider passwordPolicyProvider;

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

    @Autowired
    private EmailAddressDAO emailAddressDao;

    @Autowired
    private MailService mailService;

    boolean encrypt = true; // default encryption setting
    private static final Log log = LogFactory
            .getLog(LoginDataServiceImpl.class);

    @Deprecated
    @Transactional
    @Override
    public void addLogin(LoginEntity login) {
        if (login == null) {
            throw new NullPointerException("Login is null");
        }

        if (login.getCreateDate() == null) {
            login.setCreateDate(new Date(System.currentTimeMillis()));
        }

        loginDao.save(login);
    }

    @Transactional(readOnly = true)
    @Override
    public LoginEntity getLoginByManagedSys(String login, String sysId) {
        if (login == null) {
            throw new NullPointerException("Login is null");
        }
        return loginDao.getRecord(login, sysId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> getLoginDetailsByManagedSys(String principalName,
                                                         String managedSysId) {
        if (principalName == null) {
            throw new NullPointerException("principalName is null");
        }
        if (managedSysId == null) {
            throw new NullPointerException("manaagedSysId is null");
        }
        return loginDao.findLoginByManagedSys(principalName, managedSysId);
    }

    @Override
    public String getPassword(String login, String sysId)
            throws Exception {

        LoginEntity lg = getLoginByManagedSys(login, sysId);
        if (lg != null && lg.getPassword() != null) {
            try {
                return keyManagementService.decrypt(lg.getUserId(), KeyName.password, lg.getPassword());

//                        cryptor.decrypt(keyManagementService.getUserKey(
//                        lg.getUserId(), KeyName.password.name()), lg
//                        .getPassword());
            } catch (EncryptionException e) {
                throw new IllegalArgumentException(
                        "Unable to decrypt the password. ");
            }
        }
        return null;
    }

    /**
     * Checks to see if a sourceLogin exists for a user - domain - managed system combination
     *
     * @param principal
     * @param sysId
     * @return
     */
    @Override
    public boolean loginExists(String principal, String sysId) {
        LoginEntity lg = getLoginByManagedSys(principal, sysId);
        if (lg == null) {
            return false;
        }
        return true;
    }

    /**
     * determines if the new passowrd is equal to the current password that is associated with this principal
     *
     * @param principal
     * @param sysId
     * @param newPassword
     * @return
     */
    @Override
    public boolean isPasswordEq(String principal, String sysId, String newPassword) throws Exception {
        if (principal == null) {
            throw new NullPointerException("principal is null");
        }
        if (sysId == null) {
            throw new NullPointerException("sysId is null");
        }
        if (newPassword == null) {
            return false;
        }
        String oldPassword = getPassword(principal, sysId);
        if (oldPassword != null) {
            if (oldPassword.equals(newPassword)) {
                return true;
            }
        }
        return false;
    }

    /* 
     * DO NOT MERGE INTO 4.0!!!!  Only for 3.3.1 to solve IDMAPPS-2735 
     * Use 4.0 code 
     */
    @Override
    @Transactional(readOnly = true)
    public Integer count(LoginSearchBean searchBean) {
    	if(searchBean == null || searchBean.isUseLucene()) {
    		return loginSearchDAO.count(searchBean);
    	} else {
    		return loginDao.count(searchBean);
    	}
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> findBeans(LoginSearchBean searchBean, Integer from, Integer size) {
        List<LoginEntity> retVal = null;
        if (StringUtils.isNotEmpty(searchBean.getKey())) {
            final LoginEntity entity = loginDao.findById(searchBean.getKey());
            if (entity != null) {
                retVal = new ArrayList<LoginEntity>();
                retVal.add(entity);
            }
        } else {
        	/* 
             * DO NOT MERGE INTO 4.0!!!!  Only for 3.3.1 to solve IDMAPPS-2735 
             * Use 4.0 code 
             */
        	if(searchBean.isUseLucene()) {
        		retVal = loginSearchDAO.find(from, size, null, searchBean);
        	} else {
        		retVal = loginDao.getByExampleNoLocalize(searchBean, from, size);
        	}
        }
        return retVal;
    }

    /**
     * Sets the password for a sourceLogin. The password needs to be encrypted externally. this allow for flexiblity in
     * supporting alternate approaches to encryption.
     *
     * @param login
     * @param sysId
     * @param password
     * @return
     */
    @Override
    @Transactional
    public boolean setPassword(String login, String sysId,
                               String password, boolean preventChangeCountIncrement) {
        LoginEntity lg = getLoginByManagedSys(login, sysId);
        if (lg == null) {
            return false;
        }

        Calendar cal = Calendar.getInstance();
        Calendar expCal = Calendar.getInstance();
        PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
        searchBean.setUserId(lg.getUserId());
        searchBean.setManagedSystemId(sysId);
        Policy plcy = passwordPolicyProvider.getPasswordPolicyByUser(searchBean);

        String pswdExpValue = getPolicyAttribute(plcy.getPolicyAttributes(), "PWD_EXPIRATION");
        //String changePswdOnReset = getPolicyAttribute( plcy.getPolicyAttributes(), "CHNG_PSWD_ON_RESET");
        String gracePeriod = getPolicyAttribute(plcy.getPolicyAttributes(), "PWD_EXP_GRACE");


        lg.setPassword(password);
        lg.setPwdChanged(cal.getTime());

        // increment the change password count
        if (!preventChangeCountIncrement) {
            if (lg.getPasswordChangeCount() == null) {
                lg.setPasswordChangeCount(new Integer(1));
            } else {
                lg.setPasswordChangeCount(lg.getPasswordChangeCount() + 1);
            }
        }
        // password has been changed - we dont need to force a change password
        // on the next sourceLogin
        lg.setResetPassword(0);
        lg.setAuthFailCount(0);
        lg.setIsLocked(0);

        // password has been changed the token is no longer valid
        lg.setPswdResetToken(null);
        lg.setPswdResetTokenExp(null);

        // calculate when the password will expire
        if (pswdExpValue != null && !pswdExpValue.isEmpty() && !"0".equals(pswdExpValue)) {
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

        final PasswordHistoryEntity hist = new PasswordHistoryEntity();
        hist.setPassword(password);
        hist.setLogin(lg);
        passwordHistoryDao.save(hist);
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(String login, String sysId, String password) {
        return this.resetPassword(login, sysId, password, true);
    }


    @Override
    @Transactional
    public boolean resetPassword(String login, String sysId, String password, boolean isActivate) {

        LoginEntity lg = getLoginByManagedSys(login, sysId);
        if (lg == null) {
            return false;
        }

        PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
        searchBean.setUserId(lg.getUserId());
        searchBean.setManagedSystemId(sysId);
        Policy plcy = passwordPolicyProvider.getPasswordPolicyByUser(searchBean);

        String changePswdOnReset = getPolicyAttribute(plcy.getPolicyAttributes(),
                "CHNG_PSWD_ON_RESET");
        boolean preservePassword = "false".equalsIgnoreCase(changePswdOnReset);

        String pswdExpValue = preservePassword
                ? getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXPIRATION")
                : getPolicyAttribute(plcy.getPolicyAttributes(),
                "NUM_DAYS_FORGET_PWD_TOKEN_VALID");

        String gracePeriod = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXP_GRACE");

        if (isActivate) {
            UserEntity user = userDao.findById(lg.getUserId());
            user.setSecondaryStatus(null);
            userDao.update(user);
        }

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

        return true;
    }

    @Override
    @Transactional
    public String encryptPassword(String userId, String password)
            throws Exception {
        if (password != null) {
            return keyManagementService.encrypt(userId, KeyName.password, password);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public String decryptPassword(String userId, String password)
            throws Exception {
        if (password != null) {
            return keyManagementService.decrypt(userId, KeyName.password, password);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
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

//    @Transactional(readOnly = true)
//    public List<LoginEntity> getLoginByDomain(String domain) {
//        if (domain == null) {
//            throw new NullPointerException("domain is null");
//        }
//        List<LoginEntity> loginList = loginDao.findLoginByDomain(domain);
//        if (loginList == null || loginList.size() == 0) {
//            return null;
//        }
//        return loginList;
//    }

    @Override
    @Transactional
    public void lockLogin(String principal, String sysId) {
        final LoginEntity lg = getLoginByManagedSys(principal, sysId);
        // get the user object
        UserEntity user = userDao.findById(lg.getUserId());

        lg.setIsLocked(1);
        user.setSecondaryStatus(UserStatusEnum.LOCKED);

        // update
        updateLogin(lg);
        userDao.update(user);

    }

    @Override
    @Transactional
    public void unLockLogin(String principal, String sysId) {
        LoginEntity lg = getLoginByManagedSys(principal, sysId);
        // get the user object
        UserEntity user = userDao.findById(lg.getUserId());

        lg.setIsLocked(0);
        user.setSecondaryStatus(null);

        // update
        updateLogin(lg);
        userDao.update(user);

    }

    @Override
    @Transactional
    public void removeLogin(String login, String managedSysId) {
        if (login == null) {
            throw new NullPointerException("Login is null");
        }

        LoginEntity loginEntity = loginDao.getRecord(login, managedSysId);
        loginDao.delete(loginEntity);
    }

    @Override
    @Transactional
    public int changeIdentityName(String newPrincipalName, String newPassword, String userId, String managedSysId) {
        if (userId == null) {
            throw new NullPointerException("userId is null");
        }
        if (managedSysId == null) {
            throw new NullPointerException("managedSysId is null");
        }

        return loginDao.changeIdentity(newPrincipalName, newPassword, userId,
                managedSysId);

    }

    @Override
    @Transactional
    public void updateLogin(LoginEntity login) {
        if (login == null) {
            throw new NullPointerException("Login is null");
        }
        login.setLastUpdate(new Date(System.currentTimeMillis()));

        log.debug("Updating Identity" + login);
        loginDao.merge(login);
    }


    private PolicyAttribute getPolicyAttributeDto(Set<PolicyAttribute> attr, String name) {
        assert name != null : "Name parameter is null";

        log.debug("Attribute Set size=" + attr.size());

        for (PolicyAttribute policyAtr : attr) {
            if (policyAtr.getName().equalsIgnoreCase(name)) {
                return policyAtr;
            }
        }
        return null;

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

    @Override
    @Transactional
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
//        final List<SecurityDomain> securityDomainList = secDomainService
//                .getAllDomainsWithExclude("IDM");
//        for (SecurityDomain secDom : securityDomainList) {
        String authnPolicy = sysConfiguration.getDefaultAuthPolicyId();
        if (authnPolicy != null) {
            Policy plcy = policyDataService.getPolicy(authnPolicy);
            String autoUnlockTime = getPolicyAttribute(
                    plcy.getPolicyAttributes(), "AUTO_UNLOCK_TIME");
            if (autoUnlockTime != null) {
                loginDao.bulkUnlock(status, Integer.parseInt(autoUnlockTime));
            }
        }

//        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> getLockedUserSince(Date lastExecTime) {
        return loginDao.findLockedUsers(lastExecTime);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> getInactiveUsers(int startDays, int endDays) {
        String primaryManagedSys = sysConfiguration.getDefaultManagedSysId();

        List<LoginEntity> loginList = loginDao.findInactiveUsers(startDays,
                endDays, primaryManagedSys);

        return loginList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> getUserNearPswdExpiration(int expDays) {
        List<LoginEntity> loginList = loginDao.findUserNearPswdExp(expDays);
        return loginList;
    }

    /**
     * Returns a list of Login objects which are nearing expiry depending on PWD_WARN password attribute
     * If attribute unset, default is assumed to be 5.
     *
     * @param
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> getUsersNearPswdExpiration() {
        List<LoginEntity> loginList = new ArrayList<>();
        Policy plcy = passwordPolicyProvider.getGlobalPasswordPolicy();
        PolicyAttribute pswdExpValue = getPolicyAttributeDto(plcy.getPolicyAttributes(),
                "PWD_EXP_WARN");
        String val1 = pswdExpValue.getValue1();
        String val2 = pswdExpValue.getValue2();
        try {
            if (StringUtils.isNotBlank(val1)) {
                loginList.addAll(loginDao.findUserNearPswdExp(Integer.parseInt(val1)));
            }
        } catch (Exception e) {
            log.warn("Can't get Logins with val1=" + val1);
        }
        try {
            if (StringUtils.isNotBlank(val2)) {
                loginList.addAll(loginDao.findUserNearPswdExp(Integer.parseInt(val2)));
            }
        } catch (Exception e) {
            log.warn("Can't get Logins with val2=" + val2);
        }

        return loginList;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> usersWithPasswordExpYesterday() {
        List<LoginEntity> loginList = loginDao.findUserPswdExpYesterday();
        return loginList;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginEntity getPrimaryIdentity(String userId) {

        return getByUserIdManagedSys(userId,
                sysConfiguration.getDefaultManagedSysId());
    }

    @Override
    @Transactional(readOnly = true)
    public LoginEntity getByUserIdManagedSys(String userId, String managedSysId) {

        List<LoginEntity> loginList = getLoginByUser(userId);
        if (loginList != null) {
            for (LoginEntity lg : loginList) {
                if (lg.getManagedSysId().equals(managedSysId)) {
                    return lg;
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public int bulkResetPasswordChangeCount() {
        return loginDao.bulkResetPasswordChangeCount();

    }

    @Override
    @Transactional(readOnly = true)
    public List<LoginEntity> getAllLoginByManagedSys(String managedSysId) {
        if (managedSysId == null) {
            throw new NullPointerException("managedSysId is null");
        }
        return loginDao.findAllLoginByManagedSys(managedSysId);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginEntity getPasswordResetToken(String token) {
        return loginDao.findByPasswordResetToken(token);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginEntity getLoginDetails(final String loginId) {
        return loginDao.findById(loginId);
    }

    @Override
    @Transactional
    public void deleteLogin(String loginId) {
        loginAttrDao.deleteByLoginId(loginId);
        final LoginEntity entity = loginDao.findById(loginId);
        if (entity != null) {
            loginDao.delete(entity);
        }
    }

    @Override
    @Transactional
    public void activateDeactivateLogin(String loginId, LoginStatusEnum status) {
        final LoginEntity entity = loginDao.findById(loginId);
        if (entity != null) {
            entity.setStatus(status);
            loginDao.update(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Login getLoginDTO(String loginId) {
        return loginDozerConverter.convertToDTO(loginDao.findById(loginId), true);
    }

    @Override
    @Transactional
    public void forgotUsername(String email) throws BasicDataServiceException {
        List<UserEntity> userList = userDao.getByEmail(email);

        if (CollectionUtils.isEmpty(userList))
            throw new BasicDataServiceException(ResponseCode.NO_USER_FOUND_FOR_GIVEN_EMAIL);

        List<String> userIds = new ArrayList<>();
        for (UserEntity user : userList) {
            userIds.add(user.getId());
        }

        List<LoginEntity> loginEntityList = loginDao.findByUserIds(userIds, sysConfiguration.getDefaultManagedSysId());

        Map<String, LoginEntity> loginMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(loginEntityList)) {
            for (LoginEntity login : loginEntityList) {
                if (login != null)
                    loginMap.put(login.getUserId(), login);
            }
        }

        for (UserEntity user : userList) {
            LoginEntity login = loginMap.get(user.getId());
            if (login != null)
                sendCredentialsToUser(email, login, user);
        }
    }

    private void sendCredentialsToUser(String email, LoginEntity loginEntity, UserEntity user) {
        final NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setTo(email);
        notificationRequest.setNotificationType("FORGOT_USER_NAME");
        notificationRequest.getParamList().add(new NotificationParam(MailTemplateParameters.IDENTITY.value(), loginEntity.getLogin()));
        notificationRequest.getParamList().add(
                new NotificationParam(MailTemplateParameters.FIRST_NAME.value(), user.getFirstName()));
        notificationRequest.getParamList().add(
                new NotificationParam(MailTemplateParameters.LAST_NAME.value(), user.getLastName()));
        mailService.sendNotification(notificationRequest);
    }
}
