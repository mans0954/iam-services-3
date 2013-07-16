package org.openiam.spml2.spi.salesforce.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("salesForceCommandFactory")
public class SalesForceCommandFactory extends AbstractCommandFactory {
    @Autowired
    @Qualifier("addUserSalesForceCommand")
    private ConnectorCommand addUserSalesForceCommand;
    @Autowired
    @Qualifier("deleteUserSalesForceCommand")
    private ConnectorCommand deleteUserSalesForceCommand;
    @Autowired
    @Qualifier("lookupUserSalesForceCommand")
    private ConnectorCommand lookupUserSalesForceCommand;
    @Autowired
    @Qualifier("modifyUserSalesForceCommand")
    private ConnectorCommand modifyUserSalesForceCommand;
    @Autowired
    @Qualifier("resumeSalesForceCommand")
    private ConnectorCommand resumeSalesForceCommand;
    @Autowired
    @Qualifier("setPasswordSalesForceCommand")
    private ConnectorCommand setPasswordSalesForceCommand;
    @Autowired
    @Qualifier("suspendSalesForceCommand")
    private ConnectorCommand suspendSalesForceCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, "SalesForce");
        if(ProvisionObjectType.USER==provisionObjectType){
            switch (commandType){
                case ADD:
                    return addUserSalesForceCommand;
                case DELETE:
                    return deleteUserSalesForceCommand;
                case MODIFY:
                    return modifyUserSalesForceCommand;
                case RESUME:
                    return resumeSalesForceCommand;
                case SET_PASSWORD:
                    return setPasswordSalesForceCommand;
                case SUSPEND:
                    return suspendSalesForceCommand;
                case LOOKUP:
                    return lookupUserSalesForceCommand;
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
