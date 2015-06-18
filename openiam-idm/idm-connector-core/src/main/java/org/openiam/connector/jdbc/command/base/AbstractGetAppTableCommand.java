package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.BaseAttribute;
import org.openiam.base.BaseAttributeContainer;
import org.openiam.base.BaseProperty;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapDataTypeOptions;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractGetAppTableCommand<ExtObject extends ExtensibleObject, Req extends RequestType, Resp extends ResponseType>
        extends AbstractAppTableCommand<Req, Resp> {

    protected List<ObjectValue> createUserSelectStatement(final Connection con, final String tableName,
                                                          final String principalName, AppTableConfiguration configuration, List<AttributeMapEntity> attrMap,
                                                          String searchQuery) throws ConnectorDataException {
        if (attrMap == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute Map is null");
        List<ObjectValue> objectValList = null;
        List<ExtensibleAttribute> anotherObjects = new ArrayList<ExtensibleAttribute>();
        Set<String> linkedObject = new HashSet<String>();
        PreparedStatement statement = null;
        try {
            int colCount = 0;
            String principalFieldName = null;
            String principalFieldDataType = null;
            final StringBuilder columnList = new StringBuilder();
            for (AttributeMapEntity atr : attrMap) {
                final String objectType = atr.getMapForObjectType();
                if (compareObjectTypeWithId(objectType)) {
                    principalFieldName = atr.getName();
                    principalFieldDataType = atr.getDataType().getValue();
                } else if (compareObjectTypeWithObject(objectType)) {
                    if (PolicyMapDataTypeOptions.MEMBER_OF.equals(atr.getDataType())) {
                        linkedObject.add(atr.getName());
                        continue;
                    }
                    if (colCount > 0) {
                        columnList.append(",");
                    }
                    columnList.append(atr.getName());
                    colCount++;
                }
            }

            String sql = "";
            if (!StringUtils.isEmpty(principalName) && StringUtils.isEmpty(searchQuery)) {
                sql = String.format(SELECT_SQL, columnList, tableName, principalFieldName);
                statement = con.prepareStatement(sql);
                setStatement(statement, 1, principalFieldDataType, principalName);
            } else if (StringUtils.isEmpty(principalName) && !StringUtils.isEmpty(searchQuery)) {
                if ("*".equalsIgnoreCase(searchQuery))
                    sql = String.format(SELECT_ALL_SQL, columnList, tableName);
                else
                    sql = String.format(SELECT_ALL_SQL_QUERY, columnList, tableName, searchQuery);
                statement = con.prepareStatement(sql);
            } else {
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "search params not defined");
            }

            if (log.isDebugEnabled()) {
                log.debug(String.format("SQL: %s", sql));
            }
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                ObjectValue objectVal = new ObjectValue();
                for (String col : Arrays.asList(StringUtils.split(columnList.toString(), ','))) {
                    ExtensibleAttribute ea = new ExtensibleAttribute(col, rs.getString(col));
                    ea.setObjectType("USER");
                    if (objectValList == null) {
                        objectValList = new ArrayList<ObjectValue>();
                        objectVal.setAttributeList(new ArrayList<ExtensibleAttribute>());
                    }
                    objectVal.getAttributeList().add(ea);
                }
                if (objectVal != null) {
                    objectVal.setObjectIdentity(principalName);
                    // linked objects
                    for (String object : linkedObject) {
                        anotherObjects.add(this.selectLinkedObjects(con, configuration, object, principalName,
                                principalFieldDataType, "USER", attrMap));
                    }
                    objectVal.getAttributeList().addAll(anotherObjects);
                }
                objectValList.add(objectVal);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
        return objectValList;
    }

    private ExtensibleAttribute selectLinkedObjects(final Connection con, AppTableConfiguration configuration,
                                                    String targetObjectType, String parentIdValue, String parentIdDataType, String sourceObjectType,
                                                    List<AttributeMapEntity> attrMap) throws ConnectorDataException, SQLException {
        ExtensibleAttribute ea = new ExtensibleAttribute();
        if ("GROUP".equalsIgnoreCase(targetObjectType)) {
            List<String> columnList = new ArrayList<String>();
            String objectNameId = "";
            for (AttributeMapEntity a : attrMap) {
                if (PolicyMapDataTypeOptions.MEMBER_OF.equals(a.getDataType())) {
                    continue;
                } else if (a.getMapForObjectType().equalsIgnoreCase("GROUP_PRINCIPAL")) {
                    objectNameId = a.getName();
                    columnList.add(objectNameId);
                } else if (a.getMapForObjectType().equalsIgnoreCase(targetObjectType)) {
                    columnList.add(a.getName());
                }
            }
            if (StringUtils.isEmpty(objectNameId))
                return ea;

            String membershipTable = null;
            String membershipTableUserId = null;
            String membershipTableGroupId = null;

            // get groups from membership table
            if ("GROUP".equalsIgnoreCase(targetObjectType) && "USER".equalsIgnoreCase(sourceObjectType)) {
                membershipTable = configuration.getUserGroupTableName();
                membershipTableUserId = configuration.getUserGroupTableNameUserId();
                membershipTableGroupId = configuration.getUserGroupTableNameGroupId();
            }
            // get groups from membership table
            if ("GROUP".equalsIgnoreCase(targetObjectType) && "GROUP".equalsIgnoreCase(sourceObjectType)) {
                membershipTable = configuration.getGroupGroupTableName();
                membershipTableUserId = configuration.getGroupGroupTableNameGroupId();
                membershipTableGroupId = configuration.getGroupGroupTableNameGroupChildId();
            }

            if (CollectionUtils.isEmpty(attrMap)
                    || StringUtils.isEmpty(membershipTable) || StringUtils.isEmpty(membershipTableGroupId)
                    || StringUtils.isEmpty(membershipTableUserId))
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute Map is null");


            StringBuilder columnsForSQL = new StringBuilder();
            for (int i = 0; i < columnList.size(); i++) {
                columnsForSQL.append("g." + columnList.get(i).trim());
                if (i < columnList.size() - 1) {
                    columnsForSQL.append(',');
                }
            }
            PreparedStatement statement = null;
            String SQL_SELECT_JOIN = "SELECT %s from %s as g LEFT JOIN %s as m on g.%s = m.%s WHERE m.%s = ?";
            SQL_SELECT_JOIN = String.format(SQL_SELECT_JOIN, columnsForSQL, configuration.getGroupTableName(),
                    membershipTable, objectNameId, membershipTableGroupId, membershipTableUserId);
            statement = con.prepareStatement(SQL_SELECT_JOIN);
            setStatement(statement, 1, parentIdDataType, parentIdValue);

            ResultSet rs = statement.executeQuery();
            ea = new ExtensibleAttribute();
            ea.setName(targetObjectType);
            ea.setValue(targetObjectType);
            ea.setObjectType(targetObjectType);
            BaseAttributeContainer bac = new BaseAttributeContainer();
            bac.setAttributeList(new ArrayList<BaseAttribute>());
            while (rs.next()) {
                BaseAttribute ba = new BaseAttribute(objectNameId, rs.getString(objectNameId));
                ba.setProperties(new ArrayList<BaseProperty>());
                for (String col : columnList) {
                    BaseProperty bp = new BaseProperty();
                    bp.setName(col);
                    bp.setValue(rs.getString("g." + col));
                    ba.getProperties().add(bp);
                }
                bac.getAttributeList().add(ba);
            }
            ea.setAttributeContainer(bac);
        }

        return ea;
    }

    protected abstract boolean compareObjectTypeWithId(String objectType);

}
