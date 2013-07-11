package org.openiam.spml2.spi.orcl.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("oracleCommandFactory")
public class OracleCommandFactory extends AbstractCommandFactory {
    @Autowired
    @Qualifier("addUserOracleCommand")
    private ConnectorCommand addUserOracleCommand;
    @Autowired
    @Qualifier("deleteUserOracleCommand")
    private ConnectorCommand deleteUserOracleCommand;
    @Autowired
    @Qualifier("lookupUserOracleCommand")
    private ConnectorCommand lookupUserOracleCommand;
    @Autowired
    @Qualifier("resumeOracleCommand")
    private ConnectorCommand resumeOracleCommand;
    @Autowired
    @Qualifier("setPasswordOracleCommand")
    private ConnectorCommand setPasswordOracleCommand;
    @Autowired
    @Qualifier("suspendOracleCommand")
    private ConnectorCommand suspendOracleCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, "ORACLE");
        if(ProvisionObjectType.USER==provisionObjectType){
            switch (commandType){
                case ADD:
                    return addUserOracleCommand;
                case DELETE:
                    return deleteUserOracleCommand;
                case RESUME:
                    return resumeOracleCommand;
                case SET_PASSWORD:
                    return setPasswordOracleCommand;
                case SUSPEND:
                    return suspendOracleCommand;
                case LOOKUP:
                    return lookupUserOracleCommand;
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
