package org.openiam.idm.srvc.audit.service;

// Generated Nov 30, 2007 3:01:47 AM by Hibernate Tools 3.2.0.b11

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.exception.data.DataException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.audit.constant.CustomIdmAuditLogType;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.SearchAudit;
import org.openiam.idm.srvc.searchbean.converter.AuditLogSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.hibernate.criterion.Projections.rowCount;

/**
 * RDMBS implementation the DAO for IdmAudit
 * @author Suneet Shah 
 */
@Repository("idmAuditLogDAO")
public class IdmAuditLogDAOImpl extends BaseDaoImpl<IdmAuditLogEntity, String> implements IdmAuditLogDAO {
	
	@Autowired
	private AuditLogSearchBeanConverter converter;
	

    @Override
	protected Criteria getExampleCriteria(final IdmAuditLogEntity entity) {
    	final Criteria criteria = super.getCriteria();
    	if(entity != null) {
    		if(StringUtils.isNotBlank(entity.getId())) {
    			criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
    		}
    	}
    	return criteria;
	}

	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		Criteria criteria = super.getCriteria();
		if(searchBean != null && (searchBean instanceof AuditLogSearchBean)) {
			final AuditLogSearchBean auditSearch = (AuditLogSearchBean)searchBean;
			criteria = getExampleCriteria(converter.convert(auditSearch));
		}
		return criteria;
	}

	@Override
    protected String getPKfieldName() {
        return "id";
    }

}
