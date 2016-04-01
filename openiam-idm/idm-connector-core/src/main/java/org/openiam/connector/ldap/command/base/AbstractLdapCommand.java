package org.openiam.connector.ldap.command.base;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.common.command.AbstractCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.util.ConnectionManagerConstant;
import org.openiam.connector.util.ConnectionMgr;
import org.openiam.connector.util.connect.ConnectionFactory;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
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

public abstract class AbstractLdapCommand<Request extends RequestType, Response extends ResponseType> extends AbstractCommand<Request, Response> {

    public static final int PAGE_SIZE = 100;

    @Autowired
    private ResourceDataService resourceDataService;
    protected String patternForCTRLCHAR = "[\u0000-\u001F]";
    public static final String DN_IDENTITY_MATCH_REGEXP = "{0}=(.*?)(?:,.*)*$";
    public static final String OU_ATTRIBUTE = "ou";

    public LdapContext connect(ManagedSysEntity managedSys) throws ConnectorDataException {
        ConnectionMgr conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
        conMgr.setApplicationContext(this.applicationContext);
        if(log.isDebugEnabled()) {
        	log.debug("Connecting to directory:  " + managedSys.getName());
        }

        LdapContext ldapctx = null;
        try {
            ldapctx = conMgr.connect(managedSys);
            if(log.isDebugEnabled()) {
            	log.debug("Ldapcontext = " + ldapctx);
            }

            if (ldapctx == null) {
                throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, "Unable to connect to directory.");
            }
        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
        return ldapctx;
    }

    public Set<ResourceProp> getResourceAttributes(String resId) {
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
        for (ResourceProp rp : resSet) {
            if (rp.getName().equalsIgnoreCase(name)) {
                return rp;
            }
        }
        return null;
    }

    public boolean getResourceBoolean(Set<ResourceProp> rpSet, String property, boolean defaultValue) {
        ResourceProp prop = getResourceAttr(rpSet, property);
        if (prop == null || prop.getValue() == null) {
            return defaultValue;
        } else {
            return "Y".equalsIgnoreCase(prop.getValue());
        }
    }

    public String getResourceString(Set<ResourceProp> rpSet, String property, String defaultValue) {
        ResourceProp prop = getResourceAttr(rpSet, property);
        return (prop == null || prop.getValue() == null) ? defaultValue : prop.getValue();
    }

    protected String buildIdentityDn(String keyFieldValue, String ou, ManagedSystemObjectMatch matchObj) {
        StringBuilder builderIdentityDn = new StringBuilder();
        builderIdentityDn.append(matchObj.getKeyField());
        builderIdentityDn.append('=').append(keyFieldValue).append(',');
        if (StringUtils.isNotBlank(ou)) {
            builderIdentityDn.append(ou).append(',');
        }
        builderIdentityDn.append(matchObj.getBaseDn());
        return builderIdentityDn.toString();
    }

    protected boolean identityExists(String ldapName, LdapContext ctx) {

        try {
            LdapContext lCtx = (LdapContext) ctx.lookup(ldapName);
        } catch (NamingException ne) {
            return false;
        }
        return true;

    }

    protected String getAttrValue(ExtensibleObject obj, String attrName) {
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

    protected List<String> getAttributeNameList(List<AttributeMapEntity> attrMap) {
        List<String> strList = new ArrayList<String>();

        if (attrMap == null || attrMap.size() == 0) {
            return null;
        }
        for (AttributeMapEntity a : attrMap) {
            strList.add(a.getName());
        }

        return strList;
    }

    protected void closeContext(LdapContext ldapctx) {
        try {
            if (ldapctx != null) {
                ldapctx.close();
            }
            ldapctx = null;
        } catch (NamingException n) {
            log.error(n);
        }
    }

    protected void buildMembershipList(ExtensibleAttribute att, List<BaseAttribute> targetMembershipList) {
        if (att == null)
            return;
        if (att.getAttributeContainer() != null) {
            targetMembershipList.addAll(att.getAttributeContainer().getAttributeList());
        }
    }

    protected void buildSupervisorMembershipList(ExtensibleAttribute att, List<BaseAttribute> supervisorMembershipList) {
        if (att == null)
            return;
        if (att.getAttributeContainer() != null) {
            supervisorMembershipList.addAll(att.getAttributeContainer().getAttributeList());
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
                                                 List<BaseAttribute> targetMembershipList, boolean groupMembershipEnabled,
                                                 List<BaseAttribute> supervisorMembershipList, boolean supervisorMembershipEnabled) {

        BasicAttributes attrs = new BasicAttributes();

        // add the object class
        Attribute oc = new BasicAttribute("objectClass");
        oc.add("top");
        attrs.put(oc);

        // add the attributes
        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {
        	if(log.isDebugEnabled()) {
        		log.debug("Extensible Attribute: " + att.getName() + " " + att.getDataType());
        	}

            if (att.getDataType() == null) {
                continue;
            }

            if (att.getName().equalsIgnoreCase(idField)) {
            	if(log.isDebugEnabled()) {
            		log.debug("Attr Name=" + att.getName() + " Value=" + att.getValue() + " ignored");
            	}
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

            } else if (att.getAttributeContainer() != null && CollectionUtils.isNotEmpty(att.getAttributeContainer().getAttributeList())) {
                for (BaseAttribute attribute : att.getAttributeContainer().getAttributeList()) {
                    attrs.put(new BasicAttribute(att.getName(), attribute.getValue()));
                }
            } else if (att.getName() != null) {

                // set an attribute to null
                if ((att.getValue() == null || att.getValue().equals("null")) &&
                        (att.getValueList() == null || att.getValueList().size() == 0)) {

                    attrs.put(new BasicAttribute(att.getName(), null));

                } else {
                    // valid value

                    if (OU_ATTRIBUTE.equalsIgnoreCase(att.getName())) {
                        // skip ou
                    } else if ("unicodePwd".equalsIgnoreCase(att.getName())) {
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
                                    s = StringUtils.isNotEmpty(s) ? s : null;
                                    if (ctr == 0) {
                                        a = new BasicAttribute(att.getName(), s);
                                    } else {
                                        a.add(s);
                                    }
                                    ctr++;
                                }
                            }
                        } else {
                            a = new BasicAttribute(att.getName(), StringUtils.isNotEmpty(att.getValue()) ? att.getValue() : null);
                        }
                        attrs.put(a);
                    }
                }
            }
        }
        return attrs;
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

        if(log.isDebugEnabled()) {
        	log.debug("Search Filter=" + searchFilter);
        	log.debug("Searching BaseDN=" + objectBaseDN);
        }

        return ctx.search(objectBaseDN, searchFilter, searchCtls);
    }

}
