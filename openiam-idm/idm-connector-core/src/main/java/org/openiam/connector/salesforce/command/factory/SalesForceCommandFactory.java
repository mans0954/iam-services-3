package org.openiam.connector.salesforce.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("salesForceCommandFactory")
public class SalesForceCommandFactory extends AbstractCommandFactory {
    @Autowired
    @Qualifier("addModifyUserSalesForceCommand")
    private ConnectorCommand addModifyUserSalesForceCommand;
    @Autowired
    @Qualifier("deleteUserSalesForceCommand")
    private ConnectorCommand deleteUserSalesForceCommand;
    @Autowired
    @Qualifier("lookupUserSalesForceCommand")
    private ConnectorCommand lookupUserSalesForceCommand;
    @Autowired
    @Qualifier("resumeSalesForceCommand")
    private ConnectorCommand resumeSalesForceCommand;
    @Autowired
    @Qualifier("setPasswordSalesForceCommand")
    private ConnectorCommand setPasswordSalesForceCommand;
    @Autowired
    @Qualifier("suspendSalesForceCommand")
    private ConnectorCommand suspendSalesForceCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, "SalesForce");
        if(ExtensibleObjectType.USER==extensibleObjectType){
            switch (commandType){
                case ADD:
                case MODIFY:
                    return addModifyUserSalesForceCommand;
                case DELETE:
                    return deleteUserSalesForceCommand;
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
        } else if(ExtensibleObjectType.GROUP==extensibleObjectType){
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        } else {
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
