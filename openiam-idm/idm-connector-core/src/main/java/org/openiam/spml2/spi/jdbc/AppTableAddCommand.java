package org.openiam.spml2.spi.jdbc;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.ErrorCode;
import org.openiam.connector.type.StatusCodeType;
import org.openiam.connector.type.UserRequest;
import org.openiam.connector.type.UserResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.spi.common.AddCommand;
import org.openiam.connector.util.ResponseBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/17/12
 * Time: 7:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class AppTableAddCommand extends AbstractAppTableCommand implements AddCommand {

    private static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s)";

    public UserResponse add(UserRequest reqType) {
        final UserResponse response = new UserResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String targetID = reqType.getTargetID();
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

        final ResourceProp prop = res.getResourceProperty("TABLE_NAME");
        if(prop == null) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "No TABLE_NAME property found");
            return response;
        }

        final String tableName = prop.getPropValue();
        if (StringUtils.isBlank(tableName)) {
        	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "TABLE NAME is not defined.");
            return response;
        }

        final String principalName = reqType.getUserIdentity();


        final ExtensibleObject obj = reqType.getUser();

        if(log.isDebugEnabled()) {
            log.debug(String.format("ExtensibleObject in Add Request=%s", obj));
        }

        Connection con = null;
        try {
            con = connectionMgr.connect(managedSys);

            // build sql
            final StringBuilder columns = new StringBuilder("");
            final StringBuilder values = new StringBuilder("");

                if (identityExists(con, tableName, principalName, obj)) {
                    if(log.isDebugEnabled()) {
                        log.debug(String.format("%s exists. Returning success to the connector", principalName));
                    }
                    return response;
                }

                final List<ExtensibleAttribute> attrList = obj.getAttributes();

                if(log.isDebugEnabled()) {
                    log.debug(String.format("Number of attributes to persist in ADD = %s", attrList.size()));
                }

                int ctr = 0;
                for (final ExtensibleAttribute att : attrList) {
                    if (ctr != 0) {
                        columns.append(",");
                        values.append(",");
                    }
                    ctr++;
                    columns.append(att.getName());
                    values.append("?");
                }
                // add the primary key

                if(log.isDebugEnabled()) {
                    log.debug(String.format("Principal column name=%s", obj.getPrincipalFieldName()));
                }
                if (obj.getPrincipalFieldName() != null) {
                    if (ctr != 0) {
                        columns.append(",");
                        values.append(",");
                    }
                    columns.append(obj.getPrincipalFieldName());
                    values.append("?");
                }

                final String sql = String.format(INSERT_SQL, tableName, columns, values);

                if(log.isDebugEnabled()) {
                    log.debug(String.format("ADD SQL=%s", sql));
                }

                final PreparedStatement statement = con.prepareStatement(sql);
                // set the parameters

                if (obj.getPrincipalFieldName() != null) {

                    setStatement(statement, ctr, obj.getPrincipalFieldDataType(), principalName);
                }

                statement.executeUpdate();



        } catch (SQLException se) {
            log.error(se);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, se.toString());
        } catch (ClassNotFoundException cnfe) {
            log.error(cnfe);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, cnfe.toString());
        } catch (ParseException pe) {
            log.error(pe);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, pe.toString());
        } catch(Throwable e) {
            log.error(e);
            ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.OTHER_ERROR, e.toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException s) {
                    log.error(s.toString());
                    ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, s.toString());
                }
            }
        }


        return response;
    }
}
