package org.openiam.connector.orcl;

import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.connector.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
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
