package org.openiam.idm.srvc.recon.service;

// Generated May 29, 2010 8:20:09 PM by Hibernate Tools 3.2.2.GA

import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.openiam.idm.srvc.recon.dto.ReconciliationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


/**
 * Home object for domain model class ReconiliationResult.
 * @see org.openiam.idm.srvc.recon.service.ReconciliationResultDAO
 * @author Hibernate Tools
 */

@Repository
public class ReconciliationResultDAOImpl implements ReconciliationResultDAO {

	private static final Log log = LogFactory
			.getLog(ReconciliationResultDAO.class);

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

	public void add(ReconciliationResult transientInstance) {
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}



	public void remove(ReconciliationResult persistentInstance) {
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ReconciliationResult update(ReconciliationResult detachedInstance) {
		try {
			ReconciliationResult result = (ReconciliationResult) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ReconciliationResult findById(java.lang.String id) {
		try {
			ReconciliationResult instance = (ReconciliationResult) sessionFactory
					.getCurrentSession()
					.get(
							"org.openiam.idm.srvc.recon.dto.ReconiliationResult",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}


}
