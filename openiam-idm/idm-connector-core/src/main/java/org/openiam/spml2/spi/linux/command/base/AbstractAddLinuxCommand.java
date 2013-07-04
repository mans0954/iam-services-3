package org.openiam.spml2.spi.linux.command.base;

import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.spml2.msg.AddRequestType;
import org.openiam.spml2.msg.AddResponseType;
import org.openiam.spml2.msg.ConnectorDataException;
import org.openiam.spml2.msg.StatusCodeType;
import org.openiam.spml2.spi.linux.ssh.SSHAgent;

import java.util.List;

public abstract class AbstractAddLinuxCommand<ProvisionObject extends GenericProvisionObject> extends AbstractLinuxCommand<AddRequestType<ProvisionObject>, AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> addRequestType) throws ConnectorDataException {

        AddResponseType responseType = new AddResponseType();
        responseType.setRequestID(addRequestType.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        SSHAgent ssh = getSSHAgent(addRequestType.getTargetID());

        try {
            addObject(addRequestType.getPsoID().getID(), addRequestType.getData().getAny(), ssh);
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
        return responseType;
    }

    protected abstract void addObject(String login, List<ExtensibleObject> objectList, SSHAgent ssh) throws ConnectorDataException;
}
