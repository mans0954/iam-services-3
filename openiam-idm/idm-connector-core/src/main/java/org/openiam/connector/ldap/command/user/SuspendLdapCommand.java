package org.openiam.connector.ldap.command.user;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;

@Service("suspendLdapCommand")
public class SuspendLdapCommand extends AbstractLdapCommand<SuspendResumeRequest, ResponseType>{
    @Override
    public ResponseType execute(SuspendResumeRequest suspendRequestType) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        ConnectorConfiguration config =  getConfiguration(suspendRequestType.getTargetID(), ConnectorConfiguration.class);
        LdapContext ldapctx = this.connect(config.getManagedSys());


        try {
            String ldapName = suspendRequestType.getObjectIdentity();

            // check if this object exists in the target system
            // dont try to disable and object that does not exist
            if (identityExists(ldapName, ldapctx)) {

                // Each directory
                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(config.getManagedSys().getHandler5());
                log.debug("Directory specific object name = " + dirSpecificImp.getClass().getName());
                ModificationItem[] mods = dirSpecificImp.suspend(suspendRequestType);

                ldapctx.modifyAttributes(ldapName, mods);
            }
            return respType;
        }catch(Exception ne) {
            log.error(ne.getMessage(), ne);
            throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);
        }finally {
	 		/* close the connection to the directory */
            this.closeContext(ldapctx);
        }
    }
}
