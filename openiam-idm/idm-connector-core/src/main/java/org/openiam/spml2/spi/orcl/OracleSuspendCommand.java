package org.openiam.spml2.spi.orcl;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SuspendRequest;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.spml2.spi.common.SuspendCommand;
import org.openiam.connector.util.ResponseBuilder;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/21/12
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class OracleSuspendCommand extends AbstractOracleAccountStatusCommand implements SuspendCommand {
    @Override
    public ResponseType suspend(final SuspendRequest request) {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = request.getUserIdentity();

        /* targetID -  */
        final String targetID = request.getTargetID();

        final ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
        if(managedSys == null) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));
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

        try {
            changeAccountStatus(managedSys, principalName, AccountStatus.LOCKED);
        } catch (SQLException se) {
            log.error(se);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, se.toString());
        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, cnfe.toString());
        } catch(Throwable e) {
            log.error(e);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.toString());
        }
        return response;
    }
}
