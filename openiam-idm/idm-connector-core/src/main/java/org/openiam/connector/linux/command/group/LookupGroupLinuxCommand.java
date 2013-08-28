package org.openiam.connector.linux.command.group;

import org.openiam.connector.linux.command.base.AbstractLookupLinuxCommand;
import org.openiam.connector.linux.data.LinuxGroup;
import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("lookupGroupLinuxCommand")
public class LookupGroupLinuxCommand extends
        AbstractLookupLinuxCommand<ExtensibleUser> {

    @Override
    protected boolean lookupObject(String id, SSHAgent ssh)
            throws ConnectorDataException {
        LinuxGroup group = objectToLinuxGroup(id);
        if (group != null) {
            try {
                String result = ssh.executeCommand(group
                        .getLookupGroupCommand());
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
