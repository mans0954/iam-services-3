package org.openiam.connector.scim.command.user;

import org.openiam.connector.scim.command.base.AbstractScimAccountStatusCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/12/13 Time: 2:05 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("suspendScimCommand")
public class SuspendScimCommand extends AbstractScimAccountStatusCommand {
	@Override
	protected String getNewAccountStatus() {
		return AccountStatus.LOCKED.toString();
	}
}
