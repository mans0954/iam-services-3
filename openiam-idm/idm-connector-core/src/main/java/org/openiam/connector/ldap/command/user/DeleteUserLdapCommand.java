package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.ldap.command.base.AbstractCrudLdapCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

import javax.naming.NamingException;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("deleteUserLdapCommand")
public class DeleteUserLdapCommand extends AbstractCrudLdapCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleUser> deleteRequestType,  LdapContext ldapctx) throws ConnectorDataException {
        String delete = "DELETE";
        ManagedSystemObjectMatch matchObj = getMatchObject(deleteRequestType.getTargetID(), "USER");
        try {
            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
            ResourceProp rpOnDelete = getResourceAttr(rpSet,"ON_DELETE");

            if (rpOnDelete == null || rpOnDelete.getPropValue() == null || "DELETE".equalsIgnoreCase(rpOnDelete.getPropValue())) {
                delete = "DELETE";
            } else if (rpOnDelete.getPropValue() != null) {
                if ("DISABLE".equalsIgnoreCase(rpOnDelete.getPropValue())) {
                    delete = "DISABLE";
                }
            }

            // BY DEFAULT - we want to enable group membership
            boolean groupMembershipEnabled = isMembershipEnabled(rpSet, "GROUP_MEMBERSHIP_ENABLED");
            boolean supervisorMembershipEnabled = isMembershipEnabled(rpSet, "SUPERVISOR_MEMBERSHIP_ENABLED");

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
                String OU = getOU(deleteRequestType.getExtensibleObject());
                if(StringUtils.isNotEmpty(OU)) {
                    objectBaseDN = OU+","+matchObj.getBaseDn();
                } else {
                    objectBaseDN = matchObj.getBaseDn();
                }
            }
            //Important!!! For delete operation need to create identity in DN format
            String identityDN = matchObj.getKeyField() + "=" + identity+","+objectBaseDN;
            log.debug("Deleting.. users in ldap.." + identityDN);
            if (groupMembershipEnabled) {
                dirSpecificImp.removeAccountMemberships(identityDN, matchObj, ldapctx);
            }
            if (supervisorMembershipEnabled) {
                dirSpecificImp.removeSupervisorMemberships(identityDN, matchObj, ldapctx);
            }
            dirSpecificImp.delete(deleteRequestType, ldapctx, identityDN, delete);
        } catch (NamingException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }
}
