package org.openiam.srvc.idm.connector;

import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericOracleConnector")
@WebService(endpointInterface= "org.openiam.srvc.idm.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "OracleConnectorServicePort",
        serviceName="OracleConnectorService")
public class GenericOracleConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.ORACLE;
    }
}
