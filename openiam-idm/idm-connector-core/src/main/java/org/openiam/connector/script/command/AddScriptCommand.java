package org.openiam.connector.script.command;

import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/17/13 Time: 3:33 PM To
 * change this template use File | Settings | File Templates.
 */
@Service("addScriptCommand")
public final class AddScriptCommand extends AbstractScriptCommand<CrudRequest, ObjectResponse> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.ADD;
    }

    @Override
    protected String getFileName(ManagedSysEntity msys) {
        return msys.getAddHandler();
    }

}
