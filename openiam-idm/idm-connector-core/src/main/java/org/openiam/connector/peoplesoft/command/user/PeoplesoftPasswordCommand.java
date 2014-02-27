//package org.openiam.connector.peoplesoft.command.user;
//
//import org.apache.commons.lang.StringUtils;
//import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
//import org.openiam.idm.srvc.res.dto.Resource;
//import org.openiam.spml2.msg.ErrorCode;
//import org.openiam.spml2.msg.PSOIdentifierType;
//import org.openiam.spml2.msg.ResponseType;
//import org.openiam.spml2.msg.StatusCodeType;
//import org.openiam.spml2.msg.password.SetPasswordRequestType;
//import org.openiam.spml2.spi.common.PasswordCommand;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
///**
// *
// */
//public class PeoplesoftPasswordCommand extends AbstractPeoplesoftCommand implements PasswordCommand {
//    @Override
//    public ResponseType setPassword(SetPasswordRequestType reqType) {
//        final ResponseType response = new ResponseType();
//        response.setStatus(StatusCodeType.SUCCESS);
//
//        schemaName = res.getString("SCHEMA");
//
//        final String principalName = reqType.getPsoID().getID();
//
//        final PSOIdentifierType psoID = reqType.getPsoID();
//        /* targetID - */
//        final String targetID = psoID.getTargetID();
//
//        final String password = reqType.getPassword();
//
//        final ManagedSys managedSys = managedSysService.getManagedSys(targetID);
//        if (managedSys == null) {
//            populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No managed resource");
//            return response;
//        }
//
//        if (StringUtils.isBlank(managedSys.getResourceId())) {
//            populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION,
//                    "ResourceID is not defined in the ManagedSys Object");
//            return response;
//        }
//
//        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
//        if (res == null) {
//            populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION,
//                    "No resource for managed resource found");
//            return response;
//        }
//
//        Connection con = null;
//        try {
//            changePassword(managedSys, principalName, password);
//        } catch (SQLException se) {
//            log.error(se);
//            populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, se.toString());
//        } catch (ClassNotFoundException cnfe) {
//            log.error(cnfe);
//            populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, cnfe.toString());
//        } catch (Throwable e) {
//            log.error(e);
//            populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.toString());
//        } finally {
//            if (con != null) {
//                try {
//                    con.close();
//                } catch (SQLException s) {
//                    log.error(s);
//                    populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, s.toString());
//                }
//            }
//        }
//
//        return response;
//    }
// }
