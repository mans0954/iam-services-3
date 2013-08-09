package org.openiam.idm.srvc.meta.service;

// Generated Nov 4, 2008 12:11:29 AM by Hibernate Tools 3.2.2.GA

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.cat.service.CategoryDAO;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

// import org.apache.log4j.Category;

/**
 * DAO implementation for MetadataType
 */
@Repository("metadataTypeDAO")
public class MetadataTypeDAOImpl extends
        BaseDaoImpl<MetadataTypeEntity, String> implements MetadataTypeDAO {

    @Override
	protected Criteria getExampleCriteria(final MetadataTypeEntity entity) {
    	final Criteria criteria = getCriteria();
    	if(StringUtils.isNotBlank(entity.getMetadataTypeId())) {
    		criteria.add(Restrictions.eq(getPKfieldName(), entity.getMetadataTypeId()));
    	} else {
    		criteria.add(Restrictions.eq("active", entity.isActive()));
    		criteria.add(Restrictions.eq("syncManagedSys", entity.isSyncManagedSys()));
    		
    		if(StringUtils.isNotBlank(entity.getGrouping())) {
    			criteria.add(Restrictions.eq("grouping", entity.getGrouping()));
    		}
    	}
    	return criteria;
	}

	@SuppressWarnings("unchecked")
    @Override
    public List<MetadataTypeEntity> findTypesInCategory(String categoryId) {
    	final Criteria criteria = getCriteria().createAlias("categories", "category").add(Restrictions.eq("category.categoryId", categoryId));
        return criteria.list();
    }

    @Override
    protected String getPKfieldName() {
        return "metadataTypeId";
    }

}
