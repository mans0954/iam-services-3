package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractModifyAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<CrudRequest<ExtObject>, ObjectResponse> {
    private static final String UPDATE_SQL = "UPDATE %s SET %s WHERE %s=?";
    private static final String INSERT_SQL = "INSERT INTO %s (%s) VALUES (%s)";

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(crudRequest.getTargetID());

        final String principalName = crudRequest.getObjectIdentity();
        Connection con = this.getConnection(configuration.getManagedSys());
        final ExtensibleObject obj = crudRequest.getExtensibleObject();

        try {
            this.modifyObject(con, principalName, obj, configuration, this.getObjectType());

            return response;
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
            throw new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
            this.closeConnection(con);
        }

    }

}
