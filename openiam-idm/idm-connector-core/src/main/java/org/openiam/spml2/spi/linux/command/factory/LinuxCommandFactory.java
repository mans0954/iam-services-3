package org.openiam.spml2.spi.linux.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("linuxCommandFactory")
public class LinuxCommandFactory extends AbstractCommandFactory {
    @Autowired
    @Qualifier("addUserLinuxCommand")
    private ConnectorCommand addUserLinuxCommand;
    @Autowired
    @Qualifier("deleteUserLinuxCommand")
    private ConnectorCommand deleteUserLinuxCommand;
    @Autowired
    @Qualifier("expirePasswordLinuxCommand")
    private ConnectorCommand expirePasswordLinuxCommand;
    @Autowired
    @Qualifier("lookupUserLinuxCommand")
    private ConnectorCommand lookupUserLinuxCommand;
    @Autowired
    @Qualifier("modifyUserLinuxCommand")
    private ConnectorCommand modifyUserLinuxCommand;
    @Autowired
    @Qualifier("resumeLinuxCommand")
    private ConnectorCommand resumeLinuxCommand;
    @Autowired
    @Qualifier("setPasswordLinuxCommand")
    private ConnectorCommand setPasswordLinuxCommand;
    @Autowired
    @Qualifier("suspendRequestType")
    private ConnectorCommand suspendRequestType;
    @Autowired
    @Qualifier("testUserLinuxCommand")
    private ConnectorCommand testUserLinuxCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, "LINUX");
        if(ProvisionObjectType.USER==provisionObjectType){
            switch (commandType){
                case ADD:
                    return addUserLinuxCommand;
                case DELETE:
                    return deleteUserLinuxCommand;
                case RESUME:
                    return resumeLinuxCommand;
                case SET_PASSWORD:
                    return setPasswordLinuxCommand;
                case SUSPEND:
                    return suspendRequestType;
                case MODIFY:
                    return modifyUserLinuxCommand;
                case TEST:
                    return testUserLinuxCommand;
                case EXPIRE_PASSWORD:
                    return  expirePasswordLinuxCommand;
                case LOOKUP:
                    return lookupUserLinuxCommand;
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
