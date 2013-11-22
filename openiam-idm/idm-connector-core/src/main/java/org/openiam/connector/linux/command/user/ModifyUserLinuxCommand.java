package org.openiam.connector.linux.command.user;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("modifyUserLinuxCommand")
public class ModifyUserLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(
            CrudRequest<ExtensibleUser> crudRequest, SSHAgent ssh)
            throws ConnectorDataException {
        ExtensibleUser extensibleUser = crudRequest.getExtensibleObject();

        String originalName = isRename(extensibleUser);
        String login = (originalName == null) ? crudRequest.getObjectIdentity()
                : originalName;

        LinuxUser user = objectToLinuxUser(crudRequest.getObjectIdentity(),
                extensibleUser);

        if (user != null) {
            try {
                String sudoPassword = this.getPassword(crudRequest
                        .getTargetID());
                // Then modify account
                ssh.executeCommand(user.getUserModifyCommand(login),
                        sudoPassword);
                ssh.executeCommand(user.getUserSetDetailsCommand(),
                        sudoPassword);
                sendPassword(ssh, user, sudoPassword);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                        e.getMessage());
            }
        }
    }

    /**
     * Detects whether a given modifications list contains a rename directive
     * 
     * @param extensibleUser
     * @return Original user account name; null if unchanged
     */
    private String isRename(ExtensibleUser extensibleUser) {
        for (ExtensibleAttribute att : extensibleUser.getAttributes()) {
            if (att.getOperation() != 0 && att.getName() != null
                    && att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
                return att.getValue();
            }
        }
        return null;
    }
}
