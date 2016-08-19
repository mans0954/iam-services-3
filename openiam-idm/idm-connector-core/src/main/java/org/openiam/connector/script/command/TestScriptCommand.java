package org.openiam.connector.script.command;

import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.script.command.base.AbstractScriptCommand;
import org.openiam.provision.request.RequestType;
import org.openiam.base.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 7/18/13 Time: 1:57 AM To
 * change this template use File | Settings | File Templates.
 */
@Service("testScriptCommand")
public class TestScriptCommand extends AbstractScriptCommand<RequestType, ResponseType> {
    @Override
    protected CommandType getCommandType() {
        return CommandType.TEST;
    }

    @Override
    protected String getFileName(ManagedSysEntity msys) throws Exception {
        return msys.getTestConnectionHandler();
    }

}
