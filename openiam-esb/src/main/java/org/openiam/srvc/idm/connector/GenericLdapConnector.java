package org.openiam.srvc.idm.connector;

import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.connector.common.constants.ConnectorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("genericLdapConnector")
@WebService(endpointInterface = "org.openiam.srvc.idm.ConnectorService",
            targetNamespace = "http://www.openiam.org/service/connector",
            portName = "LDAPConnectorServicePort",
            serviceName = "LDAPConnectorService")
public class GenericLdapConnector extends AbstractConnectorService {

    @Autowired
    protected ManagedSystemService managedSystemService;
    @Autowired
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;

    protected void initConnectorType(){
        this.connectorType= ConnectorType.LDAP;
    }
}
