package org.openiam.spml2.spi.example.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("shellCommandFactory")
public class ShellCommandFactory extends AbstractCommandFactory {

    @Autowired
    @Qualifier("addUserShellCommand")
    private ConnectorCommand addUserShellCommand;
    @Autowired
    @Qualifier("deleteUserShellCommand")
    private ConnectorCommand deleteUserShellCommand;
    @Autowired
    @Qualifier("modifyUserShellCommand")
    private ConnectorCommand modifyUserShellCommand;
    @Autowired
    @Qualifier("setPasswordShellCommand")
    private ConnectorCommand setPasswordShellCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, "SHELL");
        if(ProvisionObjectType.USER==provisionObjectType){
            switch (commandType){
                case ADD:
                    return addUserShellCommand;
                case DELETE:
                    return deleteUserShellCommand;
                case MODIFY:
                    return modifyUserShellCommand;
                case SET_PASSWORD:
                    return setPasswordShellCommand;
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
