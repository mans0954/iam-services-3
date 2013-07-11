package org.openiam.spml2.spi.orcl.command.user;

import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.spi.orcl.command.base.AbstractOracleAccountStatusCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 2:06 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("resumeOracleCommand")
public class ResumeOracleCommand extends AbstractOracleAccountStatusCommand<ResumeRequestType> {
    @Override
    protected String getNewAccountStatus() {
            return AccountStatus.UNLOCKED.toString();
    }
}
