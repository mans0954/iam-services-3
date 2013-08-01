package org.openiam.spml2.spi.linux.command.user;

import org.openiam.connector.type.ConnectorDataException;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.linux.command.base.AbstractLinuxCommand;
import org.springframework.stereotype.Service;

@Service("testUserLinuxCommand")
public class TestUserLinuxCommand extends AbstractLinuxCommand<TestRequestType<ProvisionUser>, ResponseType> {
    @Override
    public ResponseType execute(TestRequestType<ProvisionUser> testRequestType) throws ConnectorDataException {
        ResponseType r = new ResponseType();
        r.setStatus((getSSHAgent(testRequestType.getPsoID().getTargetID()) == null) ? StatusCodeType.FAILURE : StatusCodeType.SUCCESS);
        return r;
    }
}
