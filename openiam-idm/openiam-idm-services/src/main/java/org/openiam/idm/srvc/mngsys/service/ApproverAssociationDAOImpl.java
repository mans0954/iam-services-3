package org.openiam.idm.srvc.mngsys.service;

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.openiam.core.dao.BaseDaoImpl;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.springframework.stereotype.Repository;


@Repository("approverAssociationDAO")
public class ApproverAssociationDAOImpl extends BaseDaoImpl<ApproverAssociationEntity, String> implements ApproverAssociationDAO {

	private static final Log log = LogFactory.getLog(ApproverAssociationDAOImpl.class);

	private SessionFactory sessionFactory;
	
	@Override
	public List<ApproverAssociationEntity> findApproversByRequestType(String requestType, int level) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Query qry = session.createQuery("from org.openiam.idm.srvc.mngsys.dto.ApproverAssociation ra " +
                    " where ra.approverLevel = :level and ra.requestType = :requestType " );
            qry.setString("requestType", requestType);
            qry.setInteger("level", level);

            return qry.list();
        } catch (HibernateException re) {
			log.error("get failed", re);
			throw re;
		}

	}

	@Override
	protected String getPKfieldName() {
		return "id";
	}
}
