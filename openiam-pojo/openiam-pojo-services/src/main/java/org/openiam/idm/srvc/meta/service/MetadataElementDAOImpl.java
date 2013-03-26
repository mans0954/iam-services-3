package org.openiam.idm.srvc.meta.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for MetadataElement
 */
@Repository("metadataElementDAO")
public class MetadataElementDAOImpl extends BaseDaoImpl<MetadataElementEntity, String> implements MetadataElementDAO {
    
	@Override
	protected Criteria getExampleCriteria(final MetadataElementEntity entity) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(entity.getId())) {
			criteria.add(Restrictions.eq("id", entity.getId()));
		} else {
			if (StringUtils.isNotEmpty(entity.getAttributeName())) {
                String name = entity.getAttributeName();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(name, "*") == 0) {
                    matchMode = MatchMode.END;
                    name = name.substring(1);
                }
                if (StringUtils.isNotEmpty(name) && StringUtils.indexOf(name, "*") == name.length() - 1) {
                	name = name.substring(0, name.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(name)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("attributeName", name, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("attributeName", name));
                    }
                }
            }
			
			if(StringUtils.isNotBlank(entity.getDataType())) {
				criteria.add(Restrictions.eq("dataType", entity.getDataType()));
			}
			
			if(entity.getMetadataType() != null && StringUtils.isNotBlank(entity.getMetadataType().getMetadataTypeId())) {
				final String metadataTypeId = entity.getMetadataType().getMetadataTypeId();
				criteria.add(Restrictions.eq("metadataType.metadataTypeId", metadataTypeId));
			}
			
			if(CollectionUtils.isNotEmpty(entity.getTemplateSet())) {
				final Set<String> templateIdSet = new HashSet<String>();
				for(final MetadataElementPageTemplateXrefEntity xref : entity.getTemplateSet()) {
					if(xref.getTemplate() != null && StringUtils.isNotBlank(xref.getTemplate().getId())) {
						templateIdSet.add(xref.getTemplate().getId());
					}
				}
				
				if(CollectionUtils.isNotEmpty(templateIdSet)) {
					criteria.createAlias("templateSet", "xref")
							.createAlias("xref.template", "template")
							.add(Restrictions.in("template.id", templateIdSet));
				}
			}
		}
		return criteria;
	}

	@SuppressWarnings("unchecked")
    @Override
    public List<MetadataElementEntity> findbyCategoryType(String categoryType) {
        final Criteria criteria = getCriteria()
        							.createAlias("metadataType", "mt")
        							.createAlias("mt.categories", "ct")
        							.add(Restrictions.eq("ct.categoryId", categoryType));
       return criteria.list();
    }

    @Override
    protected String getPKfieldName() {
        return "metadataElementId";
    }

}
