package org.openiam.connector.scim;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericScimConnector")
@WebService(endpointInterface="org.openiam.connector.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "ScimConnectorServicePort",
        serviceName="ScimConnectorService")
public class GenericScimConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SCIM;
    }
}
