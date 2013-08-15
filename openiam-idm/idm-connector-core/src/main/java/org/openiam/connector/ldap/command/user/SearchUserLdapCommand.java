package org.openiam.connector.ldap.command.user;

import org.openiam.connector.ldap.command.base.AbstractSearchLdapCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/6/13
 * Time: 10:08 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("searchUserLdapCommand")
public class SearchUserLdapCommand extends AbstractSearchLdapCommand<ExtensibleUser> {
    @Override
    protected String getObjectType() {
        return "USER";
    }
}
