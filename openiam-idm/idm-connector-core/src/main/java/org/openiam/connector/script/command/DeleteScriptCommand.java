package org.openiam.connector.script.command;

import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.spml2.constants.CommandType;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:03 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteScriptCommand")
public class DeleteScriptCommand extends AbstractScriptCommand<CrudRequest, ObjectResponse> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.DELETE;
    }
}
