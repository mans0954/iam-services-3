package org.openiam.connector;

import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.provision.type.ExtensibleObjectType;
import org.openiam.connector.common.constants.CommandType;
import org.openiam.connector.common.constants.ConnectorType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.common.command.ConnectorCommand;
import org.openiam.connector.common.factory.AbstractCommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("connectorCommandFactory")
public class ConnectorCommandFactory {
    private static final String ERROR_PATTERN = "Unsupported Operation: '%s' for object type: '%s' in %s connector";

    @Autowired
    @Qualifier("csvCommandFactory")
    private AbstractCommandFactory csvCommandFactory;

    @Autowired
    @Qualifier("ldapCommandFactory")
    private AbstractCommandFactory ldapCommandFactory;

    @Autowired
    @Qualifier("googleAppsCommandFactory")
    private AbstractCommandFactory googleAppsCommandFactory;

    @Autowired
    @Qualifier("linuxCommandFactory")
    private AbstractCommandFactory linuxCommandFactory;

    @Autowired
    @Qualifier("mySQLCommandFactory")
    private AbstractCommandFactory mySQLCommandFactory;
    @Autowired
    @Qualifier("appTableCommandFactory")
    private AbstractCommandFactory appTableCommandFactory;

    @Autowired
    @Qualifier("oracleCommandFactory")
    private AbstractCommandFactory oracleCommandFactory;
    
    @Autowired
    @Qualifier("scimCommandFactory")
    private AbstractCommandFactory scimCommandFactory;

    @Autowired
    @Qualifier("salesForceCommandFactory")
    private AbstractCommandFactory salesForceCommandFactory;

    @Autowired
    @Qualifier("scriptCommandFactory")
    private AbstractCommandFactory scriptCommandFactory;

    @Autowired
    @Qualifier("shellCommandFactory")
    private AbstractCommandFactory shellCommandFactory;


    public ConnectorCommand getConnectorCommand(CommandType commandType, ExtensibleObjectType extensibleObjectType, ConnectorType connectorType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, extensibleObjectType, connectorType);
        switch (connectorType){
            case CSV:
                return csvCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case LDAP:
                return ldapCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case GOOGLE:
                return googleAppsCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case LINUX:
                return linuxCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case MYSQL:
                return mySQLCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case AT:
                return appTableCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case ORACLE:
                return oracleCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case SALES_FORCE:
                return salesForceCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case SCRIPT:
                return scriptCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case SHELL:
                return shellCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            case SCIM:
                return scimCommandFactory.getConnectorCommand(commandType, extensibleObjectType);
            default:
                throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
