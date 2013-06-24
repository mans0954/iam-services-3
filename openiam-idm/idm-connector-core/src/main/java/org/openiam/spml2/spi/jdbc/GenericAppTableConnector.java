package org.openiam.spml2.spi.jdbc;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericAppTableConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "ApplicationTablesConnectorPort",
        serviceName = "ApplicationTablesConnector")
public class GenericAppTableConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.AT;
    }
}
