package org.openiam.spml2.spi.linux.command.user;

import com.google.gdata.client.appsforyourdomain.UserService;
import com.google.gdata.data.appsforyourdomain.AppsForYourDomainException;
import com.google.gdata.data.appsforyourdomain.provisioning.UserEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ManagedSystemObjectMatch;
import org.openiam.provision.dto.ProvisionUser;
import org.openiam.spml2.msg.*;
import org.openiam.spml2.spi.gapps.command.base.AbstractGoogleAppsCommand;
import org.openiam.spml2.spi.linux.command.base.AbstractLinuxCommand;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Service("testUserLinuxCommand")
public class TestUserLinuxCommand extends AbstractLinuxCommand<TestRequestType<ProvisionUser>, ResponseType> {
    @Override
    public ResponseType execute(TestRequestType<ProvisionUser> testRequestType) throws ConnectorDataException {
        ResponseType r = new ResponseType();
        r.setStatus((getSSHAgent(testRequestType.getPsoID().getTargetID()) == null) ? StatusCodeType.FAILURE : StatusCodeType.SUCCESS);
        return r;
    }
}
