package org.openiam.spml2.spi.ldap.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.spml2.spi.ldap.command.base.AbstractLdapCommand;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;

@Service("suspendLdapCommand")
public class SuspendLdapCommand extends AbstractLdapCommand<SuspendRequestType, ResponseType> {
    @Override
    public ResponseType execute(SuspendRequestType suspendRequestType) throws ConnectorDataException {
        ResponseType resp = new ResponseType();
        resp.setStatus(StatusCodeType.SUCCESS);
		/* PSO - Provisioning Service Object -
		 *     -  ID must uniquely specify an object on the target or in the target's namespace
		 *     -  Try to make the PSO ID immutable so that there is consistency across changes. */
        PSOIdentifierType psoID = suspendRequestType.getPsoID();
		/* targetID -  */
        String targetID = psoID.getTargetID();
		/* ContainerID - May specify the container in which this object should be created
		 *      ie. ou=Development, org=Example */


		/* A) Use the targetID to look up the connection information under managed systems */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        LdapContext ldapctx = null;
        try {

            log.debug("managedSys found for targetID=" + targetID + " " + " Name=" + managedSys.getName());
            ldapctx = this.connect(managedSys);

            if (ldapctx == null)
                throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, "Unable to connect to directory.");


            String ldapName = psoID.getID();

            // check if this object exists in the target system
            // dont try to disable and object that does not exist
            if (identityExists(ldapName, ldapctx)) {

                // Each directory
                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
                log.debug("Directory specific object name = " + dirSpecificImp.getClass().getName());
                ModificationItem[] mods = dirSpecificImp.suspend(suspendRequestType);

                ldapctx.modifyAttributes(ldapName, mods);
            }

        }catch(Exception ne) {
            log.error(ne.getMessage(), ne);
            throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);
        }finally {
	 		/* close the connection to the directory */
            this.closeContext(ldapctx);
        }
        return resp;
    }
}
