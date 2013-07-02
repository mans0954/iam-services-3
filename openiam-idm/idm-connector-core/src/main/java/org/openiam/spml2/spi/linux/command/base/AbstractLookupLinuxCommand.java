package org.openiam.spml2.spi.linux.command.base;

import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.linux.LinuxUser;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;

public abstract class AbstractLookupLinuxCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLinuxCommand<LookupRequestType<ProvisionObject>, LookupResponseType>  {
    @Override
    public LookupResponseType execute(LookupRequestType<ProvisionObject> lookupRequestType) throws ConnectorDataException {
        LookupResponseType responseType = new LookupResponseType();
        responseType.setRequestID(lookupRequestType.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        SSHAgent ssh = getSSHAgent(lookupRequestType.getPsoID().getTargetID());
        try {
            if(!lookupObject(lookupRequestType.getPsoID().getID(), ssh))
                throw new ConnectorDataException(ErrorCode.NO_SUCH_IDENTIFIER);

            return responseType;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
    }

    protected abstract boolean lookupObject(String id, SSHAgent ssh) throws ConnectorDataException;
}
