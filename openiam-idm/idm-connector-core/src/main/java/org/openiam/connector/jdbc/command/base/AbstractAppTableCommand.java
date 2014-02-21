package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseProperty;
import org.openiam.connector.common.jdbc.AbstractJDBCCommand;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapDataTypeOptions;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractAppTableCommand<Request extends RequestType, Response extends ResponseType> extends
        AbstractJDBCCommand<Request, Response> {
    protected static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s)";
    protected static final String DATE_FORMAT = "MM/dd/yyyy";
    protected static final String SELECT_SQL = "SELECT %s FROM %s WHERE %s=?";
    protected static final String SELECT_ALL_SQL = "SELECT %s FROM %s";
    protected static final String SELECT_ALL_SQL_QUERY = "SELECT %s FROM %s WHERE %s";
    protected static final String DELETE_SQL = "DELETE FROM %s WHERE %s=?";
    protected static final String UPDATE_SQL = "UPDATE %s SET %s WHERE %s=?";

    protected String getTableName(AppTableConfiguration config, String objectType) throws ConnectorDataException {
        String result = "";
        switch (objectType.toLowerCase()) {
        case "user":
            result = config.getUserTableName();
            break;
        case "group":
            result = config.getGroupTableName();
            break;
        case "role":
            result = config.getRoleTableName();
            break;
        case "email":
            result = config.getEmailTableName();
            break;
        default:
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR);
        }
        return result;
    }

    public ExtensibleObject createNewExtensibleObject(BaseAttribute ba) {
        if (ba == null || CollectionUtils.isEmpty(ba.getProperties()))
            return null;

        ExtensibleObject newEO = new ExtensibleObject();
        newEO.setAttributes(new ArrayList<ExtensibleAttribute>());
        for (BaseProperty prop : ba.getProperties()) {
            if ("1".equals(this.getAttribute(prop.getAttribute(), "principal"))) {
                newEO.setObjectId(prop.getValue());
                newEO.setPrincipalFieldName(prop.getName());
                newEO.setPrincipalFieldDataType(this.getAttribute(prop.getAttribute(), "dataType"));
            } else {
                ExtensibleAttribute ea = new ExtensibleAttribute(prop.getName(), prop.getValue());
                ea.setDataType(this.getAttribute(prop.getAttribute(), "dataType"));
                ea.setObjectType(ba.getName());
                newEO.getAttributes().add(ea);
            }
        }
        return newEO;
    }

    public String getAttribute(String attributes, String key) {
        String res = "";
        String[] attrs = StringUtils.split(attributes, ';');
        if (attrs != null || attrs.length > 0) {
            for (String attr : attrs) {
                if (attr.toLowerCase().contains(key.toLowerCase())) {
                    String[] values = StringUtils.split(attr, '=');
                    if (values != null || values.length == 2) {
                        res = values[1].trim();
                    }
                }
            }
        }
        return res;
    }

    protected abstract String getObjectType();

    protected boolean compareObjectTypeWithObject(String objectType) {
        return StringUtils.equalsIgnoreCase(objectType, this.getObjectType());
    }

    protected List<ExtensibleAttribute> getExtensibleAttributeByType(String type, List<ExtensibleAttribute> attrList) {
        if (attrList == null || StringUtils.isEmpty(type))
            return null;
        List<ExtensibleAttribute> list = new ArrayList<ExtensibleAttribute>();
        for (ExtensibleAttribute ea : attrList) {
            if (type.equalsIgnoreCase(ea.getObjectType()) || "PRINCIPAL".equalsIgnoreCase(ea.getObjectType())) {
                list.add(ea);
            }
        }
        return list;

    }

    protected AppTableConfiguration getConfiguration(String targetID) throws ConnectorDataException {
        final String USER_TABLE = "USER_TABLE";
        final String GROUP_TABLE = "GROUP_TABLE";
        final String USER_GROUP_MEMBERSHIP = "USER_GROUP_MEMBERSHIP";
        final String GROUP_GROUP_MEMBERSHIP = "GROUP_GROUP_MEMBERSHIP";
        final String GROUP_GROUP_MEMBERSHIP_GRP_ID = "GROUP_GROUP_MEMBERSHIP_GRP_ID";
        final String GROUP_GROUP_MEMBERSHIP_GRP_CHLD_ID = "GROUP_GROUP_MEMBERSHIP_GRP_CHLD_ID";
        final String USER_GROUP_MEMBERSHIP_GRP_ID = "USER_GROUP_MEMBERSHIP_GRP_ID";
        final String USER_GROUP_MEMBERSHIP_USR_ID = "USER_GROUP_MEMBERSHIP_USR_ID";
        final String ROLE_TABLE = "ROLE_TABLE";
        final String EMAIL_TABLE = "EMAIL_TABLE";
        AppTableConfiguration configuration = super.getConfiguration(targetID, AppTableConfiguration.class);

        final ResourceProp userProp = configuration.getResource().getResourceProperty(USER_TABLE);
        if (userProp == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No TABLE_NAME property found");

        final String tableName = userProp.getPropValue();
        if (StringUtils.isBlank(tableName))
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "TABLE NAME is not defined.");
        configuration.setUserTableName(tableName);
        // additional properties
        final ResourceProp groupProp = configuration.getResource().getResourceProperty(GROUP_TABLE);
        if (groupProp != null)
            configuration.setGroupTableName(groupProp.getPropValue());
        final ResourceProp roleProp = configuration.getResource().getResourceProperty(ROLE_TABLE);
        if (roleProp != null)
            configuration.setGroupTableName(roleProp.getPropValue());
        final ResourceProp emailProp = configuration.getResource().getResourceProperty(EMAIL_TABLE);
        if (emailProp != null)
            configuration.setGroupTableName(emailProp.getPropValue());

        final ResourceProp userGroupTName = configuration.getResource().getResourceProperty(USER_GROUP_MEMBERSHIP);
        if (userGroupTName != null)
            configuration.setUserGroupTableName(userGroupTName.getPropValue());
        final ResourceProp userGroupUserId = configuration.getResource().getResourceProperty(
                USER_GROUP_MEMBERSHIP_USR_ID);
        if (userGroupUserId != null)
            configuration.setUserGroupTableNameUserId(userGroupUserId.getPropValue());
        final ResourceProp userGroupGroupId = configuration.getResource().getResourceProperty(
                USER_GROUP_MEMBERSHIP_GRP_ID);
        if (userGroupGroupId != null)
            configuration.setUserGroupTableNameGroupId(userGroupGroupId.getPropValue());

        final ResourceProp groupGroupTName = configuration.getResource().getResourceProperty(GROUP_GROUP_MEMBERSHIP);
        if (groupGroupTName != null)
            configuration.setGroupGroupTableName(groupGroupTName.getPropValue());

        final ResourceProp groupGroupGrpId = configuration.getResource().getResourceProperty(
                GROUP_GROUP_MEMBERSHIP_GRP_ID);
        if (groupGroupGrpId != null)
            configuration.setGroupGroupTableNameGroupId(groupGroupGrpId.getPropValue());
        final ResourceProp groupGroupGrpChldId = configuration.getResource().getResourceProperty(
                GROUP_GROUP_MEMBERSHIP_GRP_CHLD_ID);
        if (groupGroupGrpChldId != null)
            configuration.setGroupGroupTableNameGroupChildId(groupGroupGrpChldId.getPropValue());

        return configuration;
    }

    protected void setStatement(PreparedStatement statement, int column, ExtensibleAttribute att)
            throws ConnectorDataException {
        final String dataType = att.getDataType();
        final String dataValue = att.getValue();
        setStatement(statement, column, dataType, dataValue);
    }

    protected void setStatement(PreparedStatement statement, int column, String dataType, String value)
            throws ConnectorDataException {
        try {
            if (StringUtils.equalsIgnoreCase(dataType, "date")) {
                final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                final java.util.Date d;
                d = sdf.parse(value);
                // get the date into a java.sql.Date
                statement.setDate(column, new Date(d.getTime()));
            }
            if (StringUtils.equalsIgnoreCase(dataType, "integer") || StringUtils.equalsIgnoreCase(dataType, "int")) {
                statement.setInt(column, Integer.valueOf(value));
            }

            if (StringUtils.equalsIgnoreCase(dataType, "float")) {
                statement.setFloat(column, Float.valueOf(value));
            }

            if (StringUtils.equalsIgnoreCase(dataType, "string")) {
                statement.setString(column, value);
            }

            if (StringUtils.equalsIgnoreCase(dataType, "timestamp")) {
                statement.setTimestamp(column, Timestamp.valueOf(value));
            }
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }

    protected boolean identityExists(final Connection con, final String tableName, final String principalName,
            final ExtensibleObject obj) throws ConnectorDataException {

        PreparedStatement statement = null;
        final String principalFieldName = obj.getPrincipalFieldName();
        final String principalFieldDataType = obj.getPrincipalFieldDataType();

        final String sql = String.format(SELECT_SQL, principalFieldName, tableName, principalFieldName);

        if (log.isDebugEnabled()) {
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
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, se.getMessage());
        } finally {
            this.closeStatement(statement);
        }
        return false;
    }

    protected PreparedStatement createSetPasswordStatement(final Connection con, final String resourceId,
            final String tableName, final String principalName, final String password) throws ConnectorDataException {
        String colName = null;
        String colDataType = null;

        final List<AttributeMapEntity> attrMap = attributeMaps(resourceId);
        if (attrMap == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute Map is null");

        String principalFieldName = null;
        String principalFieldDataType = null;
        for (final AttributeMapEntity atr : attrMap) {
            if (atr.getDataType() == null) {
                atr.setDataType(PolicyMapDataTypeOptions.STRING);
            }

            final String objectType = atr.getMapForObjectType();
            if (StringUtils.equalsIgnoreCase(objectType, "password")) {
                colName = atr.getAttributeName();
                colDataType = atr.getDataType().getValue();
            }

            if (StringUtils.equalsIgnoreCase(objectType, "principal")) {
                principalFieldName = atr.getAttributeName();
                principalFieldDataType = atr.getDataType().getValue();

            }
        }

        final String sql = String.format(UPDATE_SQL, tableName, colName, principalFieldName);

        if (log.isDebugEnabled()) {
            log.debug(String.format("SQL: %s", sql));
        }

        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement(sql);
            setStatement(statement, 1, colDataType, password);
            setStatement(statement, 2, principalFieldDataType, principalName);
            return statement;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            this.closeStatement(statement);
        }
    }

    protected boolean addObject(Connection con, String principalName, ExtensibleObject object,
            AppTableConfiguration config, String objectType) throws ConnectorDataException {
        // build sql
        final StringBuilder columns = new StringBuilder("");
        final StringBuilder values = new StringBuilder("");
        String sql = "";
        int ctr = 0;
        final List<ExtensibleAttribute> attrList = object.getAttributes();
        if (!CollectionUtils.isEmpty(attrList) && !StringUtils.isEmpty(this.getTableName(config, objectType))) {
            try {
                if (identityExists(con, this.getTableName(config, objectType), principalName, object)) {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("%s exists. Returning success to the connector", principalName));
                    }
                    return false;
                }
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Number of attributes to persist in ADD = %s", attrList.size()));
                }

                for (final ExtensibleAttribute att : attrList) {
                    if (att.getAttributeContainer() != null
                            && !CollectionUtils.isEmpty(att.getAttributeContainer().getAttributeList())) {
                        for (BaseAttribute a : att.getAttributeContainer().getAttributeList()) {
                            String supportedObjType = a.getName();
                            ExtensibleObject ea = this.createNewExtensibleObject(a);
                            this.addObject(con, ea.getObjectId(), ea, config, supportedObjType);
                            this.createMemberShip(con, config, principalName, objectType, ea.getObjectId(),
                                    supportedObjType);

                        }
                    } else {
                        if (ctr != 0) {
                            columns.append(",");
                            values.append(",");
                        }
                        ctr++;
                        columns.append(att.getName());
                        values.append("?");
                    }
                }
                // add the primary key

                if (log.isDebugEnabled()) {
                    log.debug(String.format("Principal column name=%s", principalName));
                }
                if (object.getPrincipalFieldName() != null) {
                    if (ctr != 0) {
                        columns.append(",");
                        values.append(",");
                    }
                    columns.append(object.getPrincipalFieldName());
                    values.append("?");
                }

                sql = String.format(INSERT_SQL, this.getTableName(config, objectType), columns, values);

                if (log.isDebugEnabled()) {
                    log.debug(String.format("ADD SQL=%s", sql));
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
            PreparedStatement statement = null;
            try {
                statement = con.prepareStatement(sql);
                // set the parameters
                int counter = 1;
                for (ExtensibleAttribute a : attrList) {
                    if (a.getObjectType().equalsIgnoreCase(objectType)) {
                        setStatement(statement, counter++, a.getDataType(), a.getValue());
                    }
                }
                if (object.getPrincipalFieldName() != null) {
                    setStatement(statement, ctr + 1, object.getPrincipalFieldDataType(), principalName);
                }
                statement.executeUpdate();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            } finally {
                this.closeStatement(statement);
            }
        }
        return true;
    }

    protected void modifyObject(Connection con, String principalName, ExtensibleObject object,
            AppTableConfiguration config, String objectType) throws ConnectorDataException {
        // build sql
        final StringBuilder columns = new StringBuilder("");
        final StringBuilder values = new StringBuilder("");
        String sql = "";
        int ctr = 0;
        final List<ExtensibleAttribute> attrList = object.getAttributes();
        if (!CollectionUtils.isEmpty(attrList) && !StringUtils.isEmpty(this.getTableName(config, objectType))) {
            try {
                if (identityExists(con, this.getTableName(config, objectType), principalName, object)) {
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("%s exists. Returning success to the connector", principalName));
                    }

                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Number of attributes to persist in ADD = %s", attrList.size()));
                    }

                    for (final ExtensibleAttribute att : attrList) {
                        if (att.getAttributeContainer() != null
                                && !CollectionUtils.isEmpty(att.getAttributeContainer().getAttributeList())) {
                            for (BaseAttribute a : att.getAttributeContainer().getAttributeList()) {
                                String supportedObjType = a.getName();
                                ExtensibleObject ea = this.createNewExtensibleObject(a);
                                this.modifyObject(con, ea.getObjectId(), ea, config, supportedObjType);
                            }
                        } else {
                            if (ctr != 0) {
                                columns.append(",");
                                values.append(",");
                            }
                            ctr++;
                            columns.append(att.getName() + "= ?");
                        }
                    }
                    // add the primary key

                    if (log.isDebugEnabled()) {
                        log.debug(String.format("Principal column name=%s", principalName));
                    }

                    sql = String.format(UPDATE_SQL, this.getTableName(config, objectType), columns,
                            object.getPrincipalFieldName());

                    PreparedStatement statement = con.prepareStatement(sql);

                    int counter = 1;
                    for (ExtensibleAttribute a : attrList) {
                        if (a.getObjectType().equalsIgnoreCase(objectType)) {
                            setStatement(statement, counter++, a.getDataType(), a.getValue());
                        }
                    }
                    if (object.getPrincipalFieldName() != null) {
                        setStatement(statement, ctr + 1, object.getPrincipalFieldDataType(), principalName);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug(String.format("UPDATE SQL=%s", sql));
                    }
                    statement.executeUpdate();
                } else {
                    this.addObject(con, principalName, object, config, objectType);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
        }
    }

    protected void createMemberShip(Connection con, AppTableConfiguration config, String parentId,
            String parentObjectType, String childId, String childObjectType) throws Exception {
        String membershipTable = config.getUserGroupTableName();
        String membershipUserColumn = config.getUserGroupTableNameUserId();
        String membershipGroupColumn = config.getUserGroupTableNameGroupId();
        if (!StringUtils.isEmpty(membershipTable) && !StringUtils.isEmpty(membershipUserColumn)
                && !StringUtils.isEmpty(membershipGroupColumn) && !StringUtils.isEmpty(childId)
                && !StringUtils.isEmpty(parentId) && "USER".equalsIgnoreCase(parentObjectType)
                && "GROUP".equalsIgnoreCase(childObjectType)) {
            PreparedStatement ps = null;
            // check is exist
            String selectSQL = "select count(*) from %s where %s = ? and %s = ?";
            selectSQL = String.format(selectSQL, membershipTable, membershipUserColumn, membershipGroupColumn);
            ps = con.prepareStatement(selectSQL);
            ps.setString(1, parentId);
            ps.setString(2, childId);
            ResultSet rs = ps.executeQuery();
            boolean isExist = false;
            while (rs.next()) {
                isExist = rs.getInt(1) > 0;
            }
            if (!isExist) {
                String sql = "INSERT INTO %s (%s,%s) VALUES (?,?)";
                sql = String.format(sql, membershipTable, membershipUserColumn, membershipGroupColumn);
                ps = con.prepareStatement(sql);
                ps.setString(1, parentId);
                ps.setString(2, childId);
                ps.executeUpdate();
            }
        }
    }

    protected void deleteMemberShip(Connection con, AppTableConfiguration config, String parentId,
            String parentObjectType) throws Exception {
        String membershipTable = config.getUserGroupTableName();
        String membershipUserColumn = config.getUserGroupTableNameUserId();
        if (!StringUtils.isEmpty(membershipTable) && !StringUtils.isEmpty(membershipUserColumn)
                && !StringUtils.isEmpty(parentId) && "USER".equalsIgnoreCase(parentObjectType)) {
            String sql = "DELETE FROM %s WHERE %s = ?";
            sql = String.format(sql, membershipTable, membershipUserColumn);
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, parentId);
            ps.executeUpdate();
        }
    }
}
