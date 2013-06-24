package org.openiam.spml2.spi.orcl;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericOracleConnector")
@WebService(endpointInterface="org.openiam.spml2.interf.ConnectorService",
        targetNamespace="http://www.openiam.org/service/connector",
        portName = "OracleConnectorServicePort",
        serviceName="OracleConnectorService")
public class GenericOracleConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.ORACLE;
    }
}
