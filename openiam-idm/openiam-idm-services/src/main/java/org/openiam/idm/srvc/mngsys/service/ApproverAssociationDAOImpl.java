package org.openiam.idm.srvc.mngsys.service;

import java.util.Collections;
import java.util.List;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.springframework.stereotype.Repository;


@Repository("approverAssociationDAO")
public class ApproverAssociationDAOImpl extends BaseDaoImpl<ApproverAssociationEntity, String> implements ApproverAssociationDAO {

	private static final Log log = LogFactory.getLog(ApproverAssociationDAOImpl.class);
	
	@Override
	protected Criteria getExampleCriteria(ApproverAssociationEntity entity) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(entity.getId())) {
			criteria.add(Restrictions.eq("id", entity.getId()));
		} else {
			if(entity.getAssociationType() != null) {
				criteria.add(Restrictions.eq("associationType", entity.getAssociationType()));
			}
			if(StringUtils.isNotBlank(entity.getAssociationEntityId())) {
				criteria.add(Restrictions.eq("associationEntityId", entity.getAssociationEntityId()));
			}
			if(StringUtils.isNotBlank(entity.getRequestType())) {
				criteria.add(Restrictions.eq("requestType", entity.getRequestType()));
			}
			if(StringUtils.isNotBlank(entity.getOnApproveEntityId())) {
				criteria.add(Restrictions.eq("onApproveEntityId", entity.getOnApproveEntityId()));
			}
			if(entity.getOnApproveEntityType() != null) {
				criteria.add(Restrictions.eq("onApproveEntityType", entity.getOnApproveEntityType()));
			}
			if(StringUtils.isNotBlank(entity.getOnRejectEntityId())) {
				criteria.add(Restrictions.eq("onRejectEntityId", entity.getOnRejectEntityId()));
			}
			if(entity.getOnRejectEntityType() != null) {
				criteria.add(Restrictions.eq("onRejectEntityType", entity.getOnRejectEntityType()));
			}
			if(StringUtils.isNotBlank(entity.getApproverEntityId())) {
				criteria.add(Restrictions.eq("approverEntityId", entity.getApproverEntityId()));
			}
			if(entity.getApproverEntityType() != null) {
				criteria.add(Restrictions.eq("approverEntityType", entity.getApproverEntityType()));
			}
			if(entity.getApproverLevel() != null) {
				criteria.add(Restrictions.eq("approverLevel", entity.getApproverLevel()));
			}
			criteria.addOrder(Order.asc("approverLevel"));
		}
		return criteria;
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}

	@Override
	public List<ApproverAssociationEntity> getByAssociation(final String associationId, final AssociationType associationType) {
		if(StringUtils.isNotBlank(associationId) && associationType != null) {
			final ApproverAssociationEntity example = new ApproverAssociationEntity();
			example.setAssociationEntityId(associationId);
			example.setAssociationType(associationType);
			return getByExample(example);
		} else {
			return Collections.EMPTY_LIST;
		}
	}
}
