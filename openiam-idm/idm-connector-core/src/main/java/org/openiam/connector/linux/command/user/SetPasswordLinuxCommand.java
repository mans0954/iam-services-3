package org.openiam.connector.linux.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.command.base.AbstractLinuxCommand;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("setPasswordLinuxCommand")
public class SetPasswordLinuxCommand extends
        AbstractLinuxCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest)
            throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        responseType.setRequestID(passwordRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        String login = passwordRequest.getObjectIdentity();
        String password = passwordRequest.getPassword();
        SSHAgent ssh = newSSHConnection(passwordRequest.getTargetID());
        try {
            LinuxUser user = new LinuxUser(null, login, password, null, null,
                    null, null, null, null, null);
            sendPassword(ssh, user,
                    this.getPassword(passwordRequest.getTargetID()));
            return responseType;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    e.getMessage());
        } finally {
            ssh.logout();
        }
    }
}
