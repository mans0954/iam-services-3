package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openiam.idm.srvc.recon.dto.ReconResultDetail;


/**
 * Home object for domain model class ReconResultDetail.
 * @see org.openiam.idm.srvc.pswd.service.ReconResultDetail
 * @author Hibernate Tools
 */
public class ReconResultDetailDAOImpl implements ReconResultDetailDAO {

	private static final Log log = LogFactory
			.getLog(ReconResultDetailDAO.class);

	private SessionFactory sessionFactory;

	
	public void setSessionFactory(SessionFactory session) {
		   this.sessionFactory = session;
	}

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void add(ReconResultDetail transientInstance) {
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}


	public void remove(ReconResultDetail persistentInstance) {
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ReconResultDetail update(ReconResultDetail detachedInstance) {
		try {
			ReconResultDetail result = (ReconResultDetail) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ReconResultDetail findById(java.lang.String id) {
		try {
			ReconResultDetail instance = (ReconResultDetail) sessionFactory
					.getCurrentSession()
					.get("org.openiam.idm.srvc.pswd.service.ReconResultDetail",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

}
