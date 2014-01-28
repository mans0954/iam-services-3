package org.openiam.connector.jdbc.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.jdbc.command.base.AbstractModifyAppTableCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("modifyUserAppTableCommand")
public class ModifyUserAppTableCommand extends AbstractModifyAppTableCommand<ExtensibleUser> {
    @Override
    protected boolean compareObjectTypeWithObject(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, "user");
    }
}
