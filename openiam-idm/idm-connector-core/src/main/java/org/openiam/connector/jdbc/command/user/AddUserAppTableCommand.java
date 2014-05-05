package org.openiam.connector.jdbc.command.user;

import org.openiam.connector.jdbc.command.base.AbstractAddAppTableCommand;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

@Service("addUserAppTableCommand")
public class AddUserAppTableCommand extends AbstractAddAppTableCommand<ExtensibleObject> {

    @Override
    protected String getObjectType() {
        return "USER";
    }
}
