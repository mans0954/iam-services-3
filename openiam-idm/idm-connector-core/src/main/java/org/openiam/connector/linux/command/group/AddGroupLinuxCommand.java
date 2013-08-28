package org.openiam.connector.linux.command.group;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.connector.linux.data.LinuxGroup;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("addGroupLinuxCommand")
public class AddGroupLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {

    @Override
    protected void performObjectOperation(
            CrudRequest<ExtensibleUser> crudRequest, SSHAgent ssh)
            throws ConnectorDataException {
        LinuxGroup group = this.objectToLinuxGroup(crudRequest
                .getObjectIdentity());
        if (group != null) {
            try {
                // add group
                ssh.executeCommand(group.getAddGroupCommand());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                        e.getMessage());
            }
        }
    }
}
