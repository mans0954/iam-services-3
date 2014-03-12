package org.openiam.idm.srvc.auth.login;

import org.openiam.idm.srvc.auth.dto.IdentityDto;

public interface IdentityService {

    String save(IdentityDto identityDto);

    IdentityDto getIdentity(String referredId, String managedSysId);

}
