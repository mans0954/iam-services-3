package org.openiam.connector.ldap.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.ldap.command.base.AbstractLookupLdapCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.List;

@Service("lookupUserLdapCommand")
public class LookupUserLdapCommand extends AbstractLookupLdapCommand<ExtensibleUser>  {
    @Override
    protected boolean lookup(ManagedSysEntity managedSys, LookupRequest<ExtensibleObject> lookupRequest, SearchResponse respType, LdapContext ldapctx) throws ConnectorDataException {
        boolean found=false;
        ManagedSystemObjectMatch matchObj = getMatchObject(lookupRequest.getTargetID(), "USER");
        String identity = lookupRequest.getSearchValue();
        String rdn = null;
        String objectBaseDN = null;
        try {
            int indx = identity.indexOf(",");
            if (indx > 0) {
                rdn = identity.substring(0, identity.indexOf(","));
                objectBaseDN = identity.substring(indx+1);
            } else {
                rdn = identity;
            }
            log.debug("looking up identity: " + identity);


            String resourceId = managedSys.getResourceId();

            log.debug("Resource id = " + resourceId);
            List<AttributeMapEntity> attrMap = managedSysService.getResourceAttributeMaps(resourceId);

            if (attrMap != null) {
                List<String> attrList = getAttributeNameList(attrMap);
                String[] attrAry = new String[attrList.size()];
                attrList.toArray(attrAry);
                log.debug("Attribute array=" + attrAry);

                NamingEnumeration results = null;

                results = lookupSearch(matchObj, ldapctx, rdn, attrAry, objectBaseDN);


                log.debug("results=" + results);
                log.debug(" results has more elements=" + results.hasMoreElements());

                ObjectValue userValue = new ObjectValue();
                userValue.setObjectIdentity(identity);

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
                            if (addToList) {
                                userValue.getAttributeList().add(extAttr);
                            }
                        }
                        respType.getObjectList().add(userValue);
                    }
                }
            }
        } catch (NamingException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
        return found;
    }
}
