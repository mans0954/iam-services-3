package org.openiam.spml2.spi.jdbc;

import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.idm.srvc.pswd.service.PasswordGenerator;
import org.openiam.spml2.spi.common.SuspendCommand;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/17/12
 * Time: 10:27 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class AppTableSuspendCommand extends AbstractAppTableCommand{

	@Autowired
	private PasswordGenerator passwordGenerator;

    public ResponseType suspend(final SuspendResumeRequest request) {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

//        final String principalName = request.getUserIdentity();
//
//        /* targetID -  */
//        final String targetID = request.getTargetID();
//
//        final String password = passwordGenerator.generatePassword(10);
//
//        final ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
//        if(managedSys == null) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));
//            return response;
//        }
//
//        if (StringUtils.isBlank(managedSys.getResourceId())) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");
//            return response;
//        }
//
//        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
//        if(res == null) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");
//            return response;
//        }
//
//        final ResourceProp prop = res.getResourceProperty("TABLE_NAME");
//        if(prop == null) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No TABLE_NAME property found");
//            return response;
//        }
//
//        final String tableName = prop.getPropValue();
//        if (StringUtils.isBlank(tableName)) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "TABLE NAME is not defined.");
//            return response;
//        }
//
//        Connection con = null;
//        try {
//            con = connectionMgr.connect(managedSys);
//            final PreparedStatement statement = createSetPasswordStatement(con, res, tableName, principalName, password);
//            statement.executeUpdate();
//        } catch (SQLException se) {
//            log.error(se);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, se.toString());
//        } catch (ClassNotFoundException cnfe) {
//            log.error(cnfe);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, cnfe.toString());
//        } catch (ParseException pe) {
//            log.error(pe);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, pe.toString());
//        } catch(Throwable e) {
//            log.error(e);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.toString());
//        } finally {
//            if (con != null) {
//                try {
//                    con.close();
//                } catch (SQLException s) {
//                    log.error(s);
//                    ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, s.toString());
//                }
//            }
//        }
        return response;
    }

}
