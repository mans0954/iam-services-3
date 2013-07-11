package org.openiam.spml2.spi.jdbc.command.base;

import org.openiam.idm.srvc.mngsys.domain.AttributeMapEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.jdbc.command.data.AppTableConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class AbstractDeleteAppTableCommand<ProvisionObject extends GenericProvisionObject> extends AbstractAppTableCommand<DeleteRequestType<ProvisionObject>, ResponseType>  {
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType) throws ConnectorDataException {
        final ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);


        final String principalName = deleteRequestType.getPsoID().getID();

        final PSOIdentifierType psoID = deleteRequestType.getPsoID();
        final String targetID = psoID.getTargetID();

        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        AppTableConfiguration configuration = this.getConfiguration(targetID, managedSys);

        Connection con = this.getConnection(managedSys);

        try {

            final PreparedStatement statement = createDeleteStatement(con, configuration.getResourceId(), configuration.getTableName(), principalName);
            statement.executeUpdate();
            return response;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw  e;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.CONNECTOR_ERROR, e.getMessage());
        } finally {
            this.closeConnection(con);
        }
    }

    public PreparedStatement createDeleteStatement(final Connection con, final String resourceId, final String tableName, final String principalName) throws ConnectorDataException {
        PreparedStatement statement = null;
        try{
            final List<AttributeMapEntity> attrMap = attributeMaps(resourceId);
            if (attrMap == null) {
                if(log.isDebugEnabled()) {
                    log.debug("Attribute Map is null");
                }
                return null;
            }

            AttributeMapEntity atr = getAttribute(attrMap);
            String principalFieldName = atr.getAttributeName();
            String principalFieldDataType = atr.getDataType();

            final String sql = String.format(DELETE_SQL, tableName, principalFieldName);

            if(log.isDebugEnabled()) {
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
        }finally {
            this.closeStatement(statement);
        }
    }

    protected abstract AttributeMapEntity getAttribute(List<AttributeMapEntity> attrMap) throws  ConnectorDataException;
}
