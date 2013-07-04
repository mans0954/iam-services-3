package org.openiam.spml2.spi.linux.command.user;

import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.msg.password.ExpirePasswordRequestType;
import org.openiam.spml2.spi.linux.command.LinuxUser;
import org.openiam.spml2.spi.linux.command.base.AbstractLinuxCommand;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("expirePasswordLinuxCommand")
public class ExpirePasswordLinuxCommand extends AbstractLinuxCommand<ExpirePasswordRequestType,ResponseType> {
    @Override
    public ResponseType execute(ExpirePasswordRequestType expirePasswordRequestType) throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        responseType.setRequestID(expirePasswordRequestType.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        String login = expirePasswordRequestType.getPsoID().getID();
        SSHAgent ssh = getSSHAgent(expirePasswordRequestType.getPsoID().getTargetID());
        try {
            LinuxUser user = new LinuxUser(null, login, null, null, null, null, null, null, null, null);
            ssh.executeCommand(user.getUserExpirePasswordCommand());
            return responseType;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            ssh.logout();
        }
    }
}
