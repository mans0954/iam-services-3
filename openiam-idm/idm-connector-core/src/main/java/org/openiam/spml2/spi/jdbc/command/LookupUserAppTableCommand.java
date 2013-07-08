package org.openiam.spml2.spi.jdbc.command;

import org.apache.commons.lang.StringUtils;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.spi.jdbc.command.base.AbstractLookupAppTableCommand;
import org.springframework.stereotype.Service;

@Service("lookupUserAppTableCommand")
public class LookupUserAppTableCommand extends AbstractLookupAppTableCommand<ProvisionUser> {
    @Override
    protected boolean compareObjectTypeWithId(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, "principal");
    }

    @Override
    protected boolean compareObjectTypeWithObject(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, "USER");
    }
}
