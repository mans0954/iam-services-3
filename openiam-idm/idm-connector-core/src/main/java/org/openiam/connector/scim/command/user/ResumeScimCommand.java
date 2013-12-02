package org.openiam.connector.scim.command.user;

import org.openiam.connector.scim.command.base.AbstractScimAccountStatusCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 2:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("resumeScimCommand")
public class ResumeScimCommand extends AbstractScimAccountStatusCommand {
    @Override
    protected String getNewAccountStatus() {
            return AccountStatus.UNLOCKED.toString();
    }
}
