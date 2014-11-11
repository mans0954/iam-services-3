package org.openiam.idm.srvc.res.service;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;

import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.springframework.stereotype.Repository;

@Repository("resourcePropDAO")
public class ResourcePropDAOImpl extends BaseDaoImpl<ResourcePropEntity, String> implements ResourcePropDAO  {

	private static final Log log = LogFactory.getLog(ResourcePropDAOImpl.class);

	@Override
	protected String getPKfieldName() {
		return "id";
	}

    public String findValueByName(String resourceId, String name) {
        Criteria criteria = getCriteria()
                .add(Restrictions.eq("name",name))
                .add(Restrictions.eq("resource.id", resourceId))
                .setProjection(Projections.property("value"));
        return (String) criteria.uniqueResult();
    }
}
