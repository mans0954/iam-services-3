package org.openiam.idm.srvc.authz.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.authz.dto.AuthAttribute;
import org.openiam.idm.srvc.authz.dto.AuthzRequest;
import org.openiam.idm.srvc.authz.dto.AuthzResponse;
import org.openiam.idm.srvc.res.dto.Resource;
import org.openiam.idm.srvc.res.dto.ResourceProp;
import org.openiam.idm.srvc.res.service.ResourceDataService;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.provision.service.ProvisionService;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "org.openiam.idm.srvc.authz.ws.AuthorizationWebService",
		targetNamespace = "urn:idm.openiam.org/srvc/authz/service",
		portName = "AuthorizationWebServicePort",
		serviceName = "AuthorizationWebService")
@Deprecated
public class AuthorizationWebServiceImpl implements AuthorizationWebService
{
    private static final Log log = LogFactory.getLog(AuthorizationWebServiceImpl.class);
    protected LoginDataService loginManager;
    protected ResourceDataService resourceDataService;
    protected UserDataService userManager;

    public LoginDataService getLoginManager() {
        return loginManager;
    }

    public void setLoginManager(LoginDataService loginManager) {
        this.loginManager = loginManager;
    }

    public ResourceDataService getResourceDataService() {
        return resourceDataService;
    }

    public void setResourceDataService(ResourceDataService resourceDataService) {
        this.resourceDataService = resourceDataService;
    }

    public UserDataService getUserManager() {
        return userManager;
    }

    public void setUserManager(UserDataService userManager) {
        this.userManager = userManager;
    }
}
