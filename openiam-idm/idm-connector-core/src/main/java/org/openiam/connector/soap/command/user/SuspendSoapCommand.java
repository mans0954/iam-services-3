package org.openiam.connector.soap.command.user;

import org.openiam.connector.soap.command.base.AbstractSoapAccountStatusCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 2:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("suspendSoapCommand")
public class SuspendSoapCommand extends AbstractSoapAccountStatusCommand {
    @Override
    protected String getNewAccountStatus() {
        return AccountStatus.LOCKED.toString();
    }

	@Override
	protected String getCommandScriptHandler(String id) {
		// TODO Auto-generated method stub
		return null;
	}
}
