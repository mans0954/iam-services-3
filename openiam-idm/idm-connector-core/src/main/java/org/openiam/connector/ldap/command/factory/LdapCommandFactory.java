package org.openiam.connector.ldap.command.factory;

import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ldapCommandFactory")
public class LdapCommandFactory extends AbstractCommandFactory {
    @Autowired
    @Qualifier("addUserLdapCommand")
    private ConnectorCommand addUserLdapCommand;
    @Autowired
    @Qualifier("addGroupLdapCommand")
    private ConnectorCommand addGroupLdapCommand;
    @Autowired
    @Qualifier("deleteUserLdapCommand")
    private ConnectorCommand deleteUserLdapCommand;
    @Autowired
    @Qualifier("deleteGroupLdapCommand")
    private ConnectorCommand deleteGroupLdapCommand;
    @Autowired
    @Qualifier("lookupAttributeNamesCommand")
    private ConnectorCommand lookupAttributeNamesCommand;
    @Autowired
    @Qualifier("lookupUserLdapCommand")
    private ConnectorCommand lookupUserLdapCommand;
    @Autowired
    @Qualifier("lookupGroupLdapCommand")
    private ConnectorCommand lookupGroupLdapCommand;
    @Autowired
    @Qualifier("modifyUserLdapCommand")
    private ConnectorCommand modifyUserLdapCommand;
    @Autowired
    @Qualifier("modifyGroupLdapCommand")
    private ConnectorCommand modifyGroupLdapCommand;
    @Autowired
    @Qualifier("resumeLdapCommand")
    private ConnectorCommand resumeLdapCommand;
    @Autowired
    @Qualifier("setPasswordLdapCommand")
    private ConnectorCommand setPasswordLdapCommand;
    @Autowired
    @Qualifier("suspendLdapCommand")
    private ConnectorCommand suspendLdapCommand;
    @Autowired
    @Qualifier("searchUserLdapCommand")
    private ConnectorCommand searchUserLdapCommand;
    @Autowired
    @Qualifier("searchGroupLdapCommand")
    private ConnectorCommand searchGroupLdapCommand;
    @Autowired
    @Qualifier("testUserLdapCommand")
    private ConnectorCommand testUserLdapCommand;

    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, "LDAP");
        if(ExtensibleObjectType.USER==extensibleObjectType){
            switch (commandType){
                case ADD:
                    return addUserLdapCommand;
                case DELETE:
                    return deleteUserLdapCommand;
                case LOOKUP_ATTRIBUTE_NAME:
                    return lookupAttributeNamesCommand;
                case LOOKUP:
                    return lookupUserLdapCommand;
                case SEARCH:
                    return  searchUserLdapCommand;
                case MODIFY:
                    return modifyUserLdapCommand;
                case RESUME:
                    return resumeLdapCommand;
                case SET_PASSWORD:
                    return setPasswordLdapCommand;
                case RESET_PASSWORD:
                    return setPasswordLdapCommand;
                case SUSPEND:
                    return suspendLdapCommand;
                case TEST:
                    return testUserLdapCommand;
                default:
                    throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
            }
        } else if(ExtensibleObjectType.GROUP==extensibleObjectType){
            switch (commandType){
                case ADD:
                    return addGroupLdapCommand;
                case DELETE:
                    return deleteGroupLdapCommand;
                case LOOKUP_ATTRIBUTE_NAME:
                    return lookupAttributeNamesCommand;
                case LOOKUP:
                    return lookupGroupLdapCommand;
                case SEARCH:
                    return  searchGroupLdapCommand;
                case MODIFY:
                    return modifyGroupLdapCommand;
                default:
                    throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
            }
        } else {
            throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }

}
