package org.openiam.idm.srvc.auth.login;

import org.openiam.idm.srvc.auth.dto.IdentityDto;

import java.util.List;

public interface IdentityService {

    String save(IdentityDto identityDto);

    IdentityDto getIdentity(String referredId, String managedSysId);

    List<IdentityDto> getIdentities(String referredId);

    void deleteIdentity(String identityId);

    void updateIdentity(IdentityDto identityDto);
}
