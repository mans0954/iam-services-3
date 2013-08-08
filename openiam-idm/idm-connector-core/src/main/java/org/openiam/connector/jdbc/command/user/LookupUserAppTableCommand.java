package org.openiam.connector.jdbc.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.jdbc.command.base.AbstractLookupAppTableCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("lookupUserAppTableCommand")
public class LookupUserAppTableCommand extends AbstractLookupAppTableCommand<ExtensibleUser> {
    @Override
    protected boolean compareObjectTypeWithId(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, "principal");
    }

    @Override
    protected boolean compareObjectTypeWithObject(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, "USER");
    }
}
