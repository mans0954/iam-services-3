package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.ldap.command.base.AbstractCrudLdapCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("modifyUserLdapCommand")
public class ModifyUserLdapCommand extends AbstractCrudLdapCommand<ExtensibleUser> {

    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleUser> crudRequest,
                                          LdapContext ldapctx) throws ConnectorDataException {
        ManagedSystemObjectMatch matchObj = getMatchObject(crudRequest.getTargetID(), ManagedSystemObjectMatch.USER);

        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();
        List<BaseAttribute> supervisorMembershipList = new ArrayList<BaseAttribute>();
        try {

            ExtensibleAttribute origIdentity = isRename(crudRequest.getExtensibleObject());

            String identity = (origIdentity != null)? origIdentity.getValue() : crudRequest.getObjectIdentity();
            //Check identity on CN format or not
            String identityPatternStr =  MessageFormat.format(DN_IDENTITY_MATCH_REGEXP, matchObj.getKeyField());
            Pattern pattern = Pattern.compile(identityPatternStr);
            Matcher matcher = pattern.matcher(identity);
            String objectBaseDN;

            if(matcher.matches()) {
                String tmp = identity;
                identity = matcher.group(1);
                String CN = matchObj.getKeyField() + "=" + identity;
                objectBaseDN =  tmp.substring(CN.length()+1);

            } else {
                // if identity is not in DN format try to find OU info in attributes
                String OU = getOU(crudRequest.getExtensibleObject());
                if(StringUtils.isNotEmpty(OU)) {
                    objectBaseDN = OU+","+matchObj.getBaseDn();
                } else {
                    objectBaseDN = matchObj.getBaseDn();
                }
            }

            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
            boolean groupMembershipEnabled = isMembershipEnabled(rpSet, "GROUP_MEMBERSHIP_ENABLED");
            boolean supervisorMembershipEnabled = isMembershipEnabled(rpSet, "SUPERVISOR_MEMBERSHIP_ENABLED");

            Directory dirSpecificImp = DirectorySpecificImplFactory.create(managedSys.getHandler5());

            ExtensibleObject obj = crudRequest.getExtensibleObject();

            List<ExtensibleAttribute> attrList = obj.getAttributes();
            List<ModificationItem> modItemList = new ArrayList<ModificationItem>();
            for (ExtensibleAttribute att : attrList) {

                log.debug("Extensible Attribute: " + att.getName() + " " + att.getDataType());

                if (att.getDataType() == null) {
                    continue;
                }

                if (att.getName().equalsIgnoreCase(matchObj.getKeyField())) {
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

                    modItemList.add(new ModificationItem(att.getOperation(), new BasicAttribute(att.getName(), att.getValueAsByteArray())));

                } else if (att.getOperation() != 0 && att.getName() != null) {

                    // set an attribute to null
                    if ((att.getValue() == null || att.getValue().contains("null")) &&
                            (att.getValueList() == null || att.getValueList().size() == 0)) {

                        modItemList.add(new ModificationItem(att.getOperation(), new BasicAttribute(att.getName(), null)));

                    } else {
                        // valid value

                        if ("unicodePwd".equalsIgnoreCase(att.getName())) {
                            Attribute a = generateActiveDirectoryPassword(att.getValue());
                            modItemList.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, a));

                        } else if (!"userPassword".equalsIgnoreCase(att.getName()) &&
                                !"ORIG_IDENTITY".equalsIgnoreCase(att.getName())) {

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
                            modItemList.add(new ModificationItem(att.getOperation(), a));
                            //modItemList.add( new ModificationItem(att.getOperation(), new BasicAttribute(att.getName(), att.getValue())));
                        }
                    }
                }
            }
            ModificationItem[] mods = new ModificationItem[modItemList.size()];
            modItemList.toArray(mods);

            log.debug("ModifyAttribute array=" + mods);

            //Important!!! For save and modify we need to create DN format
//            String identityDN = matchObj.getKeyField() + "=" + identity + "," + objectBaseDN;

            NamingEnumeration results = null;
            try {
                log.debug("Looking for user with identity=" +  identity + " in " +  objectBaseDN);
                results = lookupSearch(managedSys, matchObj, ldapctx, identity, null, objectBaseDN);

            } catch (NameNotFoundException nnfe) {
                log.debug("results=NULL");
                log.debug(" results has more elements=0");
                return;
            }

            String identityDN = null;
            int count = 0;
            while (results != null && results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                identityDN = sr.getNameInNamespace();
                count++;
            }

            if (count == 0) {
                String err = String.format("User %s was not found in %s", identity, objectBaseDN);
                log.error(err);
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER, err);
            } else if (count > 1) {
                String err = String.format("More then one user %s was found in %s", identity, objectBaseDN);
                log.error(err);
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER, err);
            }

            if (StringUtils.isNotEmpty(identityDN)) {
                log.debug("Modifying user in ldap.." + identityDN);
                ldapctx.modifyAttributes(identityDN, mods);

                if (groupMembershipEnabled) {
                    dirSpecificImp.updateAccountMembership(managedSys, targetMembershipList, identity, identityDN,
                            matchObj, ldapctx, crudRequest.getExtensibleObject());
                }

                if (supervisorMembershipEnabled) {
                    dirSpecificImp.updateSupervisorMembership(managedSys, supervisorMembershipList, identity, identityDN,
                            matchObj, ldapctx, crudRequest.getExtensibleObject());
                }
            }

            if (origIdentity != null) {
                log.debug("Renaming identity: " + identityDN);

                try {
                    ldapctx.rename(identityDN, crudRequest.getObjectIdentity());
                    log.debug("Renaming : " + identityDN);

                } catch (NamingException ne) {
                    log.error(ne.getMessage(), ne);
                    throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, ne.getMessage());
                }
            }

        } catch (NamingException ne) {
            log.error(ne.getMessage(),ne);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, ne.getMessage());
        }

    }

    private ExtensibleAttribute isRename(ExtensibleObject obj) {

        log.debug("ReName Object:" + obj.getName() + " - operation=" + obj.getOperation());

        List<ExtensibleAttribute> attrList = obj.getAttributes();
        for (ExtensibleAttribute att : attrList) {
            if (att.getOperation() != 0 && att.getName() != null) {
                if (att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
                    return att;
                }
            }
        }
        return null;
    }

}
