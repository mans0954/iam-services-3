package org.openiam.spml2.spi.script.command;

import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.DeleteRequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:03 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("deleteScriptCommand")
public class DeleteScriptCommand extends BaseScriptCommand<DeleteRequestType,ResponseType> {
    @Override
    public ResponseType execute(DeleteRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getPsoID().getTargetID();
        return runCommand(targetID, requestType);
    }
}
