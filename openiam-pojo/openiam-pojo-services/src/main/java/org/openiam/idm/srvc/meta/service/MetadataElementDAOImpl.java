package org.openiam.idm.srvc.meta.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataElementSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataElementEntity;
import org.openiam.idm.srvc.meta.domain.MetadataElementPageTemplateXrefEntity;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO Implementation for MetadataElement
 */
@Repository("metadataElementDAO")
public class MetadataElementDAOImpl extends BaseDaoImpl<MetadataElementEntity, String> implements MetadataElementDAO {


    @Override
    public MetadataElementEntity findByAttrNameTypeId(String attrName, String typeId) {
        return (MetadataElementEntity)getCriteria().add(Restrictions.eq("attributeName",attrName)).add(Restrictions.eq("metadataType.id",typeId)).uniqueResult();
    }

    @Override
    public String findIdByAttrNameTypeId(String attrName, String typeId) {
        return (String)getCriteria().add(Restrictions.eq("attributeName", attrName)).add(Restrictions.eq("metadataType.id", typeId)).setProjection(Projections.id()).uniqueResult();
    }

    @Override
	protected Criteria getExampleCriteria(final SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof MetadataElementSearchBean) {
			final MetadataElementSearchBean metaSearchBean = (MetadataElementSearchBean)searchBean;
			if(CollectionUtils.isNotEmpty(metaSearchBean.getKeys())) {
				criteria.add(Restrictions.in("id", metaSearchBean.getKeys()));
			} else {
				setAttributeNameCriteria(criteria, metaSearchBean.getAttributeName());	
				if(StringUtils.isNotBlank(metaSearchBean.getDataType())) {
					criteria.add(Restrictions.eq("dataType", metaSearchBean.getDataType()));
				}
				
				if(CollectionUtils.isNotEmpty(metaSearchBean.getTypeIdSet())) {
					criteria.add(Restrictions.in("metadataType.id", metaSearchBean.getTypeIdSet()));
				}
				
				//TODO:  Bug in Hibernate - metadataType.grouping throws org.hibernate.QueryException: could not resolve property
				if(CollectionUtils.isNotEmpty(metaSearchBean.getExcludedGroupings())) {
					//criteria.createAlias("metadataType", "mt").add(Restrictions.not(Restrictions.in("mt.grouping", metaSearchBean.getExcludedGroupings())));
					//criteria.add(Restrictions.not(Restrictions.in("metadataType.grouping", metaSearchBean.getExcludedGroupings())));
				}
				
				if(CollectionUtils.isNotEmpty(metaSearchBean.getGroupings())) {
					criteria.createAlias("metadataType", "mt")
							.add(Restrictions.in("mt.grouping", metaSearchBean.getGroupings()));
				}
				
				if(StringUtils.isNotBlank(metaSearchBean.getTemplateId())) {
					final Set<String> templateIdSet = new HashSet<String>();	
					templateIdSet.add(metaSearchBean.getTemplateId());
					setTemplateCriteria(criteria, templateIdSet);
				}
				
				if(StringUtils.isNotBlank(metaSearchBean.getResourceId())) {
					criteria.add(Restrictions.eq("resource.id", metaSearchBean.getResourceId()));
				}
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
		if (StringUtils.isNotBlank(attributeName)) {
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

    @Override
    protected String getPKfieldName() {
        return "id";
    }

	@Override
	public List<MetadataElementEntity> getByResourceId(String resourceId) {
		final MetadataElementSearchBean sb = new MetadataElementSearchBean();
		sb.setResourceId(resourceId);
		return getByExample(sb);
	}

}
