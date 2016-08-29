package org.openiam.srvc.idm.connector;

import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericPeopleSoftConnector")
@WebService(endpointInterface = "org.openiam.srvc.idm.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector", portName = "PeoplesoftDbConnectorPort", serviceName = "PeoplesoftDbConnector")
public class GenericPeopleSoftConnector extends AbstractConnectorService {
    protected void initConnectorType() {
        this.connectorType = ConnectorType.PEOPLESOFT;
    }
}
