package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.ldap.command.base.AbstractCrudLdapCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import java.util.Set;

@Service("deleteUserLdapCommand")
public class DeleteUserLdapCommand extends AbstractCrudLdapCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleUser> deleteRequestType,  LdapContext ldapctx) throws ConnectorDataException {

        try {

            String identityDN = getIdentityDN(deleteRequestType, managedSys, ldapctx);

            if (StringUtils.isNotEmpty(identityDN)) {

                log.debug("Deleting entry with DN=" + identityDN);

                Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
                ResourceProp rpOnDelete = getResourceAttr(rpSet, "ON_DELETE");

                String delete = "DELETE";
                if (rpOnDelete == null || rpOnDelete.getValue() == null || "DELETE".equalsIgnoreCase(rpOnDelete.getValue())) {
                    delete = "DELETE";
                } else if (rpOnDelete.getValue() != null) {
                    if ("DISABLE".equalsIgnoreCase(rpOnDelete.getValue())) {
                        delete = "DISABLE";
                    }
                }

                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());

                // Deleting membership of the entry being deleted

                // the group membership is enabled "by default"
                boolean groupMembershipEnabled = isMembershipEnabled(rpSet, "GROUP_MEMBERSHIP_ENABLED");
                boolean supervisorMembershipEnabled = isMembershipEnabled(rpSet, "SUPERVISOR_MEMBERSHIP_ENABLED");

                // TODO: !!!! Validate usage of identity
                String identity = deleteRequestType.getObjectIdentity();
                ManagedSystemObjectMatch matchObj = getMatchObject(deleteRequestType.getTargetID(), ManagedSystemObjectMatch.USER);

                log.debug("Deleting.. users in ldap.." + identityDN);
                if (groupMembershipEnabled) {
                    dirSpecificImp.removeAccountMemberships(managedSys, identity, identityDN, matchObj, ldapctx);
                }
                if (supervisorMembershipEnabled) {
                    dirSpecificImp.removeSupervisorMemberships(managedSys, identity, identityDN, matchObj, ldapctx);
                }

                dirSpecificImp.delete(deleteRequestType, ldapctx, identityDN, delete);
            } else {
                log.debug("Delete can not find entry with identity=" + deleteRequestType.getObjectIdentity());
            }

        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }
}
