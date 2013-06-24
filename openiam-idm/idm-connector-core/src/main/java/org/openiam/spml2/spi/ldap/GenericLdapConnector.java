package org.openiam.spml2.spi.ldap;

import org.openiam.spml2.base.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericLdapConnector")
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "LDAPConnectorServicePort", serviceName = "LDAPConnectorService")
public class GenericLdapConnector extends AbstractConnectorService {
    protected void initConnectorType(){
        this.connectorType= ConnectorType.LDAP;
    }
}
