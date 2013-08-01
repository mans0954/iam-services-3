package org.openiam.spml2.spi.linux.command.user;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.linux.command.LinuxUser;
import org.openiam.spml2.spi.linux.command.base.AbstractAddLinuxCommand;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("addUserLinuxCommand")
public class AddUserLinuxCommand extends AbstractAddLinuxCommand<ProvisionUser> {
    @Override
    protected void addObject(String login, List<ExtensibleObject> objectList, SSHAgent ssh) throws ConnectorDataException {
        LinuxUser user = objectToLinuxUser(login, objectList);
        if (user != null) {
            try {
                // Then add user
                ssh.executeCommand(user.getUserAddCommand());
                sendPassword(ssh, user);
                ssh.executeCommand(user.getUserSetDetailsCommand());

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
    }
}
