package org.openiam.connector.linux.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.SuspendResumeRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.command.base.AbstractLinuxCommand;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("resumeLinuxCommand")
public class ResumeLinuxCommand extends
        AbstractLinuxCommand<SuspendResumeRequest, ResponseType> {
    @Override
    public ResponseType execute(SuspendResumeRequest resumeRequest)
            throws ConnectorDataException {
        ResponseType responseType = new ResponseType();
        responseType.setRequestID(resumeRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        String login = resumeRequest.getObjectIdentity();
        SSHAgent ssh = getSSHAgent(resumeRequest.getTargetID());
        try {
            LinuxUser user = new LinuxUser(null, login, null, null, null, null,
                    null, null, null, null);
            ssh.executeCommand(user.getUserUnlockCommand(),
                    this.getPassword(resumeRequest.getTargetID()));
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
