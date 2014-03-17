package org.openiam.idm.srvc.meta.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.cat.service.CategoryDAO;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for MetadataType
 */
@Repository("metadataTypeDAO")
public class MetadataTypeDAOImpl extends BaseDaoImpl<MetadataTypeEntity, String> implements MetadataTypeDAO {

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
		    if (StringUtils.isNotBlank(entity.getGrouping())) {
		    	criteria.add(Restrictions.eq("grouping", entity.getGrouping()));
		    }
		    
		    if (StringUtils.isNotEmpty(entity.getDescription())) {
				String name = entity.getDescription();
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
						criteria.add(Restrictions.ilike("description", name, matchMode));
					} else {
						criteria.add(Restrictions.eq("description", name));
					}
				}
			}
		}
		return criteria;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<MetadataTypeEntity> findTypesInCategory(String categoryId) {
		final Criteria criteria = getCriteria().createAlias("categories", "category").add(
			Restrictions.eq("category.categoryId", categoryId));
		return criteria.list();
    }

    @Override
    protected String getPKfieldName() {
    	return "id";
    }

}
