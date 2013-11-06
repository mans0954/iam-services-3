package org.openiam.connector.orcl.command.base;

import java.sql.Connection;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;



@Service("testOracleCommand")
public class TestOracleCommand<ExtObject extends ExtensibleObject> extends AbstractOracleCommand<RequestType<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(RequestType<ExtObject> crudRequest) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(crudRequest.getTargetID(), ConnectorConfiguration.class);
        Connection con = this.getConnection(config.getManagedSys());
        this.closeConnection(con);
        return response;
    }
}
