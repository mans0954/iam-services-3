package org.openiam.connector.linux.command.user;

import org.openiam.connector.linux.command.base.AbstractCrudLinuxCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("modifyUserLinuxCommand")
public class ModifyUserLinuxCommand extends
        AbstractCrudLinuxCommand<ExtensibleUser> {

    // /**
    // * Detects whether a given modifications list contains a rename directive
    // *
    // * @param extensibleUser
    // * @return Original user account name; null if unchanged
    // */
    // private String isRename(ExtensibleUser extensibleUser) {
    // for (ExtensibleAttribute att : extensibleUser.getAttributes()) {
    // if (att.getOperation() != 0 && att.getName() != null
    // && att.getName().equalsIgnoreCase("ORIG_IDENTITY")) {
    // return att.getValue();
    // }
    // }
    // return null;
    // }

    @Override
    protected String getCommandScriptHandler(String id) {
        return managedSysService.getManagedSysById(id).getModifyHandler();
    }
}
