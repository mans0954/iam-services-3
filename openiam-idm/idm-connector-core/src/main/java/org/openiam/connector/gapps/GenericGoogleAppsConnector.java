package org.openiam.connector.gapps;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericGoogleAppsConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "GoogleAppsConnectorServicePort", serviceName = "GoogleAppsConnectorService")
public class GenericGoogleAppsConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.GOOGLE;
    }
}
