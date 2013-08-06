package org.openiam.connector.ldap;

import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.connector.AbstractConnectorService;
import org.openiam.idm.srvc.recon.dto.ReconciliationConfig;
import org.openiam.spml2.constants.ConnectorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.LinkedList;
import java.util.List;

@Service("genericLdapConnector")
@WebService(endpointInterface = "org.openiam.connector.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "LDAPConnectorServicePort", serviceName = "LDAPConnectorService")
public class GenericLdapConnector extends AbstractConnectorService {

    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;

    protected void initConnectorType(){
        this.connectorType= ConnectorType.LDAP;
    }
}
