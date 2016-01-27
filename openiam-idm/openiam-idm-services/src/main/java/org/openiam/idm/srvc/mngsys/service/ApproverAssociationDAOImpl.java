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
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.idm.srvc.mngsys.bean.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.AssociationType;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.springframework.stereotype.Repository;


@Repository("approverAssociationDAO")
public class ApproverAssociationDAOImpl extends BaseDaoImpl<ApproverAssociationEntity, String> implements ApproverAssociationDAO {

	private static final Log log = LogFactory.getLog(ApproverAssociationDAOImpl.class);
	
	
	
	
	@Override
	protected Criteria getExampleCriteria(SearchBean searchBean) {
		final Criteria criteria = getCriteria();
		if(searchBean != null && searchBean instanceof ApproverAssocationSearchBean) {
			final ApproverAssocationSearchBean sb = (ApproverAssocationSearchBean)searchBean;
			if(StringUtils.isNotBlank(sb.getKey())) {
				criteria.add(Restrictions.eq("id", sb.getKey()));
			} else {
				if(sb.getAssociationType() != null) {
					criteria.add(Restrictions.eq("associationType", sb.getAssociationType()));
				}
				if(StringUtils.isNotBlank(sb.getAssociationEntityId())) {
					criteria.add(Restrictions.eq("associationEntityId", sb.getAssociationEntityId()));
				}
				if(StringUtils.isNotBlank(sb.getRequestType())) {
					criteria.add(Restrictions.eq("requestType", sb.getRequestType()));
				}
				if(StringUtils.isNotBlank(sb.getOnApproveEntityId())) {
					criteria.add(Restrictions.eq("onApproveEntityId", sb.getOnApproveEntityId()));
				}
				if(sb.getOnApproveEntityType() != null) {
					criteria.add(Restrictions.eq("onApproveEntityType", sb.getOnApproveEntityType()));
				}
				if(StringUtils.isNotBlank(sb.getOnRejectEntityId())) {
					criteria.add(Restrictions.eq("onRejectEntityId", sb.getOnRejectEntityId()));
				}
				if(sb.getOnRejectEntityType() != null) {
					criteria.add(Restrictions.eq("onRejectEntityType", sb.getOnRejectEntityType()));
				}
				if(StringUtils.isNotBlank(sb.getApproverEntityId())) {
					criteria.add(Restrictions.eq("approverEntityId", sb.getApproverEntityId()));
				}
				if(sb.getApproverEntityType() != null) {
					criteria.add(Restrictions.eq("approverEntityType", sb.getApproverEntityType()));
				}
				if(sb.getApproverLevel() != null) {
					criteria.add(Restrictions.eq("approverLevel", sb.getApproverLevel()));
				}
				criteria.addOrder(Order.asc("approverLevel"));
			}
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
			final ApproverAssocationSearchBean sb = new ApproverAssocationSearchBean();
			sb.setAssociationEntityId(associationId);
			sb.setAssociationType(associationType);
			return getByExample(sb);
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public List<ApproverAssociationEntity> getByApprover(String associationId,
			AssociationType associationType) {
		if(StringUtils.isNotBlank(associationId) && associationType != null) {
			final ApproverAssocationSearchBean sb = new ApproverAssocationSearchBean();
			sb.setApproverEntityId(associationId);
			sb.setApproverEntityType(associationType);
			return getByExample(sb);
		} else {
			return Collections.EMPTY_LIST;
		}
	}
}
