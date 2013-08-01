package org.openiam.spml2.spi.orcl;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.PasswordRequest;
import org.openiam.connector.type.ResponseType;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.spml2.spi.common.PasswordCommand;
import org.openiam.connector.util.ResponseBuilder;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class OraclePasswordCommand extends AbstractOraclePasswordCommand implements PasswordCommand {
    @Override
    public ResponseType setPassword(PasswordRequest reqType) {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = reqType.getUserIdentity();

        /* targetID -  */
        final String targetID = reqType.getTargetID();

        final String password = reqType.getPassword();

        final ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        if(managedSys == null) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No managed resource");
            return response;
        }

        if (StringUtils.isBlank(managedSys.getResourceId())) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");
            return response;
        }

        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
        if(res == null) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");
            return response;
        }

        Connection con = null;
        try {
            changePassword(managedSys, principalName, password);
        } catch (SQLException se) {
            log.error(se);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, se.toString());
        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, cnfe.toString());
        } catch(Throwable e) {
            log.error(e);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException s) {
                    log.error(s);
                    ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, s.toString());
                }
            }
        }


        return response;
    }
}
