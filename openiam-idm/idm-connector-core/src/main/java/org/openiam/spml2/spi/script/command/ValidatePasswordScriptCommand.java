package org.openiam.spml2.spi.script.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.password.ValidatePasswordRequestType;
import org.openiam.spml2.msg.password.ValidatePasswordResponseType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 2:03 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("validatePasswordScriptCommand")
public class ValidatePasswordScriptCommand extends BaseScriptCommand<ValidatePasswordRequestType,ValidatePasswordResponseType> {
    @Override
    public ValidatePasswordResponseType execute(ValidatePasswordRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getPsoID().getTargetID();
        return runCommand(targetID, requestType);
    }
}
