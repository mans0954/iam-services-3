package org.openiam.connector.gapps.command.base;

import org.openiam.connector.common.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA. User: alexander Date: 8/6/13 Time: 10:49 PM To
 * change this template use File | Settings | File Templates.
 */
public abstract class AbstractCrudGoogleAppsCommand<ExtObject extends ExtensibleObject>
        extends
        AbstractGoogleAppsCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest)
            throws ConnectorDataException {
        ObjectResponse respType = new ObjectResponse();
        respType.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config = getConfiguration(
                crudRequest.getTargetID(), ConnectorConfiguration.class);
        performObjectOperation(crudRequest, config.getManagedSys());
        return respType;
    }


    protected abstract void performObjectOperation(
            CrudRequest<ExtObject> crudRequest, ManagedSysEntity managedSys)
            throws ConnectorDataException;
}
