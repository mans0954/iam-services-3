package org.openiam.spml2;

import org.openiam.provision.dto.ProvisionObjectType;
import org.openiam.spml2.constants.CommandType;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.spi.common.ConnectorCommand;
import org.openiam.spml2.spi.common.factory.AbstractCommandFactory;
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



    public ConnectorCommand getConnectorCommand(CommandType commandType, ProvisionObjectType provisionObjectType, ConnectorType connectorType) throws ConnectorDataException {
        String error = String.format(ERROR_PATTERN, commandType, provisionObjectType, connectorType);
        switch (connectorType){
            case CSV:
                return csvCommandFactory.getConnectorCommand(commandType, provisionObjectType);
            case LDAP:
                return ldapCommandFactory.getConnectorCommand(commandType, provisionObjectType);
            case GOOGLE:
                return googleAppsCommandFactory.getConnectorCommand(commandType, provisionObjectType);
            case LINUX:
                return linuxCommandFactory.getConnectorCommand(commandType, provisionObjectType);
            case MYSQL:
                return mySQLCommandFactory.getConnectorCommand(commandType, provisionObjectType);
            case AT:
                return appTableCommandFactory.getConnectorCommand(commandType, provisionObjectType);
            case ORACLE:
                return oracleCommandFactory.getConnectorCommand(commandType, provisionObjectType);
            default:
                throw new ConnectorDataException(ErrorCode.OPERATION_NOT_SUPPORTED_EXCEPTION, error);
        }
    }
}
