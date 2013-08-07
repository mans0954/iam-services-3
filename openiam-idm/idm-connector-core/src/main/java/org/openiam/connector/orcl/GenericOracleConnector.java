package org.openiam.connector.orcl;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericOracleConnector")
@WebService(endpointInterface="org.openiam.connector.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "OracleConnectorServicePort",
        serviceName="OracleConnectorService")
public class GenericOracleConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.ORACLE;
    }
}
