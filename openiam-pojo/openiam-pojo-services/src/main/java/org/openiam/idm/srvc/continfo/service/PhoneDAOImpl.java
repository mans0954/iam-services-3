package org.openiam.idm.srvc.continfo.service;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

import javax.annotation.PostConstruct;


@Repository("phoneDAO")
public class PhoneDAOImpl extends BaseDaoImpl<PhoneEntity, String> implements PhoneDAO {

	private static final Log log = LogFactory.getLog(PhoneDAOImpl.class);
	
	private String DELETE_BY_USER_ID = "DELETE FROM %s e WHERE e.parent.id = :userId";
	
	@PostConstruct
	public void initSQL() {
		DELETE_BY_USER_ID = String.format(DELETE_BY_USER_ID, domainClass.getSimpleName());
	}
	
	

    @Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof PhoneSearchBean) {
			final PhoneSearchBean sb = (PhoneSearchBean)searchBean;
			if (StringUtils.isNotBlank(sb.getKey())) {
	            criteria.add(Restrictions.eq(getPKfieldName(), sb.getKey()));
	        } else {
	            if (StringUtils.isNotBlank(sb.getParentId())) {
	            	criteria.add(Restrictions.eq("parent.id", sb.getParentId()));
	            }

	            if (StringUtils.isNotBlank(sb.getMetadataTypeId())) {
	            	criteria.add(Restrictions.eq("type.id", sb.getMetadataTypeId()));
	            }
	        }
		}
		return criteria;
	}

    @Override
    public void removeByUserId(final String userId) {
    	final Query qry = getSession().createQuery(DELETE_BY_USER_ID);
		qry.setString("userId", userId);
		qry.executeUpdate();
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}
	
}
