package org.openiam.idm.srvc.synch.service;

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.openiam.idm.srvc.synch.domain.SynchActivityLog;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class SynchActivityLog.
 */
public class SynchActivityLogHome {

	private static final Log log = LogFactory
			.getLog(SynchActivityLogHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

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

	public void persist(SynchActivityLog transientInstance) {
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(SynchActivityLog instance) {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(SynchActivityLog instance) {
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(SynchActivityLog persistentInstance) {
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public SynchActivityLog merge(SynchActivityLog detachedInstance) {
		try {
			SynchActivityLog result = (SynchActivityLog) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public SynchActivityLog findById(java.lang.String id) {
		try {
			SynchActivityLog instance = (SynchActivityLog) sessionFactory
					.getCurrentSession()
					.get("org.openiam.idm.srvc.pswd.service.SynchActivityLog",
							id);
			
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<SynchActivityLog> findByExample(SynchActivityLog instance) {
		try {
			List<SynchActivityLog> results = (List<SynchActivityLog>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"org.openiam.idm.srvc.pswd.service.SynchActivityLog")
					.add(create(instance)).list();
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
