package org.openiam.connector.csv.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/2/13
 * Time: 3:34 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCrudCSVCommand<ExtObject extends ExtensibleObject> extends AbstractCSVCommand<CrudRequest<ExtObject>, ObjectResponse>{
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(crudRequest.getTargetID(), ConnectorConfiguration.class);
        ExtObject extensibleObject = crudRequest.getExtensibleObject();
        if (extensibleObject == null) {
            throw new ConnectorDataException(ErrorCode.CSV_ERROR, "Sync object is null");
        }
        this.performObjectOperation(crudRequest.getObjectIdentity(), extensibleObject, config.getManagedSys());

        return response;
    }

    protected abstract void performObjectOperation(String objectIdentity, ExtObject extensibleObject, ManagedSysEntity managedSys)throws ConnectorDataException;
}
