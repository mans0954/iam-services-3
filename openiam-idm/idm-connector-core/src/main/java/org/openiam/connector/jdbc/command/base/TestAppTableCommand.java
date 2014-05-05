package org.openiam.connector.jdbc.command.base;

import java.sql.Connection;

import org.openiam.connector.jdbc.command.data.AppTableConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

@Service("testUserAppTableCommand")
public class TestAppTableCommand<ExtObject extends ExtensibleObject> extends
        AbstractAppTableCommand<RequestType<ExtObject>, ResponseType> {
    private final String TEST_QUERY = "SELECT * FROM %s";

    @Override
    public ResponseType execute(RequestType<ExtObject> request) throws ConnectorDataException {
        ResponseType response = new ResponseType();
        ManagedSysEntity mSys = managedSysService.getManagedSysById(request.getTargetID());
        AppTableConfiguration configuration = super.getConfiguration(request.getTargetID());
        try {
            Connection con = this.getConnection(mSys);
            String sql = String.format(TEST_QUERY, this.getTableName(configuration, this.getObjectType()));
            con.prepareStatement(sql).executeQuery();
            response.setStatus(StatusCodeType.SUCCESS);
        } catch (Exception e) {
            response.setStatus(StatusCodeType.FAILURE);
        }
        return response;
    }

    @Override
    protected String getObjectType() {
        return "USER";
    }
}
