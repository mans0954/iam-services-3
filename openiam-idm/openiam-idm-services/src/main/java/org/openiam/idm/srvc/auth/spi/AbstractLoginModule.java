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
import org.openiam.base.SysConfiguration;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.service.AuthenticationUtils;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.service.*;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.encrypt.Cryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import java.util.*;

/**
 * @author suneet
 */
public abstract class AbstractLoginModule implements LoginModule {

    @Autowired
    @Qualifier("defaultSSOToken")
    protected SSOTokenModule defaultToken;

    @Autowired
    @Qualifier("defaultProvision")
    protected ProvisionService provisionService;

    @Autowired
    protected ManagedSystemService managedSysDataService;

    @Autowired
    @Qualifier("managedSysService")
    protected ManagedSystemWebService managedSystemWebService;

    @Autowired
    @Qualifier("loginManager")
    protected LoginDataService loginManager;
    @Autowired
    @Qualifier("userManager")
    protected UserDataService userManager;

    @Autowired
    @Qualifier("cryptor")
    protected Cryptor cryptor;

    @Autowired
    @Qualifier("configurableGroovyScriptEngine")
    protected ScriptIntegration scriptRunner;

    @Autowired
    protected ManagedSystemWebService managedSysService;

    @Autowired
    protected ConnectorAdapter connectorAdapter;

    @Autowired
    protected ResourceDataService resourceService;

    @Autowired
    protected PasswordService passwordManager;

    @Autowired
    protected PolicyDataService policyDataService;

    @Autowired
    protected SysConfiguration sysConfiguration;

    @Value("${KEYSTORE}")
    protected String keystore;

    @Value("${KEYSTORE_PSWD}")
    protected String keystorePasswd;

    @Value("${org.openiam.idm.system.user.id}")
    protected String systemUserId;

    @Autowired
    protected KeyManagementService keyManagementService;

    @Autowired
    protected AuthenticationUtils authenticationUtils;

    private static final Log log = LogFactory.getLog(AbstractLoginModule.class);

    public String decryptPassword(String userId, String encPassword)
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

    public String encryptPassword(String userId, String decPassword)
            throws Exception {
        if (decPassword != null) {
            try {
                return cryptor.encrypt(keyManagementService.getUserKey(userId,
                        KeyName.password.name()), decPassword);
            } catch (EncryptionException e) {
                return null;
            }
        }
        return null;
    }

    public void setResultCode(LoginEntity lg, Subject sub, Date curDate, Policy pwdPolicy, final boolean skipPasswordCheck) throws AuthenticationException {
    	if(skipPasswordCheck) {
    		sub.setResultCode(AuthenticationConstants.RESULT_SUCCESS);
    	} else if (lg.getFirstTimeLogin() == 1) {
            sub.setResultCode(AuthenticationConstants.RESULT_SUCCESS_FIRST_TIME);
        } else if (lg.getPwdExp() != null) {
            if ((curDate.after(lg.getPwdExp()) && curDate.before(lg.getGracePeriod()))) {
                // check for password expiration, but successful login
                sub.setResultCode(AuthenticationConstants.RESULT_SUCCESS_PASSWORD_EXP);
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
                if (pwdExp > 0) {
                    throw new AuthenticationException(AuthenticationConstants.RESULT_PASSWORD_EXPIRED);
                }
            }
            sub.setResultCode(AuthenticationConstants.RESULT_SUCCESS);
        }

    }

    public Integer getDaysToPasswordExpiration(LoginEntity lg, Date curDate, Policy pwdPolicy) {
        if (pwdPolicy != null && StringUtils.isBlank(pwdPolicy.getAttribute("PWD_EXPIRATION").getValue1())) {
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

    public void setSysConfiguration(SysConfiguration sysConfiguration) {
        this.sysConfiguration = sysConfiguration;
    }

    protected SSOToken token(String userId, Map tokenParam) throws Exception {
    	if(log.isDebugEnabled()) {
    		log.debug("Generating Security Token");
    	}

        tokenParam.put("USER_ID", userId);

        SSOTokenModule tkModule = SSOTokenFactory
                .createModule((String) tokenParam.get("TOKEN_TYPE"));
        tkModule.setCryptor(cryptor);
        tkModule.setKeyManagementService(keyManagementService);
        tkModule.setTokenLife(Integer.parseInt((String) tokenParam
                .get("TOKEN_LIFE")));

        return tkModule.createToken(tokenParam);
    }

    protected String getPolicyAttribute(Set<PolicyAttribute> attr, String name) {
        assert name != null : "Name parameter is null";

        for (PolicyAttribute policyAtr : attr) {
            if (policyAtr.getName().equalsIgnoreCase(name)) {
                return policyAtr.getValue1();
            }
        }
        return null;
    }

    public LdapContext connect(String userName, String password, ManagedSysDto managedSys) throws NamingException {

        if (keystore != null && !keystore.isEmpty()) {
            System.setProperty("javax.net.ssl.trustStore", keystore);
            System.setProperty("javax.net.ssl.keyStorePassword", keystorePasswd);
        }

        if (managedSys == null) {
        	if(log.isDebugEnabled()) {
        		log.debug("ManagedSys is null");
        	}
            return null;
        }

        String hostUrl = managedSys.getHostUrl();
        if (managedSys.getPort() > 0) {
            hostUrl = hostUrl + ":" + String.valueOf(managedSys.getPort());
            if (!hostUrl.startsWith("ldap")) {
                hostUrl = "ldap://" + hostUrl;
            }
        }

        if(log.isDebugEnabled()) {
	        log.debug("connect: Connecting to target system: " + managedSys.getId());
	        log.debug("connect: Managed System object : " + managedSys);
        }
        if(log.isInfoEnabled()) {
	        log.info(" directory login = " + managedSys.getUserId());
	        log.info(" directory login passwrd= *****");
	        log.info(" javax.net.ssl.trustStore= " + System.getProperty("javax.net.ssl.trustStore"));
	        log.info(" javax.net.ssl.keyStorePassword= " + System.getProperty("javax.net.ssl.keyStorePassword"));
        }

        Hashtable<String, String> envDC = new Hashtable();
        envDC.put(Context.PROVIDER_URL, hostUrl);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
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

        } catch (NamingException ne) {
            log.error(ne.toString(), ne);

        } catch (Throwable e) {
            log.error(e.toString(), e);
        }

        return ldapContext;
    }

}
