package org.openiam.spml2.spi.script.command;

import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.script.ScriptIntegration;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.script.command.base.AbstractScriptCommand;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/17/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Service("addScriptCommand")
public final class AddScriptCommand extends BaseScriptCommand<AddRequestType,AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getTargetID();
        return runCommand(targetID, requestType);
    }
}
