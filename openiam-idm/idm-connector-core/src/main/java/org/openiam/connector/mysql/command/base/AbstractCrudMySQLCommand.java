package org.openiam.connector.mysql.command.base;

import org.openiam.connector.linux.command.base.AbstractLinuxCommand;
import org.openiam.connector.type.constant.ErrorCode;
import org.openiam.connector.type.request.CrudRequest;
import org.openiam.connector.type.response.ObjectResponse;
import org.openiam.provision.type.ExtensibleObject;
import org.openiam.connector.type.ConnectorDataException;

public class AbstractCrudMySQLCommand<ExtObject extends ExtensibleObject> extends AbstractLinuxCommand<CrudRequest<ExtObject>, ObjectResponse> {
    @Override
    public ObjectResponse execute(CrudRequest<ExtObject> crudRequest) throws ConnectorDataException {
        throw new ConnectorDataException(ErrorCode.UNSUPPORTED_OPERATION);
    }
}
