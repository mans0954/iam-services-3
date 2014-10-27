package org.openiam.idm.srvc.meta.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.cat.service.CategoryDAO;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.searchbean.converter.MetadataTypeSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for MetadataType
 */
@Repository("metadataTypeDAO")
public class MetadataTypeDAOImpl extends BaseDaoImpl<MetadataTypeEntity, String> implements MetadataTypeDAO {
	

    @Autowired
    private MetadataTypeSearchBeanConverter metadataTypeSearchBeanConverter;

	@Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
		Criteria criteria = this.getCriteria();
		if(searchBean != null) {
			if(searchBean instanceof MetadataTypeSearchBean) {
				final MetadataTypeSearchBean metadataTypSearchBean = (MetadataTypeSearchBean)searchBean;
				criteria = getExampleCriteria(metadataTypeSearchBeanConverter.convert(metadataTypSearchBean));
				if(metadataTypSearchBean.getUsedForSMSOTP() != null) {
					criteria.add(Restrictions.eq("usedForSMSOTP", metadataTypSearchBean.getUsedForSMSOTP()));
				}
			}
		}
		return criteria;
	}
	
    @Override
    protected Criteria getExampleCriteria(final MetadataTypeEntity entity) {
		final Criteria criteria = getCriteria();
		if (StringUtils.isNotBlank(entity.getId())) {
		    criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
		} else {
			/*
		    if (StringUtils.isNotBlank(entity.getDescription())) {
		    	criteria.add(Restrictions.eq("description", entity.getDescription()));
		    }
		    */
		    if (entity.getGrouping() != null) {
		    	criteria.add(Restrictions.eq("grouping", entity.getGrouping()));
		    }
		    
		    if (StringUtils.isNotEmpty(entity.getName())) {
				String name = entity.getName();
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
						criteria.add(Restrictions.ilike("name", name, matchMode));
					} else {
						criteria.add(Restrictions.eq("name", name));
					}
				}
			}
		    
		    if(CollectionUtils.isNotEmpty(entity.getCategories())) {
		    	final Set<String> categoryIds = new HashSet<>();
		    	for(final CategoryEntity category : entity.getCategories()) {
		    		categoryIds.add(category.getId());
		    	}
		    	criteria.createAlias("categories", "category").add(Restrictions.in("category.id", categoryIds));
		    }
		}
		return criteria;
    }

    @Override
    protected String getPKfieldName() {
    	return "id";
    }

}
