package org.openiam.spml2.spi.jdbc;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.RequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.spi.common.jdbc.AbstractJDBCCommand;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: Lev
 * Date: 8/17/12
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public abstract class AbstractAppTableCommand extends AbstractJDBCCommand {

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final String SELECT_SQL = "SELECT %s FROM %S WHERE %s=?";
    private static final String DELETE_SQL = "DELETE FROM %s WHERE %s=?";
    private static final String UPDATE_SQL = "UPDATE %s SET %s=? WHERE %s=?";

    protected void setStatement(PreparedStatement statement, int column, ExtensibleAttribute att)
            throws SQLException, ParseException {

        final String dataType = att.getDataType();
        final String dataValue = att.getValue();
        setStatement(statement, column, dataType, dataValue);
    }

    @Override
    public ResponseType execute(RequestType requestType) throws ConnectorDataException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void setStatement(PreparedStatement statement, int column, String dataType, String value)
            throws SQLException, ParseException {

        if(StringUtils.equalsIgnoreCase(dataType, "date")) {
            final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            final java.util.Date d = sdf.parse(value);

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
    }

    protected boolean identityExists(final Connection con, final String tableName, final String principalName, final ExtensibleObject obj) {

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
            log.error(se);

        } catch (ParseException pe) {
            log.error(pe);

        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception e) {
                log.error(e);

            }
        }
        return false;
    }

    public PreparedStatement createSelectStatement(final Connection con, final Resource res, final String tableName, final String principalName) throws SQLException, ParseException {

//        final List<AttributeMap> attrMap = attributeMaps(res);
//        if (attrMap == null) {
//            log.debug("Attribute Map is null");
//            return null;
//        }
//
//
//
//        int colCount = 0;
//        String principalFieldName = null;
//        String principalFieldDataType = null;
//        final StringBuilder columnList = new StringBuilder();
//        for (AttributeMap atr : attrMap) {
//            final String objectType = atr.getMapForObjectType();
//            if(StringUtils.equalsIgnoreCase(objectType, "principal")) {
//                principalFieldName = atr.getAttributeName();
//                principalFieldDataType = atr.getDataType();
//
//            } else if (StringUtils.equalsIgnoreCase(objectType, "USER")) {
//                if (colCount > 0) {
//                    columnList.append(",");
//                }
//                columnList.append(atr.getAttributeName());
//                colCount++;
//            }
//        }
//
//        final String sql = String.format(SELECT_SQL, columnList, tableName, principalFieldName);
//        if(log.isDebugEnabled()) {
//            log.debug(String.format("SQL: %s", sql));
//        }
//
//        final PreparedStatement statement = con.prepareStatement(sql);
//        setStatement(statement, 1, principalFieldDataType, principalName);
//
        return null;
    }

    public PreparedStatement createDeleteStatement(final Connection con, final Resource res, final String tableName, final String principalName) throws SQLException, ParseException {
//        final List<AttributeMap> attrMap = attributeMaps(res);
//        if (attrMap == null) {
//            if(log.isDebugEnabled()) {
//                log.debug("Attribute Map is null");
//            }
//            return null;
//        }
//
//        String principalFieldName = null;
//        String principalFieldDataType = null;
//        for (final AttributeMap atr : attrMap) {
//            if (StringUtils.equalsIgnoreCase(atr.getMapForObjectType(), "principal")) {
//                principalFieldName = atr.getAttributeName();
//                principalFieldDataType = atr.getDataType();
//
//            }
//        }
//
//        final String sql = String.format(DELETE_SQL, tableName, principalFieldName);
//
//        if(log.isDebugEnabled()) {
//            log.debug(String.format("SQL: %s", sql));
//        }
//
//        final PreparedStatement statement = con.prepareStatement(sql);
//        setStatement(statement, 1, principalFieldDataType, principalName);

        return null;
    }

    public PreparedStatement createSetPasswordStatement(final Connection con, final Resource res,
                                                        final String tableName, final String principalName,
                                                        final String password) throws SQLException, ParseException {
        String colName = null;
        String colDataType = null;

//        final List<AttributeMap> attrMap = attributeMaps(res);
//        if (attrMap == null) {
//            if(log.isDebugEnabled()) {
//                log.debug("Attribute Map is null");
//            }
//            return null;
//        }
//
//        String principalFieldName = null;
//        String principalFieldDataType = null;
//        for (final AttributeMap atr : attrMap) {
//            if (atr.getDataType() == null) {
//                atr.setDataType("String");
//            }
//
//            final String objectType = atr.getMapForObjectType();
//            if(StringUtils.equalsIgnoreCase(objectType, "password")) {
//                colName = atr.getAttributeName();
//                colDataType = atr.getDataType();
//            }
//
//            if(StringUtils.equalsIgnoreCase(objectType, "principal")) {
//                principalFieldName = atr.getAttributeName();
//                principalFieldDataType = atr.getDataType();
//
//            }
//        }
//
//        final String sql = String.format(UPDATE_SQL, tableName, colName, principalFieldName);
//
//        if(log.isDebugEnabled()) {
//            log.debug(String.format("SQL: %s", sql));
//        }
//
//        final PreparedStatement statement = con.prepareStatement(sql);
//        setStatement(statement, 1, colDataType, password);
//        setStatement(statement, 2, principalFieldDataType, principalName);

        return null;
    }
}
