package org.openiam.idm.srvc.audit.service;

// Generated Nov 30, 2007 3:01:47 AM by Hibernate Tools 3.2.0.b11

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
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
	

	protected Criteria getExampleCriteriaWithoutOrder(final IdmAuditLogEntity entity) {
    	final Criteria criteria = super.getCriteria();
    	if(entity != null) {
    		if(StringUtils.isNotBlank(entity.getId())) {
    			criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
    		}
            if(StringUtils.isNotBlank(entity.getAction())) {
                criteria.add(Restrictions.eq("action",entity.getAction()));
            }
    	}

    	return criteria;
	}

    protected Criteria getExampleCriteria(final IdmAuditLogEntity entity) {
        final Criteria criteria = getExampleCriteriaWithoutOrder(entity);
        criteria.addOrder(Order.desc("timestamp"));
        return criteria;
    }

    public int count(AuditLogSearchBean searchBean) {
        Criteria criteria = super.getCriteria();
        if(searchBean != null && (searchBean instanceof AuditLogSearchBean)) {
            criteria = getExampleCriteriaWithoutOrder(converter.convert(searchBean));
            if(searchBean.getFrom() != null && searchBean.getTo() != null) {
                criteria.add(Restrictions.between("timestamp", searchBean.getFrom(), searchBean.getTo()));
            } else if(searchBean.getFrom() != null) {
                criteria.add(Restrictions.gt("timestamp", searchBean.getFrom()));
            } else if(searchBean.getTo() != null) {
                criteria.add(Restrictions.lt("timestamp", searchBean.getTo()));
            }
        }
        return ((Number) criteria.setProjection(rowCount())
                .uniqueResult()).intValue();
    }

	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		Criteria criteria = super.getCriteria();
		if(searchBean != null && (searchBean instanceof AuditLogSearchBean)) {
			final AuditLogSearchBean auditSearch = (AuditLogSearchBean)searchBean;
			criteria = getExampleCriteria(converter.convert(auditSearch));
			
			if(auditSearch.getFrom() != null && auditSearch.getTo() != null) {
				criteria.add(Restrictions.between("timestamp", auditSearch.getFrom(), auditSearch.getTo()));
			} else if(auditSearch.getFrom() != null) {
				criteria.add(Restrictions.gt("timestamp", auditSearch.getFrom()));
			} else if(auditSearch.getTo() != null) {
				criteria.add(Restrictions.lt("timestamp", auditSearch.getTo()));
			}
		}
		return criteria;
	}

	@Override
    protected String getPKfieldName() {
        return "id";
    }

}
