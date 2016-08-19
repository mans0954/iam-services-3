package org.openiam.srvc.idm;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentitySearchBean;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.login.IdentityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import java.util.List;

@WebService(endpointInterface = "org.openiam.srvc.idm.IdentityWebService",
        targetNamespace = "urn:idm.openiam.org/srvc/auth/service",
        serviceName = "IdentityWebService",
        portName = "IdentityWebServicePort")

@Component("identityWS")
public class IdentityWebServiceImpl implements IdentityWebService {

    @Autowired
    @Qualifier("identityManager")
    private IdentityService identityService;

    @Override
    public String save(IdentityDto identityDto) {
        return identityService.save(identityDto);
    }

    @Override
    public IdentityDto getIdentity(String identityId) {
        return identityService.getIdentity(identityId);
    }

    @Override
    public IdentityDto getIdentityByManagedSys(String referredId, String managedSysId) {
        return identityService.getIdentityByManagedSys(referredId, managedSysId);
    }

    @Override
    public List<IdentityDto> getIdentities(String referredId) {
        return identityService.getIdentities(referredId);
    }

    @Override
    public void deleteIdentity(String identityId) {
        identityService.deleteIdentity(identityId);
    }

    @Override
    public void updateIdentity(IdentityDto identityDto) {
        identityService.updateIdentity(identityDto);
    }

    @Override
    public Response isValidIdentity(IdentityDto identityDto) {
        return identityService.isValidIdentity(identityDto);
    }

    @Override
    public List<IdentityDto> findByExample(IdentitySearchBean searchBean, String requesterId, Integer from, Integer size) {
        return identityService.findByExample(searchBean, requesterId, from, size);
    }

    @Override
    public Integer countBeans(IdentitySearchBean searchBean, String requesterId) {
        return identityService.countBeans(searchBean, requesterId);
    }
}
