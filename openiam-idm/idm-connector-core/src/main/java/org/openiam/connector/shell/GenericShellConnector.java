package org.openiam.connector.shell;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;


@Service("genericShellConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
        portName = "ShellConnectorServicePort", serviceName = "ShellConnectorService")
public class GenericShellConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SHELL;
    }
}
