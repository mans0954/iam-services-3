package org.openiam.connector.script.command;

import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.script.command.base.BaseScriptCommand;
import org.openiam.connector.common.constants.CommandType;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:57 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("testScriptCommand")
public class TestScriptCommand extends AbstractScriptCommand<RequestType,ResponseType> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.TEST;
    }
}
