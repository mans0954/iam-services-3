package org.openiam.srvc.idm.connector;

import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericScimConnector")
@WebService(endpointInterface= "org.openiam.srvc.idm.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "ScimConnectorServicePort",
        serviceName="ScimConnectorService")
public class GenericScimConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SCIM;
    }
}
