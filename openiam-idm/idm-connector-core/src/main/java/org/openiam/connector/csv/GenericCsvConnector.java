package org.openiam.connector.csv;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericCsvConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "CSVConnectorServicePort", serviceName = "CSVConnectorService")
public class GenericCsvConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.CSV;
    }

}
