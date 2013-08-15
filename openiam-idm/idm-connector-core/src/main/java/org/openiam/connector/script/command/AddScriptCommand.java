package org.openiam.connector.script.command;

import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.common.constants.CommandType;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addScriptCommand")
public final class AddScriptCommand extends AbstractScriptCommand<CrudRequest, ObjectResponse> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.ADD;
    }
}
