package org.openiam.spml2;

import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.csv.TestGroupCSVCommand;
import org.openiam.spml2.spi.csv.TestUserCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("connectorCommandFactory")
public class ConnectorCommandFactory {
    private static final String ERROR_PATTERN = "Unsupported Operation: '%s' for object type: '%s' in %s connector";

    @Autowired
    @Qualifier("addGroupCSVCommand")
    private ConnectorCommand addGroupCSVCommand;
    @Autowired
    @Qualifier("addUserCSVCommand")
    private ConnectorCommand addUserCSVCommand;
    @Autowired
    @Qualifier("deleteGroupCSVCommand")
    private ConnectorCommand deleteGroupCSVCommand;
    @Autowired
    @Qualifier("deleteUserCSVCommand")
    private ConnectorCommand deleteUserCSVCommand;
    @Autowired
    @Qualifier("lookupCSVAttributeNamesCommand")
    private ConnectorCommand lookupCSVAttributeNamesCommand;
    @Autowired
    @Qualifier("lookupGroupCSVCommand")
    private ConnectorCommand lookupGroupCSVCommand;
    @Autowired
    @Qualifier("lookupUserCSVCommand")
    private ConnectorCommand lookupUserCSVCommand;
    @Autowired
    @Qualifier("modifyGroupCSVCommand")
    private ConnectorCommand modifyGroupCSVCommand;
    @Autowired
    @Qualifier("modifyUserCsvCommand")
    private ConnectorCommand modifyUserCsvCommand;
    @Autowired
    @Qualifier("testUserCSVCommand")
    private TestUserCSVCommand testUserCSVCommand;
    @Autowired
    @Qualifier("testGroupCSVCommand")
    private TestGroupCSVCommand testGroupCSVCommand;


    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType, ConnectorType connectorType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, connectorType);
        if(ConnectorType.CSV==connectorType){
                if(ProvisionObjectType.USER==provisionObjectType){
                    switch (commandType){
                        case ADD:
                            return addUserCSVCommand;
                        case DELETE:
                            return deleteUserCSVCommand;
                        case LOOKUP_ATTRIBUTE_NAME:
                            return lookupCSVAttributeNamesCommand;
                        case LOOKUP:
                            return lookupUserCSVCommand;
                        case MODIFY:
                            return modifyUserCsvCommand;
                        case TEST:
                            return testUserCSVCommand;
                        default:
                            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
                    }
                } else if(ProvisionObjectType.GROUP==provisionObjectType){
                    switch (commandType){
                        case ADD:
                            return addGroupCSVCommand;
                        case DELETE:
                            return deleteGroupCSVCommand;
                        case LOOKUP_ATTRIBUTE_NAME:
                            return lookupCSVAttributeNamesCommand;
                        case LOOKUP:
                            return lookupGroupCSVCommand;
                        case MODIFY:
                            return modifyGroupCSVCommand;
                        case TEST:
                            return testGroupCSVCommand;
                        default:
                            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
                    }
                } else {
                    throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
                }
        } else {
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
