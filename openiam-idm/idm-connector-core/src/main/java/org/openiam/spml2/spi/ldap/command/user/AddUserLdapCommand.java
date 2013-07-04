package org.openiam.spml2.spi.ldap.command.user;

import org.openiam.base.BaseAttribute;
import org.openiam.exception.ConfigurationException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.PSOIdentifierType;
import org.openiam.spml2.spi.ldap.command.base.AbstractAddLdapCommand;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service("addUserLdapCommand")
public class AddUserLdapCommand extends AbstractAddLdapCommand<ProvisionUser> {
    @Override
    protected void addObject(ManagedSysEntity managedSys, PSOIdentifierType psoID, String targetID, List<ExtensibleObject> anyObjectList, LdapContext ldapctx) throws ConnectorDataException {
        try {
            ManagedSystemObjectMatch matchObj = null;
            boolean groupMembershipEnabled = true;
            List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();


            List<ManagedSystemObjectMatchEntity> matchObjList = managedSysObjectMatchDao.findBySystemId(targetID, "USER");
            if (matchObjList != null && matchObjList.size() > 0) {
                matchObj = managedSystemObjectMatchDozerConverter.convertToDTO(matchObjList.get(0),false);
            }

            log.debug("matchObj = " + matchObj);

            if (matchObj == null) {
                throw new ConfigurationException("LDAP configuration is missing configuration information");
            }

            Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
            ResourceProp rpGroupMembership = getResourceAttr(rpSet,"GROUP_MEMBERSHIP_ENABLED");

            // BY DEFAULT - we want to enable group membership
            if (rpGroupMembership == null || rpGroupMembership.getPropValue() == null || "Y".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                groupMembershipEnabled = true;
            }else if (rpGroupMembership.getPropValue() != null) {

                if ("N".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
                    groupMembershipEnabled = false;
                }
            }


            Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());


            log.debug("baseDN=" + matchObj.getBaseDn());
            log.debug("ID field=" + matchObj.getKeyField());
            log.debug("Group Membership enabled? " + groupMembershipEnabled);

            // get the baseDN
            String baseDN = matchObj.getBaseDn();


            // get the field that is to be used as the UniqueIdentifier
            //String ldapName = matchObj.getKeyField() +"=" + psoID.getID() + "," + baseDN;
            String ldapName = psoID.getID();

            log.debug("Checking if the identity exists: " + ldapName);

            // check if the identity exists in ldap first before creating the identity
            if (identityExists(ldapName, ldapctx)) {
                return;
            }
            //

            log.debug(ldapName + " does not exist. building attribute list");


            BasicAttributes basicAttr = getBasicAttributes(anyObjectList, matchObj.getKeyField(),
                    targetMembershipList, groupMembershipEnabled);


            log.debug("Creating users in ldap.." + ldapName);


                Context result = ldapctx.createSubcontext(ldapName, basicAttr);


            if (groupMembershipEnabled) {


                dirSpecificImp.updateAccountMembership(targetMembershipList,ldapName,  matchObj, ldapctx, anyObjectList);
            }
        } catch (NamingException e) {
           log.error(e.getMessage(), e);
           throw new  ConnectorDataException(ErrorCode.DIRECTORY_ERROR, e.getMessage());
        }
    }






}
