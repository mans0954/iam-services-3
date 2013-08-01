package org.openiam.spml2.spi.gapps.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;


public abstract class AbstarctDeleteGoogleAppsCommand<ProvisionObject extends GenericProvisionObject> extends AbstractGoogleAppsCommand<DeleteRequestType<ProvisionObject>, ResponseType> {
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType) throws ConnectorDataException {
        this.init();
        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = deleteRequestType.getPsoID();

        /* targetID - */
        String targetID = psoID.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        deleteObject(psoID, managedSys);



        ResponseType respType = new ResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }

    protected abstract void deleteObject(PSOIdentifierType psoID, ManagedSysEntity managedSys) throws ConnectorDataException;
}
