package org.openiam.idm.srvc.meta.service;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.MetadataTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.springframework.stereotype.Repository;

/**
 * DAO implementation for MetadataType
 */
@Repository("metadataTypeDAO")
public class MetadataTypeDAOImpl extends BaseDaoImpl<MetadataTypeEntity, String> implements MetadataTypeDAO {

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
					
					if (StringUtils.isNotEmpty(sb.getName())) {
						String name = sb.getName();
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
	public MetadataTypeEntity findByNameGrouping(String name, MetadataTypeGrouping grouping) {
		return (MetadataTypeEntity)getCriteria().add(Restrictions.eq("description",name)).add(Restrictions.eq("grouping",grouping)).uniqueResult();
	}
    @Override
    protected String getPKfieldName() {
    	return "id";
    }

}
