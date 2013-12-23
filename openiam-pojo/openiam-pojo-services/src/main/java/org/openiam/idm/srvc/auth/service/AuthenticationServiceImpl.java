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
import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.openiam.idm.srvc.audit.annotation.AuditLoggable;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.constant.AuditResult;
import org.openiam.idm.srvc.audit.constant.AuditSource;
import org.openiam.idm.srvc.audit.domain.AuditLogBuilder;
import org.openiam.idm.srvc.audit.service.AuditLogService;
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
import org.openiam.idm.srvc.base.AbstractBaseService;
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
public class AuthenticationServiceImpl extends AbstractBaseService implements AuthenticationService, ApplicationContextAware, BeanFactoryAware {

	@Autowired
    private AuthStateDAO authStateDao;
    
    @Autowired
    private LoginDataService loginManager;
    
    @Autowired
    private SecurityDomainDAO securityDomainDAO;
    
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
        final AuditLogBuilder auditBuilder=auditLogProvider.getAuditLogBuilder();
        try{
            auditBuilder.setRequestorUserId(userId).setTargetUser(userId).setAction(AuditAction.LOGOUT);

            if (userId == null) {
                auditBuilder.fail().setFailureReason("Target User object not passed");
                throw new NullPointerException("UserId is null");
            }

            AuthStateEntity authSt = authStateDao.findById(userId);
            if (authSt == null) {
                auditBuilder.fail().setFailureReason(String.format("Cannot find AuthState object for User: %s",userId));
                log.error("AuthState not found for userId=" + userId);
                throw new LogoutException();
            }

            authSt.setAuthState(new BigDecimal(0));
            authSt.setToken("LOGOUT");
            authStateDao.saveAuthState(authSt);
            auditBuilder.succeed();
        /*
        } catch (Throwable ex){
           if(!AuditResult.FAILURE.value().equals(auditBuilder.getEntity().getResult()))
               auditBuilder.setResult(AuditResult.FAILURE).addAttribute(AuditAttributeName.FAILURE_REASON, ex.getMessage());

           throw ex;
		*/
        } finally {
            auditLogService.enqueue(auditBuilder);
        }
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
    	final AuditLogBuilder auditBuilder=auditLogProvider.getAuditLogBuilder().setAction(AuditAction.LOGIN);
    	final AuthenticationResponse authResp = new AuthenticationResponse(ResponseStatus.FAILURE);
    	try {
	        if (request == null) {
	        	auditBuilder.fail().setFailureReason("Request object is null");
	            throw new IllegalArgumentException("Request object is null");
	        }
	
	        final String secDomainId = request.getDomainId();
	        final String principal = request.getPrincipal();
	        final String password = request.getPassword();
	        final String clientIP = request.getClientIP();
	        final String nodeIP = request.getNodeIP();
	
	        auditBuilder.setClientIP(clientIP).setRequestorPrincipal(principal);
	        
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
	        	auditBuilder.fail().setFailureReason(String.format("Security domain %s is invalid", secDomainId));
	            authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_DOMAIN);
	            return authResp;
	        }
	        auditBuilder.setManagedSysId(secDomain.getAuthSysId());
	
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
	            	/*
	                log("AUTHENTICATION", "AUTHENTICATION", "FAIL",
	                        "INVALID LOGIN", secDomainId, null, principal, null,
	                        null, clientIP, nodeIP);
					*/
	            	auditBuilder.fail().setFailureReason("Invalid Principlal");
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
	                auditBuilder.fail().setFailureReason("Invalid Password");
	                authResp.setAuthErrorCode(AuthenticationConstants.RESULT_INVALID_PASSWORD);
	                return authResp;
	
	            }
	
	            lg = loginManager.getLoginByManagedSys(principal, secDomain.getAuthSysId());
	
	            if (lg == null) {
	            	auditBuilder.fail().setFailureReason(
	            			String.format("Cannot find login for security domain '%s', principal '%s' and managedSystem '%s'", 
	            					secDomainId, principal, secDomain.getAuthSysId()));
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
	            auditBuilder.setRequestorUserId(userId).setTargetUser(userId);
	            
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
	                    log.error("Can't execute script", e);
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
	            auditBuilder.fail().setFailureReason(ie.getMessage()).setException(ie);
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
	            	final String erroCodeAsString = Integer.valueOf(ae.getErrorCode()).toString();
	            	auditBuilder.fail().setFailureReason(erroCodeAsString).addAttribute(AuditAttributeName.LOGIN_ERROR_CODE, erroCodeAsString);
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
	            	auditBuilder.fail().setFailureReason(e.getMessage()).setException(e);
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
	
	        auditBuilder.succeed();
	        authResp.setSubject(sub);
	        authResp.setStatus(ResponseStatus.SUCCESS);
    	} finally {
    		auditLogService.enqueue(auditBuilder);
    	}
        return authResp;
    }

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

    private void updateAuthState(Subject sub) {

    	AuthStateEntity state = new AuthStateEntity(sub.getDomainId(), new BigDecimal(1),
                sub.getSsoToken().getExpirationTime().getTime(), sub
                        .getSsoToken().getToken(), sub.getUserId());

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
}
