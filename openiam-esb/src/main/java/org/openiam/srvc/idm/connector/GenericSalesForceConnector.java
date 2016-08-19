package org.openiam.srvc.idm.connector;


import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericSalesForceConnector")
@WebService(endpointInterface= "org.openiam.srvc.idm.ConnectorService",
	targetNamespace="http://www.openiam.org/service/connector",
	portName = "SalesForceServicePort", serviceName="SalesForceConnectorService")
public class GenericSalesForceConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SALES_FORCE;
    }
}
