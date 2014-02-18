package org.openiam.connector.script.command;

import javax.naming.OperationNotSupportedException;

import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/18/13 Time: 1:53 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("expirePasswordCommand")
public class ExpirePasswordCommand extends AbstractScriptCommand<PasswordRequest, ResponseType> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.EXPIRE_PASSWORD;
    }

    @Override
    protected String getFileName(ManagedSysEntity msys) throws Exception {
        throw new OperationNotSupportedException();
    }

}
