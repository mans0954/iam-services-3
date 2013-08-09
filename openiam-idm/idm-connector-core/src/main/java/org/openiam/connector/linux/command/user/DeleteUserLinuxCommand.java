package org.openiam.connector.linux.command.user;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("deleteUserLinuxCommand")
public class DeleteUserLinuxCommand extends AbstractCrudLinuxCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(CrudRequest<ExtensibleUser> crudRequest, SSHAgent ssh) throws ConnectorDataException {
        LinuxUser user = objectToLinuxUser(crudRequest.getObjectIdentity(), null);
        if (user != null) {
            try {
                ssh.executeCommand(user.getUserDeleteCommand());
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
    }
}
