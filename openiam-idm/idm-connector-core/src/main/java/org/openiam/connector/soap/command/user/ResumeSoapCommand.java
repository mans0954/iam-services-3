package org.openiam.connector.soap.command.user;

import org.openiam.connector.soap.command.base.AbstractSoapAccountStatusCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 2:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("resumeSoapCommand")
public class ResumeSoapCommand extends AbstractSoapAccountStatusCommand {
    @Override
    protected String getNewAccountStatus() {
            return AccountStatus.UNLOCKED.toString();
    }
}
