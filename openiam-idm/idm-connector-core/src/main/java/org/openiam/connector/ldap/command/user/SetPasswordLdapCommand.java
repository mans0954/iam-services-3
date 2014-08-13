package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;

@Service("setPasswordLdapCommand")
public class SetPasswordLdapCommand extends AbstractLdapCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(passwordRequest.getTargetID(), ConnectorConfiguration.class);
        ManagedSysEntity managedSys = config.getManagedSys();
        LdapContext ldapctx = this.connect(managedSys);

        try {

            String identityDN = getIdentityDN(passwordRequest, managedSys, ldapctx);

            if (StringUtils.isNotEmpty(identityDN)) {
                log.debug("New password will be set for user " + identityDN);
                Directory dirSpecificImp = DirectorySpecificImplFactory.create(config.getManagedSys().getHandler5());
                ModificationItem[] mods = dirSpecificImp.setPassword(passwordRequest);
                ldapctx.modifyAttributes(identityDN, mods);
                log.debug("New password has been set for user " + identityDN);
            }

        } catch (NamingException ne) {
            log.error(ne.getMessage(), ne);
            log.debug("Returning response object from set password with Status of Failure...");
            ConnectorDataException ex =null;
            if (ne instanceof OperationNotSupportedException) {
                ex = new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION);
            } else {
                ex = new ConnectorDataException(ErrorCode.DIRECTORY_ERROR);
            }
            throw  ex;
        } catch (Exception ne) {
            log.error(ne.getMessage(), ne);
            throw  new ConnectorDataException(ErrorCode.OTHER_ERROR);
        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapctx);
        }
        return respType;
    }
}
