package org.openiam.srvc.idm.connector;

import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericRestConnector")
@WebService(endpointInterface = "org.openiam.srvc.idm.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector", portName = "RestConnectorServicePort", serviceName = "RestConnectorService")
public class GenericRestConnector extends AbstractConnectorService {
	protected void initConnectorType() {
		this.connectorType = ConnectorType.REST;
	}
}
