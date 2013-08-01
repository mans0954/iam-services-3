package org.openiam.spml2.spi.script.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.password.ResetPasswordRequestType;
import org.openiam.spml2.msg.password.ResetPasswordResponseType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:53 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("expirePasswordCommand")
public class ExpirePasswordCommand  extends BaseScriptCommand<ResetPasswordRequestType,ResetPasswordResponseType> {
    @Override
    public ResetPasswordResponseType execute(ResetPasswordRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getPsoID().getTargetID();
        return runCommand(targetID, requestType);
    }
}
