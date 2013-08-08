package org.openiam.connector.salesforce;


import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;

@Service("genericSalesForceConnector")
@WebService(endpointInterface="org.openiam.connector.ConnectorService",
	targetNamespace="http://www.openiam.org/service/connector",
	portName = "SalesForceServicePort", serviceName="SalesForceConnectorService")
public class GenericSalesForceConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SALES_FORCE;
    }
}
