package org.openiam.srvc.idm.connector;

import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;


@Service("genericShellConnector")
@WebService(endpointInterface = "org.openiam.srvc.idm.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
        portName = "ShellConnectorServicePort", serviceName = "ShellConnectorService")
public class GenericShellConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SHELL;
    }
}
