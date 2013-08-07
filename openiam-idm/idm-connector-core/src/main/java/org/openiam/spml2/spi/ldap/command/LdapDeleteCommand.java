package org.openiam.spml2.spi.ldap.command;

import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.connector.util.ConnectionMgr;


@Deprecated
public class LdapDeleteCommand extends LdapAbstractCommand {

    public ObjectResponse delete(CrudRequest reqType) {

        log.debug("delete request called..");
        ConnectionMgr conMgr = null;
        boolean groupMembershipEnabled = true;
        String delete = "DELETE";
        ObjectResponse respType = new ObjectResponse();

//        //String uid = null;
//        String ou = null;
//
//        String requestID = reqType.getRequestID();
//
//        /* targetID -  */
//        String targetID = reqType.getTargetID();
//
//        /* A) Use the targetID to look up the connection information under managed systems */
//        ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
//        ManagedSystemObjectMatch[] matchObj = managedSysService.managedSysObjectParam(targetID, "USER");
//
//
//        Set<ResourceProp> rpSet = getResourceAttributes(managedSys.getResourceId());
//
//        ResourceProp rpOnDelete = getResourceAttr(rpSet,"ON_DELETE");
//        ResourceProp rpGroupMembership = getResourceAttr(rpSet,"GROUP_MEMBERSHIP_ENABLED");
//
//        if (rpOnDelete == null || rpOnDelete.getPropValue() == null || "DELETE".equalsIgnoreCase(rpOnDelete.getPropValue())) {
//            delete = "DELETE";
//        }else if (rpOnDelete.getPropValue() != null) {
//
//            if ("DISABLE".equalsIgnoreCase(rpOnDelete.getPropValue())) {
//                delete = "DISABLE";
//            }
//        }
//
//        // BY DEFAULT - we want to enable group membership
//        if (rpGroupMembership == null || rpGroupMembership.getPropValue() == null || "Y".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
//            groupMembershipEnabled = true;
//        }else if (rpGroupMembership.getPropValue() != null) {
//
//            if ("N".equalsIgnoreCase(rpGroupMembership.getPropValue())) {
//                groupMembershipEnabled = false;
//            }
//        }
//
//
//
//
//        try {
//
//            log.debug("managedSys found for targetID=" + targetID + " " + " Name=" + managedSys.getName());
//
//
//            Directory dirSpecificImp  = DirectorySpecificImplFactory.create(managedSys.getHandler5());
//
//            conMgr = ConnectionFactory.create(ConnectionManagerConstant.LDAP_CONNECTION);
//            conMgr.setApplicationContext(ac);
//            LdapContext ldapctx = conMgr.connect(managedSys);
//
//            if (ldapctx == null) {
//                respType.setStatus(StatusCodeType.FAILURE);
//                respType.setError(ErrorCode.DIRECTORY_ERROR);
//                respType.addErrorMessage("Unable to connect to directory.");
//                return respType;
//            }
//
//            String ldapName = reqType.getUserIdentity();
//
//
//            if (groupMembershipEnabled) {
//                dirSpecificImp.removeAccountMemberships(ldapName, matchObj[0], ldapctx);
//            }
//
//            dirSpecificImp.delete(reqType, ldapctx, ldapName, delete);
//
//
//
//        } catch (NamingException ne) {
//
//            log.error(ne);
//
//            respType.setStatus(StatusCodeType.FAILURE);
//            respType.setError(ErrorCode.DIRECTORY_ERROR);
//            respType.addErrorMessage(ne.toString());
//            return respType;
//
//        } finally {
//            /* close the connection to the directory */
//            try {
//                if (conMgr != null) {
//                    conMgr.close();
//                }
//
//            } catch (NamingException n) {
//                log.error(n);
//            }
//
//        }

        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;


    }


}
