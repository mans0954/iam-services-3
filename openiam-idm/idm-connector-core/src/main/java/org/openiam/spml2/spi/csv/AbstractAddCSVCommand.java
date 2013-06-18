package org.openiam.spml2.spi.csv;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;

public abstract class AbstractAddCSVCommand<ProvisionObject extends GenericProvisionObject> extends AbstractCSVCommand<AddRequestType<ProvisionObject>, AddResponseType>{

    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException{
        AddResponseType response = new AddResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
        PSOIdentifierType psoID = addRequestType.getPsoID();
        String targetID = addRequestType.getTargetID();
        ManagedSysEntity managedSys = managedSysService
                .getManagedSysById(targetID);

        ProvisionObject provisionObject = addRequestType.getProvisionObject();
        if (provisionObject == null) {
            throw new ConnectorDataException(ErrorCode.CSV_ERROR, "Sync object is null");
        }
        this.addObjectToCsv(psoID.getID(), provisionObject, managedSys);
        return response;
    }

    protected  abstract void addObjectToCsv(String id, ProvisionObject object,  ManagedSysEntity managedSys) throws ConnectorDataException;
}
