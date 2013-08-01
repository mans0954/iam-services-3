package org.openiam.spml2.spi.jdbc;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.type.*;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysDto;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.spml2.spi.common.LookupCommand;
import org.openiam.connector.util.ResponseBuilder;

import java.sql.*;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/17/12
 * Time: 8:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppTableLookupCommand extends AbstractAppTableCommand implements LookupCommand {

    public SearchResponse lookup(LookupRequest reqType) {
        boolean found = false;

        if(log.isDebugEnabled()) {
            log.debug("AppTable lookup operation called.");
        }

        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = reqType.getSearchValue();

        /* targetID -  */
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


        Connection con = null;

        final ObjectValue userValue = new ObjectValue();
        userValue.setUserIdentity(principalName);


        try {
            con = connectionMgr.connect(managedSys);

            final PreparedStatement statement = createSelectStatement(con, res, tableName, principalName);
            if (statement == null) {
            	ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.INVALID_CONFIGURATION, "Unable to generate SQL based on configuration");
                return response;
            }
            if(log.isDebugEnabled()) {
                log.debug("Executing lookup query");
            }

            final ResultSet rs = statement.executeQuery();
            final ResultSetMetaData rsMetadata = rs.getMetaData();
            int columnCount = rsMetadata.getColumnCount();

            if(log.isDebugEnabled()) {
                log.debug(String.format("Query contains column count = %s",columnCount));
            }

            if (rs.next()) {
                found = true;
                for (int colIndx = 1; colIndx <= columnCount; colIndx++) {

                    final ExtensibleAttribute extAttr = new ExtensibleAttribute();

                    extAttr.setName(rsMetadata.getColumnName(colIndx));

                    setColumnValue(extAttr, colIndx, rsMetadata, rs);

                    userValue.getAttributeList().add(extAttr);
                }

                response.getUserList().add(userValue);
            } else {
                if(log.isDebugEnabled()) {
                    log.debug("Principal not found");
                }
                response.setStatus(StatusCodeType.FAILURE);
                return response;
            }

            response.setStatus(StatusCodeType.SUCCESS);
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
                    log.error(s);
                    ResponseBuilder.populateResponse(response, StatusCodeType.FAILURE, ErrorCode.SQL_ERROR, s.toString());
                }
            }
        }
        return response;


    }

    private void setColumnValue(ExtensibleAttribute extAttr, int colIndx, ResultSetMetaData rsMetadata, ResultSet rs)
            throws SQLException {

        final int fieldType = rsMetadata.getColumnType(colIndx);

        if(log.isDebugEnabled()) {
            log.debug(String.format("column type = %s", fieldType));
        }

        if (fieldType == Types.INTEGER) {
            if(log.isDebugEnabled()) {
                log.debug("type = Integer");
            }
            extAttr.setDataType("INTEGER");
            extAttr.setValue(String.valueOf(rs.getInt(colIndx)));
        }

        if (fieldType == Types.FLOAT || fieldType == Types.NUMERIC) {
            if(log.isDebugEnabled()) {
                log.debug("type = Float");
            }
            extAttr.setDataType("FLOAT");
            extAttr.setValue(String.valueOf(rs.getFloat(colIndx)));

        }

        if (fieldType == Types.DATE) {
            if(log.isDebugEnabled()) {
                log.debug("type = Date");
            }
            extAttr.setDataType("DATE");
            if (rs.getDate(colIndx) != null) {
                extAttr.setValue(String.valueOf(rs.getDate(colIndx).getTime()));
            }

        }
        if (fieldType == Types.TIMESTAMP) {
            if(log.isDebugEnabled()) {
                log.debug("type = Timestamp");
            }
            extAttr.setDataType("TIMESTAMP");
            extAttr.setValue(String.valueOf(rs.getTimestamp(colIndx).getTime()));

        }
        if (fieldType == Types.VARCHAR || fieldType == Types.CHAR) {
            if(log.isDebugEnabled()) {
                log.debug("type = Varchar");
            }
            extAttr.setDataType("STRING");
            if (rs.getString(colIndx) != null) {
                extAttr.setValue(rs.getString(colIndx));
            }

        }
    }
}
