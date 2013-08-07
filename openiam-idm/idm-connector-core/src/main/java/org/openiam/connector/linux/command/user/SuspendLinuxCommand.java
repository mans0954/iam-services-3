package org.openiam.connector.linux.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.command.base.AbstractLinuxCommand;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("suspendRequestType")
public class SuspendLinuxCommand extends AbstractLinuxCommand<SuspendResumeRequest,ResponseType> {
    @Override
    public ResponseType execute(SuspendResumeRequest suspendRequest) throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        responseType.setRequestID(suspendRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        String login = suspendRequest.getObjectIdentity();
        SSHAgent ssh = getSSHAgent(suspendRequest.getTargetID());
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
