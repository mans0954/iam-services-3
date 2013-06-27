package org.openiam.spml2.spi.ldap.command.factory;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("ldapCommandFactory")
public class LdapCommandFactory {
    private static final String ERROR_PATTERN = "Unsupported Operation: '%s' for object type: '%s' in LDAP connector";

    @Autowired
    @Qualifier("addUserLdapCommand")
    private ConnectorCommand addUserLdapCommand;
    @Autowired
    @Qualifier("deleteUserLdapCommand")
    private ConnectorCommand deleteUserLdapCommand;
    @Autowired
    @Qualifier("lookupUserLdapCommand")
    private ConnectorCommand lookupUserLdapCommand;
    @Autowired
    @Qualifier("modifyUserLdapCommand")
    private ConnectorCommand modifyUserLdapCommand;
    @Autowired
    @Qualifier("resumeLdapCommand")
    private ConnectorCommand resumeLdapCommand;
    @Autowired
    @Qualifier("setPasswordLdapCommand")
    private ConnectorCommand setPasswordLdapCommand;
    @Autowired
    @Qualifier("suspendLdapCommand")
    private ConnectorCommand suspendLdapCommand;




    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType) throws ConnectorDataException {

        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType);
        if(ProvisionObjectType.USER==provisionObjectType){
            switch (commandType){
                case ADD:
                    return addUserLdapCommand;
                case DELETE:
                    return deleteUserLdapCommand;
//                case LOOKUP_ATTRIBUTE_NAME:
//                    return lookupCSVAttributeNamesCommand;
                case LOOKUP:
                    return lookupUserLdapCommand;
                case MODIFY:
                    return modifyUserLdapCommand;
                case RESUME:
                    return resumeLdapCommand;
                case SET_PASSWORD:
                    return setPasswordLdapCommand;
                case SUSPEND:
                    return suspendLdapCommand;
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
