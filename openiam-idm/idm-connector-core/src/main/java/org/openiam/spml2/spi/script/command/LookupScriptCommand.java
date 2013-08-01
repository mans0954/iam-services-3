package org.openiam.spml2.spi.script.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.LookupRequestType;
import org.openiam.spml2.msg.LookupResponseType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:41 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("lookupScriptCommand")
public class LookupScriptCommand extends BaseScriptCommand<LookupRequestType,LookupResponseType> {
    @Override
    public LookupResponseType execute(LookupRequestType lookupRequestType) throws ConnectorDataException {
        String targetID = lookupRequestType.getPsoID().getTargetID();
        return runCommand(targetID, lookupRequestType);
    }
}
