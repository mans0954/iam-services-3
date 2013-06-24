package org.openiam.spml2.spi.gapps;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericGoogleAppsConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "GoogleAppsConnectorServicePort", serviceName = "GoogleAppsConnectorService")
public class GenericGoogleAppsConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.GOOGLE;
    }
}
