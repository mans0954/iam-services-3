package org.openiam.connector.jdbc.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.jdbc.command.data.AppTableConfiguration;

import java.sql.Connection;
import java.util.List;

public abstract class AbstractAddAppTableCommand<ExtObject extends ExtensibleObject> extends AbstractAppTableCommand<CrudRequest<ExtObject>, ObjectResponse> {
    protected static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s)";

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(crudRequest.getTargetID());

        final String principalName = crudRequest.getObjectIdentity();
        final ExtObject extObject = crudRequest.getExtensibleObject();

        if(log.isDebugEnabled()) {
            log.debug(String.format("ExtensibleObject in Add Request=%s", extObject));
        }

        Connection con = getConnection(configuration.getManagedSys());
        try {
            addObject(con, principalName, extObject, configuration.getTableName());
            return response;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(),e);
            throw e;
        } finally {
            this.closeConnection(con);
        }
    }

    protected abstract void addObject(Connection con, String principalName,  ExtObject extObject, String tableName) throws ConnectorDataException;
}
