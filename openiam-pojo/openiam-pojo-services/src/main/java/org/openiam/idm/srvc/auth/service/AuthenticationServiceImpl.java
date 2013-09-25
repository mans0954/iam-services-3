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
package org.openiam.idm.srvc.auth.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.BooleanResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.GroupDozerConverter;
import org.openiam.dozer.converter.RoleDozerConverter;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.LogoutException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogUtil;
import org.openiam.idm.srvc.auth.context.AuthContextFactory;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.LoginModuleSelector;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.spi.AbstractLoginModule;
import org.openiam.idm.srvc.auth.spi.LoginModule;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.service.GroupDataService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.openiam.idm.srvc.role.service.RoleDataService;
import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;
import org.openiam.idm.srvc.secdomain.dto.SecurityDomain;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDAO;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author suneet
 *
 */

@Service("authenticate")
@WebService(endpointInterface = "org.openiam.idm.srvc.auth.service.AuthenticationService", targetNamespace = "urn:idm.openiam.org/srvc/auth/service", portName = "AuthenticationServicePort", serviceName = "AuthenticationService")
@ManagedResource(objectName = "openiam:name=authenticationService", description = "Authentication Service")
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService, ApplicationContextAware, BeanFactoryAware {

	@Autowired
    private AuthStateDAO authStateDao;
    
    @Autowired
    private LoginDataService loginManager;
    
    @Autowired
    private SecurityDomainDAO securityDomainDAO;
    
    @Value("${org.openiam.core.login.authentication.context.class}")
    private String authContextClass;
    
    @Autowired
    private ResourceDataService resourceService;

    @Autowired
    @Qualifier("defaultSSOToken")
    private SSOTokenModule defaultToken;
    
    @Autowired
    private UserDataService userManager;
    
    @Autowired
    private PolicyDAO policyDao;
    
    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private AuditLogUtil auditLogUtil;
    
    @Autowired
    private SysConfiguration sysConfiguration;
    
    @Autowired
    private PasswordService passwordManager;

    @Autowired
    protected KeyManagementService keyManagementService;
    
    @Value("${org.openiam.core.login.login.module.default}")
    private String defaultLoginModule;
    
    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;
    
    private ApplicationContext ctx;
    private BeanFactory beanFactory;

    private static final Log log = LogFactory.getLog(AuthenticationServiceImpl.class);

    /*
    public AuthenticationResponse authenticate(AuthenticationContext ctx)
            throws Exception {
        AuthenticationResponse authResp = new AuthenticationResponse(
                ResponseStatus.FAILURE);

        AbstractLoginModule loginModule = null;

        if (ctx == null) {
            throw new NullPointerException(
                    "AuthenticationContext parameter is null");
        }

        String secDomainId = ctx.getDomainId();

        SecurityDomain secDomain = secDomainService
                .getSecurityDomain(secDomainId);
        if (secDomain == null) {
            authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_DOMAIN);
            return authResp;

            // throw new
            // AuthenticationException(AuthenticationConstants.RESULT_INVALID_DOMAIN);
        }
        try {
            log.debug("loginModule=" + secDomain.getDefaultLoginModule());

            // create the authentication module class
            // if the authenticationcontext has a class, that will over-ride the
            // one that is with the domain
            // later add the abilty to define a login module at the resource
            // level
            if (ctx.getLoginModule() != null) {
                loginModule = (AbstractLoginModule) LoginModuleFactory
                        .createModule(ctx.getLoginModule());
            } else {
                loginModule = (AbstractLoginModule) LoginModuleFactory
                        .createModule(secDomain.getDefaultLoginModule());
            }
           
           	//Dependency injection fails when we use our own factory. Set the
            //necessary beans directly
             
            loginModule.setLoginService(loginManager);
            loginModule.setTokenModule(defaultToken);
            loginModule.setUserService(userManager);
            loginModule.setCryptor(cryptor);
            loginModule.setSecurityDomain(secDomain);
            loginModule.setAuditUtil(auditLogUtil);

        } catch (Exception ie) {
            log.error(ie.getMessage(), ie);
            authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
            return authResp;
        }

        Map<String, Object> authParamMap = new HashMap<String, Object>();
        authParamMap.put("SEC_DOMAIN_ID", secDomainId);
        authParamMap.put("AUTH_SYS_ID", secDomain.getAuthSysId());
        ctx.setAuthParam(authParamMap);

        try {
            Subject sub = loginModule.login(ctx);
            // add the sso token to the authstate

            updateAuthState(sub);

            populateSubject(sub.getUserId(), sub);

            authResp.setSubject(sub);
            authResp.setStatus(ResponseStatus.SUCCESS);
            return authResp;

        } catch (AuthenticationException ae) {
            int errCode = ae.getErrorCode();
            switch (errCode) {
            case AuthenticationConstants.RESULT_INVALID_DOMAIN:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_DOMAIN);
                break;
            case AuthenticationConstants.RESULT_INVALID_LOGIN:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                break;
            case AuthenticationConstants.RESULT_INVALID_PASSWORD:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                break;
            case AuthenticationConstants.RESULT_INVALID_USER_STATUS:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_USER_STATUS);
                break;
            case AuthenticationConstants.RESULT_LOGIN_DISABLED:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_LOGIN_DISABLED);
                break;
            case AuthenticationConstants.RESULT_LOGIN_LOCKED:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_LOGIN_LOCKED);
                break;
            case AuthenticationConstants.RESULT_PASSWORD_EXPIRED:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
                break;
            case AuthenticationConstants.RESULT_SERVICE_NOT_FOUND:
                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_SERVICE_NOT_FOUND);
                break;
            default:
                authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
            }
            return authResp;
        }

    }
    */

    /*
    @ManagedAttribute
    public Subject authenticateByToken(String userId, String token,
            String tokenType) throws Exception {

        String tokenUserId = null;
        SSOTokenModule tkModule = SSOTokenFactory.createModule(tokenType);
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);

        if (!AuthenticationConstants.OPENIAM_TOKEN.equalsIgnoreCase(tokenType)) {
            log.debug("authenticateByToken: Token type is invalid=" + tokenType);
            Subject sub = new Subject();
            sub.setResultCode(AuthenticationConstants.RESULT_INVALID_TOKEN);
            return sub;
        }

        String tkString = tkModule.getDecryptedToken(userId, token);

        log.debug("authenticateByToken: Decrypted token=" + tkString);

        StringTokenizer tokenizer = new StringTokenizer(tkString, ":");
        if (tokenizer.hasMoreTokens()) {
            tokenUserId = tokenizer.nextToken();
        } else {
            log.debug("authenticateByToken: no userId in the token");

            Subject sub = new Subject();
            sub.setResultCode(AuthenticationConstants.RESULT_INVALID_TOKEN);
            return sub;
        }

        LoginEntity lg = loginManager.getPrimaryIdentity(tokenUserId);

        Response resp = renewToken(lg.getLogin(), token, tokenType);

        log.debug("authenticateByToken: response from renewToken=" + resp);

        if (resp.getStatus() == ResponseStatus.FAILURE) {
            Subject sub = new Subject();
            sub.setResultCode(AuthenticationConstants.RESULT_INVALID_TOKEN);
            return sub;
        }

        AuthState authSt = authStateDao.findById(tokenUserId);
        Subject sub = new Subject(tokenUserId);
        sub.setPrincipal(lg.getLogin());
        sub.setExpirationTime(authSt.getExpiration());
        sub.setResultCode(AuthenticationConstants.RESULT_SUCCESS);

        populateSubject(tokenUserId, sub);

        return sub;

    }
    */

    @Override
    @ManagedAttribute
    public void globalLogout(String userId) throws LogoutException {
        if (userId == null) {
            throw new NullPointerException("UserId is null");
        }

        AuthStateEntity authSt = authStateDao.findById(userId);
        if (authSt == null) {
            log.error("AuthState not found for userId=" + userId);
            throw new LogoutException();
        }

        authSt.setAuthState(new BigDecimal(0));
        authSt.setToken("LOGOUT");
        authStateDao.saveAuthState(authSt);

    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request object is null");
        }

        final String secDomainId = request.getDomainId();
        final String principal = request.getPrincipal();
        final String password = request.getPassword();
        final String clientIP = request.getClientIP();
        final String nodeIP = request.getNodeIP();

        AuthenticationResponse authResp = new AuthenticationResponse(
                ResponseStatus.FAILURE);

        AuthenticationContext ctx = null;
        AbstractLoginModule loginModule = null;
        String loginModName = null;
        LoginModuleSelector modSel = new LoginModuleSelector();

        LoginEntity lg = null;
        String userId = null;
        UserEntity user = null;

        SecurityDomainEntity secDomain = securityDomainDAO.findById(secDomainId);
        if (secDomain == null) {
            // throw new
            // AuthenticationException(AuthenticationConstants.RESULT_INVALID_DOMAIN);
            authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_DOMAIN);
            return authResp;
        }

        // Determine which login module to use
        // - get the Authentication policy for the domain
        String authPolicyId = secDomain.getAuthnPolicyId();
        final PolicyEntity authPolicy = policyDao.findById(authPolicyId);
        PolicyAttributeEntity modType = authPolicy.getAttribute("LOGIN_MOD_TYPE");
        PolicyAttributeEntity defaultModule = authPolicy.getAttribute("DEFAULT_LOGIN_MOD");
        loginModName = defaultModule.getValue1();
        if (modType != null) {
            // modSel.setModuleType( Integer.parseInt(modType.getValue1()));
            modSel.setModuleName(loginModName);
        }

        // log.debug("loginModule=" + secDomain.getDefaultLoginModule());

        if (StringUtils.equals(loginModName, defaultLoginModule)) {
            /* Few basic checks must be met before calling the login module. */
            /* Simplifies the login module */
            if (StringUtils.isBlank(principal)) {
                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
                        "INVALID LOGIN", secDomainId, null, principal, null,
                        null, clientIP, nodeIP);
                // throw new
                // AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);

                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                return authResp;

            }

            if (StringUtils.isBlank(password)) {

                log.debug("Invalid password");

                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
                        "INVALID PASSWORD", secDomainId, null, principal, null,
                        null, clientIP, nodeIP);
                // throw new
                // AuthenticationException(AuthenticationConstants.RESULT_INVALID_PASSWORD);

                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                return authResp;

            }

            lg = loginManager.getLoginByManagedSys(secDomainId, principal,
                    secDomain.getAuthSysId());

            if (lg == null) {
                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
                        "INVALID LOGIN", secDomainId, null, principal, null,
                        null, clientIP, nodeIP);
                // throw new
                // AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);

                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                return authResp;

            }

            // check the user status - move to the abstract class for reuse
            userId = lg.getUserId();
            
            user = userManager.getUser(userId);
        }

        try {

            log.debug("Creating authentication context");

            ctx = AuthContextFactory.createContext(authContextClass);

            PolicyAttributeEntity selPolicy = authPolicy
                    .getAttribute("LOGIN_MODULE_SEL_POLCY");
            if (selPolicy != null && StringUtils.isNotBlank(selPolicy.getValue1())) {

                log.debug("Calling policy selection rule");

                Map<String, Object> bindingMap = new HashMap<String, Object>();
                bindingMap.put("secDomainId", secDomainId);
                bindingMap.put("principal", principal);
                bindingMap.put("sysId", secDomain.getAuthSysId());
                // also bind the user and login objects to avoid
                // re-initialization of spring the scripting engine
                bindingMap.put("login", lg);
                bindingMap.put("user", user);

                try {
                    loginModName = (String) scriptRunner.execute(bindingMap,
                            selPolicy.getValue1());
                } catch (ScriptEngineException e) {
                    log.error(e);
                }

            }

            if (modSel.getModuleType() == LoginModuleSelector.MODULE_TYPE_LOGIN_MODULE) {
            	/* here for backward compatability. in case a groovy script returned an actual class name, get 
            	 * the spring bean name
            	 */
            	try {
            		loginModName = Class.forName(loginModName).getAnnotation(Component.class).value();
            	} catch(Throwable e) {
            		
            	}
            	
                loginModule = beanFactory.getBean(loginModName, AbstractLoginModule.class); 
                //loginModule = (AbstractLoginModule) LoginModuleFactory.createModule(loginModName);
                loginModule.setSecurityDomain(secDomain);
                loginModule.setUser(user);
                loginModule.setLg(lg);
                loginModule.setAuthPolicyId(authPolicyId);
            }

        } catch (Throwable ie) {
            log.error(ie.getMessage(), ie);
            // throw (new
            // AuthenticationException(AuthenticationConstants.INTERNAL_ERROR,ie.getMessage(),ie));
            authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
            return authResp;
        }
        PasswordCredential cred = (PasswordCredential) ctx
                .createCredentialObject(AuthenticationConstants.AUTHN_TYPE_PASSWORD);
        cred.setCredentials(secDomainId, principal, password);
        ctx.setCredential(AuthenticationConstants.AUTHN_TYPE_PASSWORD, cred);

        Map<String, Object> authParamMap = new HashMap<String, Object>();
        authParamMap.put("SEC_DOMAIN_ID", secDomainId);
        authParamMap.put("AUTH_SYS_ID", secDomain.getAuthSysId());
        ctx.setAuthParam(authParamMap);

        ctx.setNodeIP(nodeIP);
        ctx.setClientIP(clientIP);

        Subject sub = null;
        if (modSel.getModuleType() == LoginModuleSelector.MODULE_TYPE_LOGIN_MODULE) {
            try {
                sub = loginModule.login(ctx);

            } catch (AuthenticationException ae) {

                log.debug("Authentication error " + ae.toString());

                int errCode = ae.getErrorCode();
                switch (errCode) {
                case AuthenticationConstants.RESULT_INVALID_DOMAIN:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_DOMAIN);
                    break;
                case AuthenticationConstants.RESULT_INVALID_LOGIN:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                    break;
                case AuthenticationConstants.RESULT_INVALID_PASSWORD:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                    break;
                case AuthenticationConstants.RESULT_INVALID_USER_STATUS:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_USER_STATUS);
                    break;
                case AuthenticationConstants.RESULT_LOGIN_DISABLED:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_LOGIN_DISABLED);
                    break;
                case AuthenticationConstants.RESULT_LOGIN_LOCKED:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_LOGIN_LOCKED);
                    break;
                case AuthenticationConstants.RESULT_PASSWORD_EXPIRED:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
                    break;
                case AuthenticationConstants.RESULT_SERVICE_NOT_FOUND:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_SERVICE_NOT_FOUND);
                    break;
                case AuthenticationConstants.RESULT_INVALID_CONFIGURATION:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
                    break;
                case AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP:
                	authResp.setAuthErrorCode(AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP);
                	break;
                case AuthenticationConstants.RESULT_PASSWORD_CHANGE_AFTER_RESET:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_PASSWORD_CHANGE_AFTER_RESET);
                    break;
                default:
                    authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
                }
                return authResp;
            } catch (Throwable e) {
            	log.error("Unknown Exception", e);
                authResp.setStatus(ResponseStatus.FAILURE);
                authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
                authResp.setAuthErrorMessage(e.getMessage());
                return authResp;
            }
        } else {

        }
        // add the sso token to the authstate

        updateAuthState(sub);
        //populateSubject(sub.getUserId(), sub);

        log.debug("*** PasswordAuth complete...Returning response object");

        authResp.setSubject(sub);
        authResp.setStatus(ResponseStatus.SUCCESS);
        
        return authResp;
    }

    /*
    @ManagedAttribute
    public AuthenticationResponse passwordAuth(String secDomainId,
            String principal, String password) throws Exception {

        log.debug("*** PasswordAuth called...");

        AuthenticationResponse authResp = new AuthenticationResponse(
                ResponseStatus.FAILURE);

        AuthenticationContext ctx = null;
        AbstractLoginModule loginModule = null;
        Policy authPolicy = null;
        String loginModName = null;
        LoginModuleSelector modSel = new LoginModuleSelector();

        LoginEntity lg = null;
        String userId = null;
        UserEntity user = null;

        SecurityDomain secDomain = secDomainService
                .getSecurityDomain(secDomainId);
        if (secDomain == null) {
            // throw new
            // AuthenticationException(AuthenticationConstants.RESULT_INVALID_DOMAIN);
            authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_DOMAIN);
            return authResp;

        }

        // Determine which login module to use
        // - get the Authentication policy for the domain
        String authPolicyId = secDomain.getAuthnPolicyId();

        log.debug("Authn policyId=" + authPolicyId);

        authPolicy = policyDataService.getPolicy(authPolicyId);

        log.debug("Auth Policy object=" + authPolicy);

        PolicyAttribute modType = authPolicy.getAttribute("LOGIN_MOD_TYPE");
        PolicyAttribute defaultModule = authPolicy
                .getAttribute("DEFAULT_LOGIN_MOD");
        loginModName = defaultModule.getValue1();
        if (modType != null) {
            // modSel.setModuleType( Integer.parseInt(modType.getValue1()));
            modSel.setModuleName(loginModName);
        }

        // log.debug("loginModule=" + secDomain.getDefaultLoginModule());

        if (loginModName
                .equalsIgnoreCase("org.openiam.idm.srvc.auth.spi.DefaultLoginModule")) {
            //Few basic checks must be met before calling the login module.
            //Simplifies the login module
            if (principal == null || principal.length() == 0) {

                log.debug("Invalid login:" + principal);

                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
                        "INVALID LOGIN", secDomainId, null, principal, null,
                        null, null, null);
                // throw new
                // AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);

                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                return authResp;

            }

            if (password == null || password.equals("")) {

                log.debug("Invalid password");

                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
                        "INVALID PASSWORD", secDomainId, null, principal, null,
                        null, null, null);
                // throw new
                // AuthenticationException(AuthenticationConstants.RESULT_INVALID_PASSWORD);

                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                return authResp;

            }

            lg = loginManager.getLoginByManagedSys(secDomainId, principal,
                    secDomain.getAuthSysId());

            log.debug("login object after looking up the login:" + lg);

            if (lg == null) {
                log.debug("Login not found. Throw authentication exception");
                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
                        "INVALID LOGIN", secDomainId, null, principal, null,
                        null, null, null);
                // throw new
                // AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);

                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                return authResp;

            } else {
                log.debug("Login found. No exception thrown");
            }

            // check the user status - move to the abstract class for reuse
            userId = lg.getUserId();

            log.debug("UserId=" + userId);
            user = userManager.getUser(userId);
        }

        try {

            log.debug("Creating authentication context");

            ctx = AuthContextFactory.createContext(authContextClass);

            PolicyAttribute selPolicy = authPolicy
                    .getAttribute("LOGIN_MODULE_SEL_POLCY");
            if (selPolicy != null && selPolicy.getValue1() != null
                    && selPolicy.getValue1().length() > 0) {

                log.debug("Calling policy selection rule");

                Map<String, Object> bindingMap = new HashMap<String, Object>();
                bindingMap.put("secDomainId", secDomainId);
                bindingMap.put("principal", principal);
                bindingMap.put("sysId", secDomain.getAuthSysId());
                // also bind the user and login objects to avoid
                // re-initialization of spring the scripting engine
                bindingMap.put("login", lg);
                bindingMap.put("user", user);

                ScriptIntegration se = ScriptFactory
                        .createModule(this.scriptEngine);
                try {
                    loginModName = (String) se.execute(bindingMap,
                            selPolicy.getValue1());
                } catch (ScriptEngineException e) {
                    log.error(e);
                }

                log.debug("LoginModName from script =" + loginModName);

            } else {
                log.debug("retrieving default login module for policy");

                // test code
                Set<PolicyAttribute> attSet = authPolicy.getPolicyAttributes();
                Iterator<PolicyAttribute> it = attSet.iterator();

            }
            log.debug("login module name=" + loginModName);

            if (modSel.getModuleType() == LoginModuleSelector.MODULE_TYPE_LOGIN_MODULE) {
                loginModule = (AbstractLoginModule) LoginModuleFactory
                        .createModule(loginModName);
                //
                //Dependency injection fails when we use our own factory. Set
                //the necessary beans directly
                
                loginModule.setLoginService(loginManager);
                loginModule.setTokenModule(defaultToken);
                loginModule.setUserService(userManager);
                loginModule.setCryptor(cryptor);
                loginModule.setSecurityDomain(secDomain);
                loginModule.setAuditUtil(auditLogUtil);
                loginModule.setUser(user);
                loginModule.setLg(lg);
                loginModule.setAuthPolicyId(authPolicyId);
                loginModule.setResourceService(resourceService);
                loginModule.setPasswordManager(passwordManager);
                loginModule.setPolicyDataService(policyDataService);
                loginModule.setKeyManagementService(keyManagementService);
            }

        } catch (Exception ie) {
            log.error(ie.getMessage(), ie);
            // throw (new
            // AuthenticationException(AuthenticationConstants.INTERNAL_ERROR,ie.getMessage(),ie));
            authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
            return authResp;
        }
        PasswordCredential cred = (PasswordCredential) ctx
                .createCredentialObject(AuthenticationConstants.AUTHN_TYPE_PASSWORD);
        cred.setCredentials(secDomainId, principal, password);
        ctx.setCredential(AuthenticationConstants.AUTHN_TYPE_PASSWORD, cred);

        Map<String, Object> authParamMap = new HashMap<String, Object>();
        authParamMap.put("SEC_DOMAIN_ID", secDomainId);
        authParamMap.put("AUTH_SYS_ID", secDomain.getAuthSysId());
        ctx.setAuthParam(authParamMap);

        Subject sub = null;
        if (modSel.getModuleType() == LoginModuleSelector.MODULE_TYPE_LOGIN_MODULE) {
            try {
                sub = loginModule.login(ctx);

            } catch (AuthenticationException ae) {

                log.debug("Authentication error " + ae.toString());

                int errCode = ae.getErrorCode();
                switch (errCode) {
                case AuthenticationConstants.RESULT_INVALID_DOMAIN:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_DOMAIN);
                    break;
                case AuthenticationConstants.RESULT_INVALID_LOGIN:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                    break;
                case AuthenticationConstants.RESULT_INVALID_PASSWORD:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                    break;
                case AuthenticationConstants.RESULT_INVALID_USER_STATUS:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_USER_STATUS);
                    break;
                case AuthenticationConstants.RESULT_LOGIN_DISABLED:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_LOGIN_DISABLED);
                    break;
                case AuthenticationConstants.RESULT_LOGIN_LOCKED:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_LOGIN_LOCKED);
                    break;
                case AuthenticationConstants.RESULT_PASSWORD_EXPIRED:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
                    break;
                case AuthenticationConstants.RESULT_SERVICE_NOT_FOUND:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_SERVICE_NOT_FOUND);
                    break;
                case AuthenticationConstants.RESULT_INVALID_CONFIGURATION:
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
                    break;
                default:
                    authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
                }
                return authResp;
            }

        } else {

        }
        // add the sso token to the authstate

        updateAuthState(sub);
        populateSubject(sub.getUserId(), sub);

        log.debug("*** PasswordAuth complete...Returning response object");

        authResp.setSubject(sub);
        authResp.setStatus(ResponseStatus.SUCCESS);
        return authResp;

    }
    */

    /*
    private void populateSubject(String userId, Subject sub) {
        log.debug("populateSubject: userId=" + userId);

        final List<GroupEntity> groupList = groupManager.getGroupsForUser(
                userId, 0, Integer.MAX_VALUE);
        final List<RoleEntity> roleAry = roleManager.getUserRoles(userId, 0,
                Integer.MAX_VALUE);

        if (CollectionUtils.isNotEmpty(groupList)) {
            sub.setGroups(groupDozerConverter.convertToDTOList(groupList, true));
        }
        if (CollectionUtils.isNotEmpty(roleAry)) {
            sub.setRoles(roleDozerConverter.convertToDTOList(roleAry, true));
        }

    }
    */

    /*
    public BooleanResponse validateToken(String loginId, String token,
            String tokenType) throws Exception {

        if (loginId == null) {
            throw new IllegalArgumentException("loginId is null");
        }
        if (token == null) {
            throw new IllegalArgumentException("token is null");
        }
        if (tokenType == null) {
            throw new IllegalArgumentException("tokenType is null");
        }

        log.debug("validateToken token=" + token);

        // check if this is a valid user
        LoginEntity lg = loginManager.getLoginByManagedSys(
                this.sysConfiguration.getDefaultSecurityDomain(), loginId,
                this.sysConfiguration.getDefaultManagedSysId());
        if (lg == null) {
            log.debug("login object is null.");
            BooleanResponse resp = new BooleanResponse(false);
            return resp;
        }

        // get handler for the token type
        SSOTokenModule tkModule = SSOTokenFactory.createModule(tokenType);
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        boolean tokenStatus = tkModule.isTokenValid(lg.getUserId(), loginId,
                token);

        BooleanResponse resp = new BooleanResponse(tokenStatus);
        return resp;

    }
    */

    @Override
    public Response renewToken(String principal, String token, String tokenType) {

        log.debug("RenewToken called.");

        Response resp = new Response(ResponseStatus.SUCCESS);

        // validateToken first

        final SecurityDomainEntity secDomain = securityDomainDAO.findById(sysConfiguration.getDefaultSecurityDomain());

        PolicyEntity plcy = policyDao.findById(secDomain.getAuthnPolicyId());
        String attrValue = getPolicyAttribute(plcy.getPolicyAttributes(),
                "FAILED_AUTH_COUNT");
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(),
                "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(),
                "TOKEN_ISSUER");

        // get the userId of this token
        LoginEntity lg = loginManager.getLoginByManagedSys(
                sysConfiguration.getDefaultSecurityDomain(), principal,
                sysConfiguration.getDefaultManagedSysId());

        if (lg == null) {
            resp.setStatus(ResponseStatus.FAILURE);
            return resp;
        }

        Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);

        tokenParam.put("USER_ID", lg.getUserId());
        tokenParam.put("PRINCIPAL", principal);

        if (!isUserStatusValid(lg.getUserId())) {

            log.debug("RenewToken: user status failed for userId = "
                    + lg.getUserId());

            resp.setStatus(ResponseStatus.FAILURE);
            return resp;

        }

        AuthStateEntity authSt = authStateDao.findById(lg.getUserId());
        if (authSt != null) {

            if (authSt.getToken() == null
                    || "LOGOUT".equalsIgnoreCase(authSt.getToken())) {
                resp.setStatus(ResponseStatus.FAILURE);
                return resp;
            }

        }

        SSOTokenModule tkModule = SSOTokenFactory
                .createModule((String) tokenParam.get("TOKEN_TYPE"));
        tkModule.setCryptor(this.cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt(tokenLife));

        try {
            if (!tkModule.isTokenValid(lg.getUserId(), principal, token)) {
                resp.setStatus(ResponseStatus.FAILURE);
                return resp;
            }

            SSOToken ssoToken = tkModule.createToken(tokenParam);
            resp.setResponseValue(ssoToken);
        } catch (Throwable e) {
            resp.setStatus(ResponseStatus.FAILURE);
            resp.setErrorText(e.getMessage());
        }
        return resp;

    }

    private boolean isUserStatusValid(String userId) {

        UserEntity u = userManager.getUser(userId);

        UserStatusEnum en = u.getStatus();

        UserStatusEnum secondaryStatus = u.getSecondaryStatus();

        if (en == UserStatusEnum.DELETED || en == UserStatusEnum.INACTIVE
                || en == UserStatusEnum.LEAVE || en == UserStatusEnum.TERMINATE) {
            return false;

        }
        if (secondaryStatus != null) {

            log.debug("- Secondary status for user = "
                    + secondaryStatus.toString());

            if (secondaryStatus == UserStatusEnum.DISABLED
                    || secondaryStatus == UserStatusEnum.LOCKED
                    || secondaryStatus == UserStatusEnum.LOCKED_ADMIN) {
                return false;

            }
        }
        return true;

    }

    private String getPolicyAttribute(Set<PolicyAttributeEntity> attr, String name) {
        assert name != null : "Name parameter is null";

        for (PolicyAttributeEntity policyAtr : attr) {
        	if(StringUtils.equalsIgnoreCase(policyAtr.getName(), name)) {
                return policyAtr.getValue1();
            }
        }
        return null;

    }

    /*
    public BooleanResponse validateTokenByUser(String userId, String token,
            String tokenType) throws Exception {
        if (userId == null) {
            throw new IllegalArgumentException("userId is null");
        }
        if (token == null) {
            throw new IllegalArgumentException("token is null");
        }
        if (tokenType == null) {
            throw new IllegalArgumentException("tokenType is null");
        }

        // get the user

        UserEntity user = userManager.getUser(userId);
        if (user == null) {
            // invalid user
            BooleanResponse resp = new BooleanResponse(false);
            return resp;
        }
        // get the password policy
        SecurityDomain secDomain = secDomainService
                .getSecurityDomain(getSysConfiguration()
                        .getDefaultSecurityDomain());

        Policy plcy = policyDataService.getPolicy(secDomain.getAuthnPolicyId());
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(),
                "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(),
                "TOKEN_ISSUER");

        // get the primary identity
        LoginEntity lg = this.loginManager.getPrimaryIdentity(userId);
        SSOTokenModule tkModule = SSOTokenFactory.createModule(tokenType);
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt(tokenLife));

        boolean tokenStatus = tkModule.isTokenValid(lg.getUserId(), lg.getLogin(), token);

        BooleanResponse resp = new BooleanResponse(tokenStatus);
        return resp;

    }
    */

    private void updateAuthState(Subject sub) {

    	AuthStateEntity state = new AuthStateEntity(sub.getDomainId(), new BigDecimal(1),
                sub.getSsoToken().getExpirationTime().getTime(), sub
                        .getSsoToken().getToken(), sub.getUserId());

        authStateDao.saveAuthState(state);
    }

    /*
    private SSOToken token(String userId, Map tokenParam) throws Exception {

        tokenParam.put("USER_ID", userId);

        SSOTokenModule tkModule = SSOTokenFactory
                .createModule((String) tokenParam.get("TOKEN_TYPE"));
        return tkModule.createToken(tokenParam);
    }
    */

    public void log(String objectTypeId, String actionId, String actionStatus,
            String reason, String domainId, String userId, String principal,
            String linkedLogId, String clientId, String clientIP, String nodeIP) {

        IdmAuditLog log = new IdmAuditLog(objectTypeId, actionId, actionStatus,
                reason, domainId, userId, principal, linkedLogId, clientId);
        log.setHost(clientIP);
        log.setNodeIP(nodeIP);
        auditLogUtil.log(log);
    }

	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
