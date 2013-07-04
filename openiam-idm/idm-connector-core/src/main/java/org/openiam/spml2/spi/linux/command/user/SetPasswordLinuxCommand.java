package org.openiam.spml2.spi.linux.command.user;

import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.spi.linux.command.LinuxUser;
import org.openiam.spml2.spi.linux.command.base.AbstractLinuxCommand;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("setPasswordLinuxCommand")
public class SetPasswordLinuxCommand extends AbstractLinuxCommand<SetPasswordRequestType,ResponseType> {
    @Override
    public ResponseType execute(SetPasswordRequestType setPasswordRequestType) throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        responseType.setRequestID(setPasswordRequestType.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        String login = setPasswordRequestType.getPsoID().getID();
        String password = setPasswordRequestType.getPassword();
        SSHAgent ssh = getSSHAgent(setPasswordRequestType.getPsoID().getTargetID());
        try {
            LinuxUser user = new LinuxUser(null, login, password, null, null, null, null, null, null, null);
            sendPassword(ssh, user);
            return responseType;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            ssh.logout();
        }
    }
}
