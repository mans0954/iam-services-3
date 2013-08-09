package org.openiam.connector.linux.command.base;

import org.openiam.connector.linux.ssh.SSHAgent;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;

/**
 * Created with IntelliJ IDEA.
 * User: alexander
 * Date: 8/7/13
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractCrudLinuxCommand<ExtObject extends ExtensibleObject> extends AbstractLinuxCommand<CrudRequest<ExtObject>, ObjectResponse>  {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {

        ObjectResponse responseType = new ObjectResponse();
        responseType.setRequestID(crudRequest.getRequestID());
        responseType.setStatus(StatusCodeType.SUCCESS);

        SSHAgent ssh = getSSHAgent(crudRequest.getTargetID());

        try {
            performObjectOperation(crudRequest, ssh);
        } catch (ConnectorDataException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            ssh.logout();
        }
        return responseType;
    }

    protected abstract void performObjectOperation(CrudRequest<ExtObject> crudRequest, SSHAgent ssh)throws ConnectorDataException;
}
