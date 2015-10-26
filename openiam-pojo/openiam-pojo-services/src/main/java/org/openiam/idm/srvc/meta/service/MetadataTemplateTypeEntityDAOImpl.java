package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MetadataTemplateTypeEntityDAOImpl extends BaseDaoImpl<MetadataTemplateTypeEntity, String> implements MetadataTemplateTypeEntityDAO {

    @Override
    protected boolean cachable() {
        return true;
    }

    @Override
    protected Criteria getExampleCriteria(final MetadataTemplateTypeEntity entity) {
        final Criteria criteria = getCriteria();
        if (entity != null) {
            if (StringUtils.isNotBlank(entity.getId())) {
                criteria.add(Restrictions.eq("id", entity.getId()));
            } else {
                if (StringUtils.isNotBlank(entity.getName())) {
                    criteria.add(Restrictions.eq("name", entity.getName()));
                }
            }
        }
        return criteria;
    }

    @Override
    protected String getPKfieldName() {
        return "id";
    }

}
