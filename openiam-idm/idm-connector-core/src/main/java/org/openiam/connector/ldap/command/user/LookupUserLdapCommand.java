package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.ldap.command.base.AbstractLookupLdapCommand;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("lookupUserLdapCommand")
public class LookupUserLdapCommand extends AbstractLookupLdapCommand<ExtensibleUser>  {
    @Override
    protected boolean lookup(ManagedSysEntity managedSys, LookupRequest<ExtensibleUser> lookupRequest, SearchResponse respType, LdapContext ldapctx) throws ConnectorDataException {
        boolean found=false;
        ManagedSystemObjectMatch matchObj = getMatchObject(lookupRequest.getTargetID(), ManagedSystemObjectMatch.USER);
        String resourceId = managedSys.getResourceId();

        log.debug("Resource id = " + resourceId);
        List<AttributeMapEntity> attrMap = managedSysService.getResourceAttributeMaps(resourceId);

        String identity = lookupRequest.getSearchValue();
        try {
            //Check identity on DN format or not
            String identityPatternStr =  MessageFormat.format(DN_IDENTITY_MATCH_REGEXP, matchObj.getKeyField());
            Pattern pattern = Pattern.compile(identityPatternStr);
            Matcher matcher = pattern.matcher(identity);
            String objectBaseDN;
            if(matcher.matches()) {
                identity = matcher.group(1);
                String CN = matchObj.getKeyField()+"="+identity;
                objectBaseDN =  lookupRequest.getSearchValue().substring(CN.length()+1);
            } else {
                // if identity is not in DN format try to find OU info in attributes
                String OU = getOU(lookupRequest.getExtensibleObject());
                if(StringUtils.isNotEmpty(OU)) {
                   objectBaseDN = OU+","+matchObj.getBaseDn();
                } else {
                    objectBaseDN = matchObj.getBaseDn();
                }
            }

            log.debug("looking up identity: " + identity);

            if (attrMap != null) {
                List<String> attrList = getAttributeNameList(attrMap);
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
