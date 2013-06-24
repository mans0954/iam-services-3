package org.openiam.spml2.spi.csv.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.recon.dto.ReconciliationObject;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;

public abstract class AbstractModifyCSVCommand <ProvisionObject extends GenericProvisionObject> extends AbstractCSVCommand<ModifyRequestType<ProvisionObject>, ModifyResponseType> {

    @Override
    public ModifyResponseType execute(ModifyRequestType<ProvisionObject> modifyRequestType) throws ConnectorDataException{
        ModifyResponseType response = new ModifyResponseType();
        response.setStatus(StatusCodeType.SUCCESS);

        PSOIdentifierType psoID = modifyRequestType.getPsoID();
		/* targetID - */
        String targetID = psoID.getTargetID();

        // Data sent with request - Data must be present in the request per the
        // spec
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);

        // Initialise
        ProvisionObject provisionObject = modifyRequestType.getProvisionObject();
        if (provisionObject == null) {
            throw new ConnectorDataException(ErrorCode.CSV_ERROR, "Sync object is null");
        }
        updateObject(new ReconciliationObject<ProvisionObject>(psoID.getID(), provisionObject), managedSys);
        return response;
    }

    protected abstract void updateObject(ReconciliationObject<ProvisionObject> object, ManagedSysEntity managedSys) throws ConnectorDataException;
}
