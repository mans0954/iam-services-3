package org.openiam.connector.linux.command.user;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.util.connect.FileUtil;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("addUserLinuxCommand")
public class AddUserLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {

    @Override
    protected void performObjectOperation(
            CrudRequest<ExtensibleUser> crudRequest, SSHAgent ssh)
            throws ConnectorDataException {
        LinuxUser user = objectToLinuxUser(crudRequest.getObjectIdentity(),
                crudRequest.getExtensibleObject());
        if (user != null) {
            try {
                String sudoPassword = this.getPassword(crudRequest
                        .getTargetID());
                // Then add user
                // ssh.executeCommand(user.getUserAddCommand(), sudoPassword);
                // sendPassword(ssh, user, sudoPassword);
                String command = FileUtil
                        .get("/home/zaporozhec/Desktop/add_script.sh");
                ssh.executeCommand(command, sudoPassword);

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                        e.getMessage());
            }
        }
    }
}
