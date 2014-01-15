package org.openiam.idm.srvc.res.service;

// Generated Mar 8, 2009 12:54:32 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import static org.hibernate.criterion.Example.create;

import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.ResourceTypeSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.res.domain.ResourcePropEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.searchbean.converter.ResourceTypeSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO Implementation for ResourceType
 */
@Repository("resourceTypeDAO")
public class ResourceTypeDAOImpl extends BaseDaoImpl<ResourceTypeEntity, String>  implements ResourceTypeDAO  {

	@Autowired
	private ResourceTypeSearchBeanConverter converter;
	
	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		Criteria criteria = null;
		if(searchBean != null && searchBean instanceof ResourceTypeSearchBean) {
			final ResourceTypeEntity entity = converter.convert((ResourceTypeSearchBean)searchBean);
			criteria = getExampleCriteria(entity);
		} else {
			criteria = super.getCriteria();
		}
		return criteria;
	}

	@Override
	protected Criteria getExampleCriteria(final ResourceTypeEntity t) {
		final Criteria criteria = getCriteria();
		if(t != null) {
			criteria.add(Restrictions.eq("searchable", t.isSearchable()));
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

}
