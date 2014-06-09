package org.openiam.connector.ldap.command.base;

import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;
import javax.naming.ldap.PagedResultsResponseControl;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractSearchLdapCommand<ExtObject extends ExtensibleObject> extends AbstractLdapCommand<SearchRequest<ExtObject>, SearchResponse> {

    @Override
    public SearchResponse execute(SearchRequest<ExtObject> searchRequest) throws ConnectorDataException {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(searchRequest.getTargetID(), ConnectorConfiguration.class);
        ManagedSysEntity managedSys = config.getManagedSys();
        LdapContext ldapContext = this.connect(managedSys);

        ManagedSystemObjectMatch matchObj = getMatchObject(searchRequest.getTargetID(), getObjectType());
        try {

            log.debug("Search Filter=" + searchRequest.getSearchQuery());
            log.debug("Searching BaseDN=" + searchRequest.getBaseDN());

            SearchControls searchControls = new SearchControls();
            ldapContext.setRequestControls(new Control[] { new PagedResultsControl(PAGE_SIZE, Control.CRITICAL) });
            searchControls.setSearchScope(managedSys.getSearchScope().getValue());
            String identityAttrName = matchObj != null ? matchObj.getKeyField() : "cn";

            List<ObjectValue> objectValueList = new LinkedList<ObjectValue>();
            ObjectValue objectValue = new ObjectValue();
            objectValue.setAttributeList(new LinkedList<ExtensibleAttribute>());

            byte[] cookie = null;
            boolean found = false;
            do {
                NamingEnumeration results = ldapContext.search(searchRequest.getBaseDN(), searchRequest.getSearchQuery(), searchControls);

                while (results != null && results.hasMoreElements()) {
                    SearchResult sr = (SearchResult) results.next();
                    Attributes attrs = sr.getAttributes();
                    if (attrs != null) {
                        found = true;

                        try {
                            ExtensibleAttribute extAttr = new ExtensibleAttribute();
                            extAttr.setName("dn");
                            String dnValue = sr.getNameInNamespace();
                            extAttr.setValue(dnValue);
                            objectValue.getAttributeList().add(extAttr);
                        } catch (UnsupportedOperationException e) {
                            log.error(e.getMessage(), e);
                        }

                        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore();) {
                            ExtensibleAttribute extAttr = new ExtensibleAttribute();
                            Attribute attr = (Attribute) ae.next();

                            boolean addToList = false;

                            extAttr.setName(attr.getID());

                            NamingEnumeration e = attr.getAll();
                            boolean isMultivalued = (attr.size() > 1);
                            while (e.hasMore()) {
                                Object o = e.next();
                                if (o instanceof String) {
                                    if (isMultivalued) {
                                        BaseAttributeContainer container = extAttr.getAttributeContainer();
                                        if (container == null) {
                                            container = new BaseAttributeContainer();
                                            extAttr.setAttributeContainer(container);
                                        }
                                        container.getAttributeList().add(0,
                                                new BaseAttribute(attr.getID(), o.toString(), AttributeOperationEnum.NO_CHANGE));
                                    } else {
                                        extAttr.setValue(o.toString());
                                    }
                                    addToList = true;
                                }
                            }
                            if (addToList) {
                                objectValue.getAttributeList().add(extAttr);
                            }
                        }

                        objectValueList.add(objectValue);
                    }
                }
                Control[] controls = ldapContext.getResponseControls();
                if (controls != null) {
                    for (Control c : controls) {
                        if (c instanceof PagedResultsResponseControl) {
                            PagedResultsResponseControl prrc = (PagedResultsResponseControl)c;
                            cookie = prrc.getCookie();
                            break;
                        }
                    }
                }
                ldapContext.setRequestControls(new Control[]{ new PagedResultsControl(PAGE_SIZE, cookie, Control.CRITICAL) });
            } while (cookie != null);

            searchResponse.setObjectList(objectValueList);

            if (!found) {
                throw  new ConnectorDataException(ErrorCode.NO_RESULTS_RETURNED);
            }
            return searchResponse;

        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new  ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        } finally {
            /* close the connection to the directory */
            this.closeContext(ldapContext);
        }
    }

    protected abstract String getObjectType();
}
