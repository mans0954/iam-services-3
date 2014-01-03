package org.openiam.connector.gapps.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("googleAppsCommandFactory")
public class GoogleAppsCommandFactory extends AbstractCommandFactory {

    @Autowired
    @Qualifier("addUserGoogleAppsCommand")
    private ConnectorCommand addUserGoogleAppsCommand;
    @Autowired
    @Qualifier("deleteUserGoogleAppsCommand")
    private ConnectorCommand deleteUserGoogleAppsCommand;
    @Autowired
    @Qualifier("modifyUserGoogleAppsCommand")
    private ConnectorCommand modifyUserGoogleAppsCommand;
    @Autowired
    @Qualifier("resumeUserGoogleAppsCommand")
    private ConnectorCommand resumeUserGoogleAppsCommand;
    @Autowired
    @Qualifier("setPasswordGoogleAppsCommand")
    private ConnectorCommand setPasswordGoogleAppsCommand;
    @Autowired
    @Qualifier("suspendUserGoogleAppsCommand")
    private ConnectorCommand suspendUserGoogleAppsCommand;
    @Autowired
    @Qualifier("testUserGoogleAppsCommand")
    private ConnectorCommand testUserGoogleAppsCommand;
    @Autowired
    @Qualifier("searchUserGoogleAppsCommand")
    private ConnectorCommand searchUserGoogleAppsCommand;
    @Autowired
    @Qualifier("lookupUserGoogleAppsCommand")
    private ConnectorCommand lookupUserGoogleAppsCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType,
            ExtensibleObjectType extensibleObjectType)
            throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType,
                extensibleObjectType, "GOOGLE APPS");
        if (ExtensibleObjectType.USER == extensibleObjectType) {
            switch (commandType) {
            case ADD:
                return addUserGoogleAppsCommand;
            case DELETE:
                return deleteUserGoogleAppsCommand;
            case SEARCH:
                return searchUserGoogleAppsCommand;
            case RESUME:
                return resumeUserGoogleAppsCommand;
            case SET_PASSWORD:
                return setPasswordGoogleAppsCommand;
            case SUSPEND:
                return suspendUserGoogleAppsCommand;
            case MODIFY:
                return modifyUserGoogleAppsCommand;
            case TEST:
                return testUserGoogleAppsCommand;
            case LOOKUP:
                return lookupUserGoogleAppsCommand;
            default:
                throw new ConnectorDataException(
                        ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
            }
        } else if (ExtensibleObjectType.GROUP == extensibleObjectType) {
            throw new ConnectorDataException(
                    ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        } else {
            throw new ConnectorDataException(
                    ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }

}
