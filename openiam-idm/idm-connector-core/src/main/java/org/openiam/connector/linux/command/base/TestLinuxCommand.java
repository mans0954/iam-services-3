package org.openiam.connector.linux.command.base;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.connector.type.constant.StatusCodeType;
import org.openiam.connector.type.request.RequestType;
import org.openiam.connector.type.response.ResponseType;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.stereotype.Service;

@Service("testLinuxCommand")
public class TestLinuxCommand<ExtObject extends ExtensibleObject> extends AbstractLinuxCommand<RequestType<ExtObject>, ResponseType> {
    @Override
    public ResponseType execute(RequestType<ExtObject> testRequestType) throws ConnectorDataException {
        ResponseType r = new ResponseType();
        r.setStatus((getSSHAgent(testRequestType.getTargetID()) == null) ? StatusCodeType.FAILURE : StatusCodeType.SUCCESS);
        return r;
    }
}
