package org.openiam.connector.jdbc.command.group;

import org.openiam.connector.jdbc.command.base.AbstractAddAppTableCommand;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

@Service("addGroupAppTableCommand")
public class AddGroupAppTableCommand extends AbstractAddAppTableCommand<ExtensibleObject> {

    @Override
    protected String getObjectType() {
        return "GROUP";
    }

}
