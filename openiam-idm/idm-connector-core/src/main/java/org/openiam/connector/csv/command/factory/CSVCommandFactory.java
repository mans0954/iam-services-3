package org.openiam.connector.csv.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.openiam.connector.csv.command.group.TestGroupCSVCommand;
import org.openiam.connector.csv.command.user.TestUserCSVCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("csvCommandFactory")
public class CSVCommandFactory extends AbstractCommandFactory {

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

    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, "CSV");
        if(ExtensibleObjectType.USER==extensibleObjectType){
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
        } else if(ExtensibleObjectType.GROUP==extensibleObjectType){
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
    }
}
