package org.openiam.connector.script.command;

import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/18/13 Time: 1:55 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("resetPasswordScriptCommand")
public class ResetPasswordScriptCommand extends AbstractScriptCommand<PasswordRequest, ResponseType> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.RESET_PASSWORD;
    }

    @Override
    protected String getFileName(ManagedSysEntity msys) throws Exception {
        return msys.getPasswordHandler();
    }

}
