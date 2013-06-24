package org.openiam.spml2.spi.ldap.command.base;

import org.openiam.base.BaseAttribute;
import org.openiam.exception.ConfigurationException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSystemObjectMatchEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.ldap.dirtype.Directory;
import org.openiam.spml2.spi.ldap.dirtype.DirectorySpecificImplFactory;
import org.openiam.spml2.util.connect.ConnectionFactory;
import org.openiam.spml2.util.connect.ConnectionManagerConstant;
import org.openiam.spml2.util.connect.ConnectionMgr;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapContext;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AbstractAddLdapCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLdapCommand<AddRequestType<ProvisionObject>, AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {
        AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();

        boolean groupMembershipEnabled = true;

        String requestID = addRequestType.getRequestID();
        /* ContainerID - May specify the container in which this object should be created
           *      ie. ou=Development, org=Example */
        PSOIdentifierType containerID = addRequestType.getContainerID();
        /* PSO - Provisioning Service Object -
           *     -  ID must uniquely specify an object on the target or in the target's namespace
           *     -  Try to make the PSO ID immutable so that there is consistency across changes. */
        PSOIdentifierType psoID = addRequestType.getPsoID();
        /* targetID -  */
        String targetID = addRequestType.getTargetID();

        // Data sent with request - Data must be present in the request per the spec
        ExtensibleType data = addRequestType.getData();
        Map<QName, String> otherData = addRequestType.getOtherAttributes();

        /* Indicates what type of data we should return from this operations */
        ReturnDataType returnData = addRequestType.getReturnData();


        /* A) Use the targetID to look up the connection information under managed systems */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        LdapContext ldapctx = this.connect(managedSys);

        try {

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
            String ldapName = psoID.getID();

            log.debug("Checking if the identity exists: " + ldapName);

            // check if the identity exists in ldap first before creating the identity
            if (identityExists(ldapName, ldapctx)) {
                log.debug(ldapName + "exists. Returning success from the connector");
                return response;
            }
            //

            log.debug(ldapName + " does not exist. building attribute list");




            BasicAttributes basicAttr = getBasicAttributes(reqType.getData().getAny(), matchObj.getKeyField(),
                    targetMembershipList, groupMembershipEnabled);


            log.debug("Creating users in ldap.." + ldapName);

            Context result = ldapctx.createSubcontext(ldapName, basicAttr);

            if (groupMembershipEnabled) {


                dirSpecificImp.updateAccountMembership(targetMembershipList,ldapName,  matchObj, ldapctx, reqType.getData().getAny());
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
}
