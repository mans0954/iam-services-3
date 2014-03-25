package org.openiam.connector.ldap.command.user;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.ldap.command.base.AbstractCrudLdapCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.type.ExtensibleUser;
import org.openiam.connector.ldap.dirtype.Directory;
import org.openiam.connector.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("addUserLdapCommand")
public class AddUserLdapCommand extends AbstractCrudLdapCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleUser> addRequestType, LdapContext ldapctx) throws ConnectorDataException {
        try {
            ManagedSystemObjectMatch matchObj = getMatchObject(addRequestType.getTargetID(), "USER");

            List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();
            List<BaseAttribute> supervisorMembershipList = new ArrayList<BaseAttribute>();

            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
            boolean groupMembershipEnabled = isMembershipEnabled(rpSet, "GROUP_MEMBERSHIP_ENABLED");
            boolean supervisorMembershipEnabled = isMembershipEnabled(rpSet, "SUPERVISOR_MEMBERSHIP_ENABLED");

            Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());

            // get the field that is to be used as the UniqueIdentifier
            //String ldapName = matchObj.getKeyField() +"=" + psoID.getID() + "," + baseDN;
            String identity = addRequestType.getObjectIdentity();

            //Check identity on DN format or not
            String identityPatternStr =  MessageFormat.format(DN_IDENTITY_MATCH_REGEXP, matchObj.getKeyField());
            Pattern pattern = Pattern.compile(identityPatternStr);
            Matcher matcher = pattern.matcher(identity);
            String objectBaseDN;
            if (matcher.matches()) {
                identity = matcher.group(1);
                String CN = matchObj.getKeyField()+"="+identity;
                objectBaseDN =  addRequestType.getObjectIdentity().substring(CN.length()+1);
            } else {
                // if identity is not in DN format try to find OU info in attributes
                String OU = getOU(addRequestType.getExtensibleObject());
                if(StringUtils.isNotEmpty(OU)) {
                    objectBaseDN = OU+","+matchObj.getBaseDn();
                } else {
                    objectBaseDN = matchObj.getBaseDn();
                }
            }

            log.debug("baseDN=" + objectBaseDN);
            log.debug("ID field=" + matchObj.getKeyField());
            log.debug("Group Membership enabled? " + groupMembershipEnabled);

            log.debug("Checking if the identity exists: " + identity);

            BasicAttributes basicAttr = getBasicAttributes(addRequestType.getExtensibleObject(), matchObj.getKeyField(),
                    targetMembershipList, groupMembershipEnabled, supervisorMembershipList, supervisorMembershipEnabled);

            //Important!!! For add new record in LDAP we must to create identity in DN format
            String identityDN = matchObj.getKeyField() + "=" + identity + "," + objectBaseDN;
            log.debug("Creating users in ldap.." + identityDN);
            ldapctx.createSubcontext(identityDN, basicAttr);

            if (groupMembershipEnabled) {
                dirSpecificImp.updateAccountMembership(managedSys, targetMembershipList, identity, identityDN,
                        matchObj, ldapctx, addRequestType.getExtensibleObject());
            }

            if (supervisorMembershipEnabled) {
                dirSpecificImp.updateSupervisorMembership(managedSys, supervisorMembershipList, identity, identityDN,
                        matchObj, ldapctx, addRequestType.getExtensibleObject());
            }

        } catch (NamingException e) {
           log.error(e.getMessage(), e);
           throw new  ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }






}
