package org.openiam.connector.script.command;

import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.spml2.constants.CommandType;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 2:00 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("suspendScriptCommand")
public class SuspendScriptCommand extends AbstractScriptCommand<SuspendResumeRequest,ResponseType> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.SUSPEND;
    }
}
