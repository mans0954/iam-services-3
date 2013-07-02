package org.openiam.spml2.spi.gapps.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.common.factory.AbstractCommandFactory;
import org.openiam.spml2.spi.csv.command.TestGroupCSVCommand;
import org.openiam.spml2.spi.csv.command.TestUserCSVCommand;
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

    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, "GOOGLE APPS");
        if(ProvisionObjectType.USER==provisionObjectType){
            switch (commandType){
                case ADD:
                    return addUserGoogleAppsCommand;
                case DELETE:
                    return deleteUserGoogleAppsCommand;
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
