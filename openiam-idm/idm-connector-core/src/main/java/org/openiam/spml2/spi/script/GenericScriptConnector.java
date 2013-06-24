package org.openiam.spml2.spi.script;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericScriptConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService",
        targetNamespace = "http://www.openiam.org/service/connector",
        portName = "ScriptConnectorServicePort",
        serviceName = "ScriptConnectorService")
public class GenericScriptConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.SCRIPT;
    }
}
