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

import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("addUserLdapCommand")
public class AddUserLdapCommand extends AbstractCrudLdapCommand<ExtensibleUser> {
    @Override
    protected void performObjectOperation(ManagedSysEntity managedSys, CrudRequest<ExtensibleUser> addRequestType, LdapContext ldapctx) throws ConnectorDataException {
        try {

            List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();
            List<BaseAttribute> supervisorMembershipList = new ArrayList<BaseAttribute>();

            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
            boolean groupMembershipEnabled = isMembershipEnabled(rpSet, "GROUP_MEMBERSHIP_ENABLED");
            boolean supervisorMembershipEnabled = isMembershipEnabled(rpSet, "SUPERVISOR_MEMBERSHIP_ENABLED");

            String identity = addRequestType.getObjectIdentity();
            String keyField = (identity.matches(DN_MATCH_REGEXP))
                    ? getDnKeyField(identity)
                    : addRequestType.getObjectIdentityAttributeName();

            BasicAttributes basicAttr = getBasicAttributes(addRequestType.getExtensibleObject(),
                    keyField, targetMembershipList, groupMembershipEnabled,
                    supervisorMembershipList, supervisorMembershipEnabled);

            String identityDN = buildIdentityDN(addRequestType, managedSys, ldapctx);

            //Important!!! For add new record in LDAP we must to create identity in DN format
            log.debug("Creating users in ldap.." + identityDN);
            ldapctx.createSubcontext(identityDN, basicAttr);

            // Add membership
            log.debug("Group Membership enabled? " + groupMembershipEnabled);
            Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
            ManagedSystemObjectMatch matchObj = getMatchObject(addRequestType.getTargetID(), ManagedSystemObjectMatch.USER);

            if (groupMembershipEnabled) {
                dirSpecificImp.updateAccountMembership(managedSys, targetMembershipList,
                        addRequestType.getObjectIdentity(), identityDN,
                        matchObj, ldapctx, addRequestType.getExtensibleObject());
            }

            if (supervisorMembershipEnabled) {
                dirSpecificImp.updateSupervisorMembership(managedSys, supervisorMembershipList,
                        addRequestType.getObjectIdentity(), identityDN,
                        matchObj, ldapctx, addRequestType.getExtensibleObject());
            }

        } catch (NamingException e) {
           log.error(e.getMessage(), e);
           throw new  ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }

    protected String buildIdentityDN(CrudRequest<ExtensibleUser> request, ManagedSysEntity managedSys, LdapContext ldapctx) {
        String identity = request.getObjectIdentity();
        if (identity.matches(DN_MATCH_REGEXP)) {
            return identity;
        } else {

            String objectBaseDN = request.getBaseDN();
            // try to find OU info in attributes
            String OU = getAttributeValue(request.getExtensibleObject(), OU_ATTRIBUTE_NAME);
            if(StringUtils.isNotEmpty(OU)) {
                objectBaseDN = OU + "," + objectBaseDN;
            }
            String identityField = StringUtils.isNotBlank(request.getObjectIdentityAttributeName())
                    ? request.getObjectIdentityAttributeName()
                    : DEFAULT_IDENTITY_ATTRIBUTE_NAME;

            String identityFieldValue = getAttributeValue(request.getExtensibleObject(), identityField);
            return identityField + "=" + identityFieldValue + "," + objectBaseDN;
        }
    }

}
