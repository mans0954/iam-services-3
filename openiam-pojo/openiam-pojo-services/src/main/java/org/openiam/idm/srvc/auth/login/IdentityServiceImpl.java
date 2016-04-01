package org.openiam.idm.srvc.auth.login;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.IdentityDozerConverter;
import org.openiam.idm.searchbeans.IdentitySearchBean;
import org.openiam.idm.srvc.auth.domain.IdentityEntity;
import org.openiam.idm.srvc.auth.dto.IdentityDto;
import org.springframework.beans.BeanUtils;
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
        IdentityEntity identityEntity;
        if (StringUtils.isBlank(identityDto.getId())) {
            identityEntity = identityDozerConverter.convertToEntity(identityDto, true);
        } else {
            identityEntity = identityDAO.findById(identityDto.getId());
            BeanUtils.copyProperties(identityDto, identityEntity);
        }
        identityDAO.save(identityEntity);
        return identityEntity.getId();
    }

    @Override
    @Transactional
    public IdentityDto getIdentity(String identityId) {
        IdentityEntity identityEntity = identityDAO.findById(identityId);
        IdentityDto identityDto = null;
        if(identityEntity != null) {
            identityDto = identityDozerConverter.convertToDTO(identityEntity, true);
        }
        return identityDto;
    }

    @Override
    @Transactional(readOnly = true)
    public IdentityDto getIdentityByManagedSys(String referredId, String managedSysId) {
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
        identityDAO.merge(identityDozerConverter.convertToEntity(identityDto,true));
    }

    @Override
    @Transactional
    public List<IdentityDto> findByExample(IdentitySearchBean searchBean, String requesterId, int from, int size) {
        List<IdentityEntity> entities = identityDAO.getByExampleNoLocalize(searchBean,from,size);
        return identityDozerConverter.convertToDTOList(entities, false);
    }

    @Override
    @Transactional
    public int countBeans(IdentitySearchBean searchBean, String requesterId) {
        return identityDAO.count(searchBean);
    }

    @Override
    @Transactional
    public Response isValidIdentity(final IdentityDto identityDto) {
        ResponseCode error = null;
        final Response resp = new Response(ResponseStatus.SUCCESS);
        try {
            if(identityDto == null || StringUtils.isBlank(identityDto.getManagedSysId()) ||
                    StringUtils.isBlank(identityDto.getIdentity())) {
                error = ResponseCode.INVALID_ARGUMENTS;
            } else {

                final IdentityEntity currentEntity = identityDAO.getByIdentityManagedSys(
                        identityDto.getIdentity(), identityDto.getManagedSysId());

                if (currentEntity != null) {
                    if (StringUtils.isBlank(identityDto.getId())
                            || !identityDto.getId().equals(currentEntity.getId())) {
                        error = ResponseCode.IDENTITY_EXISTS;
                    }
                }
            }

        } catch(Throwable e) {
            error = ResponseCode.INTERNAL_ERROR;
        }

        if (error != null) {
            resp.setErrorCode(error);
            resp.setStatus(ResponseStatus.FAILURE);
        }
        return resp;
    }
}
