package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.core.dao.OrderDaoImpl;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for MetadataType
 */
@Repository("metadataTypeDAO")
public class MetadataTypeDAOImpl extends OrderDaoImpl<MetadataTypeEntity, String> implements MetadataTypeDAO {


    protected boolean cachable() {
        return true;
    }

    @Override
    public MetadataTypeEntity findByNameGrouping(String name, MetadataTypeGrouping grouping) {
        return (MetadataTypeEntity) getCriteria().setCacheable(cachable()).add(Restrictions.eq("description", name)).add(Restrictions.eq("grouping", grouping)).uniqueResult();
    }

    @Override
    protected Criteria getExampleCriteria(final SearchBean searchBean) {
		Criteria criteria = getCriteria();
		if(searchBean != null) {
			if(searchBean instanceof MetadataTypeSearchBean) {
				final MetadataTypeSearchBean sb = (MetadataTypeSearchBean)searchBean;
				if(StringUtils.isNotBlank(sb.getKey())) {
					criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
				} else {
					
					if(sb.getUsedForSMSOTP() != null) {
						criteria.add(Restrictions.eq("usedForSMSOTP", sb.getUsedForSMSOTP()));
					}
					
					if (sb.getGrouping() != null) {
				    	criteria.add(Restrictions.eq("grouping", sb.getGrouping()));
				    }
					
					final Criterion nameCriterion = getStringCriterion("name", sb.getNameToken(), sysConfig.isCaseInSensitiveDatabase());
	                if(nameCriterion != null) {
	                	criteria.add(nameCriterion);
	                }
				    
				}
			}
		}
		return criteria;
	}
	
//    @SuppressWarnings("unchecked")
//    @Override
//    public List<MetadataTypeEntity> findTypesInCategory(String categoryId) {
//		final Criteria criteria = getCriteria().createAlias("categories", "category").add(
//				Restrictions.eq("category.id", categoryId));
//		return criteria.list();
//    }
//

    @Override
    protected String getPKfieldName() {
    	return "id";
    }
    protected String getReferenceType() {
        return "MetadataTypeEntity.displayNameMap";
    }

}
