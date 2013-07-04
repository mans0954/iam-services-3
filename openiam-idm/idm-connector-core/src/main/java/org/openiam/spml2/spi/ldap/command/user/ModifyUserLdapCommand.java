package org.openiam.spml2.spi.ldap.command.user;

import org.openiam.base.BaseAttribute;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.ldap.command.base.AbstractModifyLdapCommand;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("modifyUserLdapCommand")
public class ModifyUserLdapCommand extends AbstractModifyLdapCommand<ProvisionUser>{

    @Override
    protected void modifyObject(ModifyRequestType<ProvisionUser> modifyRequestType, ManagedSysEntity managedSys, List<ModificationType> modificationList, LdapContext ldapctx) throws ConnectorDataException{
        ManagedSystemObjectMatch matchObj = null;
        boolean groupMembershipEnabled = false;
        List<ExtensibleObject> extobjectList = null;
        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();
        try {
            String ldapName = modifyRequestType.getPsoID().getID();
            ExtensibleAttribute origIdentity = isRename(modificationList);
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


            // determine if this record already exists in ldap
            // move to separate method

            List<ManagedSystemObjectMatchEntity> matchObjList =  managedSysService.managedSysObjectParam(modifyRequestType.getPsoID().getTargetID(), "USER");
            if (matchObjList != null && matchObjList.size() > 0) {
                matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
            }
    //        ManagedSystemObjectMatch[] matchObj = managedSysService.managedSysObjectParam(psoID.getTargetID(), "USER");

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

                log.debug("ldapName found in directory. Update the record..");
                List<ModificationType> modTypeList = modifyRequestType.getModification();
                for (ModificationType mod : modTypeList) {
                    ExtensibleType extType = mod.getData();
                    extobjectList = extType.getAny();
                    for (ExtensibleObject obj : extobjectList) {

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
                            } if (att.getDataType().equalsIgnoreCase("byteArray")) {

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


                    }
                }
            } else {
                // create the record in ldap
                log.debug("ldapName NOT FOUND in directory. Adding new record to directory..");

                List<ModificationType> modTypeList = modifyRequestType.getModification();
                for (ModificationType mod : modTypeList) {

                    ExtensibleType extType = mod.getData();
                    extobjectList = extType.getAny();

                    log.debug("Modify: Extensible Object List =" + extobjectList);

                    BasicAttributes basicAttr = getBasicAttributes(extobjectList, matchObj.getKeyField(),
                            targetMembershipList, groupMembershipEnabled);
                    Context result = ldapctx.createSubcontext(ldapName, basicAttr);
                }

            }
            if (groupMembershipEnabled) {
                dirSpecificImp.updateAccountMembership(targetMembershipList, ldapName, matchObj, ldapctx, extobjectList);
            }
        } catch (NamingException ne) {
           log.error(ne.getMessage(),ne);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, ne.getMessage());
        }

    }

    private ExtensibleAttribute isRename(List<ModificationType> modTypeList) {
        for (ModificationType mod : modTypeList) {
            ExtensibleType extType = mod.getData();
            List<ExtensibleObject> extobjectList = extType.getAny();
            for (ExtensibleObject obj : extobjectList) {

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
            }
        }
        return null;
    }
}
