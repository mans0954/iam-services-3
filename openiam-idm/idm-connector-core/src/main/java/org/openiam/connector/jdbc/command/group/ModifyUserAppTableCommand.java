package org.openiam.connector.jdbc.command.group;

import org.openiam.connector.jdbc.command.base.AbstractModifyAppTableCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("modifyGroupAppTableCommand")
public class ModifyUserAppTableCommand extends AbstractModifyAppTableCommand<ExtensibleUser> {

    @Override
    protected String getObjectType() {
        return "GROUP";
    }
}
