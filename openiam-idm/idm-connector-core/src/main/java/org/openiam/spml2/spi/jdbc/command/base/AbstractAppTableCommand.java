package org.openiam.spml2.spi.jdbc.command.base;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.common.jdbc.AbstractJDBCCommand;
import org.openiam.spml2.spi.jdbc.command.data.AppTableConfiguration;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public abstract class AbstractAppTableCommand<Request extends RequestType, Response extends ResponseType> extends AbstractJDBCCommand<Request, Response> {
    private static final String TABLE_NAME_PROP = "TABLE_NAME";

    protected static final String DATE_FORMAT = "MM/dd/yyyy";
    protected static final String SELECT_SQL = "SELECT %s FROM %S WHERE %s=?";
    protected static final String DELETE_SQL = "DELETE FROM %s WHERE %s=?";
    protected static final String UPDATE_SQL = "UPDATE %s SET %s=? WHERE %s=?";

    protected AppTableConfiguration getConfiguration(String targetID, ManagedSysEntity managedSys) throws ConnectorDataException{
        AppTableConfiguration configuration = new AppTableConfiguration();
        if(managedSys == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, String.format("No Managed System with target id: %s", targetID));

        if (StringUtils.isBlank(managedSys.getResourceId()))
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "ResourceID is not defined in the ManagedSys Object");

        final Resource res = resourceDataService.getResource(managedSys.getResourceId());
        if(res == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No resource for managed resource found");

        configuration.setResourceId(res.getResourceId());
        final ResourceProp prop = res.getResourceProperty(TABLE_NAME_PROP);
        if(prop == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No TABLE_NAME property found");


        final String tableName = prop.getPropValue();
        if (StringUtils.isBlank(tableName))
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "TABLE NAME is not defined.");

        configuration.setTableName(tableName);

        return  configuration;
    }

    protected void setStatement(PreparedStatement statement, int column, ExtensibleAttribute att) throws ConnectorDataException {

        final String dataType = att.getDataType();
        final String dataValue = att.getValue();
        setStatement(statement, column, dataType, dataValue);
    }

    protected void setStatement(PreparedStatement statement, int column, String dataType, String value) throws ConnectorDataException {
        try {
            if(StringUtils.equalsIgnoreCase(dataType, "date")) {
                final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                final java.util.Date d;
                d = sdf.parse(value);
                // get the date into a java.sql.Date
                statement.setDate(column, new Date(d.getTime()));
            }
            if(StringUtils.equalsIgnoreCase(dataType, "integer")) {
                statement.setInt(column, Integer.valueOf(value));
            }

            if(StringUtils.equalsIgnoreCase(dataType, "float")) {
                statement.setFloat(column, Float.valueOf(value));
            }

            if(StringUtils.equalsIgnoreCase(dataType, "string")) {
                statement.setString(column, value);
            }

            if(StringUtils.equalsIgnoreCase(dataType, "timestamp")) {
                statement.setTimestamp(column, Timestamp.valueOf(value));
            }
        } catch (ParseException e) {
           log.error(e.getMessage(),e);
           throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } catch (SQLException e) {
           log.error(e.getMessage(), e);
           throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }

    protected boolean identityExists(final Connection con, final String tableName, final String principalName, final ExtensibleObject obj) throws ConnectorDataException {

        PreparedStatement statement = null;
        final String principalFieldName = obj.getPrincipalFieldName();
        final String principalFieldDataType = obj.getPrincipalFieldDataType();

        final String sql = String.format(SELECT_SQL, principalFieldName, tableName, principalFieldName);

        if(log.isDebugEnabled()) {
            log.debug(String.format("IdentityExists(): %s", sql));
        }

        try {
            statement = con.prepareStatement(sql);
            // set the parameters
            setStatement(statement, 1, principalFieldDataType, principalName);
            final ResultSet rs = statement.executeQuery();
            if (rs != null && rs.next()) {
                final String id = rs.getString(1);
                if (id != null && !id.isEmpty()) {
                    return true;
                }
            }
        } catch (SQLException se) {
            log.error(se.getMessage(),se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return false;
    }

    protected PreparedStatement createSetPasswordStatement(final Connection con, final String resourceId, final String tableName, final String principalName,
                                                        final String password) throws  ConnectorDataException {
        String colName = null;
        String colDataType = null;

        final List<AttributeMapEntity> attrMap = attributeMaps(resourceId);
        if (attrMap == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR,"Attribute Map is null");

        String principalFieldName = null;
        String principalFieldDataType = null;
        for (final AttributeMapEntity atr : attrMap) {
            if (atr.getDataType() == null) {
                atr.setDataType("String");
            }

            final String objectType = atr.getMapForObjectType();
            if(StringUtils.equalsIgnoreCase(objectType, "password")) {
                colName = atr.getAttributeName();
                colDataType = atr.getDataType();
            }

            if(StringUtils.equalsIgnoreCase(objectType, "principal")) {
                principalFieldName = atr.getAttributeName();
                principalFieldDataType = atr.getDataType();

            }
        }

        final String sql = String.format(UPDATE_SQL, tableName, colName, principalFieldName);

        if(log.isDebugEnabled()) {
            log.debug(String.format("SQL: %s", sql));
        }

        PreparedStatement statement=null;
        try {
            statement = con.prepareStatement(sql);
            setStatement(statement, 1, colDataType, password);
            setStatement(statement, 2, principalFieldDataType, principalName);
            return statement;
        } catch (SQLException e) {
            log.error(e.getMessage(),e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
    }
}
