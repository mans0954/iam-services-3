package org.openiam.connector.ldap.command.group;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.ldap.command.base.AbstractCrudLdapCommand;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleGroup;
import org.springframework.stereotype.Service;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("deleteGroupLdapCommand")
public class DeleteGroupLdapCommand extends AbstractCrudLdapCommand<ExtensibleGroup> {
    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleGroup> deleteRequestType,  LdapContext ldapctx) throws ConnectorDataException {
        String delete = "DELETE";
        ManagedSystemObjectMatch matchObj = getMatchObject(deleteRequestType.getTargetID(), ManagedSystemObjectMatch.GROUP);
        try {
            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResource().getId());

            // BY DEFAULT - we want to enable group membership
            boolean groupMembershipEnabled = getResourceBoolean(rpSet, "GROUP_MEMBERSHIP_ENABLED", true);
            boolean supervisorMembershipEnabled = getResourceBoolean(rpSet, "SUPERVISOR_MEMBERSHIP_ENABLED", true);

            Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
            String identity = deleteRequestType.getObjectIdentity();
            //Check identity on CN format or not
            String identityPatternStr =  MessageFormat.format(DN_IDENTITY_MATCH_REGEXP, matchObj.getKeyField());
            Pattern pattern = Pattern.compile(identityPatternStr);
            Matcher matcher = pattern.matcher(identity);
            String objectBaseDN;
            if(matcher.matches()) {
                identity = matcher.group(1);
                String CN = matchObj.getKeyField()+"="+identity;
                objectBaseDN =  deleteRequestType.getObjectIdentity().substring(CN.length()+1);
            } else {
                // if identity is not in DN format try to find OU info in attributes
                String OU = getAttrValue(deleteRequestType.getExtensibleObject(), OU_ATTRIBUTE);
                if(StringUtils.isNotEmpty(OU)) {
                    objectBaseDN = OU+","+matchObj.getBaseDn();
                } else {
                    objectBaseDN = matchObj.getBaseDn();
                }
            }
            //Important!!! For delete operation need to create identity in DN format
//            String identityDN = matchObj.getKeyField() + "=" + identity+","+objectBaseDN;

            NamingEnumeration results = null;
            try {
            	if(log.isDebugEnabled()) {
            		log.debug("Looking for user with identity=" +  identity + " in " +  objectBaseDN);
            	}
                results = lookupSearch(managedSys, matchObj, ldapctx, identity, null, objectBaseDN);

            } catch (NameNotFoundException nnfe) {
            	if(log.isDebugEnabled()) {
            		log.debug("results=NULL");
            		log.debug(" results has more elements=0");
            	}
                return;
            }

            String identityDN = null;
            int count = 0;
            while (results != null && results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                identityDN = sr.getNameInNamespace();
                count++;
            }

            if (count == 0) {
                String err = String.format("User %s was not found in %s", identity, objectBaseDN);
                log.error(err);
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER, err);
            } else if (count > 1) {
                String err = String.format("More then one user %s was found in %s", identity, objectBaseDN);
                log.error(err);
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER, err);
            }

            if (StringUtils.isNotEmpty(identityDN)) {
            	if(log.isDebugEnabled()) {
            		log.debug("Deleting.. users in ldap.." + identityDN);
            	}
                if (groupMembershipEnabled) {
                    dirSpecificImp.removeAccountMemberships(managedSys, identity, identityDN, matchObj, ldapctx);
                }
                if (supervisorMembershipEnabled) {
                    dirSpecificImp.removeSupervisorMemberships(managedSys, identity, identityDN, matchObj, ldapctx);
                }
                dirSpecificImp.delete(deleteRequestType, ldapctx, identityDN, delete);
            }

        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }
}
