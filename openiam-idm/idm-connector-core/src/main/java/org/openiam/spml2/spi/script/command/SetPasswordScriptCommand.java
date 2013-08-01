package org.openiam.spml2.spi.script.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.password.SetPasswordRequestType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:55 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("setPasswordScriptCommand")
public class SetPasswordScriptCommand  extends BaseScriptCommand<SetPasswordRequestType,ResponseType> {
    @Override
    public ResponseType execute(SetPasswordRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getPsoID().getTargetID();
        return runCommand(targetID, requestType);
    }
}
