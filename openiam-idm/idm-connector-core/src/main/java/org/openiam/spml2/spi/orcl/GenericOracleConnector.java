package org.openiam.spml2.spi.orcl;

import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.connector.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;

@Service("genericOracleConnector")
@WebService(endpointInterface="org.openiam.spml2.interf.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "OracleConnectorServicePort",
        serviceName="OracleConnectorService")
public class GenericOracleConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.ORACLE;
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
