/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * Base case from which all LoginModule should be inherited.
 */
package org.openiam.idm.srvc.auth.spi;

import com.sun.jndi.ldap.LdapCtxFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentProducer;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.am.srvc.dto.ContentProvider;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.EncryptionException;
import org.openiam.exception.LogoutException;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.AuthStateId;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.service.AuthenticationModule;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.util.UserUtils;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import javax.resource.spi.IllegalStateException;

/**
 * @author suneet
 *
 */
public abstract class AbstractLoginModule implements AuthenticationModule {

    protected static final String DEFAULT_TOKEN_LIFE = "30";

    @Value("${KEYSTORE}")
    protected String keystore;

    @Value("${KEYSTORE_PSWD}")
    protected String keystorePasswd;

    @Autowired
    protected ManagedSysDAO managedSysDAO;

    @Autowired
    protected LoginDAO loginDAO;

    @Autowired
    protected UserDAO userDAO;

    @Autowired
    protected AuthProviderDao authProviderDAO;

    @Autowired
    @Qualifier("defaultSSOToken")
    protected SSOTokenModule defaultToken;

    @Autowired
    protected LoginDataService loginManager;

    @Autowired
    protected UserDataService userManager;

    @Autowired
    @Qualifier("cryptor")
    protected Cryptor cryptor;

    @Autowired
    protected ResourceDataService resourceService;

    @Autowired
    protected PasswordService passwordManager;

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Autowired
    private AuthStateDAO authStateDAO;

    @Autowired
    private URIPatternDao uriPatternDAO;

    @Transactional
    public void logout(final LogoutRequest request, final IdmAuditLog auditLog) throws Exception {
        final UserEntity userEntity = userDAO.findById(request.getUserId());
        final ManagedSysEntity managedSystem = getManagedSystem(request, auditLog);
        final LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(managedSystem.getId(), userEntity.getPrincipalList());
        auditLog.addTarget(request.getUserId(), AuditTarget.USER.value(), primaryIdentity.getLogin());

        final AuthStateEntity example = new AuthStateEntity();
        final AuthStateId id = new AuthStateId();
        id.setUserId(request.getUserId());
        example.setId(id);

        final List<AuthStateEntity> authStateList = authStateDAO.getByExample(example);

        if (CollectionUtils.isEmpty(authStateList)) {
            final String errorMessage = String.format("Cannot find AuthState object for User: %s", request.getUserId());
            auditLog.fail();
            auditLog.setFailureReason(errorMessage);
            log.error(errorMessage);
            throw new LogoutException(errorMessage);
        }

        for (final AuthStateEntity authSt : authStateList) {
            authSt.setAuthState(new BigDecimal(0));
            authSt.setToken("LOGOUT");
            authStateDAO.saveAuthState(authSt);
        }

        doLogout(request, auditLog);
    }

    @Transactional
    public final Subject login(final AuthenticationContext context) throws Exception {
        validate(context);
        LoginEntity login = getLogin(context);
        UserEntity user = getUser(context, login);
        if (user == null) {
            final AuthProviderEntity authProvider = getAuthProvider(context);
            /* check if the authentication provider supports just-in-time authentication */
            if (authProvider.getType().isSupportsJustInTimeAuthentication() && authProvider.isSupportsJustInTimeAuthentication()) {
                user = createUserForJustInTimeAuthentication(context);
                if (user == null) {

                }
                login = getLogin(context);
            }
        }
        return doLogin(context, user, login);
    }

    protected void validate(final AuthenticationContext context) throws Exception {

        final String principal = context.getPrincipal();
        final String password = context.getPassword();
        final IdmAuditLog newLoginEvent = context.getEvent();

        if (StringUtils.isBlank(principal)) {
            newLoginEvent.setFailureReason("Invalid Principal");
            throw new BasicDataServiceException(ResponseCode.INVALID_PRINCIPAL);
        }

        if (StringUtils.isBlank(password)) {
            newLoginEvent.setFailureReason("Invalid Password");
            throw new BasicDataServiceException(ResponseCode.INVALID_PASSWORD);
        }

        if (StringUtils.isBlank(context.getAuthProviderId())) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
        }

        final AuthProviderEntity authProvider = getAuthProvider(context);
        if (authProvider == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND);
        }
    }

    protected LoginEntity getLogin(final AuthenticationContext context) throws Exception {
        final IdmAuditLog newLoginEvent = context.getEvent();
        final String principal = context.getPrincipal();
        final ManagedSysEntity managedSystem = getManagedSystem(context);
        final LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSystem.getId());
        if (lg == null) {
            newLoginEvent.setFailureReason(String.format("Cannot find login for principal '%s' and managedSystem '%s'", principal, managedSystem.getId()));
            throw new BasicDataServiceException(ResponseCode.INVALID_LOGIN);
        }
        return lg;
    }

    protected UserEntity getUser(final AuthenticationContext context, final LoginEntity login) throws Exception {
        final IdmAuditLog newLoginEvent = context.getEvent();
        final String userId = login.getUserId();
        newLoginEvent.setRequestorUserId(userId);
        newLoginEvent.setTargetUser(userId, login.getLogin());
        final UserEntity user = userDAO.findById(userId);
        return user;
    }

    protected abstract Subject doLogin(final AuthenticationContext context, final UserEntity user, final LoginEntity login) throws Exception;

    protected void doLogout(final LogoutRequest request, final IdmAuditLog auditLog) throws Exception {

    }

    protected UserEntity createUserForJustInTimeAuthentication(final AuthenticationContext context) throws Exception {
        throw new IllegalStateException("createUserForJustInTimeAuthentication() should be overridden by the login module");
    }

    @Autowired
    protected KeyManagementService keyManagementService;
    private static final Log log = LogFactory.getLog(AbstractLoginModule.class);

    protected ManagedSysEntity getManagedSystem(final LogoutRequest request, final IdmAuditLog event) {
        final String patternId = request.getPatternId();
        ManagedSysEntity managedSystem = null;
        if (patternId == null) {
            managedSystem = managedSysDAO.findById(sysConfiguration.getDefaultManagedSysId());
        } else {
        	final URIPatternEntity pattern = uriPatternDAO.findById(patternId);
            final ContentProviderEntity contentProvider = (pattern != null) ? pattern.getContentProvider() : null;
            final AuthProviderEntity authProvider = (contentProvider != null) ? contentProvider.getAuthProvider() : null;
            managedSystem = (authProvider != null) ? authProvider.getManagedSystem() : null;
        }
        if (managedSystem == null) {
            final String warning = String.format("Content Provider %s -> Auth Provider does not have a managed system corresopnding to it.  Using default: %s", patternId, sysConfiguration.getDefaultManagedSysId());
            log.warn(warning);
            event.addWarning(warning);
            managedSystem = managedSysDAO.findById(sysConfiguration.getDefaultManagedSysId());
        }
        
        return managedSystem;
    }

    protected AuthProviderEntity getAuthProvider(final AuthenticationContext context) {
        return authProviderDAO.findById(context.getAuthProviderId());
    }

    protected ManagedSysEntity getManagedSystem(final AuthenticationContext context) {
        return getManagedSystem(context, getAuthProvider(context));
    }

    protected ManagedSysEntity getManagedSystem(final AuthenticationContext context, final AuthProviderEntity authProvider) {
        final IdmAuditLog event = context.getEvent();
        ManagedSysEntity managedSystem = (authProvider != null) ? authProvider.getManagedSystem() : null;
        if (managedSystem == null) {
            final String warning = String.format("Auth provider %s does not have a managed system corresopnding to it.  Using default: %s", authProvider.getId(), sysConfiguration.getDefaultManagedSysId());
            log.warn(warning);
            event.addWarning(warning);
            managedSystem = managedSysDAO.findById(sysConfiguration.getDefaultManagedSysId());
        }
        return managedSystem;
    }

    protected PolicyEntity getPolicy(final AuthenticationContext context) throws BasicDataServiceException {
        final AuthProviderEntity authProvider = getAuthProvider(context);
        final PolicyEntity policy = authProvider.getPasswordPolicy();
        final AuthProviderTypeEntity authProviderType = authProvider.getType();
        if (authProviderType.isPasswordPolicyRequired()) {
            if (policy == null) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
            }
        }
        return policy;
    }

    protected PolicyEntity getAuthPolicy(final AuthenticationContext context) throws BasicDataServiceException {
        final AuthProviderEntity authProvider = getAuthProvider(context);
        final PolicyEntity policy = authProvider.getAuthenticationPolicy();
        final AuthProviderTypeEntity authProviderType = authProvider.getType();
        if (authProviderType.isAuthnPolicyRequired()) {
            if (policy == null) {
                throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_SET);
            }
        }
        return policy;
    }

    protected String getPolicyAttribute(final PolicyEntity policy, final String attributeName) {
        final PolicyAttributeEntity entity = policy.getAttribute(attributeName);
        return (entity != null) ? entity.getValue1() : null;
    }

    protected String decryptPassword(String userId, String encPassword)
            throws Exception {
        if (encPassword != null) {
            try {
                return cryptor.decrypt(keyManagementService.getUserKey(userId,
                        KeyName.password.name()), encPassword);
            } catch (EncryptionException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Checks to see if the current date is after the start date for the user.
     *
     * @param user
     * @param curDate
     * @return
     */
    protected boolean pendingInitialStartDateCheck(UserEntity user, Date curDate) {
        if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
            if (user.getStartDate() != null
                    && curDate.before(user.getStartDate())) {
                log.debug("UserStatus= PENDING_START_DATE and user start date="
                        + user.getStartDate());
                return false;
            } else {
                log.debug("UserStatus= PENDING_START_DATE and user start date=null");
                return false;
            }
        }
        return true;
    }

    protected void checkSecondaryStatus(UserEntity user) throws BasicDataServiceException {
        if (user.getSecondaryStatus() != null) {
            if (user.getSecondaryStatus().equals(UserStatusEnum.LOCKED)
                    || user.getSecondaryStatus().equals(
                    UserStatusEnum.LOCKED_ADMIN)) {
                log.debug("User is locked. throw exception.");
                throw new BasicDataServiceException(
                        ResponseCode.RESULT_LOGIN_LOCKED);
            }
            if (user.getSecondaryStatus().equals(UserStatusEnum.DISABLED)) {
                throw new BasicDataServiceException(
                		ResponseCode.RESULT_LOGIN_DISABLED);
            }
        }

    }

    protected void setResultCode(LoginEntity lg, Subject sub, Date curDate, PolicyEntity pwdPolicy) throws BasicDataServiceException {
        if (lg.getFirstTimeLogin() == 1) {
            sub.setResultCode(ResponseCode.RESULT_SUCCESS_FIRST_TIME);
        } else if (lg.getPwdExp() != null) {
            if ((curDate.after(lg.getPwdExp()) && curDate.before(lg.getGracePeriod()))) {
                // check for password expiration, but successful login

            	sub.setResultCode(ResponseCode.RESULT_SUCCESS_PASSWORD_EXP);
            	//throw new AuthenticationException(AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP);
            }
        } else {
            if (pwdPolicy != null) {
                Integer pwdExp = 0;
                try {
                    pwdExp = Integer.parseInt(pwdPolicy.getAttribute("PWD_EXPIRATION").getValue1());
                } catch (Exception ex) {
                    log.warn("Cannot read value of PWD_EXPIRATION attribute. User 0 as default");
                }
                if(pwdExp>0){
                    throw new BasicDataServiceException(ResponseCode.RESULT_PASSWORD_EXPIRED);
                }
            }
            sub.setResultCode(ResponseCode.RESULT_SUCCESS);
        }

    }

    protected Integer setDaysToPassworExpiration(LoginEntity lg, Date curDate, Subject sub, PolicyEntity pwdPolicy) {
        if (pwdPolicy == null) {
            return null;
        }

        final PolicyAttributeEntity attribute = pwdPolicy.getAttribute("PWD_EXPIRATION");
        if (attribute == null) {
            return null;
        }

        if (StringUtils.isBlank(attribute.getValue1())) {
            return null;
        }
        if (lg.getPwdExp() == null) {
            return -1;
        }

        long DAY = 86400000L;

        // lg.getPwdExp is the expiration date/time

        long diffInMilliseconds = lg.getPwdExp().getTime() - curDate.getTime();
        long diffInDays = diffInMilliseconds / DAY;

        // treat anything that is less than a day, as zero
        if (diffInDays < 1) {
            return 0;
        }

        return (int) diffInDays;

    }

    public LdapContext connect(String userName, String password, ManagedSysEntity managedSys) throws NamingException {

        if (keystore != null && !keystore.isEmpty())  {
            System.setProperty("javax.net.ssl.trustStore", keystore);
            System.setProperty("javax.net.ssl.keyStorePassword", keystorePasswd);
        }

        if (managedSys == null) {
            log.debug("ManagedSys is null");
            return null;
        }

        String hostUrl = managedSys.getHostUrl();
        if (managedSys.getPort() > 0 ) {
            hostUrl = hostUrl + ":" + String.valueOf(managedSys.getPort());
        }

        log.debug("connect: Connecting to target system: " + managedSys.getId() );
        log.debug("connect: Managed System object : " + managedSys);

        log.info(" directory login = " + managedSys.getUserId() );
        log.info(" directory login passwrd= *****" );
        log.info(" javax.net.ssl.trustStore= " + System.getProperty("javax.net.ssl.trustStore"));
        log.info(" javax.net.ssl.keyStorePassword= " + System.getProperty("javax.net.ssl.keyStorePassword"));

        Hashtable<String, String> envDC = new Hashtable();
        envDC.put(Context.PROVIDER_URL, hostUrl);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple" ); // simple
        envDC.put(Context.SECURITY_PRINCIPAL, userName);
        envDC.put(Context.SECURITY_CREDENTIALS, password);

        // Connections Pool configuration
        envDC.put("com.sun.jndi.ldap.connect.pool", "true");
        // Here is an example of a command line that sets the maximum pool size to 20, the preferred pool size to 10, and the idle timeout to 5 minutes for pooled connections.
        envDC.put("com.sun.jndi.ldap.connect.pool.prefsize", "10");
        envDC.put("com.sun.jndi.ldap.connect.pool.maxsize", "20");
        envDC.put("com.sun.jndi.ldap.connect.pool.timeout", "300000");

        LdapContext ldapContext = null;
        try {
            ldapContext = (LdapContext) new LdapCtxFactory().getInitialContext((Hashtable) envDC);

        } catch (CommunicationException ce) {
            log.error("Throw communication exception.", ce);

        } catch(NamingException ne) {
            log.error(ne.toString(), ne);

        } catch (Throwable e) {
            log.error(e.toString(), e);
        }

        return ldapContext;
    }

    /**
     * If the password has expired, but its before the grace period then its a good login
     * If the password has expired and after the grace period, then its an exception.
     * You should also set the days to expiration
     * @param lg
     * @return
     */
    protected ResponseCode passwordExpired(final LoginEntity lg, final Date curDate, final PolicyEntity policy) {
        log.debug("passwordExpired Called.");
        log.debug("- Password Exp =" + lg.getPwdExp());
        log.debug("- Password Grace Period =" + lg.getGracePeriod());

        if (lg.getGracePeriod() == null) {
            // set an early date
            Date gracePeriodDate = getGracePeriodDate(lg, curDate, policy);
            log.debug("Calculated the gracePeriod Date to be: "
                    + gracePeriodDate);

            if (gracePeriodDate == null) {
                lg.setGracePeriod(new Date(0));
            } else {
                lg.setGracePeriod(gracePeriodDate);
            }
        }
        if (lg.getPwdExp() != null) {
            if (curDate.after(lg.getPwdExp())
                    && curDate.after(lg.getGracePeriod())) {
                // check for password expiration, but successful login
                return ResponseCode.RESULT_PASSWORD_EXPIRED;
            }
            if ((curDate.after(lg.getPwdExp()) && curDate.before(lg
                    .getGracePeriod()))) {
                // check for password expiration, but successful login
                return ResponseCode.RESULT_SUCCESS_PASSWORD_EXP;
            }
        }
        return ResponseCode.RESULT_SUCCESS_PASSWORD_EXP;
    }

    protected Date getGracePeriodDate(LoginEntity lg, Date curDate, final PolicyEntity policy) {

        final Date pwdExpDate = lg.getPwdExp();

        if (pwdExpDate == null) {
            return null;
        }

        final String gracePeriod = getPolicyAttribute(policy, "PWD_EXP_GRACE");
        final Calendar cal = Calendar.getInstance();
        cal.setTime(pwdExpDate);
        if(StringUtils.isNotEmpty(gracePeriod)) {
            cal.add(Calendar.DATE, Integer.parseInt(gracePeriod));
            log.debug(String.format("Calculated grace period date=%s",cal.getTime()));
            return cal.getTime();
        }
        return null;

    }

    protected SSOToken token(final String userId, final String tokenType, final String tokenLife, final Map tokenParam) throws Exception {

        log.debug("Generating Security Token");

        tokenParam.put("USER_ID", userId);

        SSOTokenModule tkModule = SSOTokenFactory.createModule(tokenType);
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt(tokenLife));

        return tkModule.createToken(tokenParam);
    }
}
