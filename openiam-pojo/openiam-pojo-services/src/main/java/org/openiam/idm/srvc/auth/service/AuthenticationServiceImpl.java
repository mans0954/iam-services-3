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
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.LogoutException;
import org.openiam.exception.ScriptEngineException;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.context.AuthContextFactory;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.AuthStateId;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.LoginModuleSelector;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.spi.AbstractLoginModule;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.UserUtils;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

// import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author suneet
 */

@Service("authenticate")
@WebService(endpointInterface = "org.openiam.idm.srvc.auth.service.AuthenticationService", targetNamespace = "urn:idm.openiam.org/srvc/auth/service", portName = "AuthenticationServicePort", serviceName = "AuthenticationService")
@ManagedResource(objectName = "openiam:name=authenticationService", description = "Authentication Service")
@Transactional
public class AuthenticationServiceImpl extends AbstractBaseService implements AuthenticationService, ApplicationContextAware, BeanFactoryAware {

    @Autowired
    private AuthStateDAO authStateDao;

    @Autowired
    private LoginDataService loginManager;

    @Value("${org.openiam.core.login.authentication.context.class}")
    private String authContextClass;

    @Autowired
    private UserDataService userManager;

    @Autowired
    private PolicyDAO policyDao;

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private SysConfiguration sysConfiguration;

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

    @Override
    @ManagedAttribute
    public void globalLogout(String userId) throws Throwable {
        IdmAuditLog newLogoutEvent = new IdmAuditLog();
        newLogoutEvent.setUserId(userId);
        UserEntity userEntity = userManager.getUser(userId);
        LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
        newLogoutEvent.addTarget(userId, AuditTarget.USER.value(), primaryIdentity.getLogin());
        newLogoutEvent.setAction(AuditAction.LOGOUT.value());

        try {
            if (userId == null) {
                newLogoutEvent.fail();
                newLogoutEvent.setFailureReason("Target User object not passed");
                throw new NullPointerException("UserId is null");
            }

            final AuthStateEntity example = new AuthStateEntity();
            final AuthStateId id = new AuthStateId();
            id.setUserId(userId);
            example.setId(id);

            final List<AuthStateEntity> authStateList = authStateDao.getByExample(example);

            if (CollectionUtils.isEmpty(authStateList)) {
                newLogoutEvent.fail();
                newLogoutEvent.setFailureReason(String.format("Cannot find AuthState object for User: %s", userId));
                log.error("AuthState not found for userId=" + userId);
                throw new LogoutException();
            }

            for (final AuthStateEntity authSt : authStateList) {
                authSt.setAuthState(new BigDecimal(0));
                authSt.setToken("LOGOUT");
                authStateDao.saveAuthState(authSt);
            }
            newLogoutEvent.succeed();
        } finally {
            auditLogService.enqueue(newLogoutEvent);
        }
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        IdmAuditLog newLoginEvent = new IdmAuditLog();
        newLoginEvent.setUserId(null);
        newLoginEvent.setAction(AuditAction.LOGIN.value());

        final AuthenticationResponse authResp = new AuthenticationResponse(ResponseStatus.FAILURE);
        try {
            if (request == null) {
                newLoginEvent.fail();
                newLoginEvent.setFailureReason("Request object is null");
                throw new IllegalArgumentException("Request object is null");
            }

            final String principal = request.getPrincipal();
            final String password = request.getPassword();
            final String clientIP = request.getClientIP();
            final String nodeIP = request.getNodeIP();

            newLoginEvent.setClientIP(clientIP);
            newLoginEvent.setRequestorPrincipal(principal);

            AuthenticationContext ctx = null;
            AbstractLoginModule loginModule = null;
            String loginModName = null;
            LoginModuleSelector modSel = new LoginModuleSelector();

            LoginEntity lg = null;
            String userId = null;
            UserEntity user = null;

            newLoginEvent.setManagedSysId(sysConfiguration.getDefaultManagedSysId());

            // Determine which login module to use
            // - get the Authentication policy for the domain
            String authPolicyId = sysConfiguration.getDefaultAuthPolicyId();
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
	            	/*
	                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
	                        "INVALID LOGIN", secDomainId, null, principal, null,
	                        null, clientIP, nodeIP);
					*/
                    newLoginEvent.fail();
                    newLoginEvent.setFailureReason("Invalid Principlal");
                    // throw new
                    // AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);

                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                    return authResp;

                }

                if (StringUtils.isBlank(password)) {

                    log.debug("Invalid password");
	                /*
	                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
	                        "INVALID PASSWORD", secDomainId, null, principal, null,
	                        null, clientIP, nodeIP);
					*/

                    // throw new
                    // AuthenticationException(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                    newLoginEvent.fail();
                    newLoginEvent.setFailureReason("Invalid Password");
                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_PASSWORD);
                    return authResp;

                }

                lg = loginManager.getLoginByManagedSys(principal, sysConfiguration.getDefaultManagedSysId());

                if (lg == null) {
                    newLoginEvent.fail();
                    newLoginEvent.setFailureReason(
                            String.format("Cannot find login for principal '%s' and managedSystem '%s'",
                                    principal, sysConfiguration.getDefaultManagedSysId()));
	            	/*
	                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
	                        "INVALID LOGIN", secDomainId, null, principal, null,
	                        null, clientIP, nodeIP);
					*/
                    // throw new
                    // AuthenticationException(AuthenticationConstants.RESULT_INVALID_LOGIN);

                    authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_LOGIN);
                    return authResp;

                }

                // check the user status - move to the abstract class for reuse
                userId = lg.getUserId();
                newLoginEvent.setRequestorUserId(userId);

                newLoginEvent.setTargetUser(userId, lg.getLogin());

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
                    bindingMap.put("principal", principal);
                    bindingMap.put("sysId", sysConfiguration.getDefaultManagedSysId());
                    // also bind the user and login objects to avoid
                    // re-initialization of spring the scripting engine
                    bindingMap.put("login", lg);
                    bindingMap.put("user", user);

                    try {
                        loginModName = (String) scriptRunner.execute(bindingMap,
                                selPolicy.getValue1());
                    } catch (ScriptEngineException e) {
                        log.error("Can't execute script", e);
                    }

                }

                if (modSel.getModuleType() == LoginModuleSelector.MODULE_TYPE_LOGIN_MODULE) {
	            	/* here for backward compatability. in case a groovy script returned an actual class name, get 
	            	 * the spring bean name
	            	 */
                    try {
                        loginModName = Class.forName(loginModName).getAnnotation(Component.class).value();
                    } catch (Throwable e) {

                    }

                    loginModule = beanFactory.getBean(loginModName, AbstractLoginModule.class);
                    //loginModule = (AbstractLoginModule) LoginModuleFactory.createModule(loginModName);
                    loginModule.setUser(user);
                    loginModule.setLg(lg);
                    loginModule.setSysConfiguration(sysConfiguration);
                    loginModule.setAuthPolicyId(authPolicyId);
                }

            } catch (Throwable ie) {
                log.error(ie.getMessage(), ie);
                // throw (new
                // AuthenticationException(AuthenticationConstants.INTERNAL_ERROR,ie.getMessage(),ie));
                newLoginEvent.fail();
                newLoginEvent.setFailureReason(ie.getMessage());
                newLoginEvent.setException(ie);
                authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
                return authResp;
            }
            PasswordCredential cred = (PasswordCredential) ctx
                    .createCredentialObject(AuthenticationConstants.AUTHN_TYPE_PASSWORD);
            cred.setCredentials(principal, password);
            ctx.setCredential(AuthenticationConstants.AUTHN_TYPE_PASSWORD, cred);

            Map<String, Object> authParamMap = new HashMap<String, Object>();
            authParamMap.put("AUTH_SYS_ID", sysConfiguration.getDefaultManagedSysId());
            ctx.setAuthParam(authParamMap);

            ctx.setNodeIP(nodeIP);
            ctx.setClientIP(clientIP);

            Subject sub = null;
            if (modSel.getModuleType() == LoginModuleSelector.MODULE_TYPE_LOGIN_MODULE) {
                try {
                    sub = loginModule.login(ctx);

                } catch (AuthenticationException ae) {
                    final String erroCodeAsString = Integer.valueOf(ae.getErrorCode()).toString();
                    newLoginEvent.fail();
                    newLoginEvent.setFailureReason(erroCodeAsString);
                    newLoginEvent.addAttribute(AuditAttributeName.LOGIN_ERROR_CODE, erroCodeAsString);
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
                            break;
                    }
                    return authResp;
                } catch (Throwable e) {
                    log.error("Unknown Exception", e);
                    newLoginEvent.fail();
                    newLoginEvent.setFailureReason(e.getMessage());
                    newLoginEvent.setException(e);
                    authResp.setStatus(ResponseStatus.FAILURE);
                    authResp.setAuthErrorCode(AuthenticationConstants.INTERNAL_ERROR);
                    authResp.setAuthErrorMessage(e.getMessage());
                    return authResp;
                }
            }
            // add the sso token to the authstate

            updateAuthState(sub);
            //populateSubject(sub.getUserId(), sub);

            log.debug("*** PasswordAuth complete...Returning response object");

            newLoginEvent.succeed();
            authResp.setSubject(sub);
            authResp.setStatus(ResponseStatus.SUCCESS);
        } finally {
            auditLogService.enqueue(newLoginEvent);
        }
        return authResp;
    }

    @Override
    public Response renewToken(String principal, String token, String tokenType) {

        log.debug("RenewToken called.");

        Response resp = new Response(ResponseStatus.SUCCESS);

        // validateToken first

        PolicyEntity plcy = policyDao.findById(sysConfiguration.getDefaultAuthPolicyId());
        String attrValue = getPolicyAttribute(plcy.getPolicyAttributes(), "FAILED_AUTH_COUNT");
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_ISSUER");

        // get the userId of this token
        LoginEntity lg = loginManager.getLoginByManagedSys(principal, sysConfiguration.getDefaultManagedSysId());

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

        final AuthStateId id = new AuthStateId();
        id.setUserId(lg.getUserId());
        id.setTokenType("OPENIAM");

        AuthStateEntity authSt = authStateDao.findById(id);
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
            if (attr != null && policyAtr.getDefaultParametr() != null && StringUtils.equalsIgnoreCase(policyAtr.getDefaultParametr().getName(), name)) {
                return policyAtr.getValue1();
            }
        }
        return null;

    }

    private void updateAuthState(Subject sub) {

        final AuthStateEntity state = new AuthStateEntity(null, new BigDecimal(1),
                sub.getSsoToken().getExpirationTime().getTime(),
                sub.getSsoToken().getToken(), sub.getUserId(),
                "OPENIAM");

        authStateDao.saveAuthState(state);
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

    @Override
    public List<AuthStateEntity> findBeans(AuthStateSearchBean searchBean,
                                           int from, int size) {
        return authStateDao.getByExample(searchBean, from, size);
    }

    @Override
    @Transactional
    public Response save(final AuthStateEntity entity) {
        final Response response = new Response(ResponseStatus.SUCCESS);
        try {
            authStateDao.saveAuthState(entity);
        } catch (Throwable e) {
            log.error("Can't validate resource", e);
            response.setErrorText(e.getMessage());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}
