package org.openiam.idm.srvc.res.service;


import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import static org.hibernate.criterion.Example.create;

import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.domain.ResourceUserEntity;
import org.springframework.stereotype.Repository;

@Repository("resourceUserDAO")
public class ResourceUserDAOImpl extends BaseDaoImpl<ResourceUserEntity, String>  implements ResourceUserDAO {

	@Override
	protected String getPKfieldName() {
		return "resourceUserId";
	}

	@Override
	public ResourceUserEntity getRecord(String resourceId, String userId) {
		final Criteria criteria = getCriteria()
									.add(Restrictions.eq("resourceId", resourceId))
									.add(Restrictions.eq("userId", userId));
		final List<ResourceUserEntity> resultList = criteria.list();
		return (CollectionUtils.isNotEmpty(resultList) && resultList.size() == 1) ? resultList.get(0) : null;
	}
	
}
