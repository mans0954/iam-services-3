package org.openiam.connector.linux.command.user;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.linux.data.LinuxUser;
import org.openiam.connector.linux.command.base.AbstractLookupLinuxCommand;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("lookupUserLinuxCommand")
public class LookupUserLinuxCommand extends
        AbstractLookupLinuxCommand<ExtensibleUser> {

    @Override
    protected boolean lookupObject(String id, SSHAgent ssh)
            throws ConnectorDataException {
        LinuxUser user = objectToLinuxUser(id, null);
        if (user != null) {
            try {
                String result = ssh.executeCommand(user.getUserExistsCommand());
                return (result != null && result.trim().length() > 0);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                        e.getMessage());
            }
        }
        return false;
    }
}
