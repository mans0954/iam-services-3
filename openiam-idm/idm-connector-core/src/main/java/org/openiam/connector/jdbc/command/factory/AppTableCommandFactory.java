package org.openiam.connector.jdbc.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("appTableCommandFactory")
public class AppTableCommandFactory extends AbstractCommandFactory {
    @Autowired
    @Qualifier("addUserAppTableCommand")
    private ConnectorCommand addUserAppTableCommand;
    @Autowired
    @Qualifier("deleteUserAppTableCommand")
    private ConnectorCommand deleteUserAppTableCommand;
    @Autowired
    @Qualifier("lookupUserAppTableCommand")
    private ConnectorCommand lookupUserAppTableCommand;
    @Autowired
    @Qualifier("modifyUserAppTableCommand")
    private ConnectorCommand modifyUserAppTableCommand;
    @Autowired
    @Qualifier("resumeAppTableCommand")
    private ConnectorCommand resumeAppTableCommand;
    @Autowired
    @Qualifier("setPasswordAppTableCommand")
    private ConnectorCommand setPasswordAppTableCommand;
    @Autowired
    @Qualifier("suspendAppTableCommand")
    private ConnectorCommand suspendAppTableCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, "APP TABLE");
        if(ExtensibleObjectType.USER==extensibleObjectType){
            switch (commandType){
                case ADD:
                    return addUserAppTableCommand;
                case DELETE:
                    return deleteUserAppTableCommand;
                case RESUME:
                    return resumeAppTableCommand;
                case SET_PASSWORD:
                    return setPasswordAppTableCommand;
                case SUSPEND:
                    return suspendAppTableCommand;
                case MODIFY:
                    return modifyUserAppTableCommand;
                case LOOKUP:
                    return lookupUserAppTableCommand;
                default:
                    throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
            }
        } else if(ExtensibleObjectType.GROUP==extensibleObjectType){
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        } else {
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
