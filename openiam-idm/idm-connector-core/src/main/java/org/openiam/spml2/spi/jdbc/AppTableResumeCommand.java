package org.openiam.spml2.spi.jdbc;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.SuspendResumeRequest;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.exception.EncryptionException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.spml2.spi.common.ResumeCommand;
import org.openiam.connector.util.ResponseBuilder;
import org.springframework.beans.factory.annotation.Required;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/17/12
 * Time: 10:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class AppTableResumeCommand extends AbstractAppTableCommand implements ResumeCommand {

    private LoginDataService loginManager;

    public ResponseType resume(SuspendResumeRequest request) {
        Connection con = null;

        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

//        final String principalName = request.getObjectIdentity();
//
//        /* targetID -  */
//        final String targetID = request.getTargetID();
//
//        List<LoginEntity> loginList = loginManager.getLoginDetailsByManagedSys(principalName, targetID);
//        if (loginList == null || loginList.isEmpty()) {
//        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_IDENTIFIER, "Principal not found");
//            return response;
//        }
//
//        try {
//
//            final LoginEntity login = loginList.get(0);
//            final String encPassword = login.getPassword();
//            final String decPassword = loginManager.decryptPassword(login.getUserId(),encPassword);
//
//            final ManagedSysDto managedSys = managedSysService.getManagedSys(targetID);
//            if(managedSys == null) {
//            	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));
//                return response;
//            }
//
//            if (StringUtils.isBlank(managedSys.getResourceId())) {
//            	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");
//                return response;
//            }
//
//            final Resource res = resourceDataService.getResource(managedSys.getResourceId());
//            if(res == null) {
//            	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");
//                return response;
//            }
//
//            final ResourceProp prop = res.getResourceProperty("TABLE_NAME");
//            if(prop == null) {
//            	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No TABLE_NAME property found");
//                return response;
//            }
//
//            final String tableName = prop.getPropValue();
//            if (StringUtils.isBlank(tableName)) {
//            	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "TABLE NAME is not defined.");
//                return response;
//            }
//
//
//            con = connectionMgr.connect(managedSys);
//
//            final PreparedStatement statement = createSetPasswordStatement(con, res, tableName, principalName, decPassword);
//
//            statement.executeUpdate();
//
//        } catch (SQLException se) {
//            log.error(se);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, se.toString());
//        } catch (ClassNotFoundException cnfe) {
//            log.error(cnfe);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, cnfe.toString());
//        } catch (ParseException pe) {
//            log.error(pe);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, pe.toString());
//        } catch (EncryptionException ee) {
//            log.error(ee);
//            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, ee.toString());
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

    @Required
    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }
}
