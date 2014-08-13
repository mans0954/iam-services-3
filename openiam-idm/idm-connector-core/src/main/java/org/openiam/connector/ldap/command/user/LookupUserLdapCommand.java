package org.openiam.connector.ldap.command.user;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.ldap.command.base.AbstractLookupLdapCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;

@Service("lookupUserLdapCommand")
public class LookupUserLdapCommand extends AbstractLookupLdapCommand<ExtensibleUser>  {
    @Override
    protected boolean lookup(ManagedSysEntity managedSys, LookupRequest<ExtensibleUser> lookupRequest, SearchResponse respType, LdapContext ldapctx) throws ConnectorDataException {

        boolean found = false;

        try {

            ExtensibleObject object = lookupRequest.getExtensibleObject();
            List<String> attrList = getAttributeNameList(object);
            if (CollectionUtils.isEmpty(attrList)) {
                attrList = getAttributeNameList(managedSys.getResourceId());
            }

            if (CollectionUtils.isNotEmpty(attrList)) {

                String[] attrAry = new String[attrList.size()];
                attrList.toArray(attrAry);
                log.debug("Attribute array=" + attrAry);

                String identityDN = getIdentityDN(lookupRequest, managedSys, ldapctx);
                Attributes attrs = lookupName(ldapctx, identityDN, attrAry);

                if (attrs != null) {
                    ObjectValue objectValue = attributesToObjectValue(attrs, null);

                    objectValue.setObjectIdentity(lookupRequest.getObjectIdentity());
                    ExtensibleAttribute dnAttr = new ExtensibleAttribute(DN_ATTRIBUTE_NAME, identityDN);
                    objectValue.getAttributeList().add(dnAttr);

                    respType.getObjectList().add(objectValue);
                    found = true;
                }
            }
        } catch (NamingException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
        return found;
    }

    private List<String> getAttributeNameList(String resourceId) {
        log.debug(" get attributes for Resource id = " + resourceId);
        List<AttributeMapEntity> attrMap = managedSysService.getResourceAttributeMaps(resourceId);
        if (CollectionUtils.isEmpty(attrMap)) {
            return null;
        }
        List<String> strList = new ArrayList<>();
        for (AttributeMapEntity a : attrMap) {
            strList.add(a.getAttributeName());
        }
        return strList;
    }

    private List<String> getAttributeNameList(ExtensibleObject extObject) {
        if (extObject == null || CollectionUtils.isEmpty(extObject.getAttributes())) {
            return null;
        }
        List<String> strList = new ArrayList<>();
        for (ExtensibleAttribute a : extObject.getAttributes()) {
            strList.add(a.getName());
        }
        return strList;
    }
}
