package org.openiam.idm.srvc.auth.login;

import org.openiam.dozer.converter.IdentityDozerConverter;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public List<IdentityDto> getIdentities(String referredID) {
        List<IdentityEntity> identityEntities = identityDAO.findByReferredId(referredID);
        List<IdentityDto> identityDtoList = Collections.EMPTY_LIST;
        if(identityDtoList != null) {
            identityDtoList = identityDozerConverter.convertToDTOList(identityEntities, true);
        }
        return identityDtoList;
    }

    @Override
    @Transactional
    public void deleteIdentity(String identityID) {
        IdentityEntity identityEntity = identityDAO.findById(identityID);
        identityDAO.delete(identityEntity);
    }

    @Override
    @Transactional
    public void updateIdentity(IdentityDto identityDto) {
        identityDAO.update(identityDozerConverter.convertToEntity(identityDto,true));
    }
}
