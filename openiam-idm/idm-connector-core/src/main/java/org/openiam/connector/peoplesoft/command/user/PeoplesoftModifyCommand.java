package org.openiam.connector.peoplesoft.command.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleUser;
import org.springframework.stereotype.Service;

/**
 */
@Service("modifyUserPeopleSoftCommand")
public class PeoplesoftModifyCommand extends AbstractPeoplesoftCommand<CrudRequest<ExtensibleUser>, ObjectResponse> {

    @Override
    public ObjectResponse execute(CrudRequest<ExtensibleUser> reqType) throws ConnectorDataException {
        ObjectResponse response = new ObjectResponse();
        Connection con = null;

        String displayName = null;
        String role = null;
        String email = null;
        String employeeId = null;
        String status = null;

        String principalName = reqType.getObjectIdentity();
        final String targetID = reqType.getTargetID();
        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, String.format(
                    "No Managed System with target id: %s", targetID));
        }
        String schemaName = managedSys.getHostUrl();
        if (principalName != null) {
            principalName = principalName.toUpperCase();
        }

        try {
            con = this.getConnection(managedSys);
            final List<ExtensibleAttribute> attrList = reqType.getExtensibleObject().getAttributes();
            for (ExtensibleAttribute att : attrList) {
                if (att.getOperation() > 0 && att.getName() != null) {
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

            int version = (getVersion(con, schemaName) + 1);

            if (identityExists(con, principalName, schemaName)) {
                updateUser(con, principalName, displayName, email, Integer.valueOf(status), schemaName);
            }

            if (!StringUtils.isBlank(role)) {
                if (!roleExists(con, principalName, role, schemaName)) {
                    addToRole(con, principalName, role, schemaName);
                }
            }

            if (!StringUtils.isBlank(email)) {
                if (!emailExists(con, principalName, schemaName)) {
                    insertEmail(con, principalName, email, schemaName);

                } else {

                    updateEmail(con, principalName, email, schemaName);

                }
            }

            if (!roleExlatoprExists(con, principalName, schemaName)) {
                insertRoleExlatopr(con, principalName, displayName, email, employeeId, schemaName);
            }

            if (!userAttrExists(con, principalName, schemaName)) {
                insertUserAttribute(con, principalName, schemaName);

            }

            if (!pspruhdefnExists(con, principalName, schemaName)) {

                insertPSPRUHDEFN(con, principalName, version, schemaName);

            }

            if (!pspruhtabExists(con, principalName, schemaName)) {
                insertPSPRUHTAB(con, principalName, schemaName);

            }

            if (!psruhtabpgltExists(con, principalName, schemaName)) {
                insertPSPRUHTABPGLT(con, principalName, schemaName);

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
