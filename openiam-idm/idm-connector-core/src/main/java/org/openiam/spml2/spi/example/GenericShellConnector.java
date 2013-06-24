package org.openiam.spml2.spi.example;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;


@Service("genericShellConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
        portName = "ShellConnectorServicePort", serviceName = "ShellConnectorService")
public class GenericShellConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SHELL;
    }
}
