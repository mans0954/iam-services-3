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

import java.io.IOException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.URIPatternDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.am.srvc.domain.URIPatternEntity;
import org.openiam.base.SysConfiguration;
import org.openiam.base.request.OTPServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.concurrent.AuditLogHolder;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.LogoutException;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditTarget;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.AuthStateId;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.base.request.AuthenticationRequest;
import org.openiam.base.request.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.OTPRequestType;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.spi.AbstractSMSOTPModule;
import org.openiam.idm.srvc.auth.spi.AbstractScriptableLoginModule;
import org.openiam.idm.srvc.auth.spi.AbstractTOTPModule;
import org.openiam.idm.srvc.auth.spi.GoogleAuthTOTPModule;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.base.response.AuthenticationResponse;
import org.openiam.idm.srvc.continfo.dto.Phone;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.util.CustomJacksonMapper;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.UserUtils;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * @author suneet
 */

@Service
public class AuthenticationServiceImpl implements AuthenticationServiceService, BeanFactoryAware, ApplicationContextAware {

    @Autowired
    private AuthStateDAO authStateDao;

    @Autowired
    private URIPatternDao uriPatternDAO;

    @Autowired
    private LoginDataService loginManager;

    @Autowired
    private UserDataService userManager;

    @Autowired
    private AuthProviderDao authProviderDAO;

    @Autowired
    @Qualifier("cryptor")
    private Cryptor cryptor;

    @Autowired
    private SysConfiguration sysConfiguration;

    @Autowired
    protected KeyManagementService keyManagementService;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    private ScriptIntegration scriptRunner;

    private ApplicationContext ctx;

    private BeanFactory beanFactory;

    @Autowired
    private CustomJacksonMapper jacksonMapper;

    @Autowired
    protected AuditLogService auditLogService;

    private static final Log log = LogFactory.getLog(AuthenticationServiceImpl.class);

    private AuthProviderEntity getAuthProvider(final String patternId, final IdmAuditLogEntity event) throws BasicDataServiceException {
        AuthProviderEntity authProvider = null;
        if (StringUtils.isNotBlank(patternId)) {
            final URIPatternEntity uriPattern = uriPatternDAO.findById(patternId);
            if (uriPattern == null) {
                event.addWarning(String.format("Content provider with ID %s not found", patternId));
            } else {
                authProvider = uriPattern.getAuthProvider();
                if (authProvider == null) {
                    event.addWarning(String.format("URI Pattern '%s' does not have an authenticaitno pattern.  Using the content provider's pattern", patternId));
                    authProvider = uriPattern.getContentProvider().getAuthProvider();
                }
            }
        }

        if (authProvider == null) {
            final String warning = String.format("No Content Provider of URI Pattern information provided, or could not resolve.  Using default AUth Provider: %s", sysConfiguration.getDefaultAuthProviderId());
            event.addWarning(warning);
            log.warn(warning);
            authProvider = authProviderDAO.findById(sysConfiguration.getDefaultAuthProviderId());
        }

        if (authProvider == null) {
            final String error = String.format("Could not find Authentication Provider with ID: %s.  Failing...", sysConfiguration.getDefaultAuthProviderId());
            event.setFailureReason(error);
            event.addWarning(error);
            log.error(error);
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND, "Authentication provider is not found for given request");
        }
        return authProvider;
    }

    private AuthenticationModule getLoginModule(String springBeanName, String groovyScript) throws BasicDataServiceException {
        AuthenticationModule loginModule = null;

        if (StringUtils.isNotBlank(springBeanName)) {
            try {
                loginModule = (AuthenticationModule) ctx.getBean(springBeanName, AuthenticationModule.class);
            } catch (Throwable e) {
                log.error(String.format("Error while getting spring bean: %s", springBeanName), e);
                throw new BasicDataServiceException(ResponseCode.AUTHENTICATION_EXCEPTION, e.getMessage());
            }
        } else if (StringUtils.isNotBlank(groovyScript)) {
            try {
                loginModule = (AbstractScriptableLoginModule) scriptRunner.instantiateClass(null, groovyScript);
            } catch (Exception e) {
                log.error(String.format("Error while getting spring bean: %s", groovyScript), e);
                throw new BasicDataServiceException(ResponseCode.AUTHENTICATION_EXCEPTION, e.getMessage());
            }
        }
        // get default login module
        if (loginModule == null) {
            loginModule = ctx.getBean(sysConfiguration.getDefaultLoginModule(), AuthenticationModule.class);
        }
        return loginModule;
    }

    @Override
    @Transactional
    @ManagedAttribute
    //@Transactional
    public void globalLogoutRequest(final LogoutRequest request) throws BasicDataServiceException {
        final IdmAuditLogEntity newLogoutEvent = AuditLogHolder.getInstance().getEvent();

        final String userId = request.getUserId();
        final String patternId = request.getPatternId();
        newLogoutEvent.setUserId(userId);
        newLogoutEvent.setAction(AuditAction.LOGOUT.value());

        if (userId == null) {
            throw new BasicDataServiceException(ResponseCode.USER_NOT_SET, "Target User object not passed");
        }

        final AuthProviderEntity authProvider = getAuthProvider(patternId, newLogoutEvent);
        final String springBeanName = authProvider.getSpringBeanName();
        final String groovyScript = authProvider.getGroovyScriptURL();
        AuthenticationModule loginModule = getLoginModule(springBeanName, groovyScript);

        // TODO: refactor this when refactor login modules.
        // TODO: Login Modules should throw BasicDataServiceException or any inherited classes instead of Exception
        try {
            loginModule.logout(request, newLogoutEvent);
        } catch (Exception e) {
            log.error(String.format("Error while logout: %s", e.getMessage()), e);
            throw new BasicDataServiceException(ResponseCode.LOGOUT_EXCEPTION, e.getMessage());
        }

        final UserEntity userEntity = userManager.getUser(userId);
        final LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
        newLogoutEvent.addTarget(userId, AuditTarget.USER.value(), primaryIdentity.getLogin());

        final AuthStateId id = new AuthStateId();
        id.setUserId(userId);
        final AuthStateSearchBean sb = new AuthStateSearchBean();
        sb.setKey(id);
        final List<AuthStateEntity> authStateList = authStateDao.getByExample(sb);

        if (CollectionUtils.isEmpty(authStateList)) {
            final String errorMessage = String.format("Cannot find AuthState object for User: %s", userId);
            log.error(errorMessage);
            throw new BasicDataServiceException(ResponseCode.LOGOUT_EXCEPTION, errorMessage);
        }

        for (final AuthStateEntity authSt : authStateList) {
            authSt.setAuthState(new BigDecimal(0));
            authSt.setToken("LOGOUT");
            authStateDao.saveAuthState(authSt);
        }
    }

    @Override
    @Transactional
    public Subject login(final AuthenticationRequest request) throws BasicDataServiceException {
        final IdmAuditLogEntity newLoginEvent = AuditLogHolder.getInstance().getEvent();
        newLoginEvent.setUserId(null);
        newLoginEvent.setAction(AuditAction.LOGIN.value());

        if (request == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "AuthenticationRequest is null or empty");
        }

        if (StringUtils.isBlank(request.getLanguageId())) {
            throw new BasicDataServiceException(ResponseCode.LANGUAGE_REQUIRED, "Language is a required parameter for AuthenticationRequest");
        }

        final AuthProviderEntity authProvider = getAuthProvider(request.getPatternId(), newLoginEvent);
        if (authProvider != null) {
            newLoginEvent.setAuthProviderId(authProvider.getId());
        }

        final AuthenticationContext authenticationContext = new AuthenticationContext(request);
        authenticationContext.setAuthProviderId(authProvider.getId());
        authenticationContext.setEvent(newLoginEvent);

        final String principal = request.getPrincipal();
        final String clientIP = request.getClientIP();

        newLoginEvent.setClientIP(clientIP);
        newLoginEvent.setRequestorPrincipal(principal);

        final String springBeanName = authProvider.getSpringBeanName();
        final String groovyScript = authProvider.getGroovyScriptURL();
        AuthenticationModule loginModule = getLoginModule(springBeanName, groovyScript);

        // TODO: refactor this when refactor login modules.
        // TODO: Login Modules should throw BasicDataServiceException or any inherited classes instead of Exception
        Subject sub = null;
        try {
            sub = loginModule.login(authenticationContext);
        } catch (BasicDataServiceException e) {
            log.error(String.format("Error while authentication: %s", e.getMessage()), e);
            throw e;
        }catch (Exception e) {
            log.error(String.format("Error while authentication: %s", e.getMessage()), e);
            throw new BasicDataServiceException(ResponseCode.AUTHENTICATION_EXCEPTION, e.getMessage());
        }
        updateAuthState(sub);

        return sub;
    }

    @Override
    @Transactional
    public SSOToken renewToken(final String principal, final String token, final String tokenType, final String patternId) throws BasicDataServiceException {
        PolicyEntity policy = null;
        ManagedSysEntity managedSystem = null;
        if (StringUtils.isNotBlank(patternId)) {
            final URIPatternEntity uriPattern = uriPatternDAO.findById(patternId);
            if (uriPattern != null) {
                final ContentProviderEntity contentProvider = uriPattern.getContentProvider();
                if (contentProvider != null) {
                    final AuthProviderEntity authProvider = contentProvider.getAuthProvider();
                    if (authProvider != null) {
                        policy = authProvider.getPasswordPolicy();
                        managedSystem = authProvider.getManagedSystem();
                    }
                }
            }
        }
        if (policy == null || managedSystem == null) {
            final AuthProviderEntity authProvider = authProviderDAO.findById(sysConfiguration.getDefaultAuthProviderId());
            policy = authProvider.getPasswordPolicy();
            managedSystem = authProvider.getManagedSystem();
        }
        String tokenLife = getPolicyAttribute(policy, "TOKEN_LIFE");
        final String tokenIssuer = getPolicyAttribute(policy, "TOKEN_ISSUER");
        if (StringUtils.isBlank(tokenLife)) {
            tokenLife = "30"; //default;
        }

        // get the userId of this token
        final LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSystem.getId());
        if (lg == null) {
            throw new BasicDataServiceException(ResponseCode.INVALID_PRINCIPAL, "Login object is not found for given principal");
        }

        final Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);

        tokenParam.put("USER_ID", lg.getUserId());
        tokenParam.put("PRINCIPAL", principal);

        if (!isUserStatusValid(lg.getUserId())) {
            String msg = String.format("RenewToken: user status failed for userId = %s", lg.getUserId());
            if (log.isDebugEnabled()) {
                log.debug(msg);
            }
            throw new BasicDataServiceException(ResponseCode.RESULT_INVALID_USER_STATUS, msg);
        }

        final AuthStateId id = new AuthStateId();
        id.setUserId(lg.getUserId());
        id.setTokenType("OPENIAM");

        AuthStateEntity authSt = authStateDao.findById(id);
        if (authSt != null) {
            if (authSt.getToken() == null || "LOGOUT".equalsIgnoreCase(authSt.getToken())) {
                throw new BasicDataServiceException(ResponseCode.INVALID_AUTH_STATE, "AuthStateEntity has no token or user has already logged out");
            }

        }

        SSOTokenModule tkModule = SSOTokenFactory.createModule((String) tokenParam.get("TOKEN_TYPE"));
        tkModule.setCryptor(this.cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt(tokenLife));

        try {
            if (!tkModule.isTokenValid(lg.getUserId(), principal, token)) {
                throw new BasicDataServiceException(ResponseCode.INVALID_TOKEN, "Invalid token is provided");
            }

            final SSOToken ssoToken = tkModule.createToken(tokenParam);
            return ssoToken;
        } catch (BasicDataServiceException e) {
            throw e;
        } catch (Throwable e) {
            throw new BasicDataServiceException(ResponseCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    private boolean isUserStatusValid(String userId) {

        UserEntity u = userManager.getUser(userId);

        UserStatusEnum en = u.getStatus();

        UserStatusEnum secondaryStatus = u.getSecondaryStatus();

        if (en == UserStatusEnum.DELETED || en == UserStatusEnum.INACTIVE
                || en == UserStatusEnum.LEAVE || en == UserStatusEnum.TERMINATED) {
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

    private String getPolicyAttribute(final PolicyEntity policy, final String name) {
        assert name != null : "Name parameter is null";
        final PolicyAttributeEntity entity = policy.getAttribute(name);
        return (entity != null) ? entity.getValue1() : null;
    }

    private void updateAuthState(Subject sub) {
        log.info(String.format("Subject=%s", sub));
        log.info(String.format("Subject.ssoToken=%s", sub.getSsoToken()));
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
    public List<AuthStateEntity> findBeans(AuthStateSearchBean searchBean, int from, int size) {
        return authStateDao.getByExample(searchBean, from, size);
    }

    @Override
    @Transactional
    public void save(final AuthStateEntity entity) {
        authStateDao.saveAuthState(entity);
    }

    private LoginEntity getLogin(final String userId, final String managedSysId) {
        final List<LoginEntity> principals = loginManager.getLoginByUser(userId);
        LoginEntity login = null;
        if (CollectionUtils.isNotEmpty(principals)) {
            for (final LoginEntity principal : principals) {
                if (StringUtils.equals(principal.getManagedSysId(), managedSysId)) {
                    login = principal;
                    break;
                }
            }
        }
        return login;
    }


    @Override
    @Transactional
    public String getOTPSecretKey(OTPServiceRequest request) throws BasicDataServiceException {
        final IdmAuditLogEntity event = AuditLogHolder.getInstance().getEvent();
        event.setUserId(null);
        event.setAction(AuditAction.GET_QR_CODE.value());
        event.setUriPatternId(request.getPatternId());
        event.addAttributeAsJson(AuditAttributeName.PHONE, request.getPhone(), jacksonMapper);
        event.setUserId(request.getUserId());

        final String userId = request.getUserId();
        final Phone phone = request.getPhone();

        final AuthProviderEntity authProvider = getAuthProvider(request.getPatternId(), event);
        if (authProvider == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND_FOR_CONTENT_PROVIDER);
        }
        event.setAuthProviderId(authProvider.getId());

        final ManagedSysEntity managedSystem = authProvider.getManagedSystem();
        if (managedSystem == null) {
            throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
        }
        event.setManagedSysId(managedSystem.getId());

        final LoginEntity login = getLogin(userId, managedSystem.getId());

        if (login == null) {
            throw new BasicDataServiceException(ResponseCode.PRINCIPAL_NOT_FOUND);
        }
        event.setPrincipal(login.getLogin());

        if (request.getRequestType() == null) {
            throw new BasicDataServiceException(ResponseCode.OTP_TYPE_MISSING);
        }

        final AbstractTOTPModule module = new GoogleAuthTOTPModule();
        final String secretKey = module.generateSecret(phone, login);
        return secretKey;

    }

    @Override
    @Transactional
    public void sendOTPToken(final OTPServiceRequest request) throws BasicDataServiceException {
        final IdmAuditLogEntity event = AuditLogHolder.getInstance().getEvent();
        event.setUserId(null);
        event.setAction(AuditAction.SEND_SMS_OTP_TOKEN.value());
        event.setUriPatternId(request.getPatternId());
        event.addAttributeAsJson(AuditAttributeName.PHONE, request.getPhone(), jacksonMapper);
        event.setUserId(request.getUserId());

        final Phone phone = request.getPhone();
        final String userId = request.getUserId();
        final AuthProviderEntity authProvider = getAuthProvider(request.getPatternId(), event);
        if (authProvider == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND_FOR_CONTENT_PROVIDER);
        }
        event.setAuthProviderId(authProvider.getId());

        final ManagedSysEntity managedSystem = authProvider.getManagedSystem();
        if (managedSystem == null) {
            throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
        }
        event.setManagedSysId(managedSystem.getId());

        if (request.getRequestType() == null) {
            throw new BasicDataServiceException(ResponseCode.OTP_TYPE_MISSING);
        }

        final PolicyEntity policy = authProvider.getPasswordPolicy();
        final PolicyAttributeEntity attribute = policy.getAttribute("OTP_SMS_LIFETIME");
        int numOfMinutesOfSMSValidity = 30;
        if (attribute != null) {
            try {
                numOfMinutesOfSMSValidity = Integer.valueOf(attribute.getValue1());
            } catch (Throwable e) {
                log.warn("Can't parse numOfMinutesOfSMSValidity. Check OTP_SMS_LIFETIME in Auth Policy");
            }
        }

        AbstractSMSOTPModule module = null;
        try {
            module = (AbstractSMSOTPModule) scriptRunner.instantiateClass(null, authProvider.getSmsOTPGroovyScript());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        if (module == null) {
            final String errorMessage = String.format("Could not create %s from groovy script %s", AbstractSMSOTPModule.class, authProvider.getSmsOTPGroovyScript());
            event.addWarning(errorMessage);
            log.error(errorMessage);
            throw new BasicDataServiceException(ResponseCode.SMS_OTP_GROOVY_SCRIPT_REQUIRED);
        }

        final LoginEntity login = getLogin(userId, managedSystem.getId());
        if (login == null) {
            throw new BasicDataServiceException(ResponseCode.PRINCIPAL_NOT_FOUND);
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, numOfMinutesOfSMSValidity);
        calendar.set(Calendar.MILLISECOND, 0);
        //login.setSmsCodeExpiration(calendar.getTime());
        login.setSmsCodeExpiration(new Timestamp(calendar.getTime().getTime()));
        loginManager.updateLogin(login);

        String token = null;
        try {
            token = module.generateSMSToken(phone, login);
        } catch (InvalidKeyException e) {
            log.error(String.format("Cannot create token: %s", e.getMessage()), e);
            throw new BasicDataServiceException(ResponseCode.SMS_TOKEN_GENERATE_ERROR, e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            log.error(String.format("Cannot create token: %s", e.getMessage()), e);
            throw new BasicDataServiceException(ResponseCode.SMS_TOKEN_GENERATE_ERROR, e.getMessage());
        }
        event.addAttribute(AuditAttributeName.SMS_TOKEN, token);
    }

    private String decryptTOTP(final Phone phone, final String userId) {
        final String secret = phone.getTotpSecret();
        String retVal = null;
        if (secret != null) {
            try {
                retVal = keyManagementService.decrypt(userId, KeyName.token, secret);
            } catch (Throwable e) {
                log.error("Can't decrypt secret", e);
            }
        }
        return retVal;
    }

    @Override
    public void confirmOTPToken(final OTPServiceRequest request) throws BasicDataServiceException {
        final IdmAuditLogEntity event = AuditLogHolder.getInstance().getEvent();
        event.setUserId(request.getUserId());
        event.setAction(AuditAction.CONFIRM_SMS_OTP_TOKEN.value());
        event.setUriPatternId(request.getPatternId());
        event.addAttributeAsJson(AuditAttributeName.PHONE, request.getPhone(), jacksonMapper);
        event.setUserId(request.getUserId());

        final String userId = request.getUserId();
        final AuthProviderEntity authProvider = getAuthProvider(request.getPatternId(), event);
        if (authProvider == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND_FOR_CONTENT_PROVIDER);
        }
        event.setAuthProviderId(authProvider.getId());
        final ManagedSysEntity managedSystem = authProvider.getManagedSystem();
        if (managedSystem == null) {
            throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
        }
        event.setManagedSysId(managedSystem.getId());
        final LoginEntity login = getLogin(userId, managedSystem.getId());

        if (login == null) {
            throw new BasicDataServiceException(ResponseCode.PRINCIPAL_NOT_FOUND);
        }
        event.setPrincipal(login.getLogin());

        if (request.getRequestType() == null) {
            throw new BasicDataServiceException(ResponseCode.OTP_TYPE_MISSING);
        }

        if (OTPRequestType.SMS.equals(request.getRequestType())) {
            String smsCode = null;
            try {
                final AbstractSMSOTPModule module = (AbstractSMSOTPModule) scriptRunner.instantiateClass(null, authProvider.getSmsOTPGroovyScript());
                smsCode = module.generateRFC4226Token(login);
            } catch (Exception e) {
                throw new BasicDataServiceException(ResponseCode.SMS_TOKEN_GENERATE_ERROR);
            }

            if (!StringUtils.equals(request.getOtpCode(), smsCode)) {
                throw new BasicDataServiceException(ResponseCode.SMS_CODES_NOT_EQUAL);
            }

            if (login.getSmsCodeExpiration() != null) {
                if (new Date().after(login.getSmsCodeExpiration())) {
                    throw new BasicDataServiceException(ResponseCode.SMS_CODE_EXPIRED);
                }
            }
            login.setSmsActive(true);
        } else {
            final AbstractTOTPModule module = new GoogleAuthTOTPModule();
            final String secret = (StringUtils.isNotBlank(request.getSecret())) ? request.getSecret() : decryptTOTP(request.getPhone(), userId);
            try {
                if (!module.validateToken(secret, Integer.valueOf(request.getOtpCode()).intValue())) {
                    throw new BasicDataServiceException(ResponseCode.TOPT_CODE_INVALID);
                }
                login.setToptActive(true);
            } catch (NumberFormatException e) {
                throw new BasicDataServiceException(ResponseCode.TOPT_CODE_INVALID);
            }
        }
        login.setSmsCodeExpiration(null);
        loginManager.updateLogin(login);
    }

    @Override
    @Transactional
    public void clearOTPActiveStatus(final OTPServiceRequest request) throws BasicDataServiceException {
        final IdmAuditLogEntity event = AuditLogHolder.getInstance().getEvent();
        event.setUserId(null);
        event.setAction(AuditAction.CLEAR_SMS_OTP_STATUS.value());
        event.setUriPatternId(request.getPatternId());
        event.setUserId(request.getUserId());

        final AuthProviderEntity authProvider = getAuthProvider(request.getPatternId(), event);
        final ManagedSysEntity managedSystem = (authProvider != null) ? authProvider.getManagedSystem() : null;
        if (authProvider == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND_FOR_CONTENT_PROVIDER);
        }
        event.setAuthProviderId(authProvider.getId());

        final LoginEntity login = getLogin(request.getUserId(), managedSystem.getId());

        if (login == null) {
            throw new BasicDataServiceException(ResponseCode.PRINCIPAL_NOT_FOUND);
        }
        event.setPrincipal(login.getLogin());
        event.setManagedSysId(managedSystem.getId());

        login.setSmsActive(false);
        login.setToptActive(false);
        loginManager.updateLogin(login);
    }

    @Override
    public boolean isOTPActive(final OTPServiceRequest request) throws BasicDataServiceException {
        final IdmAuditLogEntity event = AuditLogHolder.getInstance().getEvent();
        event.setUserId(null);
        event.setAction(AuditAction.GET_SMS_OTP_STATUS.value());
        event.setUriPatternId(request.getPatternId());
        event.setUserId(request.getUserId());

        boolean retVal = false;
        final AuthProviderEntity authProvider = getAuthProvider(request.getPatternId(), event);
        if (authProvider == null) {
            throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND_FOR_CONTENT_PROVIDER);
        }
        event.setAuthProviderId(authProvider.getId());

        if (request.getRequestType() == null) {
            throw new BasicDataServiceException(ResponseCode.OTP_TYPE_MISSING);
        }

        final ManagedSysEntity managedSystem = authProvider.getManagedSystem();
        if (managedSystem == null) {
            throw new BasicDataServiceException(ResponseCode.MANAGED_SYSTEM_NOT_SET);
        }
        event.setManagedSysId(managedSystem.getId());

        final LoginEntity login = getLogin(request.getUserId(), managedSystem.getId());

        if (login == null) {
            throw new BasicDataServiceException(ResponseCode.PRINCIPAL_NOT_FOUND);
        }
        event.setPrincipal(login.getLogin());

        switch (request.getRequestType()) {
            case SMS:
                retVal = login.isSmsActive();
                break;
            case TOPT:
                retVal = login.isToptActive();
                break;
        }
        return retVal;
    }
}
