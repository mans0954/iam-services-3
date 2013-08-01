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

public abstract class AbstractAddCSVCommand<ExtObject extends ExtensibleObject> extends AbstractCSVCommand<CrudRequest<ExtObject>, ObjectResponse>{

    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> addRequest) throws ConnectorDataException {
        ObjectResponse response = new ObjectResponse();
        response.setStatus(StatusCodeType.SUCCESS);

        ConnectorConfiguration config =  getConfiguration(addRequest.getTargetID(), ConnectorConfiguration.class);
        ExtObject extensibleObject = addRequest.getExtensibleObject();
        if (extensibleObject == null) {
            throw new ConnectorDataException(ErrorCode.CSV_ERROR, "Sync object is null");
        }
        this.addObjectToCsv(addRequest.getObjectIdentity(), extensibleObject, config.getManagedSys());

        return response;
    }

    protected  abstract void addObjectToCsv(String id, ExtObject object,  ManagedSysEntity managedSys) throws ConnectorDataException;
}
