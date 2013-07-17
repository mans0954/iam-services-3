package org.openiam.spml2.spi.script.command;

import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.suspend.ResumeRequestType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 2:01 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("resumeScriptCommand")
public class ResumeScriptCommand extends BaseScriptCommand<ResumeRequestType,ResponseType> {
    @Override
    public ResponseType execute(ResumeRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getPsoID().getTargetID();
        return runCommand(targetID, requestType);
    }
}
