package org.openiam.connector.ldap.dirtype;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.base.SysConfiguration;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.ldap.LdapContext;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implements directory specific extensions for standard LDAP v3
 * User: suneetshah
 */
public class LdapV3 implements Directory {

    @Autowired
    private PasswordGenerator passwordGenerator;

    final static String PASSWORD_ATTRIBUTE = "userPassword";
    // let it be for future
//    final static String PASSWORD_LAST_SET = "pwdLastSet";

    Map<String, Object> objectMap = new HashMap<String, Object>();
    private static final Log log = LogFactory.getLog(LdapV3.class);

    // Should be any way to split set password and reset password
    public ModificationItem[] resetPassword(PasswordRequest reqType) throws UnsupportedEncodingException {

        ModificationItem[] mods = new ModificationItem[1];
        String userPassword = getUserPassword(reqType.getExtensibleObject());
        if (StringUtils.isEmpty(userPassword)) {
            userPassword = reqType.getPassword();
        }
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(PASSWORD_ATTRIBUTE, userPassword));
        //skip for now for LDAP
//        mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(PASSWORD_LAST_SET, "0"));

        return mods;
    }

    public ModificationItem[] setPassword(PasswordRequest reqType) throws UnsupportedEncodingException {

        ModificationItem[] mods = new ModificationItem[1];
        String userPassword = getUserPassword(reqType.getExtensibleObject());
        if (StringUtils.isEmpty(userPassword)) {
            userPassword = reqType.getPassword();
        }
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(PASSWORD_ATTRIBUTE, userPassword));

        return mods;
    }

    public ModificationItem[] suspend(SuspendResumeRequest request) {

        String scrambledPswd = getScrambledPswd(request.getExtensibleObject());

        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(PASSWORD_ATTRIBUTE, scrambledPswd));

        return mods;
    }

    public ModificationItem[] resume(SuspendResumeRequest request) {

        String ldapName = (String) objectMap.get("LDAP_NAME");
        LoginDataService loginManager = (LoginDataService) objectMap.get("LOGIN_MANAGER");
        SysConfiguration sysConfiguration = (SysConfiguration) objectMap.get("CONFIGURATION");
        String targetID = (String) objectMap.get("TARGET_ID");

        try {

            String decPassword = getUserPassword(request.getExtensibleObject());
            if (StringUtils.isEmpty(decPassword)) {
                // get the current password for the user.
                LoginEntity login = loginManager.getLoginByManagedSys(ldapName, targetID);
                String encPassword = login.getPassword();
                decPassword = loginManager.decryptPassword(login.getUserId(), encPassword);
            }

            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(PASSWORD_ATTRIBUTE, decPassword));
            return mods;
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }

    public void delete(CrudRequest reqType, LdapContext ldapctx, String ldapName, String onDelete) throws NamingException {

        if ("DELETE".equalsIgnoreCase(onDelete)) {

            ldapctx.destroySubcontext(ldapName);

        } else if ("DISABLE".equalsIgnoreCase(onDelete)) {

            String scrambledPswd = getScrambledPswd(reqType.getExtensibleObject());

            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(PASSWORD_ATTRIBUTE, scrambledPswd));
            ldapctx.modifyAttributes(ldapName, mods);

        }
    }

    /* Group membership functions */

    public void removeAccountMemberships(ManagedSysEntity managedSys, String identity, String identityDN, ManagedSystemObjectMatch matchObj, LdapContext ldapctx) {

        List<String> currentMembershipList = userMembershipList(managedSys, identityDN, matchObj, ldapctx);

        // remove membership
        if (currentMembershipList != null) {

            for (String s : currentMembershipList) {
                try {
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("uniqueMember", identityDN));
                    ldapctx.modifyAttributes(s, mods);
                } catch (NamingException ne) {
                    log.error(ne);
                }
            }
        }
    }

    public void removeSupervisorMemberships(ManagedSysEntity managedSys, String identity, String identityDN, ManagedSystemObjectMatch matchObj, LdapContext ldapctx) {

        List<String> currentSupervisorMembershipList = userSupervisorMembershipList(managedSys, identity, matchObj, ldapctx);

        // remove membership
        if (currentSupervisorMembershipList != null) {

            for (String s : currentSupervisorMembershipList) {
                try {
                    log.debug("Removing supervisor: " + s + " from " + identityDN);
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("manager", s));
                    ldapctx.modifyAttributes(identityDN, mods);
                } catch (NamingException ne) {
                    log.error(ne);
                }
            }
        }
    }

    public void updateAccountMembership(ManagedSysEntity managedSys, List<BaseAttribute> targetMembershipList, String identity, String identityDN,
                                        ManagedSystemObjectMatch matchObj, LdapContext ldapctx,
                                        ExtensibleObject obj) {

        List<String> currentMembershipList = userMembershipList(managedSys, identityDN, matchObj, ldapctx);

        log.debug("Current ldap role membership:" + currentMembershipList);

        if (targetMembershipList == null && currentMembershipList != null) {

            for (String s : currentMembershipList) {
                try {
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("uniqueMember", identityDN));
                    ldapctx.modifyAttributes(s, mods);
                } catch (NamingException ne) {
                    log.error(ne);
                }
            }
        }
        //
        if (targetMembershipList != null) {
            for (BaseAttribute ba : targetMembershipList) {

                String groupName = ba.getName();
                boolean exists = isMemberOf(currentMembershipList, groupName);

                if (ba.getOperationEnum() == AttributeOperationEnum.DELETE) {
                    if (exists) {
                        // remove the group membership
                        try {

                            ModificationItem mods[] = new ModificationItem[1];
                            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("uniqueMember", identityDN));
                            ldapctx.modifyAttributes(groupName, mods);
                        } catch (NamingException ne) {
                            log.error(ne);
                        }
                    }
                } else if (ba.getOperationEnum() == null
                        || ba.getOperationEnum() == AttributeOperationEnum.ADD
                        || ba.getOperationEnum() == AttributeOperationEnum.NO_CHANGE) {
                    if (!exists) {
                        // add the user to the group
                        try {
                            ModificationItem mods[] = new ModificationItem[1];
                            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("uniqueMember", identityDN));
                            ldapctx.modifyAttributes(groupName, mods);
                        } catch (NamingException ne) {
                            log.error(ne);
                        }
                    }
                }
            }
        }
    }

    public void updateSupervisorMembership(ManagedSysEntity managedSys, List<BaseAttribute> supervisorMembershipList, String identity, String identityDN,
                                           ManagedSystemObjectMatch matchObj, LdapContext ldapctx, ExtensibleObject obj) {

        List<String> currentSupervisorMembershipList = userSupervisorMembershipList(managedSys, identity, matchObj, ldapctx);

        log.debug("Current ldap supervisor membership:" + currentSupervisorMembershipList);

        if (supervisorMembershipList == null && currentSupervisorMembershipList != null) {

            for (String s : currentSupervisorMembershipList) {
                try {
                    log.debug("Removing supervisor: " + s + " from " + identityDN);
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("manager", s));
                    ldapctx.modifyAttributes(identityDN, mods);
                } catch (NamingException ne) {
                    log.error(ne);
                }
            }
        }
        if (supervisorMembershipList != null) {

            for (BaseAttribute ba : supervisorMembershipList) {

                String supervisorName = ba.getName();
                boolean exists = isMemberOf(currentSupervisorMembershipList, supervisorName);

                if (ba.getOperationEnum() == AttributeOperationEnum.DELETE) {
                    if (exists) {
                        // remove the supervisor membership
                        try {
                            ModificationItem mods[] = new ModificationItem[1];
                            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("manager", supervisorName));
                            ldapctx.modifyAttributes(identityDN, mods);
                        } catch (NamingException ne) {
                            log.error(ne);
                        }
                    }
                } else if (ba.getOperationEnum() == null
                        || ba.getOperationEnum() == AttributeOperationEnum.ADD
                        || ba.getOperationEnum() == AttributeOperationEnum.NO_CHANGE) {
                    if (!exists) {
                        // add the user to the group
                        try {
                            ModificationItem mods[] = new ModificationItem[1];
                            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("manager", supervisorName));
                            ldapctx.modifyAttributes(identityDN, mods);
                        } catch (NamingException ne) {
                            log.error(ne);
                        }
                    }
                }
            }
        }
    }

    public void setAttributes(String name, Object obj) {
        objectMap.put(name, obj);
    }

    protected boolean isMemberOf(List<String> membershipList, String objectName) {

        if (membershipList == null || membershipList.isEmpty()) {
            return false;
        }
        for (String member : membershipList) {
            if (member.equalsIgnoreCase(objectName)) {
                return true;
            }
        }
        return false;
    }

    protected List<String> userMembershipList(ManagedSysEntity managedSys, String userDN, ManagedSystemObjectMatch matchObj, LdapContext ldapctx) {

        List<String> currentMembershipList = new ArrayList<String>();

        log.debug("isMemberOf()...");
        log.debug(" - userDN =" + userDN);
        log.debug(" - MembershipObjectDN=" + matchObj.getSearchBaseDn());

        String userSearchFilter = "(&(objectClass=groupOfUniqueNames)(uniqueMember=" + userDN + "))";
        String searchBase = matchObj.getSearchBaseDn();

        try {

            SearchControls ctls = new SearchControls();

            String userReturnedAtts[] = {"uniqueMember"};
            ctls.setReturningAttributes(userReturnedAtts);
            ctls.setSearchScope(managedSys.getSearchScope().getValue());

            NamingEnumeration answer = ldapctx.search(searchBase, userSearchFilter, ctls);

            //Loop through the search results
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();

                String objectName = sr.getNameInNamespace();
                log.debug("Adding to current membership list " + objectName);
                currentMembershipList.add(objectName);
            }

        } catch (Exception e) {
            log.error("userMembershipList", e);
        }

        if (currentMembershipList.isEmpty()) {
            return null;
        }

        return currentMembershipList;
    }

    protected List<String> userSupervisorMembershipList(
            ManagedSysEntity managedSys, String userDN, ManagedSystemObjectMatch matchObj, LdapContext ldapctx) {

        List<String> currentSupervisorMembershipList = new ArrayList<String>();

        log.debug("isManager()...");
        log.debug(" - userDN =" + userDN);
        log.debug(" - MembershipObjectDN=" + matchObj.getSearchBaseDn());

        String searchBase = matchObj.getSearchBaseDn();
        String userSearchFilter = matchObj.getSearchFilterUnescapeXml();
        // replace the place holder in the search filter
        if (StringUtils.isNotBlank(userSearchFilter)) {
            userSearchFilter = userSearchFilter.replace("?", userDN);
        }

        try {

            SearchControls ctls = new SearchControls();

            String userReturnedAtts[] = {"manager"};
            ctls.setReturningAttributes(userReturnedAtts);
            ctls.setSearchScope(managedSys.getSearchScope().getValue());

            NamingEnumeration answer = ldapctx.search(searchBase, userSearchFilter, ctls);

            //Loop through the search results
            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult) answer.next();

                Attributes attrs = sr.getAttributes();
                if (attrs != null) {

                    try {
                        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
                            Attribute attr = (Attribute) ae.next();

                            for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
                                currentSupervisorMembershipList.add((String) e.next());
                            }
                        }
                    } catch (NamingException e) {
                        log.error("Problem listing membership: " + e.toString());
                    }
                }
            }

        } catch (Exception e) {
            log.error("userSupervisorMembershipList", e);
        }

        if (currentSupervisorMembershipList.isEmpty()) {
            return null;
        }

        return currentSupervisorMembershipList;

    }

    protected String getScrambledPswd(ExtensibleObject extObject) {
        String scrambledPswd = getUserPassword(extObject);
        if (StringUtils.isEmpty(scrambledPswd)) {
            scrambledPswd = passwordGenerator.generatePassword(10);
        }
        return scrambledPswd;
    }

    private String getUserPassword(ExtensibleObject extObject) {
        String scrambledPswd = null;
        if (extObject != null && CollectionUtils.isNotEmpty(extObject.getAttributes())) {
            for (final ExtensibleAttribute attr : extObject.getAttributes()) {
                if (attr.getName().equalsIgnoreCase(PASSWORD_ATTRIBUTE)) {
                    scrambledPswd = attr.getValue();
                    break;
                }
            }
        }
        return scrambledPswd;
    }

}
