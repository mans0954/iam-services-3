package org.openiam.connector.ldap.command.base;

import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.util.ConnectionManagerConstant;
import org.openiam.connector.util.ConnectionMgr;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.util.connect.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapContext;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class AbstractLdapCommand<Request extends RequestType, Response extends ResponseType>  extends AbstractCommand<Request, Response> {

    @Autowired
    private ResourceDataService resourceDataService;

    protected LdapContext connect(ManagedSysEntity managedSys) throws ConnectorDataException {
        ConnectionMgr conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
        conMgr.setApplicationContext(this.applicationContext);
        log.debug("Connecting to directory:  " + managedSys.getName());

        LdapContext ldapctx = null;
        try {
            ldapctx = conMgr.connect(managedSys);
            log.debug("Ldapcontext = " + ldapctx);

            if (ldapctx == null) {
                throw  new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, "Unable to connect to directory.");
            }
        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw  new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
        return ldapctx;
    }

    public Set<ResourceProp> getResourceAttributes (String resId ) {
        Resource r = resourceDataService.getResource(resId);
        if (r != null) {
            return r.getResourceProps();
        }
        return null;
    }
    public ResourceProp getResourceAttr(Set<ResourceProp> resSet, String name) {
        if (resSet == null) {
            return null;
        }
        for (ResourceProp rp : resSet ) {
            if ( rp.getName().equalsIgnoreCase(name))  {
                return rp;
            }
        }
        return null;
    }

    protected boolean identityExists(String ldapName, LdapContext ctx) {

        try {
            LdapContext lCtx = (LdapContext) ctx.lookup(ldapName);
        } catch (NamingException ne) {
            return false;
        }
        return true;

    }

    protected String getOU(ExtensibleObject obj) {
        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {
            if (att.getName().equalsIgnoreCase("ou")) {
                return att.getValue();
            }
        }
        return null;
    }

    protected List<String> getAttributeNameList(List<AttributeMapEntity> attrMap) {
        List<String> strList = new ArrayList<String>();

        if (attrMap == null || attrMap.size() == 0) {
            return null;
        }
        for (AttributeMapEntity a : attrMap) {
            strList.add(a.getAttributeName());
        }

        return strList;
    }

    protected void closeContext(LdapContext ldapctx) {
        try {
            if (ldapctx != null) {
                ldapctx.close();
            }
            ldapctx=null;
        } catch (NamingException n) {
            log.error(n);
        }
    }

    protected void buildMembershipList( ExtensibleAttribute att ,List<BaseAttribute>targetMembershipList) {
        if (att == null)
            return;
        if (att.getAttributeContainer() != null) {
            targetMembershipList.addAll( att.getAttributeContainer().getAttributeList() );
        }
    }

    protected Attribute generateActiveDirectoryPassword(String cleartextPassword) {
        try {
            byte[] password = ("\"" + cleartextPassword + "\"").getBytes("UTF-16LE");
            return new BasicAttribute("unicodePwd", password);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    protected BasicAttributes getBasicAttributes(ExtensibleObject obj, String idField,
                                               List<BaseAttribute> targetMembershipList, boolean groupMembershipEnabled) {
        BasicAttributes attrs = new BasicAttributes();

        // add the object class
        Attribute oc = new BasicAttribute("objectclass");
        oc.add("top");

        // add the ou for this record
        Attribute ouSet = new BasicAttribute("ou");
        String ou = getOU(obj);
        log.debug("GetAttributes() - ou=" + ou);
        if (ou != null && ou.length() > 0) {
            ouSet.add(ou);
            attrs.put(ouSet);
        }

        // add the structural classes
        attrs.put(oc);

        // add the identifier

        // add the attributes
        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {

            log.debug("Attr Name=" + att.getName() + " " + att.getDataType() + " " + att.getValue());


            if (att.getDataType() == null || !att.getDataType().equalsIgnoreCase("memberOf")) {

                if (att.getName().equalsIgnoreCase(idField)) {
                    log.debug("Attr Name=" + att.getName() + " Value=" + att.getValue() + " ignored");
                    continue;
                }

                Attribute a = null;
                if (att.isMultivalued()) {
                    List<String> valList = att.getValueList();
                    if (valList != null && valList.size() > 0) {
                        int ctr = 0;
                        for (String s : valList) {
                            if (ctr == 0) {
                                a = new BasicAttribute(att.getName(), valList.get(ctr));
                            } else {
                                a.add(valList.get(ctr));
                            }
                            ctr++;
                        }

                    }
                } else if ("unicodePwd".equalsIgnoreCase(att.getName())) {
                    a = generateActiveDirectoryPassword(att.getValue());
                } else {
                    // add a password to a user separately. If OpenLDAP is not using PPolicy the password is not hashed
                    a = new BasicAttribute(att.getName(), att.getValue());
                }
                if (a != null) {
                    attrs.put(a);
                }

            } else {
                if ("memberOf".equalsIgnoreCase(att.getDataType())) {
                    if (groupMembershipEnabled) {
                        buildMembershipList(att, targetMembershipList);
                    }
                }
            }
        }
        return attrs;
    }

    protected NamingEnumeration lookupSearch(ManagedSystemObjectMatch matchObj, LdapContext ctx,
                                           String searchValue, String[] attrAry, String objectBaseDN) throws NamingException {

        String attrIds[] = {"1.1", "+", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};

        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(attrIds);


        String searchFilter = matchObj.getSearchFilter();
        // replace the place holder in the search filter
        searchFilter = searchFilter.replace("?", searchValue);

        if (objectBaseDN == null) {
            objectBaseDN = matchObj.getSearchBaseDn();
        }


        log.debug("Search Filter=" + searchFilter);
        log.debug("Searching BaseDN=" + objectBaseDN);

        return ctx.search(objectBaseDN, searchFilter, searchCtls);
    }

    protected boolean isInDirectory(String ldapName, ManagedSystemObjectMatch matchObj, LdapContext ldapctx) {
        int indx = ldapName.indexOf(",");
        String rdn = null;
        String objectBaseDN = null;
        if (indx > 0) {
            rdn = ldapName.substring(0, ldapName.indexOf(","));
            objectBaseDN = ldapName.substring(indx + 1);
        } else {
            rdn = ldapName;
        }
        log.debug("Lookup rdn = " + rdn);
        log.debug("Search in: " + objectBaseDN);

        String[] attrAry = {"uid", "cn", "fn"};
        NamingEnumeration results = null;
        try {
            //results = search(matchObj, ldapctx, rdn, attrAry);
            results = lookupSearch(matchObj, ldapctx, rdn, attrAry, objectBaseDN);
            if (results != null && results.hasMoreElements()) {
                return true;
            }
            return false;
        } catch (NamingException ne) {
            log.error(ne);
            return false;
        }
    }
}
