package org.openiam.spml2.spi.script.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addScriptCommand")
public final class AddScriptCommand extends BaseScriptCommand<AddRequestType,AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getTargetID();
        return runCommand(targetID, requestType);
    }
}
