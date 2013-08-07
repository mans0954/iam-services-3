package org.openiam.connector.orcl.command.user;

import org.openiam.connector.orcl.command.base.AbstractOracleAccountStatusCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 2:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("resumeOracleCommand")
public class ResumeOracleCommand extends AbstractOracleAccountStatusCommand {
    @Override
    protected String getNewAccountStatus() {
            return AccountStatus.UNLOCKED.toString();
    }
}
