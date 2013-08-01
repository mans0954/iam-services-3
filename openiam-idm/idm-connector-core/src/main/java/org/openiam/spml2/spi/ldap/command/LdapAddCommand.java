package org.openiam.spml2.spi.ldap.command;

import org.openiam.base.BaseAttribute;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.exception.ConfigurationException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.spi.ldap.command.base.LdapAbstractCommand;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.connector.util.ConnectionManagerConstant;
import org.openiam.connector.util.ConnectionMgr;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * LdapAddCommand implements the add operation for the LdapConnector
 * User: suneetshah
 */
public class LdapAddCommand extends LdapAbstractCommand {

    public ObjectResponse add(CrudRequest reqType) {

        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);
        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();

        boolean groupMembershipEnabled = true;



        log.debug("add request called..");

        String requestID = reqType.getRequestID();

        /* targetID -  */
        String targetID = reqType.getTargetID();


        /* A) Use the targetID to look up the connection information under managed systems */
        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);


        ConnectionMgr conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
        conMgr.setApplicationContext(ac);
        try {
            log.debug("Connecting to directory:  " + managedSys.getName());

            LdapContext ldapctx = conMgr.connect(managedSys);

            log.debug("Ldapcontext = " + ldapctx);

            if (ldapctx == null) {
                response.setStatus(StatusCodeType.FAILURE);
                response.setError(ErrorCode.DIRECTORY_ERROR);
                response.addErrorMessage("Unable to connect to directory.");
                return response;
            }


            ManagedSystemObjectMatch matchObj = null;
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
            String ldapName = reqType.getUserIdentity();

            log.debug("Checking if the identity exists: " + ldapName);

            // check if the identity exists in ldap first before creating the identity
            if (identityExists(ldapName, ldapctx)) {
                log.debug(ldapName + "exists. Returning success from the connector");
                return response;
            }
            //

            log.debug(ldapName + " does not exist. building attribute list");




            BasicAttributes basicAttr = getBasicAttributes(reqType.getUser(), matchObj.getKeyField(),
                    targetMembershipList, groupMembershipEnabled);


            log.debug("Creating users in ldap.." + ldapName);

            Context result = ldapctx.createSubcontext(ldapName, basicAttr);

            if (groupMembershipEnabled) {


                dirSpecificImp.updateAccountMembership(targetMembershipList,ldapName,  matchObj, ldapctx, reqType.getUser());
            }


        } catch (NamingException ne) {

            ne.printStackTrace();

            log.error(ne);
            // return a response object - even if it fails so that it can be logged.
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.DIRECTORY_ERROR);
            response.addErrorMessage(ne.toString());

        } catch (Exception e) {

            e.printStackTrace();

            log.error(e);
            // return a response object - even if it fails so that it can be logged.
            response.setStatus(StatusCodeType.FAILURE);
            response.setError(ErrorCode.DIRECTORY_ERROR);
            response.addErrorMessage(e.toString());


        } finally {
            /* close the connection to the directory */
            try {
                if (conMgr != null) {
                    conMgr.close();
                }
            } catch (NamingException n) {
                log.error(n);
            }

        }


        return response;
    }

    private ModificationItem[] getLdapPassword( List<ExtensibleObject> requestAttribute, String ldapName) {

        for (ExtensibleObject obj : requestAttribute) {
            List<ExtensibleAttribute> attrList = obj.getAttributes();
            for (ExtensibleAttribute att : attrList) {

                if ("userPassword".equalsIgnoreCase(att.getName())) {
                    ModificationItem mods[] = new ModificationItem[1];
                    mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(att.getName(), att.getValue()));
                    return mods;
                }
            }
        }
        return null;

    }


}
