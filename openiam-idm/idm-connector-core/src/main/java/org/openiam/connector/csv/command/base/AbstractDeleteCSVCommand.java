package org.openiam.connector.csv.command.base;

import org.openiam.connector.data.ConnectorConfiguration;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;

public abstract class AbstractDeleteCSVCommand<ExtObject extends ExtensibleObject> extends AbstractCSVCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> deleteRequest)throws ConnectorDataException {
        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

		/* targetID - */
        String targetID = deleteRequest.getTargetID();
        ConnectorConfiguration config =  getConfiguration(deleteRequest.getTargetID(), ConnectorConfiguration.class);

        // Initialise
        ExtObject extensibleObject = deleteRequest.getExtensibleObject();
        if (extensibleObject == null) {
            throw new ConnectorDataException(ErrorCode.CSV_ERROR, "Sync object is null");
        }
        deleteObject(deleteRequest.getObjectIdentity(), extensibleObject, config.getManagedSys());
        return response;
    }

    protected abstract void deleteObject(String id, ExtObject object, ManagedSysEntity managedSys)  throws ConnectorDataException;
}
