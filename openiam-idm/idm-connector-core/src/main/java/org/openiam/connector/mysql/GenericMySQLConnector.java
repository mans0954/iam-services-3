package org.openiam.connector.mysql;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericMySQLConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "MySQLConnectorPort",
        serviceName = "MySQLConnector")
public class GenericMySQLConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.MYSQL;
    }
}
