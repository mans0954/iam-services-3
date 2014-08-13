package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
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
        final ManagedSysEntity managedSys = config.getManagedSys();
        LdapContext ldapctx = this.connect(managedSys);

        try {
            String identityDN = getIdentityDN(suspendRequestType, managedSys, ldapctx);

            // don't try to disable an object that does not exist
            if (StringUtils.isNotEmpty(identityDN)) {
                // Each directory
                Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
                log.debug("Directory specific object name = " + dirSpecificImp.getClass().getName());
                ModificationItem[] mods = dirSpecificImp.suspend(suspendRequestType);

                log.debug("Modifying for Suspend.. users in ldap.." + identityDN);
                ldapctx.modifyAttributes(identityDN, mods);
            } else {

                respType.setStatus(StatusCodeType.FAILURE);
                return respType;
            }

            return respType;

        } catch(Exception ne) {
            log.error(ne.getMessage(), ne);
            throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);
        } finally {
	 		/* close the connection to the directory */
            this.closeContext(ldapctx);
        }
    }


}
