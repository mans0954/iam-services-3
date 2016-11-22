package org.openiam.idm.srvc.meta.service;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataTemplateTypeFieldSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataFieldTemplateXrefEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTemplateTypeFieldEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MetadataTemplateTypeFieldEntityDAOImpl extends BaseDaoImpl<MetadataTemplateTypeFieldEntity, String> implements MetadataTemplateTypeFieldEntityDAO {

    @Override
    protected boolean cachable() {
        return true;
    }

    @Override
    protected Criteria getExampleCriteria(SearchBean searchBean) {
        final Criteria criteria = getCriteria();
        if (searchBean != null && searchBean instanceof MetadataTemplateTypeFieldSearchBean) {
            final MetadataTemplateTypeFieldSearchBean sb = (MetadataTemplateTypeFieldSearchBean) searchBean;
            if(CollectionUtils.isNotEmpty(sb.getKeySet())) {
                criteria.add(Restrictions.in(getPKfieldName(), sb.getKeySet()));
            } else {
                if (StringUtils.isNotBlank(sb.getName())) {
                    criteria.add(Restrictions.eq("name", sb.getName()));
                }

                if (StringUtils.isNotBlank(sb.getTemplateTypeId())) {
                    criteria.add(Restrictions.eq("templateType.id", sb.getTemplateTypeId()));
                }

                if (StringUtils.isNotBlank(sb.getTemplateId())) {
                    criteria.createAlias("fieldXrefs", "xref").add(Restrictions.eq("xref.template.id", sb.getTemplateId()));
                }
            }
        }
        return criteria;
    }

    @Override
    protected Criteria getExampleCriteria(final MetadataTemplateTypeFieldEntity entity) {
        final Criteria criteria = getCriteria();
        if (entity != null) {
            if (StringUtils.isNotBlank(entity.getId())) {
                criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
            } else {
                if (entity.getTemplateType() != null && StringUtils.isNotBlank(entity.getTemplateType().getId())) {
                    criteria.add(Restrictions.eq("templateType.id", entity.getTemplateType().getId()));
                }

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
