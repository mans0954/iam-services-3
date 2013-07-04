package org.openiam.spml2.spi.ldap.command.user;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.AttributeMap;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.ldap.command.base.AbstractLookupLdapCommand;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.util.List;

@Service("lookupUserLdapCommand")
public class LookupUserLdapCommand extends AbstractLookupLdapCommand<ProvisionUser>  {
    @Override
    protected boolean lookup(PSOIdentifierType psoId, ManagedSysEntity managedSys, LookupResponseType respType, LdapContext ldapctx) throws ConnectorDataException {
        boolean found=false;
        ManagedSystemObjectMatch matchObj = null;
        String rdn = null;
        String objectBaseDN = null;
        String identity = psoId.getID();
        try {
            int indx = identity.indexOf(",");
            if (indx > 0) {
                rdn = identity.substring(0, identity.indexOf(","));
                objectBaseDN = identity.substring(indx+1);
            } else {
                rdn = identity;
            }
            log.debug("looking up identity: " + identity);

            List<ManagedSystemObjectMatchEntity> matchObjList =  managedSysService.managedSysObjectParam(psoId.getTargetID(), "USER");
            if (matchObjList != null && matchObjList.size() > 0) {
                matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
            }

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

                ExtensibleObject extObj = new ExtensibleObject();
                extObj.setObjectId(identity);

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
                                extObj.getAttributes().add(extAttr);
                            }
                        }
                        respType.addObject(extObj);
                        extObj = new ExtensibleObject();
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
