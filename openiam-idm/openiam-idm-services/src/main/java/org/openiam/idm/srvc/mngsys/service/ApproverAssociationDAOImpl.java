package org.openiam.idm.srvc.mngsys.service;

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
import org.hibernate.criterion.Restrictions;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.springframework.stereotype.Repository;


@Repository("approverAssociationDAO")
public class ApproverAssociationDAOImpl extends BaseDaoImpl<ApproverAssociationEntity, String> implements ApproverAssociationDAO {

	private static final Log log = LogFactory.getLog(ApproverAssociationDAOImpl.class);
	
	@Override
	protected Criteria getExampleCriteria(ApproverAssociationEntity entity) {
		final Criteria criteria = getCriteria();
		if(StringUtils.isNotBlank(entity.getRequestType())) {
			criteria.add(Restrictions.eq("requestType", entity.getRequestType()));
		}
		if(entity.getApproverLevel() != null) {
			criteria.add(Restrictions.eq("approverLevel", entity.getApproverLevel()));
		}
		return criteria;
	}

	@Override
	public List<ApproverAssociationEntity> findApproversByRequestType(String requestType, int level) {
		final ApproverAssociationEntity example = new ApproverAssociationEntity();
		example.setRequestType(requestType);
		example.setApproverLevel(level);
		return getByExample(example);
	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
