package org.openiam.connector.ldap.command.user;

import org.openiam.connector.ldap.command.base.AbstractTestLdapCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("testUserLdapCommand")
public class TestUserLdapCommand extends AbstractTestLdapCommand<User, ExtensibleUser> {
}