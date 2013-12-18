package org.openiam.connector.soap;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericSoapConnector")
@WebService(endpointInterface="org.openiam.connector.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "SoapConnectorServicePort",
        serviceName="SoapConnectorService")
public class GenericSoapConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SOAP;
    }
}
