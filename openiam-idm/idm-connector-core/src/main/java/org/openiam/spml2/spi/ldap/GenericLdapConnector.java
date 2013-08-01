package org.openiam.spml2.spi.ldap;

import org.apache.cxf.common.util.StringUtils;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemObjectMatchDAO;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.connector.AbstractConnectorService;
import org.openiam.spml2.constants.ConnectorType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;
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
@WebService(endpointInterface = "org.openiam.spml2.interf.ConnectorService", targetNamespace = "http://www.openiam.org/service/connector",
            portName = "LDAPConnectorServicePort", serviceName = "LDAPConnectorService")
public class GenericLdapConnector extends AbstractConnectorService {

    @Autowired
    protected ManagedSystemService managedSysService;
    @Autowired
    protected ManagedSystemObjectMatchDAO managedSysObjectMatchDao;

    protected void initConnectorType(){
        this.connectorType= ConnectorType.LDAP;
    }

    @Override
    public SearchResponse search(@WebParam(name = "searchRequest", targetNamespace = "") SearchRequest searchRequest) {
        System.out.println("LDAP SEARCH EXECUTION ==============================================================");
        SearchResponse searchResponse = new SearchResponse();
        ConnectionMgr conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
        conMgr.setApplicationContext(applicationContext);
        if(StringUtils.isEmpty(searchRequest.getTargetID())) {
            log.error("Search Target Managed System isn't set.");
            searchResponse.setStatus(StatusCodeType.FAILURE);
            return searchResponse;
        }

        ManagedSysEntity mSys = managedSysService.getManagedSysById(searchRequest.getTargetID());

        ManagedSystemObjectMatchEntity matchObj = null;
        List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao.findBySystemId(mSys.getManagedSysId(), "USER");
        if (matchObjList != null && matchObjList.size() > 0) {
            matchObj = matchObjList.get(0);
        }
        try {
            LdapContext ldapContext = conMgr.connect(mSys);

            log.debug("Search Filter=" + searchRequest.getSearchQuery());
            log.debug("Searching BaseDN=" + searchRequest.getBaseDN());

            SearchControls searchControls = new SearchControls();
            NamingEnumeration results = ldapContext.search(searchRequest.getBaseDN(), searchRequest.getSearchQuery(), searchControls);

            String identityAttrName = matchObj != null ? matchObj.getKeyField() : "cn";

            List<ObjectValue> userValues = new LinkedList<ObjectValue>();

            ObjectValue user = new ObjectValue();
            user.setAttributeList(new LinkedList<ExtensibleAttribute>());
            boolean found = false;
            while (results != null && results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                Attributes attrs = sr.getAttributes();
                if (attrs != null) {
                    found = true;
                    for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
                        ExtensibleAttribute extAttr = new ExtensibleAttribute();
                        Attribute attr = (Attribute) ae.next();

                        boolean addToList = false;

                        extAttr.setName(attr.getID());

                        NamingEnumeration e = attr.getAll();

                        while (e.hasMore()) {
                            Object o = e.next();
                            if (o instanceof String) {
                                extAttr.setValue(o.toString());
                                addToList = true;
                            }
                        }
                        if(identityAttrName.equalsIgnoreCase(extAttr.getName())) {
                            user.setUserIdentity(extAttr.getValue());
                        }
                        if (addToList) {
                            user.getAttributeList().add(extAttr);
                        }
                    }
                    userValues.add(user);
                    user = new ObjectValue();
                    user.setAttributeList(new LinkedList<ExtensibleAttribute>());
                }
            }
            searchResponse.setUserList(userValues);
            if (!found) {
                searchResponse.setStatus(StatusCodeType.FAILURE);
            } else {
                searchResponse.setStatus(StatusCodeType.SUCCESS);
            }
        } catch (NamingException e) {
            searchResponse.setStatus(StatusCodeType.FAILURE);
            e.printStackTrace();
        }

        return searchResponse;
    }
}
