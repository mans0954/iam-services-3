package org.openiam.spml2.spi.csv.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;

public abstract class AbstractDeleteCSVCommand <ProvisionObject extends GenericProvisionObject> extends AbstractCSVCommand<DeleteRequestType<ProvisionObject>, ResponseType> {
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType)throws ConnectorDataException {
        ResponseType response = new ResponseType();
        response.setStatus(StatusCodeType.SUCCESS);
        PSOIdentifierType psoID = deleteRequestType.getPsoID();
		/* targetID - */
        String targetID = psoID.getTargetID();

        // Data sent with request - Data must be present in the request per the
        // spec
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        // Initialise
        ProvisionObject provisionObject = deleteRequestType.getProvisionObject();
        if (provisionObject == null) {
            throw new ConnectorDataException(ErrorCode.CSV_ERROR, "Sync object is null");
        }
        deleteObject(psoID.getID(), provisionObject, managedSys);
        return response;
    }

    protected abstract void deleteObject(String id, ProvisionObject object, ManagedSysEntity managedSys)  throws ConnectorDataException;
}
