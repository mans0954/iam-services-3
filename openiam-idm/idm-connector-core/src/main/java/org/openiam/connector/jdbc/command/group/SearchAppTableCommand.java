package org.openiam.connector.jdbc.command.group;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.jdbc.command.base.AbstractSearchAppTableCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("searchGroupAppTableCommand")
public class SearchAppTableCommand extends AbstractSearchAppTableCommand<ExtensibleUser> {

    @Override
    protected boolean compareObjectTypeWithId(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, "principal");
    }

    @Override
    protected String getObjectType() {
        return "GROUP";
    }

}
