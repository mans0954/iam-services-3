package org.openiam.idm.srvc.res.service;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for ResourceType
 */
@Repository("resourceTypeDAO")
public class ResourceTypeDAOImpl extends BaseDaoImpl<ResourceTypeEntity, String> implements ResourceTypeDAO {

    @Override
    protected Criteria getExampleCriteria(SearchBean searchBean) {
        Criteria criteria = getCriteria();
        if (searchBean != null && searchBean instanceof ResourceTypeSearchBean) {
        	final ResourceTypeSearchBean sb = (ResourceTypeSearchBean) searchBean;
            if(sb.getSupportsHierarchy() != null) {
            	criteria.add(Restrictions.eq("supportsHierarchy", sb.getSupportsHierarchy()));
            }
            if (!StringUtils.isEmpty(sb.getKey())) {
                criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
            }
            
            if (Boolean.FALSE.equals(sb.getSelectAll())) {
                criteria.add(Restrictions.eq("searchable", sb.isSearchable()));
            }
            
            if (!StringUtils.isEmpty(sb.getDescription())) {

                String desc = sb.getDescription();
                MatchMode matchMode = null;
                if (StringUtils.indexOf(desc, "*") == 0) {
                    matchMode = MatchMode.END;
                    desc = desc.substring(1);
                }
                if (StringUtils.isNotEmpty(desc) && StringUtils.indexOf(desc, "*") == desc.length() - 1) {
                    desc = desc.substring(0, desc.length() - 1);
                    matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
                }

                if (StringUtils.isNotEmpty(desc)) {
                    if (matchMode != null) {
                        criteria.add(Restrictions.ilike("description", desc, matchMode));
                    } else {
                        criteria.add(Restrictions.eq("description", desc));
                    }
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
