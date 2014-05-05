package org.openiam.connector.peoplesoft;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericPeopleSoftConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector", portName = "PeoplesoftDbConnectorPort", serviceName = "PeoplesoftDbConnector")
public class GenericPeopleSoftConnector extends AbstractConnectorService {
    protected void initConnectorType() {
        this.connectorType = ConnectorType.PEOPLESOFT;
    }
}
