package org.openiam.connector.rest.command.user;

import org.openiam.connector.rest.command.base.AbstractRestAccountStatusCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 2:06 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("resumeRestCommand")
public class ResumeRestCommand extends AbstractRestAccountStatusCommand {
	@Override
	protected String getNewAccountStatus() {
		return AccountStatus.UNLOCKED.toString();
	}

	@Override
	protected String getCommandScriptHandler(String id) {
		return managedSysService.getManagedSysById(id).getSuspendHandler();
	}
}
