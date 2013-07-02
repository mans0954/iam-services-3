package org.openiam.spml2.spi.mysql.command;

import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.spi.mysql.command.base.AbstractAddMySQLCommand;
import org.springframework.stereotype.Service;

@Service("addUserMySQLCommand")
public class AddUserMySQLCommand extends AbstractAddMySQLCommand<ProvisionUser> {
}
