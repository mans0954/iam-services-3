package org.openiam.connector.peoplesoft.command.user;

import java.sql.Connection;
import java.sql.SQLException;

import org.openiam.connector.peoplesoft.command.base.AbstractPeoplesoftCommand;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.PasswordRequest;
import org.openiam.base.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service("setPasswordPeopleSoftCommand")
public class PeoplesoftPasswordCommand extends AbstractPeoplesoftCommand<PasswordRequest, ResponseType> {
    @Override
    public ResponseType execute(PasswordRequest reqType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = reqType.getObjectIdentity();

        /* targetID - */
        final String targetID = reqType.getPassword();

        final String password = reqType.getPassword();

        final ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        if (managedSys == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "No managed resource");
        }
        String schemaName = managedSys.getHostUrl();
        if (managedSys.getResource() == null) {
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,
                    "ResourceID is not defined in the ManagedSys Object");
        }

        Connection con = null;
        try {
            changePassword(managedSys, principalName, password, schemaName);
        } catch (SQLException se) {
            log.error(se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.toString());
        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, cnfe.toString());
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
