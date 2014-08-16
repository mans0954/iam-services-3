package org.openiam.idm.srvc.auth.login;

import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.IdentitySearchBean;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.openiam.idm.srvc.auth.dto.IdentityTypeEnum;

import java.util.List;

public interface IdentityService {

    String save(IdentityDto identityDto);

    IdentityDto getIdentity(String referredId, String managedSysId);

    List<IdentityDto> getIdentities(String referredId);

    void deleteIdentity(String identityId);

    void updateIdentity(IdentityDto identityDto);

    List<IdentityDto> findByExample(IdentitySearchBean searchBean, String requesterId, int from, int size);

    int countBeans(final IdentitySearchBean searchBean, final String requesterId);
}
