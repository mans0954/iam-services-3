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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.AuthenticationException;
import org.openiam.idm.srvc.auth.context.AuthenticationContext;
import org.openiam.idm.srvc.auth.context.PasswordCredential;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.dto.Subject;
import org.openiam.idm.srvc.auth.service.AuthenticationConstants;
import org.openiam.idm.srvc.auth.ws.LoginResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.service.AttributeMapDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSysDAO;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
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

// import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
// import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;

/**
 * DefaultLoginModule provides basic password based authentication using the OpenIAM repository.
 *
 * @author suneet
 */
@Scope("prototype")
@Component("activeDirectoryLoginModule")
public class ActiveDirectoryLoginModule extends AbstractLoginModule {

    private static final Log log = LogFactory
            .getLog(ActiveDirectoryLoginModule.class);

    PolicyAttribute baseDNAttribute = null;
    @Autowired
    private ManagedSysDAO managedSysDAO;

    @Autowired
    private AttributeMapDAO attributeMapDAO;


    public ActiveDirectoryLoginModule() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.openiam.idm.srvc.auth.spi.LoginModule#globalLogout(java.lang.String,
     * java.lang.String)
     */
    /*
    public void globalLogout(String securityDomain, String principal) {

    }
    */

    @Override
    public Subject login(AuthenticationContext authContext) throws Exception {

        Subject sub = new Subject();

        String authPolicyId = sysConfiguration.getDefaultAuthPolicyId();
        Policy authPolicy = policyDataService.getPolicy(authPolicyId);
        PolicyAttribute policyAttribute = authPolicy
                .getAttribute("MANAGED_SYS_ID");

        baseDNAttribute = authPolicy
                .getAttribute("BASEDN");

        if (policyAttribute == null) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        }
        ManagedSysEntity mngSys = managedSysDAO.findById(policyAttribute.getValue1());
        if (mngSys == null)
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_CONFIGURATION);
        String host = mngSys.getHostUrl();
        String managedSysId = mngSys.getId();
        String adminUserName = mngSys.getUserId();
        String adminPassword = this.decryptPassword(systemUserId, mngSys.getPswd());
        String protocol = mngSys.getCommProtocol();
        String baseDn = null;
        Set<ManagedSystemObjectMatchEntity> managedSystemObjectMatchEntities = mngSys.getMngSysObjectMatchs();
        if (CollectionUtils.isNotEmpty(managedSystemObjectMatchEntities)) {
            for (ManagedSystemObjectMatchEntity objectMatchEntity : managedSystemObjectMatchEntities) {
                if ("USER".equals(objectMatchEntity.getObjectType())) {
                    baseDn = objectMatchEntity.getBaseDn();
                    break;
                }
            }
        }

        log.debug("login() in ActiveDirectoryLoginModule called");

        // current date
        Date curDate = new Date(System.currentTimeMillis());
        PasswordCredential cred = (PasswordCredential) authContext
                .getCredential();

        String principal = cred.getPrincipal();
        String password = cred.getPassword();


        // check the user against AD

        // connect to ad with the admin account
        LdapContext ldapCtx = connect(adminUserName, adminPassword, host, protocol);
        log.info("Connection as admin to ad = " + ldapCtx);
        // search for the identity in the base dn
        List<AttributeMapEntity> attributeMapEntities = attributeMapDAO.findByManagedSysId(managedSysId);
        String[] returnArgs = new String[attributeMapEntities.size() + 1];
        int iter = 0;
        for (AttributeMapEntity attributeMapEntity : attributeMapEntities) {
            returnArgs[iter++] = attributeMapEntity.getAttributeName();
        }
        returnArgs[iter] = "distinguishedName";
        NamingEnumeration nameEnum = search(ldapCtx, principal, baseDn, returnArgs);

        // if found that get the full name, add the password and try to
        // authenticate.
        String distinguishedName = getDN(nameEnum);

        log.info("Distinguished name=" + distinguishedName);
        //maybe reset password and distinguishedName is entered
        if (distinguishedName == null) {
            distinguishedName = principal;
        }
        if (distinguishedName == null) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID_LOGIN",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);

        }
        // try to login to AD with this user
        LdapContext tempCtx = connect(distinguishedName, password, host, protocol);
        if (tempCtx == null) {
//            log("AUTHENTICATION", "AUTHENTICATION", "FAIL", "INVALID_PASSWORD",
//                    domainId, null, principal, null, null, clientIP, nodeIP);
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_PASSWORD);

        }

        log.debug("Authentication policyid="
                + sysConfiguration.getDefaultAuthPolicyId());
        // get the authentication lock out policy
        Policy plcy = policyDataService.getPolicy(sysConfiguration
                .getDefaultAuthPolicyId());
//        String attrValue = getPolicyAttribute(plcy.getPolicyAttributes(), "FAILED_AUTH_COUNT");

        String tokenType = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_TYPE");
        String tokenLife = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_LIFE");
        String tokenIssuer = getPolicyAttribute(plcy.getPolicyAttributes(), "TOKEN_ISSUER");

        Map tokenParam = new HashMap();
        tokenParam.put("TOKEN_TYPE", tokenType);
        tokenParam.put("TOKEN_LIFE", tokenLife);
        tokenParam.put("TOKEN_ISSUER", tokenIssuer);
        tokenParam.put("PRINCIPAL", distinguishedName);
        LoginResponse lg2Resp = loginManager.getLoginByManagedSys(distinguishedName, managedSysId);

        if (lg2Resp.getStatus() == ResponseStatus.FAILURE) {
            throw new AuthenticationException(
                    AuthenticationConstants.RESULT_INVALID_LOGIN);
        }
        Login lg = lg2Resp.getPrincipal();
        UserEntity user = this.userManager.getUser(lg.getUserId());


        // update the login and user records to show this authentication
        lg.setLastAuthAttempt(new Date(System.currentTimeMillis()));
        lg.setLastLogin(new Date(System.currentTimeMillis()));
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
//                user.getId(), principal, null, null, clientIP, nodeIP);

        return sub;
    }

    public LdapContext connect(String userName, String password, String host, String protocol) {

        // LdapContext ctxLdap = null;
        Hashtable<String, String> envDC = new Hashtable();

        System.setProperty("javax.net.ssl.trustStore", keystore);

        log.info("Connecting to AD using principal=" + userName);

        envDC.put(Context.PROVIDER_URL, host);
        envDC.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
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

        }
        return null;
    }

    private NamingEnumeration search(LdapContext ctx, String searchValue, String baseDn, String returnedAtts[]) {
        SearchControls searchCtls = new SearchControls();

        // Specify the attributes to returned
//        String returnedAtts[] = {"distinguishedName", "sAMAccountName", "cn",
//                "sn", "userPrincipalName"};
        searchCtls.setReturningAttributes(returnedAtts);

        // Specify the search scope
        try {
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            String searchFilter = "(&(objectClass=person)(sAMAccountName="
                    + searchValue + "))";
            if (baseDNAttribute != null && StringUtils.isNotBlank(baseDNAttribute.getValue1())) {
                searchFilter = baseDNAttribute.getValue1().replace("?", searchValue);
            }

            System.out.println("Search Filter=" + searchFilter);
            System.out.println("BaseDN=" + baseDn);
            NamingEnumeration result = ctx.search(baseDn, searchFilter, searchCtls);
            return result;
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        return null;

    }

    private String getDN(NamingEnumeration nameEnum) {
        String distinguishedName = null;

        try {
            while (nameEnum.hasMoreElements()) {
                SearchResult sr = (SearchResult) nameEnum.next();
                Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    distinguishedName = (String) attrs.get("distinguishedName")
                            .get();
                    log.info("getDN distguished name=" + distinguishedName);
                    if (distinguishedName != null) {
                        return distinguishedName;
                    }
                }
            }
        } catch (NamingException ne) {
            ne.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.openiam.idm.srvc.auth.spi.LoginModule#logout(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    /*
    public void logout(String securityDomain, String principal,
            String managedSysId) {

        log("AUTHENTICATION", "LOGOUT", "SUCCESS", null, securityDomain, null,
                principal, null, null, null, null);

    }
    */
}
