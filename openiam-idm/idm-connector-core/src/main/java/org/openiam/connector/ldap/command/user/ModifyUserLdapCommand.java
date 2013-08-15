package org.openiam.connector.ldap.command.user;

import org.openiam.base.BaseAttribute;
import org.openiam.connector.ldap.command.base.AbstractCrudLdapCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
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

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("modifyUserLdapCommand")
public class ModifyUserLdapCommand extends AbstractCrudLdapCommand<ExtensibleUser> {

    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleUser> crudRequest, LdapContext ldapctx) throws ConnectorDataException {
        ManagedSystemObjectMatch matchObj = getMatchObject(crudRequest.getTargetID(), "USER");
        boolean groupMembershipEnabled = false;
        List<ExtensibleObject> extobjectList = null;
        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();
        try {
            String ldapName = crudRequest.getObjectIdentity();

            ExtensibleAttribute origIdentity = isRename(crudRequest.getExtensibleObject());
            if (origIdentity != null) {
                log.debug("Renaming identity: " + origIdentity.getValue());

                try {
                    ldapctx.rename(origIdentity.getValue(), ldapName);
                    log.debug("Renaming : " + origIdentity.getValue());

                } catch (NamingException ne) {
                    log.error(ne.getMessage(), ne);
                    throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, ne.getMessage());
                }
            }

            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
            ResourceProp rpGroupMembership = getResourceAttr(rpSet, "GROUP_MEMBERSHIP_ENABLED");

            // BY DEFAULT - we want to enable group membership
            if (rpGroupMembership == null || rpGroupMembership.getPropValue() == null || "Y".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                groupMembershipEnabled = true;
            } else if (rpGroupMembership.getPropValue() != null) {

                if ("N".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                    groupMembershipEnabled = false;
                }
            }

            Directory dirSpecificImp = DirectorySpecificImplFactory.create(managedSys.getHandler5());


            if (isInDirectory(ldapName, matchObj, ldapctx)) {
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

                    if (att.getDataType().equalsIgnoreCase("memberOf")) {
                        if (groupMembershipEnabled) {
                            buildMembershipList(att, targetMembershipList);
                        }
                    }
                    if (att.getDataType().equalsIgnoreCase("byteArray")) {

                        modItemList.add(new ModificationItem(att.getOperation(), new BasicAttribute(att.getName(), att.getValueAsByteArray())));
                    } else if (att.getOperation() != 0 && att.getName() != null) {

                        // set an attribute to null
                        if ((att.getValue() == null || att.getValue().contains("null")) && (att.getValueList() == null || att.getValueList().size() == 0)) {


                            modItemList.add(new ModificationItem(att.getOperation(), new BasicAttribute(att.getName(), null)));
                        } else {
                            // valid value

                            //  if (!att.getDataType().equalsIgnoreCase("memberOf")) {
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
                                                a = new BasicAttribute(att.getName(), valList.get(ctr));
                                            } else {
                                                a.add(valList.get(ctr));
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
                log.debug("ldapName=" + ldapName);
                ldapctx.modifyAttributes(ldapName, mods);
            } else {
                // create the record in ldap
                log.debug("ldapName NOT FOUND in directory. Adding new record to directory..");
                BasicAttributes basicAttr = getBasicAttributes(crudRequest.getExtensibleObject(), matchObj.getKeyField(),
                        targetMembershipList, groupMembershipEnabled);
                Context result = ldapctx.createSubcontext(ldapName, basicAttr);
            }
            if (groupMembershipEnabled) {
                dirSpecificImp.updateAccountMembership(targetMembershipList, ldapName, matchObj, ldapctx, crudRequest.getExtensibleObject());
            }
        } catch (NamingException ne) {
           log.error(ne.getMessage(),ne);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, ne.getMessage());
        }

    }

    private ExtensibleAttribute isRename(ExtensibleObject obj) {

        log.debug("ReName Object:" + obj.getName() + " - operation=" + obj.getOperation());

        List<ExtensibleAttribute> attrList = obj.getAttributes();
        List<ModificationItem> modItemList = new ArrayList<ModificationItem>();
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
