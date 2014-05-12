package org.openiam.idm.srvc.auth.ws;

import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "org.openiam.idm.srvc.auth.ws.IdentityWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/auth/service",
        serviceName = "IdentityWebService",
        portName = "IdentityWebServicePort")

@Component("identityWS")
public class IdentityWebServiceImpl implements IdentityWebService {

    @Autowired
    @Qualifier("identityManager")
    private IdentityService identityService;

    @Override
    public String save(@WebParam(name = "identity", targetNamespace = "") IdentityDto identityDto) {
        return identityService.save(identityDto);
    }

    @Override
    public IdentityDto getIdentity(@WebParam(name = "referredId", targetNamespace = "") String referredId, @WebParam(name = "managedSysId", targetNamespace = "") String managedSysId) {
        return identityService.getIdentity(referredId, managedSysId);
    }

    @Override
    public List<IdentityDto> getIdentities(@WebParam(name = "referredId", targetNamespace = "") String referredId) {
        return identityService.getIdentities(referredId);
    }

    @Override
    public void deleteIdentity(@WebParam(name = "identityId", targetNamespace = "") String identityId) {
        identityService.deleteIdentity(identityId);
    }

    @Override
    public void updateIdentity(@WebParam(name = "identity", targetNamespace = "") IdentityDto identityDto) {
        identityService.updateIdentity(identityDto);
    }
}
