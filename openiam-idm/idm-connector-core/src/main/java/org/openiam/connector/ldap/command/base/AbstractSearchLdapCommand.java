package org.openiam.connector.ldap.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/6/13
 * Time: 9:32 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractSearchLdapCommand<ExtObject extends ExtensibleObject> extends AbstractLdapCommand<SearchRequest<ExtObject>, SearchResponse> {

    @Override
    public SearchResponse execute(SearchRequest<ExtObject> searchRequest) throws ConnectorDataException {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(searchRequest.getTargetID(), ConnectorConfiguration.class);
        LdapContext ldapContext = this.connect(config.getManagedSys());


        ManagedSystemObjectMatch matchObj = getMatchObject(searchRequest.getTargetID(), getObjectType());
        try {
            log.debug("Search Filter=" + searchRequest.getSearchQuery());
            log.debug("Searching BaseDN=" + searchRequest.getBaseDN());

            SearchControls searchControls = new SearchControls();
            NamingEnumeration results = ldapContext.search(searchRequest.getBaseDN(), searchRequest.getSearchQuery(), searchControls);

            String identityAttrName = matchObj != null ? matchObj.getKeyField() : "cn";

            List<ObjectValue> objectValueList = new LinkedList<ObjectValue>();

            ObjectValue objectValue = new ObjectValue();
            objectValue.setAttributeList(new LinkedList<ExtensibleAttribute>());
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
                            objectValue.setObjectIdentity(extAttr.getValue());
                        }
                        if (addToList) {
                            objectValue.getAttributeList().add(extAttr);
                        }
                    }
                    objectValueList.add(objectValue);
                    objectValue = new ObjectValue();
                    objectValue.setAttributeList(new LinkedList<ExtensibleAttribute>());
                }
            }
            searchResponse.setObjectList(objectValueList);
            if (!found) {
                throw  new ConnectorDataException(ErrorCode.NO_RESULTS_RETURNED);
            }
            return searchResponse;

        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapContext);
        }
    }

    protected abstract String getObjectType();
}
