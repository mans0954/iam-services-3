package org.openiam.spml2.spi.jdbc.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.common.factory.AbstractCommandFactory;
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

    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, "APP TABLE");
        if(ProvisionObjectType.USER==provisionObjectType){
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
        } else if(ProvisionObjectType.GROUP==provisionObjectType){
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        } else {
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
