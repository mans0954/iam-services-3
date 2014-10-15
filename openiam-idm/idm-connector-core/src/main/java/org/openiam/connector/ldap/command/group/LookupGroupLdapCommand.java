package org.openiam.connector.ldap.command.group;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.connector.ldap.command.base.AbstractLookupLdapCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleGroup;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("lookupGroupLdapCommand")
public class LookupGroupLdapCommand extends AbstractLookupLdapCommand<ExtensibleGroup> {
    @Override
    protected boolean lookup(ManagedSysEntity managedSys, LookupRequest<ExtensibleGroup> lookupRequest, SearchResponse respType, LdapContext ldapctx) throws ConnectorDataException {
        boolean found = false;
        ManagedSystemObjectMatch matchObj = getMatchObject(lookupRequest.getTargetID(), ManagedSystemObjectMatch.GROUP);
        String resourceId = managedSys.getResource() == null ? null : managedSys.getResource().getId();

        String identity = lookupRequest.getSearchValue();
        try {
            //Check identity on DN format or not
            String identityPatternStr = MessageFormat.format(DN_IDENTITY_MATCH_REGEXP, matchObj.getKeyField());
            Pattern pattern = Pattern.compile(identityPatternStr);
            Matcher matcher = pattern.matcher(identity);
            String objectBaseDN;
            if (matcher.matches()) {
                identity = matcher.group(1);
                String CN = matchObj.getKeyField() + "=" + identity;
                objectBaseDN = lookupRequest.getSearchValue().substring(CN.length() + 1);
            } else {
                // if identity is not in DN format try to find OU info in attributes
                String OU = getOU(lookupRequest.getExtensibleObject());
                if (StringUtils.isNotEmpty(OU)) {
                    objectBaseDN = OU + "," + matchObj.getBaseDn();
                } else {
                    objectBaseDN = matchObj.getBaseDn();
                }
            }

            log.debug("looking up identity: " + identity);

            List<String> attrList = new ArrayList<String>();
            ExtensibleObject object = lookupRequest.getExtensibleObject();
            List<ExtensibleAttribute> listAttrs = (object != null) ? object.getAttributes() : new ArrayList<ExtensibleAttribute>();
            if (CollectionUtils.isNotEmpty(listAttrs)) {
                for (ExtensibleAttribute ea : listAttrs) {
                    attrList.add(ea.getName());
                }
            } else {
                log.debug("Resource id = " + resourceId);
                List<AttributeMapEntity> attrMap = managedSysService.getResourceAttributeMaps(resourceId);
                if (attrMap != null) {
                    attrList = getAttributeNameList(attrMap);
                }
            }

            if (CollectionUtils.isNotEmpty(attrList)) {

                String[] attrAry = new String[attrList.size()];
                attrList.toArray(attrAry);
                log.debug("Attribute array=" + attrAry);

                NamingEnumeration results = null;
                try {
                    results = lookupSearch(managedSys, matchObj, ldapctx, identity, attrAry, objectBaseDN);
                } catch (NameNotFoundException nnfe) {
                    log.debug("results=NULL");
                    log.debug(" results has more elements=0");
                    return false;
                }

                log.debug("results=" + results);
                log.debug(" results has more elements=" + results.hasMoreElements());

                while (results != null && results.hasMoreElements()) {
                    SearchResult sr = (SearchResult) results.next();
                    Attributes attrs = sr.getAttributes();
                    if (attrs != null) {

                        ObjectValue objectValue = new ObjectValue();
                        objectValue.setObjectIdentity(identity);

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

                        for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
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
                                        container.getAttributeList().add(
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
                        respType.getObjectList().add(objectValue);
                    }
                }
            }
        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
        return found;
    }
}
