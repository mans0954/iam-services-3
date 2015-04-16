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
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.exception.AuthenticationException;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.SSOToken;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.sso.SSOTokenFactory;
import org.openiam.idm.srvc.auth.sso.SSOTokenModule;
import org.openiam.idm.srvc.auth.ws.LoginDataWebService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.idm.srvc.mngsys.ws.ManagedSystemWebService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDataService;
import org.openiam.idm.srvc.pswd.service.PasswordService;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.idm.srvc.user.ws.UserDataWebService;
import org.openiam.provision.resp.LookupUserResponse;
import org.openiam.provision.service.*;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.script.ScriptIntegration;
import org.openiam.util.MuleContextProvider;
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
    @Qualifier("loginWS")
    protected LoginDataWebService loginManager;

    @Autowired
    @Qualifier("userWS")
    protected UserDataWebService userDataWebService;

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

    @Value("${org.openiam.auth.credentials.validator.groovy.script}")
    protected String authCredentialsValidatorScript;

    @Autowired
    protected AuthCredentialsValidator defaultAuthCredentialsValidator;

    @Autowired
    protected KeyManagementService keyManagementService;

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

    /**
     * Checks to see if the current date is after the start date for the user.
     *
     * @param user
     * @param curDate
     * @return
     */
    public boolean pendingInitialStartDateCheck(UserEntity user, Date curDate) {
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

    public void checkSecondaryStatus(UserEntity user) throws AuthenticationException {
        if (user.getSecondaryStatus() != null) {
            if (user.getSecondaryStatus().equals(UserStatusEnum.LOCKED)
                    || user.getSecondaryStatus().equals(
                    UserStatusEnum.LOCKED_ADMIN)) {
                log.debug("User is locked. throw exception.");
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_LOGIN_LOCKED);
            }
            if (user.getSecondaryStatus().equals(UserStatusEnum.DISABLED)) {
                throw new AuthenticationException(
                        AuthenticationConstants.RESULT_LOGIN_DISABLED);
            }
        }

    }

    public void setResultCode(Login lg, Subject sub, Date curDate, Policy pwdPolicy) throws AuthenticationException {
        if (lg.getFirstTimeLogin() == 1) {
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

    public Integer getDaysToPasswordExpiration(Login lg, Date curDate, Policy pwdPolicy) {
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

    protected String getPolicyAttribute(Set<PolicyAttribute> attr, String name) {
        assert name != null : "Name parameter is null";

        for (PolicyAttribute policyAtr : attr) {
            if (policyAtr.getName().equalsIgnoreCase(name)) {
                return policyAtr.getValue1();
            }
        }
        return null;
    }

    /**
     * If the password has expired, but its before the grace period then its a good login
     * If the password has expired and after the grace period, then its an exception.
     * You should also set the days to expiration
     *
     * @param lg
     * @return
     */
    protected int passwordExpired(Login lg, Date curDate) {
        log.debug("passwordExpired Called.");
        log.debug("- Password Exp =" + lg.getPwdExp());
        log.debug("- Password Grace Period =" + lg.getGracePeriod());

        if (lg.getGracePeriod() == null) {
            // set an early date
            Date gracePeriodDate = getGracePeriodDate(lg, curDate);
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

    private Date getGracePeriodDate(Login lg, Date curDate) {

        Date pwdExpDate = lg.getPwdExp();

        if (pwdExpDate == null) {
            return null;
        }

        Policy plcy = passwordManager.getPasswordPolicy(lg.getLogin(), lg.getManagedSysId());
        if (plcy == null) {
            return null;
        }

        /*
        String pswdExpValue = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXPIRATION");
        String changePswdOnReset = getPolicyAttribute(
                plcy.getPolicyAttributes(), "CHNG_PSWD_ON_RESET");
		*/
        String gracePeriod = getPolicyAttribute(plcy.getPolicyAttributes(),
                "PWD_EXP_GRACE");

        log.debug("Grace period policy value= " + gracePeriod);

        Calendar cal = Calendar.getInstance();
        cal.setTime(pwdExpDate);

        log.debug("Password Expiration date =" + pwdExpDate);

        if (gracePeriod != null && !gracePeriod.isEmpty()) {
            cal.add(Calendar.DATE, Integer.parseInt(gracePeriod));
            log.debug("Calculated grace period date=" + cal.getTime());
            return cal.getTime();
        }
        return null;

    }

    public LdapContext connect(String userName, String password, ManagedSysDto managedSys) throws NamingException {

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

}
