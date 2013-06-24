package org.openiam.spml2.spi.linux;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericLinuxConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "LinuxConnectorServicePort",
        serviceName = "LinuxConnectorService")
public class GenericLinuxConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.LINUX;
    }
}
