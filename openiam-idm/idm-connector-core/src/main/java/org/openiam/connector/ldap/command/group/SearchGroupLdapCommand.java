package org.openiam.connector.ldap.command.group;

import org.openiam.connector.ldap.command.base.AbstractSearchLdapCommand;
import org.openiam.provision.type.ExtensibleGroup;
import org.springframework.stereotype.Service;

@Service("searchGroupLdapCommand")
public class SearchGroupLdapCommand extends AbstractSearchLdapCommand<ExtensibleGroup> {
    @Override
    protected String getObjectType() {
        return "GROUP";
    }
}
