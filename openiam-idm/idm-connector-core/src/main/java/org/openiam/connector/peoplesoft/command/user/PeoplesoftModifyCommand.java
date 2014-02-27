package org.openiam.connector.peoplesoft.command.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.provision.type.ExtensibleUser;

/**
 */
public class PeoplesoftModifyCommand extends AbstractPeoplesoftCommand<CrudRequest<ExtensibleUser>, ObjectResponse> {

    @Override
    public ObjectResponse execute(CrudRequest<ExtensibleUser> reqType) throws ConnectorDataException {
        ObjectResponse response = new ObjectResponse();
        Connection con = null;
        List<BaseAttribute> targetMembershipList = new ArrayList<BaseAttribute>();

        String displayName = null;
        String role = null;
        String email = null;
        String employeeId = null;
        String symbolicID;
        String password = null;
        String status = null;

        String principalName = reqType.getObjectIdentity();
        final String targetID = reqType.getTargetID();
        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, String.format(
                    "No Managed System with target id: %s", targetID));
        }

        if (principalName != null) {
            principalName = principalName.toUpperCase();
        }

        try {
            con = this.getConnection(managedSys);
            final List<ExtensibleAttribute> attrList = reqType.getExtensibleObject().getAttributes();
            for (ExtensibleAttribute att : attrList) {
                if (att.getOperation() != 0 && att.getName() != null) {
                    if (att.getObjectType().equalsIgnoreCase("USER")) {

                        System.out.println("Attribute: " + att.getName() + " -> " + att.getValue());
                        if (StringUtils.equalsIgnoreCase("displayName", att.getName())) {
                            displayName = att.getValue();
                        }
                        if (StringUtils.equalsIgnoreCase("role", att.getName())) {
                            role = att.getValue();
                        }
                        if (StringUtils.equalsIgnoreCase("email", att.getName())) {
                            email = att.getValue();
                        }
                        if (StringUtils.equalsIgnoreCase("employeeId", att.getName())) {
                            employeeId = att.getValue();
                        }
                        if (StringUtils.equalsIgnoreCase("status", att.getName())) {
                            status = att.getValue();
                            if (status == null) {
                                status = "0";

                            }
                        }

                    }
                }
            }

            int version = (getVersion(con) + 1);

            if (identityExists(con, principalName)) {
                updateUser(con, principalName, displayName, email, Integer.valueOf(status));
            }

            if (!StringUtils.isBlank(role)) {
                if (!roleExists(con, principalName, role)) {
                    addToRole(con, principalName, role);
                }
            }

            if (!StringUtils.isBlank(email)) {
                if (!emailExists(con, principalName)) {
                    insertEmail(con, principalName, email);

                } else {

                    updateEmail(con, principalName, email);

                }
            }

            if (!roleExlatoprExists(con, principalName)) {
                insertRoleExlatopr(con, principalName, displayName, email, employeeId);
            }

            if (!userAttrExists(con, principalName)) {
                insertUserAttribute(con, principalName);

            }

            if (!pspruhdefnExists(con, principalName)) {

                insertPSPRUHDEFN(con, principalName, version);

            }

            if (!pspruhtabExists(con, principalName)) {
                insertPSPRUHTAB(con, principalName);

            }

            if (!psruhtabpgltExists(con, principalName)) {
                insertPSPRUHTABPGLT(con, principalName);

            }

        } catch (SQLException se) {
            log.error(se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.toString());
        } catch (Throwable e) {
            log.error(e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException s) {
                    log.error(s);
                    throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, s.toString());
                }
            }
        }

        response.setStatus(StatusCodeType.SUCCESS);
        return response;
    }

    /**
     * List of Oracle roles that we want to grant access to
     * 
     * @param att
     * @param targetMembershipList
     */
    protected void buildMembershipList(ExtensibleAttribute att, List<BaseAttribute> targetMembershipList) {

        if (att == null)
            return;

        List<String> membershipList = att.getValueList();
        if (membershipList != null) {
            for (String s : membershipList) {
                targetMembershipList.add(new BaseAttribute(s, s));
            }

        }

    }

}
