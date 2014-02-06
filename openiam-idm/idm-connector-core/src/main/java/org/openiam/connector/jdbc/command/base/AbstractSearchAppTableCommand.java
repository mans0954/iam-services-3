package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.SearchRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.dto.PolicyMapObjectTypeOptions;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractSearchAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<SearchRequest<ExtObject>, SearchResponse> {
    @Override
    public SearchResponse execute(SearchRequest<ExtObject> searchRequest) throws ConnectorDataException {
        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String searchQuery = searchRequest.getSearchQuery();
        AppTableConfiguration configuration = this.getConfiguration(searchRequest.getTargetID());
        Connection con = this.getConnection(configuration.getManagedSys());

        try {

            final PreparedStatement statement = createSelectStatement(con, configuration.getResourceId(),
                    this.getTableName(configuration, this.getObjectType()), searchQuery);
            if (log.isDebugEnabled()) {
                log.debug("Executing lookup query");
            }

            final ResultSet rs = statement.executeQuery();
            final ResultSetMetaData rsMetadata = rs.getMetaData();
            int columnCount = rsMetadata.getColumnCount();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Query contains column count = %s", columnCount));
            }
            while (rs.next()) {
                ObjectValue objectValue = new ObjectValue();
                for (int colIndx = 1; colIndx <= columnCount; colIndx++) {
                    final ExtensibleAttribute extAttr = new ExtensibleAttribute();
                    extAttr.setName(rsMetadata.getColumnName(colIndx));
                    setColumnValue(extAttr, colIndx, rsMetadata, rs);
                    objectValue.getAttributeList().add(extAttr);
                }
                response.getObjectList().add(objectValue);
            }
            statement.close();
            response.setStatus(StatusCodeType.SUCCESS);
            return response;
        } catch (SQLException se) {
            log.error(se.getMessage(), se);
            throw new ConnectorDataException(ErrorCode.SQL_ERROR, se.getMessage());
        } finally {

            this.closeConnection(con);
        }
    }

    private PreparedStatement createSelectStatement(final Connection con, final String resourceId,
            final String tableName, final String searchQuery) throws ConnectorDataException {
        final List<AttributeMapEntity> attrMap = attributeMaps(resourceId);
        if (attrMap == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute Map is null");

        PreparedStatement statement = null;
        try {
            final StringBuilder columnList = new StringBuilder();
            for (AttributeMapEntity atr : attrMap) {
                if (PolicyMapObjectTypeOptions.PRINCIPAL.name().equals(atr.getMapForObjectType())
                        || PolicyMapObjectTypeOptions.USER.name().equals(atr.getMapForObjectType())) {
                    columnList.append(atr.getAttributeName());
                    columnList.append(",");
                }
            }
            columnList.deleteCharAt(columnList.length() - 1);
            String sql = null;
            if (StringUtils.isEmpty(searchQuery) || "*".equals(searchQuery)) {
                sql = String.format(SELECT_ALL_SQL, columnList, tableName);
            } else {
                sql = String.format(SELECT_ALL_SQL_QUERY, columnList, tableName, searchQuery);
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format("SQL: %s", sql));
            }
            statement = con.prepareStatement(sql);
            return statement;

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }

    }

    private void setColumnValue(ExtensibleAttribute extAttr, int colIndx, ResultSetMetaData rsMetadata, ResultSet rs)
            throws SQLException {

        final int fieldType = rsMetadata.getColumnType(colIndx);

        if (log.isDebugEnabled()) {
            log.debug(String.format("column type = %s", fieldType));
        }

        if (fieldType == Types.INTEGER) {
            if (log.isDebugEnabled()) {
                log.debug("type = Integer");
            }
            extAttr.setDataType("INTEGER");
            extAttr.setValue(String.valueOf(rs.getInt(colIndx)));
        }

        if (fieldType == Types.FLOAT || fieldType == Types.NUMERIC) {
            if (log.isDebugEnabled()) {
                log.debug("type = Float");
            }
            extAttr.setDataType("FLOAT");
            extAttr.setValue(String.valueOf(rs.getFloat(colIndx)));

        }

        if (fieldType == Types.DATE) {
            if (log.isDebugEnabled()) {
                log.debug("type = Date");
            }
            extAttr.setDataType("DATE");
            if (rs.getDate(colIndx) != null) {
                extAttr.setValue(String.valueOf(rs.getDate(colIndx).getTime()));
            }

        }
        if (fieldType == Types.TIMESTAMP) {
            if (log.isDebugEnabled()) {
                log.debug("type = Timestamp");
            }
            extAttr.setDataType("TIMESTAMP");
            extAttr.setValue(String.valueOf(rs.getTimestamp(colIndx).getTime()));

        }
        if (fieldType == Types.VARCHAR || fieldType == Types.CHAR) {
            if (log.isDebugEnabled()) {
                log.debug("type = Varchar");
            }
            extAttr.setDataType("STRING");
            if (rs.getString(colIndx) != null) {
                extAttr.setValue(rs.getString(colIndx));
            }

        }
    }

    abstract protected boolean compareObjectTypeWithId(String objectType);
}
