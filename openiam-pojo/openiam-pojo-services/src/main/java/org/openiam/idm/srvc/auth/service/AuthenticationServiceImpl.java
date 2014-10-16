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
import org.openiam.am.srvc.dao.AuthProviderDao;
import org.openiam.am.srvc.dao.ContentProviderDao;
import org.openiam.am.srvc.domain.AuthProviderEntity;
import org.openiam.am.srvc.domain.AuthProviderTypeEntity;
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.base.SysConfiguration;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
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
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.domain.AuthStateEntity;
import org.openiam.idm.srvc.auth.domain.AuthStateId;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.AuthenticationRequest;
import org.openiam.idm.srvc.auth.dto.LoginModuleSelector;
import org.openiam.idm.srvc.auth.dto.LogoutRequest;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.login.AuthStateDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.spi.AbstractScriptableLoginModule;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.auth.ws.AuthenticationResponse;
import org.openiam.idm.srvc.base.AbstractBaseService;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
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
	private ContentProviderDao contentProviderDAO;
    
    @Autowired
    private LoginDataService loginManager;
    
    @Autowired
    private UserDataService userManager;
    
    @Autowired
    private PolicyDAO policyDao;
    
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

    private static final Log log = LogFactory.getLog(AuthenticationServiceImpl.class);
    
    private AuthProviderEntity getAuthProvider(final String contentProviderId, final IdmAuditLog event) throws BasicDataServiceException {
    	AuthProviderEntity authProvider = null;
        if(StringUtils.isNotBlank(contentProviderId)) {
        	final ContentProviderEntity contentProvider = contentProviderDAO.findById(contentProviderId);
        	if(contentProvider == null) {
        		event.addWarning(String.format("Content provider with ID %s not found", contentProviderId));
        	} else {
        		authProvider = contentProvider.getAuthProvider();
        	}
        }
        
        if(authProvider == null) {
        	final String warning = String.format("No Content Provider of URI Pattern information provided, or could not resolve.  Using default AUth Provider: %s", sysConfiguration.getDefaultAuthProviderId());
        	event.addWarning(warning);
        	log.warn(warning);
        	authProvider = authProviderDAO.findById(sysConfiguration.getDefaultAuthProviderId());
        }
        
        if(authProvider == null) {
        	final String error = String.format("Could not find Authentication Provider with ID: %s.  Failing...", sysConfiguration.getDefaultAuthProviderId());
        	event.setFailureReason(error);
        	event.addWarning(error);
    		log.error(error);
    		throw new BasicDataServiceException(ResponseCode.AUTH_PROVIDER_NOT_FOUND);
    	}
        return authProvider;
    }
    
    @Override
    @Transactional
    @ManagedAttribute
    //@Transactional
	public Response globalLogoutWithContentProvider(final LogoutRequest request) {
    	final String userId = request.getUserId();
    	final String contentProviderId = request.getContentProviderId();
    	final IdmAuditLog newLogoutEvent = new IdmAuditLog();
        newLogoutEvent.setUserId(userId);
        newLogoutEvent.setAction(AuditAction.LOGOUT.value());
        final Response authResp = new Response(ResponseStatus.SUCCESS);

        try{
            if (userId == null) {
                newLogoutEvent.fail();
                newLogoutEvent.setFailureReason("Target User object not passed");
                throw new NullPointerException("UserId is null");
            }
            
            final AuthProviderEntity authProvider = getAuthProvider(contentProviderId, newLogoutEvent);
            final String springBeanName = authProvider.getSpringBeanName();
	        final String groovyScript = authProvider.getGroovyScriptURL();
	        AuthenticationModule loginModule = null;
	        if(StringUtils.isNotBlank(springBeanName)) {
	        	try {
	        		loginModule = (AuthenticationModule)ctx.getBean(springBeanName, AuthenticationModule.class);
	        	} catch(Throwable e) {
	        		log.error(String.format("Error while getting spring bean: %s", springBeanName), e);
	        	}
	        	
	        } else if(StringUtils.isNotBlank(groovyScript)) {
	        	loginModule = (AbstractScriptableLoginModule)scriptRunner.instantiateClass(null, groovyScript);
	        }
	        
	        if(loginModule == null) {
	        	loginModule = ctx.getBean(sysConfiguration.getDefaultLoginModule(), AuthenticationModule.class);
	        }
	        
	        loginModule.logout(request, newLogoutEvent);
        	
            final UserEntity userEntity = userManager.getUser(userId);
            final LoginEntity primaryIdentity = UserUtils.getUserManagedSysIdentityEntity(sysConfiguration.getDefaultManagedSysId(), userEntity.getPrincipalList());
            newLogoutEvent.addTarget(userId, AuditTarget.USER.value(), primaryIdentity.getLogin());
        	            
            final AuthStateEntity example = new AuthStateEntity();
            final AuthStateId id = new AuthStateId();
            id.setUserId(userId);
            example.setId(id);
            
            final List<AuthStateEntity> authStateList = authStateDao.getByExample(example);

            if (CollectionUtils.isEmpty(authStateList)) {
            	final String errorMessage = String.format("Cannot find AuthState object for User: %s", userId);
                newLogoutEvent.fail();
                newLogoutEvent.setFailureReason(errorMessage);
                log.error(errorMessage);
                throw new LogoutException(errorMessage);
            }
            
            for(final AuthStateEntity authSt : authStateList) {
            	authSt.setAuthState(new BigDecimal(0));
            	authSt.setToken("LOGOUT");
            	authStateDao.saveAuthState(authSt);
            }
            newLogoutEvent.succeed();
        } catch (BasicDataServiceException e) {
    		authResp.fail();
    		authResp.setErrorCode(e.getCode());
    		authResp.setErrorTokenList(e.getErrorTokenList());
    		newLogoutEvent.fail();
    		newLogoutEvent.setFailureReason(e.getMessage());
            newLogoutEvent.setException(e);
            newLogoutEvent.setFailureReason(e.getCode());
        } catch (Throwable e) {
            log.error("Can't Logout", e);
            authResp.fail();
            authResp.setErrorText(e.getMessage());
            newLogoutEvent.fail();
            newLogoutEvent.setFailureReason(e.getMessage());
            newLogoutEvent.setException(e);
    	} finally {
            auditLogService.enqueue(newLogoutEvent);
        }
        return authResp;
	}

    @Override
    @ManagedAttribute
    @Deprecated
    public void globalLogout(String userId) throws Throwable {
    	final LogoutRequest request = new LogoutRequest();
    	request.setUserId(userId);
        globalLogoutWithContentProvider(request);
    }

    @Override
    @Transactional
    public AuthenticationResponse login(final AuthenticationRequest request) {
        final IdmAuditLog newLoginEvent = new IdmAuditLog();
        newLoginEvent.setUserId(null);
        newLoginEvent.setAction(AuditAction.LOGIN.value());

    	final AuthenticationResponse authResp = new AuthenticationResponse(ResponseStatus.FAILURE);
    	try {
	        if (request == null) {
	            throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS);
	        }
	        
	        if(StringUtils.isBlank(request.getLanguageId())) {
	        	throw new BasicDataServiceException(ResponseCode.LANGUAGE_REQUIRED);
	        }
	        
	        final AuthProviderEntity authProvider = getAuthProvider(request.getContentProviderId(), newLoginEvent);
	        
	        final AuthenticationContext authenticationContext = new AuthenticationContext(request);
	        authenticationContext.setAuthProviderId(authProvider.getId());
	        authenticationContext.setEvent(newLoginEvent);
	        
	        final String principal = request.getPrincipal();
	        final String clientIP = request.getClientIP();

            newLoginEvent.setClientIP(clientIP);
            newLoginEvent.setRequestorPrincipal(principal);
	                
	        final String springBeanName = authProvider.getSpringBeanName();
	        final String groovyScript = authProvider.getGroovyScriptURL();
	        AuthenticationModule loginModule = null;
	        if(StringUtils.isNotBlank(springBeanName)) {
	        	try {
	        		loginModule = (AuthenticationModule)ctx.getBean(springBeanName, AuthenticationModule.class);
	        	} catch(Throwable e) {
	        		log.error(String.format("Error while getting spring bean: %s", springBeanName), e);
	        	}
	        } else if(StringUtils.isNotBlank(groovyScript)) {
	        	loginModule = (AbstractScriptableLoginModule)scriptRunner.instantiateClass(null, groovyScript);
	        }
	        
	        if(loginModule == null) {
	        	loginModule = ctx.getBean(sysConfiguration.getDefaultLoginModule(), AuthenticationModule.class);
	        }

	        final Subject sub = loginModule.login(authenticationContext);
	        updateAuthState(sub);
	        newLoginEvent.succeed();
	        authResp.setSubject(sub);
	        authResp.succeed();
	    /*
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
		*/
    	} catch (BasicDataServiceException e) {
    		authResp.fail();
    		authResp.setErrorCode(e.getCode());
    		authResp.setErrorTokenList(e.getErrorTokenList());
    		//authResp.setErrorCode(AuthenticationConstants.INTERNAL_ERROR);
    		newLoginEvent.fail();
            newLoginEvent.setFailureReason(e.getMessage());
            newLoginEvent.setException(e);
            newLoginEvent.setFailureReason(e.getCode());
        } catch (Throwable e) {
            log.error("Can't login", e);
            authResp.fail();
            authResp.setErrorText(e.getMessage());
            authResp.setErrorCode(ResponseCode.INTERNAL_ERROR);
            newLoginEvent.fail();
            newLoginEvent.setFailureReason(e.getMessage());
            newLoginEvent.setException(e);
    	} finally {
    		auditLogService.enqueue(newLoginEvent);
    	}
        return authResp;
    }

    @Override
    @Transactional
    public Response renewToken(final String principal, final String token, final String tokenType, final String contentProviderId) {
        final Response resp = new Response(ResponseStatus.SUCCESS);

        //final String contentProviderId = null;
        //TODO:  this is temproary.  Need to pass in content provider ID as argument from UI and proxy
        PolicyEntity policy = null;
        ManagedSysEntity managedSystem = null;
        if(StringUtils.isNotBlank(contentProviderId)) {
        	final ContentProviderEntity contentProvider = contentProviderDAO.findById(contentProviderId);
        	if(contentProvider != null) {
        		final AuthProviderEntity authProvider = contentProvider.getAuthProvider();
        		if(authProvider != null) {
        			policy = authProvider.getPolicy();
        			managedSystem = authProvider.getManagedSystem();
        		}
        	}
        }
        if(policy == null || managedSystem == null) {
        	final AuthProviderEntity authProvider = authProviderDAO.findById(sysConfiguration.getDefaultAuthProviderId());
        	policy = authProvider.getPolicy();
        	managedSystem = authProvider.getManagedSystem();
        }
        String tokenLife = getPolicyAttribute(policy,  "TOKEN_LIFE");
        final String tokenIssuer = getPolicyAttribute(policy, "TOKEN_ISSUER");
        if(StringUtils.isBlank(tokenLife)) {
        	tokenLife = "30"; //default;
        }

        // get the userId of this token
        final LoginEntity lg = loginManager.getLoginByManagedSys(principal, managedSystem.getId());

        if (lg == null) {
            resp.fail();
            return resp;
        }

        final Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);

        tokenParam.put("USER_ID", lg.getUserId());
        tokenParam.put("PRINCIPAL", principal);

        if (!isUserStatusValid(lg.getUserId())) {

            log.debug(String.format("RenewToken: user status failed for userId = %s", lg.getUserId()));

            resp.fail();
            return resp;

        }
        
        final AuthStateId id = new AuthStateId();
        id.setUserId(lg.getUserId());
        id.setTokenType("OPENIAM");

        AuthStateEntity authSt = authStateDao.findById(id);
        if (authSt != null) {

            if (authSt.getToken() == null
                    || "LOGOUT".equalsIgnoreCase(authSt.getToken())) {
            	resp.fail();
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
            	resp.fail();
                return resp;
            }

            final SSOToken ssoToken = tkModule.createToken(tokenParam);
            resp.setResponseValue(ssoToken);
        } catch (Throwable e) {
        	resp.fail();
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

    private String getPolicyAttribute(final PolicyEntity policy, final String name) {
        assert name != null : "Name parameter is null";
        final PolicyAttributeEntity entity = policy.getAttribute(name);
        return (entity != null) ? entity.getValue1() : null;
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
