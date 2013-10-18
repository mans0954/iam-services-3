package org.openiam.connector.ldap.dirtype;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.ldap.LdapContext;

/**
 * Provides Active Directory specific functionality
 */
public class ActiveDirectoryImpl implements Directory {

    public static final int UF_ACCOUNTDISABLE     = 2;//0x0002
    public static final int UF_LOCKOUT            = 16;//0x0010
    public static final int UF_PASSWORD_EXPIRED   = 8388608;//0x800000
    public static final int UF_DONT_EXPIRE_PASSWD = 65536;//0x00010000
    public static final int UF_NORMAL_ACCOUNT     = 512;//0x0200
    public static final int UF_PASSWD_NOTREQD = 0x0020;
    public static final int UF_PASSWD_CANT_CHANGE = 0x0040;

    protected static final Log log = LogFactory.getLog(ActiveDirectoryImpl.class);


    public ModificationItem[] setPassword(PasswordRequest reqType) throws UnsupportedEncodingException {


        byte[] password = ("\"" + reqType.getPassword() + "\"").getBytes("UTF-16LE");

        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", password));
        return mods;


    }

    public ModificationItem[] suspend(SuspendResumeRequest request) {

        log.debug("suspending AD user.");

        ModificationItem[] mods = new ModificationItem[1];

        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl",
                Integer.toString(UF_NORMAL_ACCOUNT +  UF_ACCOUNTDISABLE)));
        return mods;

    }

    public ModificationItem[] resume(SuspendResumeRequest request) {

        log.debug("Enabling AD user.");

        ModificationItem[] mods = new ModificationItem[1];

        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl",
                Integer.toString(UF_NORMAL_ACCOUNT )));
        return mods;

    }

    public void delete(CrudRequest reqType, LdapContext ldapctx, String ldapName, String onDelete) throws NamingException {

        if ("DELETE".equalsIgnoreCase(onDelete)) {

            ldapctx.destroySubcontext(ldapName);

        } else if ( "DISABLE".equalsIgnoreCase(onDelete)) {

            ModificationItem[] mods = new ModificationItem[1];

            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userAccountControl",
                    Integer.toString(UF_NORMAL_ACCOUNT +  UF_ACCOUNTDISABLE)));

            ldapctx.modifyAttributes(ldapName, mods);

        }

    }

    public void removeAccountMemberships( String identity, String identityDN, ManagedSystemObjectMatch matchObj, LdapContext ldapctx ) {

        List<String> currentMembershipList = userMembershipList(identity, matchObj, ldapctx);

        // remove membership
        if (currentMembershipList != null) {

            for (String s : currentMembershipList) {
                try {
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("member", identityDN));
                    ldapctx.modifyAttributes(s, mods);
                } catch (NamingException ne ) {
                    log.error(ne);
                }
            }
        }

    }

    public void removeSupervisorMemberships( String identity, String identityDN, ManagedSystemObjectMatch matchObj, LdapContext ldapctx ) {

        List<String> currentSupervisorMembershipList = userSupervisorMembershipList(identity, matchObj, ldapctx);

        // remove membership
        if (currentSupervisorMembershipList != null) {

            for (String s : currentSupervisorMembershipList) {
                try {
                    log.debug("Removing supervisor: " + s + " from " + identity);
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("manager", s));
                    ldapctx.modifyAttributes(identityDN, mods);
                } catch (NamingException ne ) {
                    log.error(ne);
                }
            }
        }

    }

    public void updateAccountMembership(List<BaseAttribute> targetMembershipList, String identity, String identityDN,
                                        ManagedSystemObjectMatch matchObj, LdapContext ldapctx,
                                        ExtensibleObject obj) {

        String samAccountName = getSamAccountName(obj);

        List<String> currentMembershipList = userMembershipList(samAccountName, matchObj, ldapctx);

        log.debug("- Current Active Dir group membership:" + currentMembershipList);
        log.debug("- Target Active Dir group membership:"  + targetMembershipList);

        if (targetMembershipList == null && currentMembershipList != null) {
            // remove all associations
            for (String s : currentMembershipList) {
                try {
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("member", identityDN));
                    ldapctx.modifyAttributes(s, mods);
                } catch (NamingException ne ) {
                    log.error(ne);
                }
            }
        }

        if (targetMembershipList != null) {
            for (BaseAttribute ba : targetMembershipList) {

                String groupName =  ba.getName();
                boolean exists = isMemberOf(currentMembershipList, groupName);

                if (ba.getOperationEnum() == AttributeOperationEnum.DELETE) {
                    if (exists) {
                        // remove the group membership
                        try {

                            ModificationItem mods[] = new ModificationItem[1];
                            mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("member", identityDN));
                            ldapctx.modifyAttributes(groupName, mods);
                        }catch (NamingException ne ) {
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
                            mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("member", identityDN));
                            ldapctx.modifyAttributes(groupName, mods);
                        } catch (NamingException ne ) {
                            log.error(ne);
                        }
                    }
                }
            }
        }
    }

    public void updateSupervisorMembership(List<BaseAttribute> supervisorMembershipList, String identity, String identityDN,
                                      ManagedSystemObjectMatch matchObj, LdapContext ldapctx, ExtensibleObject obj) {

        List<String> currentSupervisorMembershipList = userSupervisorMembershipList(identity, matchObj, ldapctx);

        log.debug("Current ldap supervisor membership:" + currentSupervisorMembershipList);

        if (supervisorMembershipList == null && currentSupervisorMembershipList != null) {

            for (String s : currentSupervisorMembershipList) {
                try {
                    log.debug("Removing supervisor: " + s + " from " + identity);
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("manager", s));
                    ldapctx.modifyAttributes(identityDN, mods);
                } catch (NamingException ne ) {
                    log.error(ne);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(supervisorMembershipList)) {

            BaseAttribute ba = supervisorMembershipList.get(0); // 1 manager is allowed for AD

            String supervisorName =  ba.getName();
            boolean exists = isMemberOf(currentSupervisorMembershipList, supervisorName);

            if (ba.getOperationEnum() == AttributeOperationEnum.DELETE) {
                if (exists) {
                    // remove the supervisor membership
                    try {
                        ModificationItem mods[] = new ModificationItem[1];
                        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("manager", supervisorName));
                        ldapctx.modifyAttributes(identityDN, mods);
                    } catch (NamingException ne ) {
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
                    } catch (NamingException ne ) {
                        log.error(ne);
                    }
                }
            }
        }
    }

    private String getSamAccountName(ExtensibleObject obj) {
        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {
            if ("sAMAccountName".equalsIgnoreCase(att.getName())) {
                return att.getValue();
            }
        }
        return null;
    }

    public void setAttributes(String name, Object obj) {

    }

    protected boolean isMemberOf(List<String> membershipList, String objectName)  {

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

    protected List<String> userMembershipList(String identityDN, ManagedSystemObjectMatch matchObj, LdapContext ldapctx) {

        List<String> currentMembershipList = new ArrayList<String>();

        String searchBase = matchObj.getSearchBaseDn();
        String userSearchFilter = matchObj.getSearchFilter();
        // replace the place holder in the search filter
        if (StringUtils.isNotBlank(userSearchFilter)) {
            userSearchFilter = userSearchFilter.replace("?", identityDN);
        } else {
            userSearchFilter = "(&(objectclass=user)(sAMAccountName=" + identityDN + "))";
        }

        try {

            SearchControls ctls = new SearchControls();

            String userReturnedAtts[]={"memberOf"};
            ctls.setReturningAttributes(userReturnedAtts);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search object only

            NamingEnumeration answer = ldapctx.search(searchBase, userSearchFilter, ctls);

            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult)answer.next();

                Attributes attrs = sr.getAttributes();
                if (attrs != null) {

                    try {
                        for (NamingEnumeration ae = attrs.getAll();ae.hasMore();) {
                            Attribute attr = (Attribute)ae.next();

                            for (NamingEnumeration e = attr.getAll();e.hasMore();) {
                                currentMembershipList.add ((String)e.next());
                            }
                        }
                    }
                    catch (NamingException e)	{
                        log.error("Problem listing membership: " + e.toString());
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.toString());
        }

        if (currentMembershipList.isEmpty()) {
            return null;
        }

        return currentMembershipList;
    }

    protected List<String> userSupervisorMembershipList(
            String identityDN,  ManagedSystemObjectMatch matchObj, LdapContext ldapctx) {

        List<String> currentMembershipList = new ArrayList<String>();

        String searchBase = matchObj.getSearchBaseDn();
        String userSearchFilter = matchObj.getSearchFilter();
        // replace the place holder in the search filter
        if (StringUtils.isNotBlank(userSearchFilter)) {
            userSearchFilter = userSearchFilter.replace("?", identityDN);
        } else {
            userSearchFilter = "(&(objectclass=user)(sAMAccountName=" + identityDN + "))";
        }

        try {

            SearchControls ctls = new SearchControls();

            String userReturnedAtts[]={"manager"};
            ctls.setReturningAttributes(userReturnedAtts);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search object only

            NamingEnumeration answer = ldapctx.search(searchBase, userSearchFilter, ctls);

            while (answer.hasMoreElements()) {
                SearchResult sr = (SearchResult)answer.next();

                Attributes attrs = sr.getAttributes();
                if (attrs != null) {

                    try {
                        for (NamingEnumeration ae = attrs.getAll();ae.hasMore();) {
                            Attribute attr = (Attribute)ae.next();

                            for (NamingEnumeration e = attr.getAll();e.hasMore();) {
                                currentMembershipList.add ((String)e.next());
                            }
                        }
                    }
                    catch (NamingException e)	{
                        log.error("Problem listing membership: " + e.toString());
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.toString());
        }

        if (currentMembershipList.isEmpty()) {
            return null;
        }

        return currentMembershipList;

    }

}
