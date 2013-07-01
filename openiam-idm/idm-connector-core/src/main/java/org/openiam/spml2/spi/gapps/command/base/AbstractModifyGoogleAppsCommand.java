package org.openiam.spml2.spi.gapps.command.base;

import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;

import java.util.List;

public abstract class AbstractModifyGoogleAppsCommand<ProvisionObject extends GenericProvisionObject> extends AbstractGoogleAppsCommand<ModifyRequestType<ProvisionObject>, ModifyResponseType>{
    @Override
    public ModifyResponseType execute(ModifyRequestType<ProvisionObject> modifyRequestType) throws ConnectorDataException {
        this.init();
        /*
         * PSO - Provisioning Service Object - - ID must uniquely specify an
         * object on the target or in the target's namespace - Try to make the
         * PSO ID immutable so that there is consistency across changes.
         */
        PSOIdentifierType psoID = modifyRequestType.getPsoID();

        /* targetID - */
        String targetID = psoID.getTargetID();

        /*
         * A) Use the targetID to look up the connection information under
         * managed systems
         */
        ManagedSysEntity managedSys = managedSysService.getManagedSysById(targetID);
        modifyObject(psoID, managedSys, modifyRequestType.getModification());

        ModifyResponseType respType = new ModifyResponseType();
        respType.setStatus(StatusCodeType.SUCCESS);
        return respType;
    }

    protected abstract void  modifyObject(PSOIdentifierType psoID,  ManagedSysEntity managedSys, List<ModificationType> modTypeList) throws ConnectorDataException;
}
