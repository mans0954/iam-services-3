package org.openiam.spml2.spi.linux.command.base;

import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;

import java.util.List;

public abstract class AbstractModifyLinuxCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLinuxCommand<ModifyRequestType<ProvisionObject>, ModifyResponseType>  {
    @Override
    public ModifyResponseType execute(ModifyRequestType<ProvisionObject> modifyRequestType) throws ConnectorDataException {
        log.debug("Modify user called");

        ModifyResponseType responseType = new ModifyResponseType();
        responseType.setRequestID(modifyRequestType.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        PSOIdentifierType psoID = modifyRequestType.getPsoID();
        String targetID = psoID.getTargetID();

        SSHAgent ssh = getSSHAgent(targetID);
        try {
            modifyObject(psoID.getID(), modifyRequestType.getModification(), ssh);
            return responseType;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
    }

    protected abstract void modifyObject(String id, List<ModificationType> modification, SSHAgent ssh)throws ConnectorDataException;
}
