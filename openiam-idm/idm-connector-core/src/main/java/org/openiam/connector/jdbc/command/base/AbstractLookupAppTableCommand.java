package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.ObjectValue;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.LookupRequest;
import org.openiam.connector.type.response.SearchResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractLookupAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<LookupRequest<ExtObject>, SearchResponse> {
    @Override
    public SearchResponse execute(LookupRequest<ExtObject> lookupRequest) throws ConnectorDataException {
        final SearchResponse response = new SearchResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = lookupRequest.getSearchValue();
        AppTableConfiguration configuration = this.getConfiguration(lookupRequest.getTargetID());
        Connection con = this.getConnection(configuration.getManagedSys());

        final ObjectValue objectValue = new ObjectValue();
        objectValue.setObjectIdentity(principalName);

        try {

            final PreparedStatement statement = createSelectStatement(con, configuration.getResourceId(),
                    this.getTableName(configuration, this.getObjectType()), principalName);
            if (log.isDebugEnabled()) {
                log.debug("Executing lookup query");
            }

            final ResultSet rs = statement.executeQuery();
            final ResultSetMetaData rsMetadata = rs.getMetaData();
            int columnCount = rsMetadata.getColumnCount();

            if (log.isDebugEnabled()) {
                log.debug(String.format("Query contains column count = %s", columnCount));
            }

            if (rs.next()) {
                for (int colIndx = 1; colIndx <= columnCount; colIndx++) {

                    final ExtensibleAttribute extAttr = new ExtensibleAttribute();

                    extAttr.setName(rsMetadata.getColumnName(colIndx));

                    setColumnValue(extAttr, colIndx, rsMetadata, rs);
                    objectValue.getAttributeList().add(extAttr);
                }

                response.getObjectList().add(objectValue);
            } else {
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER, "Principal not found");
            }
            this.closeStatement(statement);
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
            final String tableName, final String principalName) throws ConnectorDataException {
        final List<AttributeMapEntity> attrMap = attributeMaps(resourceId);
        if (attrMap == null)
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, "Attribute Map is null");

        PreparedStatement statement = null;
        try {
            int colCount = 0;
            String principalFieldName = null;
            String principalFieldDataType = null;
            final StringBuilder columnList = new StringBuilder();
            for (AttributeMapEntity atr : attrMap) {
                final String objectType = atr.getMapForObjectType();
                if (compareObjectTypeWithId(objectType)) {
                    principalFieldName = atr.getAttributeName();
                    principalFieldDataType = atr.getDataType().getValue();
                } else if (compareObjectTypeWithObject(objectType)) {
                    if (colCount > 0) {
                        columnList.append(",");
                    }
                    columnList.append(atr.getAttributeName());
                    colCount++;
                }
            }

            final String sql = String.format(SELECT_SQL, columnList, tableName, principalFieldName);
            if (log.isDebugEnabled()) {
                log.debug(String.format("SQL: %s", sql));
            }
            statement = con.prepareStatement(sql);
            setStatement(statement, 1, principalFieldDataType, principalName);
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
            if (rs.getTimestamp(colIndx) != null)
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

    protected abstract boolean compareObjectTypeWithId(String objectType);

}
