package org.openiam.connector.ldap.command.user;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.connector.ldap.command.base.AbstractLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;
import javax.naming.ldap.LdapContext;
import java.util.List;

@Service("setPasswordLdapCommand")
public class SetPasswordLdapCommand extends AbstractLdapCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest) throws ConnectorDataException {
        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(passwordRequest.getTargetID(), ConnectorConfiguration.class);
        LdapContext ldapctx = this.connect(config.getManagedSys());

        try {

            String ldapName = passwordRequest.getObjectIdentity();

            Directory dirSpecificImp = DirectorySpecificImplFactory.create(config.getManagedSys().getHandler5());

            ModificationItem[] mods = dirSpecificImp.setPassword(passwordRequest);
            ldapctx.modifyAttributes(ldapName, mods);

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
