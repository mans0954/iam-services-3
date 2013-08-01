package org.openiam.spml2.spi.mysql.command.base;

import org.openiam.provision.dto.GenericProvisionObject;
import org.openiam.spml2.msg.AddRequestType;
import org.openiam.spml2.msg.AddResponseType;
import org.openiam.connector.type.ConnectorDataException;
import org.openiam.spml2.msg.ErrorCode;

public class AbstractAddMySQLCommand<ProvisionObject extends GenericProvisionObject> extends AbstractMySQLCommand<AddRequestType<ProvisionObject>, AddResponseType> {
    @Override
    public AddResponseType execute(AddRequestType<ProvisionObject> provisionObjectAddRequestType) throws ConnectorDataException {
        throw new ConnectorDataException(ErrorCode.UNSUPPORTED_OPERATION);
    }
}
