package org.openiam.connector.script.command;

import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.connector.type.request.PasswordRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

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
