package org.openiam.connector.script.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("scriptCommandFactory")
public class ScriptCommandFactory extends AbstractCommandFactory {
    @Autowired
    @Qualifier("addScriptCommand")
    private ConnectorCommand addScriptCommand;
    @Autowired
    @Qualifier("deleteScriptCommand")
    private ConnectorCommand deleteScriptCommand;
    @Autowired
    @Qualifier("expirePasswordCommand")
    private ConnectorCommand expirePasswordCommand;
    @Autowired
    @Qualifier("lookupScriptCommand")
    private ConnectorCommand lookupScriptCommand;
    @Autowired
    @Qualifier("modifyScriptCommand")
    private ConnectorCommand modifyScriptCommand;
    @Autowired
    @Qualifier("resumeScriptCommand")
    private ConnectorCommand resumeScriptCommand;
    @Autowired
    @Qualifier("setPasswordScriptCommand")
    private ConnectorCommand setPasswordScriptCommand;
    @Autowired
    @Qualifier("suspendScriptCommand")
    private ConnectorCommand suspendScriptCommand;
    @Autowired
    @Qualifier("testScriptCommand")
    private ConnectorCommand testScriptCommand;
    @Autowired
    @Qualifier("validatePasswordScriptCommand")
    private ConnectorCommand validatePasswordScriptCommand;
    @Autowired
    @Qualifier("searchScriptCommand")
    private ConnectorCommand searchScriptCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType)
            throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, "SalesForce");
        switch (commandType) {
        case ADD:
            return addScriptCommand;
        case DELETE:
            return deleteScriptCommand;
        case MODIFY:
            return modifyScriptCommand;
        case RESUME:
            return resumeScriptCommand;
        case SET_PASSWORD:
            return setPasswordScriptCommand;
        case RESET_PASSWORD:
            return setPasswordScriptCommand;
        case SUSPEND:
            return suspendScriptCommand;
        case LOOKUP:
            return lookupScriptCommand;
        case EXPIRE_PASSWORD:
            return expirePasswordCommand;
        case TEST:
            return testScriptCommand;
        case VALIDATE_PASSWORD:
            return validatePasswordScriptCommand;
        case SEARCH:
            return searchScriptCommand;
        default:
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
