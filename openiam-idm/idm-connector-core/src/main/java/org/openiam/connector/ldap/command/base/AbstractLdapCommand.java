package org.openiam.connector.ldap.command.base;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.util.ConnectionManagerConstant;
import org.openiam.connector.util.ConnectionMgr;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.util.connect.ConnectionFactory;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractLdapCommand<Request extends RequestType, Response extends ResponseType>  extends AbstractCommand<Request, Response> {

    public static final int PAGE_SIZE = 100;

    @Autowired
    private ResourceDataService resourceDataService;

    public static final String DN_IDENTITY_MATCH_REGEXP = "{0}=(.*?)(?:,.*)*$";
    public static final String DN_MATCH_REGEXP = "(\\w+)=(.*?)(?:,.*)*$";

    public static final String DN_ATTRIBUTE_NAME = "dn";
    public static final String DEFAULT_IDENTITY_ATTRIBUTE_NAME = "cn";
    public static final String OU_ATTRIBUTE_NAME = "ou";

    public LdapContext connect(ManagedSysEntity managedSys) throws ConnectorDataException {
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
        Resource r = resourceDataService.getResource(resId, null);
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

    public boolean isMembershipEnabled(Set<ResourceProp> rpSet, String property) {
        ResourceProp rpSupervisorMembership = getResourceAttr(rpSet, property);
        // BY DEFAULT - we want to enable membership
        if (rpSupervisorMembership == null || rpSupervisorMembership.getValue() == null
                || "Y".equalsIgnoreCase(rpSupervisorMembership.getValue())) {
            return true;

        } else if (rpSupervisorMembership.getValue() != null) {
            if ("N".equalsIgnoreCase(rpSupervisorMembership.getValue())) {
                return false;
            }
        }
        return false;
    }

    protected boolean identityExists(String ldapName, LdapContext ctx) {

        try {
            LdapContext lCtx = (LdapContext) ctx.lookup(ldapName);
        } catch (NamingException ne) {
            return false;
        }
        return true;

    }

    protected String getAttributeValue(ExtensibleObject obj, String attrName) {
        if (obj != null) {
            List<ExtensibleAttribute> attrList = obj.getAttributes();
            for (ExtensibleAttribute att : attrList) {
                if (att.getName().equalsIgnoreCase(attrName)) {
                    return att.getValue();
                }
            }
        }
        return null;
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

    protected void buildMembershipList(ExtensibleAttribute att, List<BaseAttribute>targetMembershipList) {
        if (att == null)
            return;
        if (att.getAttributeContainer() != null) {
            targetMembershipList.addAll( att.getAttributeContainer().getAttributeList() );
        }
    }

    protected void buildSupervisorMembershipList(ExtensibleAttribute att, List<BaseAttribute>supervisorMembershipList) {
        if (att == null)
            return;
        if (att.getAttributeContainer() != null) {
            supervisorMembershipList.addAll( att.getAttributeContainer().getAttributeList() );
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

    protected BasicAttributes getBasicAttributes(ExtensibleObject obj, String keyField,
                    List<BaseAttribute> targetMembershipList, boolean groupMembershipEnabled,
                    List<BaseAttribute> supervisorMembershipList, boolean supervisorMembershipEnabled) {

        BasicAttributes attrs = new BasicAttributes();

        // add the object class
        Attribute oc = new BasicAttribute("objectClass");
        oc.add("top");

        // add the ou for this record
        Attribute ouSet = new BasicAttribute(OU_ATTRIBUTE_NAME);
        String ou = getAttributeValue(obj, OU_ATTRIBUTE_NAME);
        log.debug("getAttribute(ou)=" + ou);
        if (ou != null && ou.length() > 0) {
            ouSet.add(ou);
            attrs.put(ouSet);
        }

        // add the structural classes
        attrs.put(oc);

        // add the attributes
        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {

            log.debug("Extensible Attribute: " + att.getName() + " " + att.getDataType());

            if (att.getDataType() == null) {
                continue;
            }

            if (att.getName().equalsIgnoreCase(keyField)) {
                log.debug("Attr Name=" + att.getName() + " Value=" + att.getValue() + " ignored");
                continue;
            }

            if (att.getDataType().equalsIgnoreCase("manager")) {
                if (supervisorMembershipEnabled) {
                    buildSupervisorMembershipList(att, supervisorMembershipList);
                }
            } else if (att.getDataType().equalsIgnoreCase("memberOf")) {
                if (groupMembershipEnabled) {
                    buildMembershipList(att, targetMembershipList);
                }
            } else if (att.getDataType().equalsIgnoreCase("byteArray")) {

                attrs.put(new BasicAttribute(att.getName(), att.getValueAsByteArray()));

            } else if (att.getName() != null) {

                // set an attribute to null
                if ((att.getValue() == null || att.getValue().equals("null")) &&
                        (att.getValueList() == null || att.getValueList().size() == 0)) {

                    attrs.put(new BasicAttribute(att.getName(), null));

                } else {
                    // valid value

                    if ("unicodePwd".equalsIgnoreCase(att.getName())) {
                        Attribute a = generateActiveDirectoryPassword(att.getValue());
                        attrs.put(a);

                    } else if ("userPassword".equalsIgnoreCase(att.getName())) {
                        attrs.put(new BasicAttribute(att.getName(), att.getValue()));

                    } else {
                        Attribute a = null;
                        if (att.isMultivalued()) {
                            List<String> valList = att.getValueList();
                            if (valList != null && valList.size() > 0) {
                                int ctr = 0;
                                for (String s : valList) {
                                    if (ctr == 0) {
                                        a = new BasicAttribute(att.getName(), s);
                                    } else {
                                        a.add(s);
                                    }
                                    ctr++;
                                }
                            }
                        } else {
                            a = new BasicAttribute(att.getName(), att.getValue());
                        }
                        attrs.put(a);
                    }
                }
            }
        }
        return attrs;
    }

    public String getDnKeyField(String identityDN) {
        Pattern pattern = Pattern.compile(DN_MATCH_REGEXP);
        Matcher matcher = pattern.matcher(identityDN);
        return matcher.matches() ? matcher.group(1) : null;
    }

    public NamingEnumeration lookupSearch(ManagedSysEntity managedSys, ManagedSystemObjectMatch matchObj, LdapContext ctx,
                                           String searchValue, String[] attrAry, String objectBaseDN) throws NamingException {

        // !! TimeOut Error with  Oracle LDAP: String attrIds[] = {"1.1", "+", "*", "accountUnlockTime", "aci", "aclRights", "aclRightsInfo", "altServer", "attributeTypes", "changeHasReplFixupOp", "changeIsReplFixupOp", "copiedFrom", "copyingFrom", "createTimestamp", "creatorsName", "deletedEntryAttrs", "dITContentRules", "dITStructureRules", "dncomp", "ds-pluginDigest", "ds-pluginSignature", "ds6ruv", "dsKeyedPassword", "entrydn", "entryid", "hasSubordinates", "idmpasswd", "isMemberOf", "ldapSchemas", "ldapSyntaxes", "matchingRules", "matchingRuleUse", "modDNEnabledSuffixes", "modifiersName", "modifyTimestamp", "nameForms", "namingContexts", "nsAccountLock", "nsBackendSuffix", "nscpEntryDN", "nsds5ReplConflict", "nsIdleTimeout", "nsLookThroughLimit", "nsRole", "nsRoleDN", "nsSchemaCSN", "nsSizeLimit", "nsTimeLimit", "nsUniqueId", "numSubordinates", "objectClasses", "parentid", "passwordAllowChangeTime", "passwordExpirationTime", "passwordExpWarned", "passwordHistory", "passwordPolicySubentry", "passwordRetryCount", "pwdAccountLockedTime", "pwdChangedTime", "pwdFailureTime", "pwdGraceUseTime", "pwdHistory", "pwdLastAuthTime", "pwdPolicySubentry", "pwdReset", "replicaIdentifier", "replicationCSN", "retryCountResetTime", "subschemaSubentry", "supportedControl", "supportedExtension", "supportedLDAPVersion", "supportedSASLMechanisms", "supportedSSLCiphers", "targetUniqueId", "vendorName", "vendorVersion"};
        String attrIds[] = ArrayUtils.isEmpty(attrAry) ? new String[0] : attrAry;

        SearchControls searchCtls = new SearchControls();
        searchCtls.setReturningAttributes(attrIds);
        searchCtls.setSearchScope(managedSys.getSearchScope().getValue());
        searchCtls.setTimeLimit(0);
        searchCtls.setCountLimit(10000);

        String searchFilter = matchObj.getSearchFilterUnescapeXml();
        // replace the place holder in the search filter
        if (StringUtils.isNotBlank(searchFilter)) {
            searchFilter = searchFilter.replace("?", searchValue);
        }

        if (objectBaseDN == null) {
            objectBaseDN = matchObj.getSearchBaseDn();
        }

        log.debug("Search Filter=" + searchFilter);
        log.debug("Searching BaseDN=" + objectBaseDN);

        return ctx.search(objectBaseDN, searchFilter, searchCtls);
    }

    public Attributes lookupName(LdapContext ctx, String distName, String[] attrAry)
            throws NamingException {

        if (distName == null || ctx == null) {
            return null;
        }
        String attrIds[] = ArrayUtils.isEmpty(attrAry) ? new String[0] : attrAry;
        log.debug("Lookup DN=" + distName);
        return ctx.getAttributes(distName, attrIds);
    }

    /**
     * Returns distinguished name for the identity specified by request. If identity is stored in DN format
     * it is returned unchanged. If identity is an unique attribute value, then DN is searched in LDAP.
     * The search should return one and only one value, in other case it decided to fail and Null is returned.
     */
    protected String getIdentityDN(RequestType<ExtensibleUser> request, ManagedSysEntity managedSys, LdapContext ldapctx)
            throws NamingException {

        String identity = request.getObjectIdentity();
        if (identity.matches(DN_MATCH_REGEXP)) {
            try {
                log.debug("Looking for user with identity (dn) = " +  identity);
                lookupName(ldapctx, identity, null);
            } catch (NameNotFoundException nnfe) {
                log.debug("results=NULL");
                return null;
            }
            return identity;
        }

        ManagedSystemObjectMatch matchObj = getMatchObject(request.getTargetID(), ManagedSystemObjectMatch.USER);

        String objectBaseDN = matchObj.getBaseDn();
        // try to find OU info in attributes
        String OU = getAttributeValue(request.getExtensibleObject(), OU_ATTRIBUTE_NAME);
        if(StringUtils.isNotEmpty(OU)) {
            objectBaseDN = OU + "," + objectBaseDN;
        }

        NamingEnumeration results = null;
        try {
            log.debug("Looking for user with identity=" +  identity + " in " +  objectBaseDN);
            results = lookupSearch(managedSys, matchObj, ldapctx, identity, null, objectBaseDN);

        } catch (NameNotFoundException nnfe) {
            log.debug("results=NULL");
            log.debug(" results has more elements=0");
            return null;

        }

        if (results != null && results.hasMoreElements()) {
            SearchResult sr = (SearchResult) results.next();
            if (!results.hasMoreElements()) {
                return sr.getNameInNamespace();
            } else {
                String err = String.format("More then one user %s was found in %s", identity, objectBaseDN);
                log.error(err);
            }
        } else {
            String err = String.format("User %s was not found in %s", identity, objectBaseDN);
            log.error(err);
        }
        return null;
    }

    protected ObjectValue attributesToObjectValue(Attributes attrs, String identityAttrName) throws NamingException {
        ObjectValue objectValue = new ObjectValue();
        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {

            ExtensibleAttribute extAttr = new ExtensibleAttribute();
            Attribute attr = (Attribute) ae.next();

            boolean addToList = false;

            extAttr.setName(attr.getID());

            NamingEnumeration e = attr.getAll();
            boolean isMultivalued = (attr.size() > 1);
            while (e.hasMore()) {
                Object o = e.next();
                if (o instanceof String) {
                    if (isMultivalued) {
                        BaseAttributeContainer container = extAttr.getAttributeContainer();
                        if (container == null) {
                            container = new BaseAttributeContainer();
                            extAttr.setAttributeContainer(container);
                        }
                        container.getAttributeList().add(
                                new BaseAttribute(attr.getID(), o.toString(), AttributeOperationEnum.NO_CHANGE));
                    } else {
                        extAttr.setValue(o.toString());
                    }
                    addToList = true;
                }
            }
            if(StringUtils.isNotEmpty(identityAttrName) && identityAttrName.equalsIgnoreCase(extAttr.getName())) {
                objectValue.setObjectIdentity(extAttr.getValue());
            }
            if (addToList) {
                objectValue.getAttributeList().add(extAttr);
            }
        }
        return objectValue;
    }
}
