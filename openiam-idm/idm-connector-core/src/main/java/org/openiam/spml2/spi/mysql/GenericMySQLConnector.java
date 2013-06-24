package org.openiam.spml2.spi.mysql;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericMySQLConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "MySQLConnectorPort",
        serviceName = "MySQLConnector")
public class GenericMySQLConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.MYSQL;
    }
}
