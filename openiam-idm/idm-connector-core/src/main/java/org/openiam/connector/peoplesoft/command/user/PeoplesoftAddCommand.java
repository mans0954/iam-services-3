package org.openiam.connector.peoplesoft.command.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Add command for the peoplesoft connector User: Suneet
 */
public class PeoplesoftAddCommand extends AbstractPeoplesoftCommand<CrudRequest<ExtensibleObject>, ObjectResponse> {

    @Override
    public ObjectResponse execute(CrudRequest<ExtensibleObject> request) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        String displayName = null;
        String role = null;
        String email = null;
        String employeeId = null;
        String symbolicID;
        String password = null;
        String status = null;

        schemaName = res.getString("SCHEMA");

        final String targetID = request.getTargetID();
        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR);
        }

        if (StringUtils.isBlank(managedSys.getResourceId())) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "managed system not linked with resource");
        }

        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
        if (res == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "resource is null");
        }

        String principalName = request.getObjectIdentity();
        if (principalName == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "No principal sent");
        }
        principalName = principalName.toUpperCase();

        final List<ExtensibleAttribute> objectList = request.getExtensibleObject().getAttributes();

        if (log.isDebugEnabled()) {
            log.debug(String.format("ExtensibleObject in Add Request=%s", objectList));
        }

        final List<AttributeMapEntity> attributeMap = this.attributeMaps(res.getId());

        // get the attributes that are needed for this operation

        if (log.isDebugEnabled()) {
            log.debug(String.format("Number of attributes to persist in ADD = %s", objectList.size()));
        }

        if (CollectionUtils.isNotEmpty(attributeMap)) {
            for (final ExtensibleAttribute att : objectList) {
                // for(final AttributeMap attribute : attributeMap) {

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
                if (StringUtils.equalsIgnoreCase("password", att.getName())) {
                    password = att.getValue();
                }

                if (StringUtils.equalsIgnoreCase("status", att.getName())) {
                    status = att.getValue();
                    if (status == null) {
                        status = "0";

                    }
                }

                // }
            }
        }

        Connection con = null;
        try {
            con = this.getConnection(managedSys);
            symbolicID = getSymbolicID(con);
            if (symbolicID == null) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("SymbolicID not found"));
                }
                return response;
            }

            int version = (getVersion(con) + 1);

            // get the required attributes

            // check if the identity exists
            // if it does then ignore the record - check the role membership
            // if it does not then create the record - add the users role
            // membership

            if (!identityExists(con, principalName)) {
                insertUser(con, principalName, displayName, employeeId, email, symbolicID, password, version,
                        Integer.valueOf(status));
            }
            // check if this user already has a role membership
            if (!StringUtils.isBlank(role)) {
                if (!roleExists(con, principalName, role)) {
                    addToRole(con, principalName, role);
                }
            }

            if (!StringUtils.isBlank(email)) {
                if (!emailExists(con, principalName)) {
                    insertEmail(con, principalName, email);

                }
            }

            if (!roleExlatoprExists(con, principalName)) {
                insertRoleExlatopr(con, principalName, displayName, email, employeeId);
            }

            if (!userAttrExists(con, principalName)) {
                insertUserAttribute(con, principalName);

            }

            if (!pspruhdefnExists(con, principalName)) {
                System.out.println("pspruhdefn DOES NOT Exist - inserting record...");

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
                    log.error(s.toString());
                    throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, s.toString());
                }
            }
        }

        return response;
    }
}
