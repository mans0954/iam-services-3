package org.openiam.srvc.idm.connector;

import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericSoapConnector")
@WebService(endpointInterface= "org.openiam.srvc.idm.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "SoapConnectorServicePort",
        serviceName="SoapConnectorService")
public class GenericSoapConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SOAP;
    }
}
