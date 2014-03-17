package org.openiam.connector.peoplesoft.command.user;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA. User: Lev Date: 8/21/12 Time: 10:46 AM To change
 * this template use File | Settings | File Templates.
 */
@Service("deleteUserPeopleSoftCommand")
public class PeoplesoftDeleteCommand extends AbstractPeoplesoftCommand<CrudRequest<ExtensibleObject>, ObjectResponse> {

    private static final String DROP_USER = "DROP USER \"%s\"";

    @Override
    public ObjectResponse execute(CrudRequest<ExtensibleObject> request) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = request.getObjectIdentity();
        final String targetID = request.getTargetID();

        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, String.format(
                    "No Managed System with target id: %s", targetID));
        }

        if (StringUtils.isBlank(managedSys.getResourceId())) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    "ResourceID is not defined in the ManagedSys Object");
        }

        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
        if (res == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "No resource for managed resource found");
        }

        Connection con = null;
        try {
            final String sql = String.format(DROP_USER, principalName);
            con = this.getConnection(managedSys);
            con.createStatement().execute(sql);
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

        return response;
    }
}
