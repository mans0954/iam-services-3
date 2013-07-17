package org.openiam.spml2.spi.script.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.common.factory.AbstractCommandFactory;
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

    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, "SalesForce");
        switch (commandType){
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
            case SUSPEND:
                return suspendScriptCommand;
            case LOOKUP:
                return lookupScriptCommand;
            case EXPIRE_PASSWORD:
                return  expirePasswordCommand;
            case TEST:
                return testScriptCommand;
            case VALIDATE_PASSWORD:
                return validatePasswordScriptCommand;
            default:
                throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
