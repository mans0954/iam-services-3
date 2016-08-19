package org.openiam.connector.linux.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.PasswordRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.command.base.AbstractLinuxCommand;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("expirePasswordLinuxCommand")
public class ExpirePasswordLinuxCommand extends
        AbstractLinuxCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest passwordRequest)
            throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        responseType.setRequestID(passwordRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        String login = passwordRequest.getObjectIdentity();
        SSHAgent ssh = getSSHAgent(passwordRequest.getTargetID());
        try {
            LinuxUser user = new LinuxUser(null, login, null, null, null, null,
                    null, null, null, null);
            ssh.executeCommand(user.getUserExpirePasswordCommand(),
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
