package org.openiam.idm.srvc.audit.service;

// Generated Nov 30, 2007 3:01:47 AM by Hibernate Tools 3.2.0.b11

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.searchbean.converter.AuditLogSearchBeanConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * RDMBS implementation the DAO for IdmAudit
 * @author Suneet Shah
 */
@Repository("idmAuditLogDAO")
public class IdmAuditLogDAOImpl extends BaseDaoImpl<IdmAuditLogEntity, String> implements IdmAuditLogDAO {

    @Autowired
    private AuditLogSearchBeanConverter converter;


    @Override
    public IdmAuditLogEntity findByRequesterId(String requesterId, String correlationID) {
        final Criteria criteria = super.getCriteria();
        IdmAuditLogEntity auditLogEntity = (IdmAuditLogEntity)criteria.add(Restrictions.and(Restrictions.eq("userId",requesterId),Restrictions.eq("coorelationId",correlationID))).uniqueResult();
        return auditLogEntity;
    }

    @Override
    protected Criteria getExampleCriteria(final IdmAuditLogEntity entity) {
        final Criteria criteria = super.getCriteria();
        if(entity != null) {
            if(StringUtils.isNotBlank(entity.getId())) {
                criteria.add(Restrictions.eq(getPKfieldName(), entity.getId()));
            }
            if(StringUtils.isNotEmpty(entity.getAction())) {
                criteria.add(Restrictions.eq("action",entity.getAction()));
            }
            if(StringUtils.isNotEmpty(entity.getResult())) {
                criteria.add(Restrictions.eq("result",entity.getResult()));
            }
        }
        return criteria;
    }

    @Override
    public List<String> getIDsByExample(SearchBean searchBean, int from, int size) {
        final Criteria criteria = getExampleCriteria(searchBean);
        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        criteria.addOrder(Order.desc("timestamp"));
        criteria.setProjection(Projections.id());
        List<String> resultList =  (List<String>)criteria.list();

        return resultList;
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

            if(StringUtils.isNotBlank(auditSearch.getManagedSysId())) {
                criteria.add(Restrictions.eq("managedSysId", auditSearch.getManagedSysId()));
            }

            if(StringUtils.isNotBlank(auditSearch.getSource())) {
                criteria.add(Restrictions.eq("source", auditSearch.getSource()));
            }

            if(StringUtils.isNotEmpty(auditSearch.getParentId()) && auditSearch.isParentOnly()) {
                criteria.add(Restrictions.isEmpty("parentLogs"));
            }
            if(StringUtils.isNotBlank(auditSearch.getUserId()) &&
                    StringUtils.isNotBlank(auditSearch.getTargetId())) {
                Criterion sourceCriterion = Restrictions.eq("userId", auditSearch.getUserId());
                criteria.createAlias("targets", "tar", Criteria.INNER_JOIN);

                criteria.add(Restrictions.or(sourceCriterion, Restrictions.and(Restrictions.eq("tar.targetId", auditSearch.getTargetId()),Restrictions.eq("tar.targetType", auditSearch.getTargetType()))));
            } else {
                if(StringUtils.isNotBlank(auditSearch.getUserId())) {
                    criteria.add(Restrictions.eq("userId", auditSearch.getUserId()));
                }

                if(StringUtils.isNotBlank(auditSearch.getTargetId())
                || StringUtils.isNotBlank(auditSearch.getTargetType())) {

                    criteria.createAlias("targets", "tar");

                    if(StringUtils.isNotBlank(auditSearch.getTargetId())) {
                        criteria.add(Restrictions.eq("tar.targetId", auditSearch.getTargetId()));
                    }

                    if(StringUtils.isNotBlank(auditSearch.getTargetType())) {
                        criteria.add(Restrictions.eq("tar.targetType", auditSearch.getTargetType()));
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