package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractAddAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<CrudRequest<ExtObject>, ObjectResponse> {

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        AppTableConfiguration configuration = this.getConfiguration(crudRequest.getTargetID());

        final String principalName = crudRequest.getObjectIdentity();
        final ExtObject extObject = crudRequest.getExtensibleObject();

        if (log.isDebugEnabled()) {
            log.debug(String.format("ExtensibleObject in Add Request=%s", extObject));
        }
        Connection con = getConnection(configuration.getManagedSys());
        try {
            addObject(con, principalName, extObject, configuration, this.getObjectType());
            return response;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            this.closeConnection(con);
        }
    }
}
