package org.openiam.spml2.spi.jdbc.command;

import org.apache.commons.lang.StringUtils;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.spi.jdbc.command.base.AbstractModifyAppTableCommand;
import org.springframework.stereotype.Service;

@Service("modifyUserAppTableCommand")
public class ModifyUserAppTableCommand extends AbstractModifyAppTableCommand<ProvisionUser> {
    @Override
    protected boolean compareObjectTypeWithObject(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, "user");
    }
}
