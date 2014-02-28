package org.openiam.connector.peoplesoft.command.user;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;

/**
 * Created with IntelliJ IDEA. User: Lev Date: 8/21/12 Time: 10:50 AM To change
 * this template use File | Settings | File Templates.
 */
public class PeoplesoftSuspendCommand extends AbstractPeoplesoftCommand<SuspendResumeRequest, ResponseType> {
    @Override
    public ResponseType execute(SuspendResumeRequest request) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
        Connection con = null;

        final String principalName = request.getObjectIdentity();

        /* targetID - */
        final String targetID = request.getTargetID();

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, String.format(
                    "No Managed System with target id: %s", targetID));
        }
        String schemaName = managedSys.getHostUrl();
        if (StringUtils.isBlank(managedSys.getResourceId())) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    "ResourceID is not defined in the ManagedSys Object");
        }

        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
        if (res == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "No resource for managed resource found");
        }

        try {
            con = this.getConnection(managedSys);
            updateUserLock(con, principalName, 1, schemaName);
        } catch (SQLException se) {
            log.error(se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.toString());
        } catch (Throwable e) {
            log.error(e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.toString());
        }
        return response;
    }

}
