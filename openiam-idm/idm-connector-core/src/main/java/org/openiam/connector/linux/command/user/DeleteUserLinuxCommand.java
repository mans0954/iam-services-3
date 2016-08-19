package org.openiam.connector.linux.command.user;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("deleteUserLinuxCommand")
public class DeleteUserLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {

    @Override
    protected String getCommandScriptHandler(String id) {
        return managedSysService.getManagedSysById(id).getDeleteHandler();
    }
}
