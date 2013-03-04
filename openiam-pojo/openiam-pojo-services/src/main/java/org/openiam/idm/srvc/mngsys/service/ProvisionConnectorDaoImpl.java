package org.openiam.idm.srvc.mngsys.service;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.mngsys.domain.ProvisionConnectorEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProvisionConnectorDaoImpl extends BaseDaoImpl<ProvisionConnectorEntity, String> implements ProvisionConnectorDao {

    @Override
    protected String getPKfieldName() {
        return "connectorId";
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public List<MetadataTypeEntity> getMetadataTypes() {
        DetachedCriteria ownerCriteria = DetachedCriteria.forClass(ProvisionConnectorEntity.class);
        ownerCriteria.setProjection(Property.forName("metadataTypeId"));
        Criteria criteria = getSession().createCriteria(MetadataTypeEntity.class);
        criteria.add(Property.forName("metadataTypeId").in(ownerCriteria));
        return criteria.list();
    }
}
