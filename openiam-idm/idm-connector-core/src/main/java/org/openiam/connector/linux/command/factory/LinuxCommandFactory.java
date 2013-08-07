package org.openiam.connector.linux.command.factory;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
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
    @Qualifier("testLinuxCommand")
    private ConnectorCommand testLinuxCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, "LINUX");
        if(ExtensibleObjectType.USER==extensibleObjectType){
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
                    return testLinuxCommand;
                case EXPIRE_PASSWORD:
                    return  expirePasswordLinuxCommand;
                case LOOKUP:
                    return lookupUserLinuxCommand;
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
