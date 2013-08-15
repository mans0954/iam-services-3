package org.openiam.connector.script;

import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericScriptConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "ScriptConnectorServicePort",
        serviceName = "ScriptConnectorService")
public class GenericScriptConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SCRIPT;
    }
}
