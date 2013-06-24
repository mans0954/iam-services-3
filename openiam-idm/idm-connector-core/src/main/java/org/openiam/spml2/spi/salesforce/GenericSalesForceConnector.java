package org.openiam.spml2.spi.salesforce;


import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericSalesForceConnector")
@WebService(endpointInterface="org.openiam.spml2.interf.ConnectorService",
	targetNamespace="http://www.openiam.org/service/connector",
	portName = "SalesForceServicePort", serviceName="SalesForceConnectorService")
public class GenericSalesForceConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SALES_FORCE;
    }
}
