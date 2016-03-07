package org.openiam.idm.srvc.synch.service;

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.openiam.idm.srvc.synch.domain.ActivityLogDetail;

import static org.hibernate.criterion.Example.create;

/**
 * Home object for domain model class ActivityLogDetail.
 */
public class ActivityLogDetailHome {

	private static final Log log = LogFactory
			.getLog(ActivityLogDetailHome.class);

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

	public void persist(ActivityLogDetail transientInstance) {
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(ActivityLogDetail instance) {
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(ActivityLogDetail instance) {
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(ActivityLogDetail persistentInstance) {
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public ActivityLogDetail merge(ActivityLogDetail detachedInstance) {
		try {
			ActivityLogDetail result = (ActivityLogDetail) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public ActivityLogDetail findById(java.lang.String id) {
		try {
			ActivityLogDetail instance = (ActivityLogDetail) sessionFactory
					.getCurrentSession()
					.get("org.openiam.idm.srvc.pswd.service.ActivityLogDetail",
							id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List<ActivityLogDetail> findByExample(ActivityLogDetail instance) {
		try {
			List<ActivityLogDetail> results = (List<ActivityLogDetail>) sessionFactory
					.getCurrentSession()
					.createCriteria(
							"org.openiam.idm.srvc.pswd.service.ActivityLogDetail")
					.add(create(instance)).list();
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}
