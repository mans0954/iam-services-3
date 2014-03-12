package org.openiam.idm.srvc.auth.login;

import org.openiam.dozer.converter.IdentityDozerConverter;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("identityManager")
public class IdentityServiceImpl implements IdentityService {

    @Autowired
    private IdentityDAO identityDAO;
    @Autowired
    private IdentityDozerConverter identityDozerConverter;

    @Override
    @Transactional
    public String save(IdentityDto identityDto) {
        IdentityEntity identityEntity = identityDozerConverter.convertToEntity(identityDto, true);
        identityDAO.save(identityEntity);
        return identityEntity.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityDto getIdentity(String referredId, String managedSysId) {
        IdentityEntity identityEntity = identityDAO.findByManagedSysId(referredId, managedSysId);
        IdentityDto identityDto = null;
        if(identityEntity != null) {
            identityDto = identityDozerConverter.convertToDTO(identityEntity, true);
        }
        return identityDto;
    }
}
