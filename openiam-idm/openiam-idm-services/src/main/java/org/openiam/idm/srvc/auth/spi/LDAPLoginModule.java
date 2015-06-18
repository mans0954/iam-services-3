/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the Lesser GNU General
 * Public License version 3 as published by the Free Software Foundation.
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
 *
 */
package org.openiam.idm.srvc.auth.spi;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.ResourceSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.ProvisionService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.*;

/**
 * LDAPLoginModule provides basic password based authentication using an LDAP directory.
 *
 */
@Component("ldapLoginModule")
public class LDAPLoginModule extends AbstractLoginModule {

    private static final Log log = LogFactory.getLog(LDAPLoginModule.class);

    @Autowired
    @Qualifier("defaultProvision")
    protected ProvisionService provisionService;

    @Autowired
    @Qualifier("managedSysService")
    protected ManagedSystemWebService managedSystemWebService;

	@Override
	protected void validate(AuthenticationContext context) throws Exception {
		super.validate(context);

        //TODO: add default validation against OpenIAM or use custom validation against OpenLDAP
		
	}

	@Override
	protected Subject doLogin(AuthenticationContext context, UserEntity user,
			LoginEntity login) throws Exception {

        // current date
        final Date curDate = new Date();
        final String clientIP = context.getClientIP();
        final String principal = context.getPrincipal();
        final String password = context.getPassword();

        if (user != null && user.getStatus() != null) {
            if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
                if (!pendingInitialStartDateCheck(user, curDate)) {
                    throw new BasicDataServiceException(ResponseCode.RESULT_INVALID_USER_STATUS);
                }
            }
            if (!user.getStatus().equals(UserStatusEnum.ACTIVE)
                    && !user.getStatus().equals(UserStatusEnum.PENDING_INITIAL_LOGIN)) {
                throw new BasicDataServiceException(
                        ResponseCode.RESULT_INVALID_USER_STATUS);
            }
            // check the secondary status
            checkSecondaryStatus(user);

        }

        // Find user in target system
        List<ExtensibleAttribute> attrs = new ArrayList<>();
        attrs.add(new ExtensibleAttribute("distinguishedName", null));
        LookupUserResponse resp = provisionService.getTargetSystemUser(principal, login.getManagedSysId(), attrs);
        log.debug("Lookup for user identity =" + principal + " in target system = " + login.getManagedSysId() + ". Result = " + resp.getStatus() + ", " + resp.getErrorCode());

        if (resp.isFailure()) {
            throw new BasicDataServiceException(ResponseCode.INVALID_PRINCIPAL);
        }

        String distinguishedName = null;
        if (CollectionUtils.isNotEmpty(resp.getAttrList())) {
            distinguishedName = resp.getAttrList().get(0).getValue();
        }
        if (StringUtils.isEmpty(distinguishedName)) {
            throw new BasicDataServiceException(ResponseCode.INVALID_PRINCIPAL);
        }

        final PolicyEntity policy = getAuthPolicy(context);
        final String tokenType = getPolicyAttribute(policy, "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(policy, "TOKEN_LIFE");
        final String tokenIssuer = getPolicyAttribute(policy, "TOKEN_ISSUER");

        final IdmAuditLog newLoginEvent = context.getEvent();
        if(StringUtils.isBlank(tokenType)) {
            final String warning = String.format("Property %s not valid for policy key %s for policy %s", tokenType, "TOKEN_TYPE", policy);
            newLoginEvent.addWarning(warning);
            log.warn(warning);
        }

        if(StringUtils.isBlank(tokenType)) {
            final String warning = String.format("Property %s not valid for policy key %s for policy %s", tokenType, "TOKEN_TYPE", policy);
            newLoginEvent.addWarning(warning);
            log.warn(warning);
        }

        if(StringUtils.isBlank(tokenLife) || !NumberUtils.isNumber(tokenLife)) {
            final String warning = String.format("Property %s not valid for policy key %s for policy %s.  Defaulting to %s", tokenLife, "TOKEN_LIFE", policy, DEFAULT_TOKEN_LIFE);
            newLoginEvent.addWarning(warning);
            log.error(warning);
            tokenLife = DEFAULT_TOKEN_LIFE;
        }

        final Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);

        ManagedSysEntity mSys = getManagedSystem(context);
        LdapContext ldapCtx = connect(distinguishedName, password, mSys);
        if (ldapCtx == null) {
            // if failed auth count is part of the polices, then do the
            // following processing
            final String attrValue = getPolicyAttribute(policy, "FAILED_AUTH_COUNT");
            if (StringUtils.isNotBlank(attrValue)) {

                int authFailCount = Integer.parseInt(attrValue);
                // increment the auth fail counter
                int failCount = 0;
                if (login.getAuthFailCount() != null) {
                    failCount = login.getAuthFailCount();
                }
                failCount++;
                login.setAuthFailCount(failCount);
                login.setLastAuthAttempt(new Date(System.currentTimeMillis()));
                if (failCount >= authFailCount) {
                    // lock the record and save the record.
                    login.setIsLocked(1);
                    loginManager.updateLogin(login);

                    // set the flag on the primary user record
                    user.setSecondaryStatus(UserStatusEnum.LOCKED);
                    userManager.updateUser(user);
                    newLoginEvent.addAttribute(AuditAttributeName.FAIL_COUNT, Integer.valueOf(failCount).toString());
                    newLoginEvent.addWarning(String.format("User %s has fail count %s.  Setting secondary status to locked, and login record to locked", user.getId(), failCount));
                    throw new BasicDataServiceException(
                            ResponseCode.RESULT_LOGIN_LOCKED);

                } else {
                    // update the counter save the record
                    loginManager.updateLogin(login);
                    newLoginEvent.addAttribute(AuditAttributeName.FAIL_COUNT, Integer.valueOf(authFailCount).toString());
                    newLoginEvent.addWarning(String.format("User %s has fail count %s", user.getId(), failCount));
                    throw new BasicDataServiceException(
                            ResponseCode.RESULT_INVALID_PASSWORD);
                }

            } else {
                final String warning = String.format("No '%s' policy attribute found on policy %s", "FAILED_AUTH_COUNT", policy);
                newLoginEvent.addWarning(warning);
                log.warn(warning);
                throw new BasicDataServiceException(
                        ResponseCode.RESULT_INVALID_PASSWORD);

            }
        }

        final Subject sub = new Subject();
        // validate the password expiration rules
        log.debug("Validating the state of the password - expired or not");
        ResponseCode pswdResult = passwordExpired(login, curDate, policy);
        if (pswdResult == ResponseCode.RESULT_PASSWORD_EXPIRED) {
            newLoginEvent.addWarning(String.format("Password Expired"));
            throw new BasicDataServiceException(
                    ResponseCode.RESULT_PASSWORD_EXPIRED);
        }
        Integer daysToExp = setDaysToPassworExpiration(login, curDate, sub, policy);
        if (daysToExp!=null) {
            sub.setDaysToPwdExp(0);
            if(daysToExp > -1)
                sub.setDaysToPwdExp(daysToExp);
        }
        // check password policy if it is necessary to change it after reset

        if(login.getResetPassword()>0){
            String chngPwdAttr = getPolicyAttribute(policy, "CHNG_PSWD_ON_RESET");
            if (StringUtils.isNotBlank(chngPwdAttr)) {
                boolean changePasswordAfterResult = (StringUtils.equalsIgnoreCase(Boolean.TRUE.toString(), chngPwdAttr));
                if(!changePasswordAfterResult) {
                    try {
                        changePasswordAfterResult = (Integer.parseInt(chngPwdAttr) > 0);
                    } catch(Throwable e) {}
                }
                if(changePasswordAfterResult) {
                    throw new BasicDataServiceException(ResponseCode.RESULT_PASSWORD_CHANGE_AFTER_RESET);
                }
            }
        }

        log.debug("-login successful");
        // good login - reset the counters
        Date curTime = new Date(System.currentTimeMillis());

        login.setLastAuthAttempt(curDate);

        // move the current login to prev login fields
        login.setPrevLogin(login.getLastLogin());
        login.setPrevLoginIP(login.getLastLoginIP());

        // assign values to the current login
        login.setLastLogin(curDate);
        login.setLastLoginIP(clientIP);

        login.setAuthFailCount(0);
        login.setFirstTimeLogin(0);
        log.debug("-Good Authn: Login object updated.");
        loginManager.updateLogin(login);

        // check the user status
        if (UserStatusEnum.PENDING_INITIAL_LOGIN.equals(user.getStatus()) ||
                // after the start date
                UserStatusEnum.PENDING_START_DATE.equals(user.getStatus())) {
            user.setStatus(UserStatusEnum.ACTIVE);
            userManager.updateUser(user);
        }

        // Successful login
        log.debug("-Populating subject after authentication");

        sub.setUserId(login.getUserId());
        sub.setPrincipal(principal);
        sub.setSsoToken(token(login.getUserId(), tokenType, tokenLife, tokenParam));
        setResultCode(login, sub, curDate, policy);

        newLoginEvent.setSuccessReason("Succssfull authentication into Default Login Module");
        return sub;

	}

	

	/*
    private static final Log log = LogFactory.getLog(LDAPLoginModule.class);

    String host = null;
    String baseDn = null;
    String adminUserName = null;
    String adminPassword = null;
    String protocol = null;
    String searchFilter = null;
    String pkAttribute = null;
    String managedSysId = null;
    String dn = null;

    LdapContext ctxLdap = null;

    public LDAPLoginModule() {
    }

    public void init(ManagedSysEntity mse) throws Exception {

        log.debug("AuthRepository Properties from Managed System in init = " + mse);

        host = mse.getHostUrl();
        adminUserName = mse.getUserId();
        adminPassword = this.decryptPassword(systemUserId, mse.getPswd());
        protocol = mse.getCommProtocol();
        managedSysId = mse.getId();
        Set<ManagedSystemObjectMatchEntity> managedSystemObjectMatchEntities = mse.getMngSysObjectMatchs();
        if (CollectionUtils.isNotEmpty(managedSystemObjectMatchEntities)) {
            for (ManagedSystemObjectMatchEntity objectMatchEntity : managedSystemObjectMatchEntities) {
                if ("USER".equals(objectMatchEntity.getObjectType())) {
                    baseDn = objectMatchEntity.getBaseDn();
                    searchFilter = objectMatchEntity.getSearchFilter();
                    pkAttribute = objectMatchEntity.getKeyField();
                    dn = objectMatchEntity.getKeyField();
                    break;
                }
            }
        }

    }

    /*
    public void globalLogout(String securityDomain, String principal) {
        // TODO Auto-generated method stub

    }
    */

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.auth.spi.LoginModule#login(org.openiam.idm.srvc.
     * auth.context.AuthenticationContext)
     */
	/*
    @Override
    public Subject login(AuthenticationContext authContext) throws Exception {
        Subject sub = new Subject();
        log.debug("login() in LDAPLoginModule called");
        String clientIP = authContext.getClientIP();
        String nodeIP = authContext.getNodeIP();

        Policy authPolicy = policyDataService.getPolicy(authPolicyId);
        PolicyAttribute policyAttribute = authPolicy
                .getAttribute("MANAGED_SYS_ID");
        if (policyAttribute == null) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }
        ManagedSysEntity mse = managedSysDAO.findById(policyAttribute.getValue1());
        if (mse == null)
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);

        String host = mse.getHostUrl();
        String adminUserName = mse.getUserId();
        String adminPassword = this.decryptPassword(systemUserId, mse.getPswd());
        String protocol = mse.getCommProtocol();
        String managedSysId = mse.getId();
        String baseDn = null;
        String searchFilter = null;
        String pkAttribute = null;
        String dn = null;
        Set<ManagedSystemObjectMatchEntity> managedSystemObjectMatchEntities = mse.getMngSysObjectMatchs();
        if (CollectionUtils.isNotEmpty(managedSystemObjectMatchEntities)) {
            for (ManagedSystemObjectMatchEntity objectMatchEntity : managedSystemObjectMatchEntities) {
                if ("USER".equals(objectMatchEntity.getObjectType())) {
                    baseDn = objectMatchEntity.getBaseDn();
                    searchFilter = objectMatchEntity.getSearchFilter();
                    pkAttribute = objectMatchEntity.getKeyField();
                    dn = objectMatchEntity.getKeyField();
                    break;
                }
            }
        }

        // current date
        Date curDate = new Date(System.currentTimeMillis());
        PasswordCredential cred = (PasswordCredential) authContext
                .getCredential();

        String principal = cred.getPrincipal();
        String password = cred.getPassword();
        String distinguishedName = null;

        // search for the login value passed in
        // if found - get the dn
        // authenticate with the dn
        // if ok - then success
        // // get the user status in idm and check that

        LdapContext ldapCtx = connect(adminUserName, adminPassword, host, protocol);
        NamingEnumeration ne = search(ldapCtx, principal,dn,searchFilter,baseDn);
        if (ne == null) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        try {
            while (ne.hasMore()) {
                SearchResult sr = (SearchResult) ne.next();

                distinguishedName = sr.getNameInNamespace();

            }

        } catch (NamingException e) {
            log.error(e);
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);

        }

        log.debug("Distinguished name=" + distinguishedName);

        if (distinguishedName == null || distinguishedName.length() == 0) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }

        log.debug("Authentication policyid="
                + sysConfiguration.getDefaultAuthPolicyId());
        // get the authentication lock out policy
        Policy plcy = policyDataService.getPolicy(sysConfiguration.getDefaultAuthPolicyId());
        String attrValue = getPolicyAttribute(plcy.getPolicyAttributes(), "FAILED_AUTH_COUNT");

        String tokenType = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_ISSUER");

        Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", principal);

       LoginResponse loginResponce = loginManager.getLoginByManagedSys(distinguishedName, managedSysId);

        if (loginResponce.getStatus() == ResponseStatus.FAILURE) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                    "MATCHING IDENTITY NOT FOUND", domainId, null, principal,
//                    null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        Login lg = loginResponce.getPrincipal();
        UserEntity user = this.userManager.getUser(lg.getUserId());

        // try to login to AD with this user
        LdapContext tempCtx = connect(distinguishedName, password, host, protocol);
        if (tempCtx == null) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                    "RESULT_INVALID_PASSWORD", domainId, null, principal, null,
//                    null, clientIP, nodeIP);
            // update the auth fail count
            if (attrValue != null && attrValue.length() > 0) {

                int authFailCount = Integer.parseInt(attrValue);
                // increment the auth fail counter
                int failCount = 0;
                if (lg.getAuthFailCount() != null) {
                    failCount = lg.getAuthFailCount().intValue();
                }
                failCount++;
                lg.setAuthFailCount(failCount);
                lg.setLastAuthAttempt(new Date(System.currentTimeMillis()));
                if (failCount >= authFailCount) {
                    // lock the record and save the record.
                    lg.setIsLocked(1);
                    loginManager.saveLogin(lg);

                    // set the flag on the primary user record
                    user.setSecondaryStatus(UserStatusEnum.LOCKED);
                    userManager.updateUser(user);

//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "ACCOUNT_LOCKED", domainId, null, principal, null,
//                            null, clientIP, nodeIP);
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_LOGIN_LOCKED);
                } else {
                    // update the counter save the record
                    loginManager.saveLogin(lg);
//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "INVALID_PASSWORD", domainId, null, principal,
//                            null, null, clientIP, nodeIP);

                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_INVALID_PASSWORD);
                }
            } else {
                log.debug("No auth fail password policy value found");
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_PASSWORD);

            }

        }

        if (user.getStatus() != null) {
            if (user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {
                if (!pendingInitialStartDateCheck(user, curDate)) {
//                    log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                            "INVALID USER STATUS", domainId, null, principal,
//                            null, null, clientIP, nodeIP);
                    throw new AuthenticationException(
                            AuthenticationConstants.RESULT_INVALID_USER_STATUS);
                }
            }
            if (!user.getStatus().equals(UserStatusEnum.ACTIVE)
                    && !user.getStatus().equals(
                    UserStatusEnum.PENDING_INITIAL_LOGIN)) {
                // invalid status
//                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
//                        "INVALID USER STATUS", domainId, null, principal, null,
//                        null, clientIP, nodeIP);
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_INVALID_USER_STATUS);
            }
            this.checkSecondaryStatus(user);
        }

        int pswdResult = passwordExpired(lg, curDate);
        if (pswdResult == AuthenticationConstants.RESULT_PASSWORD_EXPIRED) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "PASSWORD_EXPIRED",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
        }
        Integer daysToExp = setDaysToPassworExpiration(lg, curDate, sub, null);
        if (daysToExp != null) {
            sub.setDaysToPwdExp(0);
            if (daysToExp > -1)
                sub.setDaysToPwdExp(daysToExp);
        }

        // update the login and user records to show this authentication
        lg.setLastAuthAttempt(curDate);

        // move the current login to prev login fields
        lg.setPrevLogin(lg.getLastLogin());
        lg.setPrevLoginIP(lg.getLastLoginIP());

        // assign values to the current login
        lg.setLastLogin(curDate);
        lg.setLastLoginIP(clientIP);

        // lg.setLastAuthAttempt(new Date(System.currentTimeMillis()));
        // lg.setLastLogin(new Date(System.currentTimeMillis()));

        lg.setAuthFailCount(0);
        lg.setFirstTimeLogin(0);

        log.info("Good Authn: Login object updated.");

        loginManager.saveLogin(lg);

        // check the user status
        if (user.getStatus() != null) {

            if (user.getStatus().equals(UserStatusEnum.PENDING_INITIAL_LOGIN) ||
                    // after the start date
                    user.getStatus().equals(UserStatusEnum.PENDING_START_DATE)) {

                user.setStatus(UserStatusEnum.ACTIVE);
                userManager.updateUser(user);
            }
        }

        // Successful login
        sub.setUserId(lg.getUserId());
        sub.setPrincipal(distinguishedName);
        sub.setSsoToken(token(lg.getUserId(), tokenParam));
        setResultCode(lg, sub, curDate, null);

        // send message into to audit log

//        log("AUTHENTICATION", "AUTHENTICATION", "SUCCESS", null, domainId,
//                user.getId(), distinguishedName, null, null, clientIP,
//                nodeIP);

        return sub;
    }

    public LdapContext connect(String userName, String password, String host, String protocol) {

        // LdapContext ctxLdap = null;
        Hashtable<String, String> envDC = new Hashtable();

        System.setProperty("javax.net.ssl.trustStore", keystore);

        log.info("Connecting to ldap using principal=" + userName);

        envDC.put(Context.PROVIDER_URL, host);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
        envDC.put(Context.SECURITY_PRINCIPAL, userName); // "administrator@diamelle.local"
        envDC.put(Context.SECURITY_CREDENTIALS, password);
        if (protocol != null && protocol.equalsIgnoreCase("SSL")) {
            envDC.put(Context.SECURITY_PROTOCOL, protocol);
        }

        try {
            return (new InitialLdapContext(envDC, null));
        } catch (NamingException ne) {
            log.info(ne.getMessage());
            return null;

        }
    }

    private NamingEnumeration search(LdapContext ctx, String searchValue, String dn, String searchFilter, String baseDn) {
        SearchControls searchCtls = new SearchControls();

        // Specify the attributes to returned
        String returnedAtts[] = {dn};
        searchCtls.setReturningAttributes(returnedAtts);

        // Specify the search scope
        try {
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchFilter_ = searchFilter.replace("?", searchValue);


            log.debug("Search Filter=" + searchFilter);
            log.debug("BaseDN=" + baseDn);

            return ctx.search(baseDn, searchFilter_, searchCtls);
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        return null;

    }

    private String getDN(NamingEnumeration nameEnum) {
        String uid = null;

        try {
            while (nameEnum.hasMoreElements()) {
                SearchResult sr = (SearchResult) nameEnum.next();
                Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    uid = (String) attrs.get("uid").get();
                    log.info("getDN: uid=" + uid);
                    if (uid != null) {
                        return uid;
                    }
                }
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        return null;
    }

    private int passwordExpired(LoginEntity lg, Date curDate) {
        if (lg.getGracePeriod() == null) {
            // set an early date
            lg.setGracePeriod(new Date(0));
        }
        if (lg.getPwdExp() != null) {
            if (curDate.after(lg.getPwdExp())
                    && curDate.after(lg.getGracePeriod())) {
                // check for password expiration, but successful login
                return AuthenticationConstants.RESULT_PASSWORD_EXPIRED;
            }
            if ((curDate.after(lg.getPwdExp()) && curDate.before(lg
                    .getGracePeriod()))) {
                // check for password expiration, but successful login
                return AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP;
            }
        }
        return AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP;
    }

    private String getPolicyAttribute(Set<PolicyAttribute> attr, String name) {
        assert name != null : "Name parameter is null";

        for (PolicyAttribute policyAtr : attr) {
            if (policyAtr.getName().equalsIgnoreCase(name)) {
                return policyAtr.getValue1();
            }
        }
        return null;

    }

    private SSOToken token(String userId, Map tokenParam) throws Exception {

        log.debug("Generating Security Token");

        tokenParam.put("USER_ID", userId);

        SSOTokenModule tkModule = SSOTokenFactory
                .createModule((String) tokenParam.get("TOKEN_TYPE"));
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt((String) tokenParam
                .get("TOKEN_LIFE")));

        return tkModule.createToken(tokenParam);
    }
	*/
}
