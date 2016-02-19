package org.openiam.idm.srvc.msg.service;

// Generated Nov 27, 2009 11:18:13 PM by Hibernate Tools 3.2.2.GA

import java.util.List;

import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openiam.idm.srvc.msg.domain.NotificationEntity;
import org.openiam.idm.srvc.msg.dto.NotificationType;


/**
 * Home object for domain model class SysMessageDelivery.
 * @see org.openiam.idm.srvc.msg.domain.NotificationEntity
 * @author Hibernate Tools
 */
public class NotificationDAOImpl implements NotificationDAO {

	private static final Log log = LogFactory
			.getLog(NotificationDAO.class);

	private SessionFactory sessionFactory;
	

	public void setSessionFactory(SessionFactory session) {
		   this.sessionFactory = session;
	}

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext().lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public NotificationEntity add(NotificationEntity transientInstance) {
		try {
			sessionFactory.getCurrentSession().save(transientInstance);
			return transientInstance;
		} catch (HibernateException re) {
			log.error("persist failed", re);
			throw re;
		}
	}


	public void remove(NotificationEntity persistentInstance) {
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public NotificationEntity update(NotificationEntity detachedInstance) {
		try {
            NotificationEntity result = (NotificationEntity) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public NotificationEntity findById(java.lang.String id) {
		try {
            NotificationEntity instance = (NotificationEntity) sessionFactory
					.getCurrentSession()
					.get(NotificationEntity.class,id);
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

    @Override
    public NotificationEntity findByName(String name) {
        try {
            NotificationEntity instance = (NotificationEntity) sessionFactory
                    .getCurrentSession()
                    .createCriteria(NotificationEntity.class)
                    .add(Restrictions.eq("name",name)).uniqueResult();
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    /* (non-Javadoc)
      * @see org.openiam.idm.srvc.msg.service.SysMessageDAO#findAll()
      */
	public List<NotificationEntity> findAll() {
		try {
			Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(NotificationEntity.class).addOrder(Order.asc("name"));

			List<NotificationEntity> results = (List<NotificationEntity>)criteria.list();
			return results;
		} catch (HibernateException re) {
			log.error("get failed", re);
			throw re;
		}
	}

    @Override
    public List<NotificationEntity> findConfigurableList() {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(NotificationEntity.class)
                    .add(Restrictions.eq("type", NotificationType.CONFIGURABLE))
                    .addOrder(Order.asc("name"));

            List<NotificationEntity> results = (List<NotificationEntity>)criteria.list();
            return results;
        } catch (HibernateException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public List<NotificationEntity> findSystemList() {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(NotificationEntity.class)
                    .add(Restrictions.eq("type", NotificationType.SYSTEM))
                    .addOrder(Order.asc("name"));

            List<NotificationEntity> results = (List<NotificationEntity>)criteria.list();
            return results;
        } catch (HibernateException re) {
            log.error("get failed", re);
            throw re;
        }
    }
}
