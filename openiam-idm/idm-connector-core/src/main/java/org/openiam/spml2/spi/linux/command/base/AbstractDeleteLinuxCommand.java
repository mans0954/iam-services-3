package org.openiam.spml2.spi.linux.command.base;

import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.DeleteRequestType;
import org.openiam.spml2.msg.ResponseType;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;

public abstract class AbstractDeleteLinuxCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLinuxCommand<DeleteRequestType<ProvisionObject>, ResponseType> {
    @Override
    public ResponseType execute(DeleteRequestType<ProvisionObject> deleteRequestType) throws ConnectorDataException {
        log.debug("Delete user called");

        ResponseType responseType = new ResponseType();
        responseType.setRequestID(deleteRequestType.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        SSHAgent ssh = getSSHAgent(deleteRequestType.getPsoID().getTargetID());
        try {
            deleteObject(deleteRequestType.getPsoID().getID(),  ssh);
            return responseType;
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
    }

    protected abstract void deleteObject(String id, SSHAgent ssh) throws ConnectorDataException;
}
