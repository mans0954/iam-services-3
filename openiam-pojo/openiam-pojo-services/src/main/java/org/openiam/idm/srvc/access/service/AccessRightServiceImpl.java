package org.openiam.idm.srvc.access.service;

import org.openiam.dozer.converter.AccessRightDozerConverter;
import org.openiam.idm.searchbeans.AccessRightSearchBean;
import org.openiam.idm.srvc.access.domain.AccessRightEntity;
import org.openiam.idm.srvc.access.dto.AccessRight;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.meta.service.MetadataTypeDAO;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
public class AccessRightServiceImpl implements AccessRightService {

    @Autowired
    private AccessRightDAO dao;
    @Autowired
    private MetadataTypeDAO metadataTypeDAO;
    @Autowired
    private AccessRightDozerConverter converter;

    @Override
    @Transactional
    public void save(AccessRightEntity entity) {
        if (entity.getMetadataTypeEntity1() != null && entity.getMetadataTypeEntity1().getId() == null) {
            entity.setMetadataTypeEntity1(null);
        } else {
            entity.setMetadataTypeEntity1(metadataTypeDAO.findById(entity.getMetadataTypeEntity1().getId()));
        }
        if (entity.getMetadataTypeEntity2() != null && entity.getMetadataTypeEntity2().getId() == null) {
            entity.setMetadataTypeEntity2(null);
        } else {
            entity.setMetadataTypeEntity2(metadataTypeDAO.findById(entity.getMetadataTypeEntity2().getId()));
        }
        if (entity.getId() != null) {
            dao.merge(entity);
        } else {
            dao.save(entity);
        }
    }

    @Override
    @Transactional
    public void delete(String id) {
        final AccessRightEntity entity = get(id);
        if (entity != null) {
            dao.delete(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AccessRightEntity get(String id) {
        return dao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<AccessRightEntity> findBeans(AccessRightSearchBean sb, int from, int size, final Language language) {
        return dao.getByExample(sb, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public int count(final AccessRightSearchBean searchBean) {
        return dao.count(searchBean);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccessRightEntity> findByIds(Collection<String> ids) {
        return dao.findByIds(ids);
    }


    @Override
    @Transactional(readOnly = true)
    @LocalizedServiceGet
    public List<AccessRight> findBeansDTO(final AccessRightSearchBean searchBean, final int from, final int size, final Language language) {
        final List<AccessRightEntity> entities = this.findBeans(searchBean, from, size, language);
        final List<AccessRight> dtos = converter.convertToDTOList(entities, true);
        return dtos;
    }

}
