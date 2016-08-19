package org.openiam.connector.mysql.command.base;

import org.openiam.connector.linux.command.base.AbstractLinuxCommand;
import org.openiam.provision.constant.ErrorCode;
import org.openiam.provision.request.CrudRequest;
import org.openiam.base.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.type.ConnectorDataException;

public class AbstractCrudMySQLCommand<ExtObject extends ExtensibleObject> extends AbstractLinuxCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        throw new ConnectorDataException(ErrorCode.UNSUPPORTED_OPERATION);
    }
}
