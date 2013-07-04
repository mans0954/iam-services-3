package org.openiam.spml2.spi.linux.command.user;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.ModificationType;
import org.openiam.spml2.spi.linux.command.LinuxUser;
import org.openiam.spml2.spi.linux.command.base.AbstractModifyLinuxCommand;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("modifyUserLinuxCommand")
public class ModifyUserLinuxCommand extends AbstractModifyLinuxCommand<ProvisionUser> {

    @Override
    protected void modifyObject(String principal, List<ModificationType> modification, SSHAgent ssh) throws ConnectorDataException {
        String originalName = isRename(modification);
        String login = (originalName == null) ? principal : originalName;

        LinuxUser user = objectToLinuxUser(principal, modification.get(0).getData().getAny());

        if (user != null) {
            try {
                // Then modify account
                ssh.executeCommand(user.getUserModifyCommand(login));
                ssh.executeCommand(user.getUserSetDetailsCommand());
                sendPassword(ssh, user);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
    }

    /**
     * Detects whether a given modifications list contains a rename directive
     *
     * @param modTypeList modifications list
     * @return Original user account name; null if unchanged
     */
    private String isRename(List<ModificationType> modTypeList) {
        for (ModificationType mod : modTypeList) {
            for (ExtensibleObject obj : mod.getData().getAny()) {
                for (ExtensibleAttribute att : obj.getAttributes()) {
                    if (att.getOperation() != 0 && att.getName() != null && att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
                        return att.getValue();
                    }
                }
            }
        }
        return null;
    }
}
