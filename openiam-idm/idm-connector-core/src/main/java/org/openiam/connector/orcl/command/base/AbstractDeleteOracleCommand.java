package org.openiam.connector.orcl.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.constant.StatusCodeType;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;

import java.sql.Connection;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 7/12/13
 * Time: 12:27 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractDeleteOracleCommand<ExtObject extends ExtensibleObject> extends AbstractOracleCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> deleteRequestType) throws ConnectorDataException {
        final ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        final String dataId = deleteRequestType.getObjectIdentity();
        ConnectorConfiguration config =  getConfiguration(deleteRequestType.getTargetID(), ConnectorConfiguration.class);
        Connection con = getConnection(config.getManagedSys());
        try {
            deleteObject(dataId, con);
            return response;
        }  catch(Throwable e) {
            log.error(e.getMessage(),e);
            throw  new ConnectorDataException(ErrorCode.OTHER_ERROR, e.getMessage());
        } finally {
           this.closeConnection(con);
        }
    }

    protected abstract void deleteObject(String dataId,  Connection con)throws ConnectorDataException;
}
