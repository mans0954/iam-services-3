package org.openiam.idm.srvc.recon.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationSituation;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("doNothingUserCommand")
public class DoNothingUserCommand extends BaseReconciliationUserCommand {
    private static final Log log = LogFactory.getLog(DoNothingUserCommand.class);

    public DoNothingUserCommand() {
    }

    @Override
    public boolean execute(ReconciliationSituation config, String principal, String mSysID, User user, List<ExtensibleAttribute> attributes) {
        log.debug("Entering DoNothingCommand");
        log.debug("Do nothing for user: " + user.getId());
		ProvisionUser pUser = new ProvisionUser(user);
		pUser.setSrcSystemId(mSysID);
		try {
			executeScript(config.getScript(), attributes, pUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return true;
    }
}
