package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractDeleteAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<CrudRequest<ExtObject>, ObjectResponse> {

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String principalName = crudRequest.getObjectIdentity();
        AppTableConfiguration configuration = this.getConfiguration(crudRequest.getTargetID());
        Connection con = this.getConnection(configuration.getManagedSys());

        try {
            try {
                this.deleteMemberShip(con, configuration, principalName, this.getObjectType());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
            }
            final PreparedStatement statement = createDeleteStatement(con, configuration.getResourceId(),
                    this.getTableName(configuration, this.getObjectType()), principalName);
            statement.executeUpdate();

            this.closeStatement(statement);
            return response;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {

            this.closeConnection(con);
        }
    }

    public PreparedStatement createDeleteStatement(final Connection con, final String resourceId,
                                                   final String tableName, final String principalName) throws ConnectorDataException {
        PreparedStatement statement = null;
        try {
            final List<AttributeMapEntity> attrMap = attributeMaps(resourceId);
            if (attrMap == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Attribute Map is null");
                }
                return null;
            }

            AttributeMapEntity atr = getAttribute(attrMap);
            String principalFieldName = atr.getName();
            String principalFieldDataType = atr.getDataType().getValue();

            final String sql = String.format(DELETE_SQL, tableName, principalFieldName);

            if (log.isDebugEnabled()) {
                log.debug(String.format("SQL: %s", sql));
            }

            statement = con.prepareStatement(sql);
            setStatement(statement, 1, principalFieldDataType, principalName);
            return statement;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        }
    }

    protected abstract AttributeMapEntity getAttribute(List<AttributeMapEntity> attrMap) throws ConnectorDataException;
}
