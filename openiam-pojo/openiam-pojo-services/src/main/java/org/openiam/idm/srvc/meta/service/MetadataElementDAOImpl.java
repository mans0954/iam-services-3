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
import org.openiam.am.srvc.domain.ContentProviderEntity;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for MetadataElement
 */
@Repository("metadataElementDAO")
public class MetadataElementDAOImpl extends BaseDaoImpl<MetadataElementEntity, String> implements MetadataElementDAO {
    
	
	
	@Override
	protected Criteria getExampleCriteria(final SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof MetadataElementSearchBean) {
			final MetadataElementSearchBean metaSearchBean = (MetadataElementSearchBean)searchBean;
			if(CollectionUtils.isNotEmpty(metaSearchBean.getKeys())) {
				criteria.add(Restrictions.in("id", metaSearchBean.getKeys()));
			} else {
				setAttributeNameCriteria(criteria, metaSearchBean.getAttributeName());	
				if(CollectionUtils.isNotEmpty(metaSearchBean.getTypeIdSet())) {
					criteria.add(Restrictions.in("metadataType.metadataTypeId", metaSearchBean.getTypeIdSet()));
				}
				
				if(StringUtils.isNotBlank(metaSearchBean.getTemplateId())) {
					final Set<String> templateIdSet = new HashSet<String>();	
					templateIdSet.add(metaSearchBean.getTemplateId());
					setTemplateCriteria(criteria, templateIdSet);
				}
			}
		}
		return criteria;
	}

	@Override
	protected Criteria getExampleCriteria(final MetadataElementEntity entity) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(entity.getId())) {
			criteria.add(Restrictions.eq("id", entity.getId()));
		} else {
			setAttributeNameCriteria(criteria, entity.getAttributeName());			
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
				
				setTemplateCriteria(criteria, templateIdSet);
			}
			
			if(entity.getResource() != null && StringUtils.isNotEmpty(entity.getResource().getResourceId())) {
            	criteria.add(Restrictions.eq("resource.resourceId", entity.getResource().getResourceId()));
            }
		}
		return criteria;
	}
	
	private void setTemplateCriteria(final Criteria criteria, final Set<String> templateIdSet) {
		if(CollectionUtils.isNotEmpty(templateIdSet)) {
			criteria.createAlias("templateSet", "xref")
					.createAlias("xref.template", "template")
					.add(Restrictions.in("template.id", templateIdSet));
		}
	}
	
	private void setAttributeNameCriteria(final Criteria criteria, final String attributeName) {
		if (StringUtils.isNotEmpty(attributeName)) {
            String name = attributeName;
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

	@Override
	public List<MetadataElementEntity> getByResourceId(String resourceId) {
		final MetadataElementEntity entity = new MetadataElementEntity();
		final ResourceEntity resource = new ResourceEntity();
		resource.setResourceId(resourceId);
		entity.setResource(resource);
		return getByExample(entity);
	}

}
