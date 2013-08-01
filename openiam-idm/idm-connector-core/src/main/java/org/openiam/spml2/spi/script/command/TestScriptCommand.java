package org.openiam.spml2.spi.script.command;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.TestRequestType;
import org.openiam.spml2.spi.script.command.base.BaseScriptCommand;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/18/13
 * Time: 1:57 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("testScriptCommand")
public class TestScriptCommand extends BaseScriptCommand<TestRequestType,ResponseType> {
    @Override
    public ResponseType execute(TestRequestType requestType) throws ConnectorDataException {
        String targetID = requestType.getPsoID().getTargetID();
        return runCommand(targetID, requestType);
    }
}
