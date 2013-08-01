package org.openiam.spml2.spi.ldap.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.spi.ldap.command.base.AbstractDeleteLdapCommand;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import java.util.List;
import java.util.Set;

@Service("deleteUserLdapCommand")
public class DeleteUserLdapCommand extends AbstractDeleteLdapCommand<ProvisionUser> {
    @Override
    protected void deleteObject(DeleteRequestType<ProvisionUser> deleteRequestType, ManagedSysEntity managedSys, LdapContext ldapctx) throws ConnectorDataException {
        boolean groupMembershipEnabled = true;
        String delete = "DELETE";
        ManagedSystemObjectMatch matchObj = null;
        try {
            PSOIdentifierType psoID = deleteRequestType.getPsoID();
            /* targetID -  */
            String targetID = psoID.getTargetID();

            List<ManagedSystemObjectMatchEntity> matchObjList =  managedSysService.managedSysObjectParam(targetID, "USER");
            if (matchObjList != null && matchObjList.size() > 0) {
                matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
            }


            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());

            ResourceProp rpOnDelete = getResourceAttr(rpSet,"ON_DELETE");
            ResourceProp rpGroupMembership = getResourceAttr(rpSet,"GROUP_MEMBERSHIP_ENABLED");

            if (rpOnDelete == null || rpOnDelete.getPropValue() == null || "DELETE".equalsIgnoreCase(rpOnDelete.getPropValue())) {
                delete = "DELETE";
            }else if (rpOnDelete.getPropValue() != null) {

                if ("DISABLE".equalsIgnoreCase(rpOnDelete.getPropValue())) {
                    delete = "DISABLE";
                }
            }

            // BY DEFAULT - we want to enable group membership
            if (rpGroupMembership == null || rpGroupMembership.getPropValue() == null || "Y".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                groupMembershipEnabled = true;
            }else if (rpGroupMembership.getPropValue() != null) {

                if ("N".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                    groupMembershipEnabled = false;
                }
            }

            Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
            String ldapName = psoID.getID();


            if (groupMembershipEnabled) {
                dirSpecificImp.removeAccountMemberships(ldapName, matchObj, ldapctx);
            }
            dirSpecificImp.delete(deleteRequestType, ldapctx, ldapName, delete);
        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }
}
