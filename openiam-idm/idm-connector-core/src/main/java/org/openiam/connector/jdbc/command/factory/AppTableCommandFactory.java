package org.openiam.connector.jdbc.command.factory;

import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
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
    @Autowired
    @Qualifier("searchUserAppTableCommand")
    private ConnectorCommand searchUserAppTableCommand;
    @Autowired
    @Qualifier("testUserAppTableCommand")
    private ConnectorCommand testUserAppTableCommand;
    @Autowired
    @Qualifier("addGroupAppTableCommand")
    private ConnectorCommand addGroupAppTableCommand;
    @Autowired
    @Qualifier("deleteGroupAppTableCommand")
    private ConnectorCommand deleteGroupAppTableCommand;
    @Autowired
    @Qualifier("lookupGroupAppTableCommand")
    private ConnectorCommand lookupGroupAppTableCommand;
    @Autowired
    @Qualifier("modifyGroupAppTableCommand")
    private ConnectorCommand modifyGroupAppTableCommand;
    @Autowired
    @Qualifier("searchGroupAppTableCommand")
    private ConnectorCommand searchGroupAppTableCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType)
            throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, "APP TABLE");
        if (ExtensibleObjectType.USER == extensibleObjectType) {
            switch (commandType) {
                case ADD:
                    return addUserAppTableCommand;
                case DELETE:
                    return deleteUserAppTableCommand;
                case RESUME:
                    return resumeAppTableCommand;
                case RESET_PASSWORD:
                case SET_PASSWORD:
                    return setPasswordAppTableCommand;
                case SUSPEND:
                    return suspendAppTableCommand;
                case MODIFY:
                    return modifyUserAppTableCommand;
                case LOOKUP:
                    return lookupUserAppTableCommand;
                case TEST:
                    return testUserAppTableCommand;
                case SEARCH:
                    return searchUserAppTableCommand;
                default:
                    throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
            }
        } else if (ExtensibleObjectType.GROUP == extensibleObjectType) {
            switch (commandType) {
                case ADD:
                    return addGroupAppTableCommand;
                case DELETE:
                    return deleteGroupAppTableCommand;
                case LOOKUP:
                    return lookupGroupAppTableCommand;
                case SEARCH:
                    return searchGroupAppTableCommand;
                case MODIFY:
                    return modifyGroupAppTableCommand;
                default:
                    throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
            }
        } else {
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
