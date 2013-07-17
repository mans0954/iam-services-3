package org.openiam.spml2.spi.script.command;

import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ModifyRequestType;
import org.openiam.spml2.msg.ModifyResponseType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:44 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("modifyScriptCommand")
public class ModifyScriptCommand extends BaseScriptCommand<ModifyRequestType,ModifyResponseType> {
    @Override
    public ModifyResponseType execute(ModifyRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getPsoID().getTargetID();
        return runCommand(targetID, requestType);
    }
}
