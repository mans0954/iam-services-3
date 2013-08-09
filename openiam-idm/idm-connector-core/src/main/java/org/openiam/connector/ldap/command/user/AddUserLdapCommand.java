package org.openiam.connector.ldap.command.user;

import org.openiam.base.BaseAttribute;
import org.openiam.connector.ldap.command.base.AbstractCrudLdapCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("addUserLdapCommand")
public class AddUserLdapCommand extends AbstractCrudLdapCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleUser> addRequestType, LdapContext ldapctx) throws ConnectorDataException {
        try {
            ManagedSystemObjectMatch matchObj = getMatchObject(addRequestType.getTargetID(), "USER");


            boolean groupMembershipEnabled = true;
            List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();



            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
            ResourceProp rpGroupMembership = getResourceAttr(rpSet,"GROUP_MEMBERSHIP_ENABLED");

            // BY DEFAULT - we want to enable group membership
            if (rpGroupMembership == null || rpGroupMembership.getPropValue() == null || "Y".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                groupMembershipEnabled = true;
            }else if (rpGroupMembership.getPropValue() != null) {

                if ("N".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                    groupMembershipEnabled = false;
                }
            }


            Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());


            log.debug("baseDN=" + matchObj.getBaseDn());
            log.debug("ID field=" + matchObj.getKeyField());
            log.debug("Group Membership enabled? " + groupMembershipEnabled);

            // get the baseDN
            String baseDN = matchObj.getBaseDn();


            // get the field that is to be used as the UniqueIdentifier
            //String ldapName = matchObj.getKeyField() +"=" + psoID.getID() + "," + baseDN;
            String ldapName = addRequestType.getObjectIdentity();

            log.debug("Checking if the identity exists: " + ldapName);

            // check if the identity exists in ldap first before creating the identity
            if (identityExists(ldapName, ldapctx)) {
                return;
            }
            log.debug(ldapName + " does not exist. building attribute list");


            BasicAttributes basicAttr = getBasicAttributes(addRequestType.getExtensibleObject(), matchObj.getKeyField(),
                    targetMembershipList, groupMembershipEnabled);

            log.debug("Creating users in ldap.." + ldapName);
            Context result = ldapctx.createSubcontext(ldapName, basicAttr);

            if (groupMembershipEnabled) {
                dirSpecificImp.updateAccountMembership(targetMembershipList,ldapName,  matchObj, ldapctx, addRequestType.getExtensibleObject());
            }
        } catch (NamingException e) {
           log.error(e.getMessage(), e);
           throw new  ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }






}
