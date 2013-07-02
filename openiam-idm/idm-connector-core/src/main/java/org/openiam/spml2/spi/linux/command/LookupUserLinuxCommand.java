package org.openiam.spml2.spi.linux.command;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.linux.LinuxUser;
import org.openiam.spml2.spi.linux.command.base.AbstractLookupLinuxCommand;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

@Service("lookupUserLinuxCommand")
public class LookupUserLinuxCommand extends AbstractLookupLinuxCommand<ProvisionUser> {

    @Override
    protected boolean lookupObject(String id, SSHAgent ssh) throws ConnectorDataException {
        LinuxUser user = objectToLinuxUser(id, null);
        if (user != null) {
            try {
               String result = ssh.executeCommand(user.getUserExistsCommand());
               return  (result != null && result.trim().length() > 0);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
       return false;
    }
}
