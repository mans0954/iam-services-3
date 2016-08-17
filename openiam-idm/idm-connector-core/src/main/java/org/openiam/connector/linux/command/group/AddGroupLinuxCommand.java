package org.openiam.connector.linux.command.group;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("addGroupLinuxCommand")
public class AddGroupLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {

    @Override
    protected String getCommandScriptHandler(String id) {
        return managedSysService.getManagedSysById(id).getAddHandler();
    }
}
