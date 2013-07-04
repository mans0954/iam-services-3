package org.openiam.spml2.spi.linux.command.user;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.linux.command.LinuxUser;
import org.openiam.spml2.spi.linux.command.base.AbstractDeleteLinuxCommand;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("deleteUserLinuxCommand")
public class DeleteUserLinuxCommand extends AbstractDeleteLinuxCommand<ProvisionUser> {
    @Override
    protected void deleteObject(String id, SSHAgent ssh) throws ConnectorDataException {
        LinuxUser user = objectToLinuxUser(id, null);
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
