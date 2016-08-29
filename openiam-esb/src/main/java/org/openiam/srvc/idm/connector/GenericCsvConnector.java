package org.openiam.srvc.idm.connector;

import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericCsvConnector")
@WebService(endpointInterface = "org.openiam.srvc.idm.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "CSVConnectorServicePort", serviceName = "CSVConnectorService")
public class GenericCsvConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.CSV;
    }

}
