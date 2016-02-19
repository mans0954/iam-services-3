package org.openiam.idm.srvc.msg.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.openiam.idm.srvc.msg.domain.MailTemplateEntity;

import javax.naming.InitialContext;
import java.util.List;

public class MailTemplateDAOImpl implements MailTemplateDAO {

    private static final Log log = LogFactory
            .getLog(MailTemplateDAO.class);

    private SessionFactory sessionFactory;

    @Override
    public MailTemplateEntity add(MailTemplateEntity transientInstance) {
        try {
            sessionFactory.getCurrentSession().persist(transientInstance);
            return transientInstance;
        } catch (HibernateException re) {
            log.error("persist failed", re);
            throw re;
        }
    }

    @Override
    public void remove(MailTemplateEntity persistentInstance) {
        try {
            sessionFactory.getCurrentSession().delete(persistentInstance);
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }

    @Override
    public MailTemplateEntity update(MailTemplateEntity detachedInstance) {
        try {
            MailTemplateEntity result = (MailTemplateEntity) sessionFactory
                    .getCurrentSession().merge(detachedInstance);
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    @Override
    public MailTemplateEntity findById(String id) {
        try {
            MailTemplateEntity instance = (MailTemplateEntity) sessionFactory
                    .getCurrentSession()
                    .get(MailTemplateEntity.class,id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }

    @Override
    public List<MailTemplateEntity> findAll() {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(MailTemplateEntity.class).addOrder(Order.asc("name"));

            List<MailTemplateEntity> results = (List<MailTemplateEntity>)criteria.list();
            return results;
        } catch (HibernateException re) {
            log.error("get failed", re);
            throw re;
        }
    }

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
}
