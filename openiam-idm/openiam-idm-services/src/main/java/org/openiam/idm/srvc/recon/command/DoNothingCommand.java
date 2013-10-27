package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.recon.service.ReconciliationCommand;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.type.ExtensibleAttribute;

import java.util.List;

public class DoNothingCommand implements ReconciliationCommand {
    private static final Log log = LogFactory.getLog(DoNothingCommand.class);

    public boolean execute(Login login, User user, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DoNothingCommand");
        log.debug("Do nothing for user :" + login.getUserId());
        return false;
    }
}
