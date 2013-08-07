package org.openiam.connector.mysql.command.user;

import org.openiam.connector.mysql.command.base.AbstractCrudMySQLCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

@Service("addUserMySQLCommand")
public class AddUserMySQLCommand extends AbstractCrudMySQLCommand<ExtensibleUser> {
}
