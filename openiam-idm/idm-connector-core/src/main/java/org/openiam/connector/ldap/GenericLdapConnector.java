package org.openiam.connector.ldap;

import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.connector.AbstractConnectorService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericLdapConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "LDAPConnectorServicePort", serviceName = "LDAPConnectorService")
public class GenericLdapConnector extends AbstractConnectorService {

    @Autowired
    protected ManagedSystemService managedSystemService;
    @Autowired
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;

    protected void initConnectorType(){
        this.connectorType= ConnectorType.LDAP;
    }
}
