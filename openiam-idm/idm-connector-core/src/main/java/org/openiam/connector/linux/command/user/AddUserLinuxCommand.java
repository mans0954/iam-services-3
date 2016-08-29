package org.openiam.connector.linux.command.user;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("addUserLinuxCommand")
public class AddUserLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {

    @Override
    protected String getCommandScriptHandler(String mSysId) {
        return managedSysService.getManagedSysById(mSysId).getAddHandler();
    }
}
