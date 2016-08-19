package org.openiam.connector.script.command;

import javax.naming.OperationNotSupportedException;

import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.request.PasswordRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/18/13 Time: 2:03 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("validatePasswordScriptCommand")
public class ValidatePasswordScriptCommand extends AbstractScriptCommand<PasswordRequest, ResponseType> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.VALIDATE_PASSWORD;
    }

    @Override
    public ResponseType execute(PasswordRequest request) throws ConnectorDataException {
        return this.runCommand(request.getTargetID(), request);
    }

    @Override
    protected String getFileName(ManagedSysEntity msys) throws Exception {
        throw new OperationNotSupportedException();
    }
}
