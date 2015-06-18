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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
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
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.synch.dto.Attribute;
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
    protected static final String GROUP = "GROUP";
    protected static final String USER = "USER";


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
            return new ExtensibleObject();

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
            if (type.equalsIgnoreCase(ea.getObjectType()) || PolicyMapObjectTypeOptions.PRINCIPAL.name().equalsIgnoreCase(ea.getObjectType())) {
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
        final String PRINCIPAL_PASSWORD = "PRINCIPAL_PASSWORD";
        final String USER_STATUS_FIELD = "USER_STATUS_FIELD";
        final String USER_STATUS_ACTIVE = "USER_STATUS_ACTIVE";
        final String USER_STATUS_INACTIVE = "USER_STATUS_INACTIVE";
        final String INCLUDE_IN_PASSWORD_SYNC = "INCLUDE_IN_PASSWORD_SYNC";
        final String INCLUDE_IN_STATUS_SYNC = "INCLUDE_IN_STATUS_SYNC";
        final String GROUP_TO_GROUP_PK_GENERATOR = "GROUP_TO_GROUP_PK_COLUMN_NAME";
        final String USER_TO_GROUP_PK_GENERATOR = "USER_TO_GROUP_PK_COLUMN_NAME";

        AppTableConfiguration configuration = super.getConfiguration(targetID, AppTableConfiguration.class);

        final ResourceProp userProp = configuration.getResource().getResourceProperty(USER_TABLE);
        if (userProp == null)
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No TABLE_NAME property found");

        final String tableName = userProp.getValue();
        if (StringUtils.isBlank(tableName))
            throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "TABLE NAME is not defined.");
        configuration.setUserTableName(tableName);
        // additional properties
        final ResourceProp groupProp = configuration.getResource().getResourceProperty(GROUP_TABLE);
        if (groupProp != null)
            configuration.setGroupTableName(groupProp.getValue());
        final ResourceProp roleProp = configuration.getResource().getResourceProperty(ROLE_TABLE);
        if (roleProp != null)
            configuration.setGroupTableName(roleProp.getValue());
        final ResourceProp emailProp = configuration.getResource().getResourceProperty(EMAIL_TABLE);
        if (emailProp != null)
            configuration.setGroupTableName(emailProp.getValue());

        final ResourceProp incudeInPasswordSync = configuration.getResource().getResourceProperty(INCLUDE_IN_PASSWORD_SYNC);
        if (incudeInPasswordSync != null && "Y".equals(incudeInPasswordSync.getValue())) {
            final ResourceProp principalPassword = configuration.getResource().getResourceProperty(PRINCIPAL_PASSWORD);
            if (principalPassword != null)
                configuration.setPrincipalPassword(principalPassword.getValue());
            else {
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No PRINCIPAL_PASSWORD property found");
            }
        } else {
            log.debug("Password will not be synced, set and reset");
        }

        final ResourceProp incudeInStatusSync = configuration.getResource().getResourceProperty(INCLUDE_IN_STATUS_SYNC);
        if (incudeInStatusSync != null && "Y".equals(incudeInStatusSync.getValue())) {

            final ResourceProp userStatus = configuration.getResource().getResourceProperty(USER_STATUS_FIELD);
            if (userStatus != null)
                configuration.setUserStatus(userStatus.getValue());
            else {
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No USER_STATUS property found");
            }

            final ResourceProp userStatusActive = configuration.getResource().getResourceProperty(USER_STATUS_ACTIVE);
            if (userStatusActive != null)
                configuration.setActiveUserStatus(userStatusActive.getValue());
            else {
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No USER_STATUS_ACTIVE property found");
            }

            final ResourceProp userStatusInactive = configuration.getResource().getResourceProperty(USER_STATUS_INACTIVE);
            if (userStatusInactive != null)
                configuration.setInactiveUserStatus(userStatusInactive.getValue());
            else {
                throw new ConnectorDataException(ErrorCode.INVALID_CONFIGURATION, "No USER_STATUS_INACTIVE property found");
            }
        } else {
            log.debug("Status will not be synced. Suspend and resume will not work!");
        }
        final ResourceProp userGroupTName = configuration.getResource().getResourceProperty(USER_GROUP_MEMBERSHIP);
        if (userGroupTName != null)
            configuration.setUserGroupTableName(userGroupTName.getValue());
        final ResourceProp userGroupUserId = configuration.getResource().getResourceProperty(
                USER_GROUP_MEMBERSHIP_USR_ID);
        if (userGroupUserId != null)
            configuration.setUserGroupTableNameUserId(userGroupUserId.getValue());
        final ResourceProp userGroupGroupId = configuration.getResource().getResourceProperty(
                USER_GROUP_MEMBERSHIP_GRP_ID);
        if (userGroupGroupId != null)
            configuration.setUserGroupTableNameGroupId(userGroupGroupId.getValue());

        final ResourceProp groupGroupTName = configuration.getResource().getResourceProperty(GROUP_GROUP_MEMBERSHIP);
        if (groupGroupTName != null)
            configuration.setGroupGroupTableName(groupGroupTName.getValue());

        final ResourceProp groupGroupGrpId = configuration.getResource().getResourceProperty(
                GROUP_GROUP_MEMBERSHIP_GRP_ID);
        if (groupGroupGrpId != null)
            configuration.setGroupGroupTableNameGroupId(groupGroupGrpId.getValue());
        final ResourceProp groupGroupGrpChldId = configuration.getResource().getResourceProperty(
                GROUP_GROUP_MEMBERSHIP_GRP_CHLD_ID);
        if (groupGroupGrpChldId != null)
            configuration.setGroupGroupTableNameGroupChildId(groupGroupGrpChldId.getValue());

        final ResourceProp groupGroupPKGenerator = configuration.getResource().getResourceProperty(
                GROUP_TO_GROUP_PK_GENERATOR);
        if (groupGroupPKGenerator != null)
            configuration.setGroupToGroupPKGenerator(groupGroupPKGenerator.getValue());
        final ResourceProp userGroupPKGenerator = configuration.getResource().getResourceProperty(
                USER_TO_GROUP_PK_GENERATOR);
        if (userGroupPKGenerator != null)
            configuration.setUserToGroupPKGenerator(userGroupPKGenerator.getValue());

        return configuration;
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

    protected PreparedStatement createChangeUserControlParamsStatement(final Connection con, final AppTableConfiguration configuration,
                                                                       final String tableName, final String principalName, final String targetValue, boolean isPasswordIssue) throws ConnectorDataException {
        String colName = null;
        String colDataType = null;

        final List<AttributeMapEntity> attrMap = attributeMaps(configuration.getResourceId());
        if (attrMap == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute Map is null");

        String principalFieldName = null;
        String principalFieldDataType = null;
        String controlParam = isPasswordIssue ? configuration.getPrincipalPassword() : configuration.getUserStatus();
        for (final AttributeMapEntity atr : attrMap) {
            if (atr.getDataType() == null) {
                atr.setDataType(PolicyMapDataTypeOptions.STRING);
            }

            if (StringUtils.equalsIgnoreCase(atr.getName(), controlParam)) {
                colName = atr.getName();
                colDataType = atr.getDataType().getValue();
            }

            if (StringUtils.equalsIgnoreCase(atr.getMapForObjectType(), "principal")) {
                principalFieldName = atr.getName();
                principalFieldDataType = atr.getDataType().getValue();

            }
        }

        final String sql = String.format(UPDATE_SQL, tableName, colName + "=?", principalFieldName);

        if (log.isDebugEnabled()) {
            log.debug(String.format("SQL: %s", sql));
        }

        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement(sql);
            setStatement(statement, 1, colDataType, targetValue);
            setStatement(statement, 2, principalFieldDataType, principalName);
            return statement;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
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
                            && att.getAttributeContainer().getAttributeList() != null) {
                        String supportedObjType = null;
                        for (BaseAttribute a : att.getAttributeContainer().getAttributeList()) {
                            supportedObjType = a.getName();
                            ExtensibleObject ea = this.createNewExtensibleObject(a);
                            this.addObject(con, ea.getObjectId(), ea, config, supportedObjType);

                        }
                        if (supportedObjType == null) {
                            this.deleteMemberShip(con, config, principalName, objectType);
                        } else {
                            this.manageMemberShip(con, config, principalName, objectType, att.getAttributeContainer().getAttributeList(),
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
                    if (a.getAttributeContainer() == null && a.getObjectType().equalsIgnoreCase(objectType)) {
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
                                && att.getAttributeContainer().getAttributeList() != null) {
                            String supportedObjType = null;
                            for (BaseAttribute a : att.getAttributeContainer().getAttributeList()) {
                                supportedObjType = a.getName();
                                ExtensibleObject ea = this.createNewExtensibleObject(a);
                                this.modifyObject(con, ea.getObjectId(), ea, config, supportedObjType);
                            }
                            if (supportedObjType == null) {
                                this.deleteMemberShip(con, config, principalName, objectType);
                            } else {
                                this.manageMemberShip(con, config, principalName, objectType, att.getAttributeContainer().getAttributeList(),
                                        supportedObjType);
                            }
                        } else {
                            if (ctr != 0) {
                                columns.append(",");
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
                        if (a.getAttributeContainer() == null && a.getObjectType().equalsIgnoreCase(objectType)) {
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

    protected void manageMemberShip(Connection con, AppTableConfiguration config, String parentId,
                                    String parentObjectType, List<BaseAttribute> childs, String childObjectType) throws Exception {
        if (childs == null) {
            log.debug("No any linked entities");
            return;
        }
        String membershipTable = null;
        String membershipUserColumn = null;
        String membershipGroupColumn = null;
        String pkMembershipName = null;
        if (USER.equalsIgnoreCase(parentObjectType)
                && GROUP.equalsIgnoreCase(childObjectType)) {
            membershipTable = config.getUserGroupTableName();
            membershipUserColumn = config.getUserGroupTableNameUserId();
            membershipGroupColumn = config.getUserGroupTableNameGroupId();
            pkMembershipName = config.getUserToGroupPKGenerator();
        }

        if (GROUP.equalsIgnoreCase(parentObjectType)
                && GROUP.equalsIgnoreCase(childObjectType)) {
            membershipTable = config.getGroupGroupTableName();
            membershipUserColumn = config.getGroupGroupTableNameGroupId();
            membershipGroupColumn = config.getGroupGroupTableNameGroupChildId();
            pkMembershipName = config.getUserToGroupPKGenerator();
        }

        List<String> childIds = new ArrayList<>();
        for (BaseAttribute ba : childs) {
            childIds.add(createNewExtensibleObject(ba).getObjectId());
        }

        if (!StringUtils.isEmpty(membershipTable) && !StringUtils.isEmpty(membershipUserColumn)
                && !StringUtils.isEmpty(membershipGroupColumn) && !CollectionUtils.isEmpty(childIds)
                && !StringUtils.isEmpty(parentId)) {
            PreparedStatement ps = null;
            // select all linked groups
            String selectLindedGroupdIdsSQL = "select %s from %s where %s = ? ";
            selectLindedGroupdIdsSQL = String.format(selectLindedGroupdIdsSQL, membershipGroupColumn, membershipTable, membershipUserColumn);
            ps = con.prepareStatement(selectLindedGroupdIdsSQL);
            ps.setString(1, parentId);
            ResultSet rs = ps.executeQuery();
            List<String> groupIdsFromTargetSystem = new ArrayList<>();
            while (rs.next()) {
                groupIdsFromTargetSystem.add(rs.getString(1));
            }
            Iterator<String> groupIdFromTargetSystemIterator = groupIdsFromTargetSystem.iterator();
            while (groupIdFromTargetSystemIterator.hasNext()) {
                String groupIdFromTargetSystem = groupIdFromTargetSystemIterator.next();
                if (childIds.contains(groupIdFromTargetSystem)) {
                    //link existed
                    childIds.remove(groupIdFromTargetSystem);
                    groupIdFromTargetSystemIterator.remove();
                }
            }

            String sql = null;
            if (CollectionUtils.isNotEmpty(childIds)) {
                //add from childIds
                sql = null;
                boolean isUsePK = false;
                if (StringUtils.isNotBlank(pkMembershipName)) {
                    sql = "INSERT INTO %s (%s,%s,%s) VALUES (?,?,?)";
                    isUsePK = true;
                } else {
                    sql = "INSERT INTO %s (%s,%s) VALUES (?,?)";
                }
                String sqlPrepared = null;
                try {
                    for (String childId : childIds) {
                        if (isUsePK) {
                            sqlPrepared = String.format(sql, membershipTable, membershipUserColumn, membershipGroupColumn, pkMembershipName);
                        } else {
                            sqlPrepared = String.format(sql, membershipTable, membershipUserColumn, membershipGroupColumn);

                        }
                        ps = con.prepareStatement(sqlPrepared);
                        ps.setString(1, parentId);
                        ps.setString(2, childId);
                        if (isUsePK) {
                            ps.setString(3, parentId + "_" + childId);
                        }
                        ps.executeUpdate();
                    }
                } catch (Exception e) {
                    log.error("Exception during add Group to User");
                    log.error(e);
                    throw new ConnectorDataException(ErrorCode.SQL_ERROR, e);
                }
            }
            //delete from groupIdsFromTargetSystem
            if (CollectionUtils.isNotEmpty(groupIdsFromTargetSystem)) {
                sql = "DELETE FROM %s WHERE %s=? AND ";
                StringBuilder grouptIdsClause = new StringBuilder();
                grouptIdsClause.append("(");
                for (int i = 0; i < groupIdsFromTargetSystem.size(); i++) {
                    grouptIdsClause.append(membershipGroupColumn);
                    grouptIdsClause.append("=?");
                    if (i != groupIdsFromTargetSystem.size() - 1) {
                        grouptIdsClause.append(" OR ");
                    }
                }
                grouptIdsClause.append(")");
                sql += grouptIdsClause.toString();

                log.debug("SQL CLAUSE TO DELETE MAMBERSHIP OF " + childObjectType + "=" + sql);
                // check is exist
                sql = String.format(sql, membershipTable, membershipUserColumn);
                ps = con.prepareStatement(sql);
                ps.setString(1, parentId);
                int ctr = 2;
                for (String childId : groupIdsFromTargetSystem) {
                    ps.setString(ctr++, childId);
                }
                try {
                    ps.executeUpdate();
                } catch (Exception e) {
                    log.error(String.format("Exception during delete %s from %s", childObjectType, parentObjectType));
                    log.error(e);
                    throw new ConnectorDataException(ErrorCode.SQL_ERROR, e);
                }
            }

        }
    }

    protected void deleteMemberShip(Connection con, AppTableConfiguration config, String parentId,
                                    String parentObjectType) throws Exception {

        if (StringUtils.isEmpty(parentId) || StringUtils.isEmpty(parentObjectType))
            return;

        String membershipTable = null;
        String membershipKeyDeletionColumn = null;
        String membershipKeyChildDeletionColumn = null;
        if (USER.equalsIgnoreCase(parentObjectType)) {
            membershipTable = config.getUserGroupTableName();
            membershipKeyDeletionColumn = config.getUserGroupTableNameUserId();
        }

        if (GROUP.equalsIgnoreCase(parentObjectType)) {
            membershipTable = config.getGroupGroupTableName();
            membershipKeyDeletionColumn = config.getGroupGroupTableNameGroupId();
            membershipKeyChildDeletionColumn = config.getGroupGroupTableNameGroupChildId();
        }
        deleteStatement(con, parentId, membershipTable, membershipKeyDeletionColumn);
        deleteStatement(con, parentId, membershipTable, membershipKeyChildDeletionColumn);
    }

    private void deleteStatement(Connection con, String parentId, String membershipTable, String membershipKeyDeletionColumn) throws SQLException {
        if (!StringUtils.isEmpty(membershipTable) && !StringUtils.isEmpty(membershipKeyDeletionColumn)) {
            String sql = "DELETE FROM %s WHERE %s = ?";
            sql = String.format(sql, membershipTable, membershipKeyDeletionColumn);
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, parentId);
            ps.executeUpdate();
        }
    }
}
