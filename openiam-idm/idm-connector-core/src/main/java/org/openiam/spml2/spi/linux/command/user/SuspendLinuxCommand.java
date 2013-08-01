package org.openiam.spml2.spi.linux.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.msg.suspend.SuspendRequestType;
import org.openiam.spml2.spi.linux.command.LinuxUser;
import org.openiam.spml2.spi.linux.command.base.AbstractLinuxCommand;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("suspendRequestType")
public class SuspendLinuxCommand extends AbstractLinuxCommand<SuspendRequestType,ResponseType> {
    @Override
    public ResponseType execute(SuspendRequestType suspendRequestType) throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        responseType.setRequestID(suspendRequestType.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        String login = suspendRequestType.getPsoID().getID();
        SSHAgent ssh = getSSHAgent(suspendRequestType.getPsoID().getTargetID());
        try {
            LinuxUser user = new LinuxUser(null, login, null, null, null, null, null, null, null, null);
            ssh.executeCommand(user.getUserLockCommand());
            return responseType;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            ssh.logout();
        }
    }
}
