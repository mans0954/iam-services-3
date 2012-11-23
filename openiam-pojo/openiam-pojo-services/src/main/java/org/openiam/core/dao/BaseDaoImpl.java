package org.openiam.core.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

public abstract class BaseDaoImpl<T, PrimaryKey extends Serializable> implements BaseDao<T, PrimaryKey> {
    protected final Log log = LogFactory.getLog(this.getClass());
    protected final Class<T> domainClass;

    @Autowired
    @Qualifier("sessionFactory")
    protected SessionFactory sessionFactory;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public BaseDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        Type arg;
        if (t instanceof ParameterizedType) {
            arg = ((ParameterizedType) t).getActualTypeArguments()[0];
        } else if (t instanceof Class) {
            arg = ((ParameterizedType) ((Class) t).getGenericSuperclass()).getActualTypeArguments()[0];

        } else {
            throw new RuntimeException("Can not handle type construction for '" + getClass() + "'!");
        }

        if (arg instanceof Class) {
            this.domainClass = (Class<T>) arg;
        } else if (arg instanceof ParameterizedType) {
            this.domainClass = (Class<T>) ((ParameterizedType) arg).getRawType();
        } else {
            throw new RuntimeException("Problem determining generic class for '" + getClass() + "'! ");
        }
    }
    
    protected Criteria getExampleCriteria(T t) {
    	return getCriteria().add(Example.create(t));
    }

    @Override
	public List<T> getByExample(T t, int startAt, int size) {
		final Criteria criteria = getExampleCriteria(t);
		if(startAt > -1) {
			criteria.setFirstResult(startAt);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
		
		return (List<T>)criteria.list();
	}



	@Override
	public List<T> getByExample(T t) {
		return getByExample(t, -1, -1);
	}

	@Override
	public int count(T t) {
		return ((Number)getExampleCriteria(t).setProjection(rowCount())
                .uniqueResult()).intValue();
    }

	protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }
    
    protected Criteria getCriteria() {
    	return sessionFactory.getCurrentSession().createCriteria(domainClass);
    }

    @SuppressWarnings({"unchecked"})
    public T findById(PrimaryKey id) {
        if (id == null) {
            return null;
        }
        return (T) this.getSession().get(domainClass, id);
    }
    
    @SuppressWarnings("unchecked")
	public List<T> findByIds(Collection<PrimaryKey> idCollection) {
    	if(CollectionUtils.isEmpty(idCollection)) {
    		return (List<T>)Collections.EMPTY_LIST;
    	}
    	
    	final Criteria criteria = getSession().createCriteria(domainClass)
    							.add(Restrictions.in(getPKfieldName(), idCollection));
    	return criteria.list();
    }
    
    @SuppressWarnings("unchecked")
	public List<T> findByIds(Collection<PrimaryKey> idCollection, final int from, final int size) {
    	if(CollectionUtils.isEmpty(idCollection)) {
    		return (List<T>)Collections.EMPTY_LIST;
    	}
    	
    	final Criteria criteria = getSession().createCriteria(domainClass)
    							.add(Restrictions.in(getPKfieldName(), idCollection));
    	
		if(from > -1) {
			criteria.setFirstResult(from);
		}
		
		if(size > -1) {
			criteria.setMaxResults(size);
		}
    	return criteria.list();
    }

    @SuppressWarnings({"unchecked"})
    public T findById(PrimaryKey id, String... fetchFields) {
        if (id == null) {
            return null;
        }
        Criteria criteria = sessionFactory
                .getCurrentSession()
                .createCriteria(domainClass)
                .add(eq(getPKfieldName(), id))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (fetchFields != null) {
            for (String field : fetchFields) {
                criteria.setFetchMode(field, FetchMode.JOIN);
            }
        }
        return (T) criteria.uniqueResult();
    }

    protected abstract String getPKfieldName();

    @SuppressWarnings({"unchecked"})
    public List<T> findAll() {
        return sessionFactory.getCurrentSession()
                .createCriteria(domainClass)
                .list();
    }

    public Long countAll() {
        return ((Number)
                sessionFactory.getCurrentSession()
                        .createCriteria(domainClass)
                        .setProjection(rowCount())
                        .uniqueResult())
                .longValue();
    }

    public void save(T entity) {
        sessionFactory.getCurrentSession()
                .saveOrUpdate(entity);
    }

    public void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    public void save(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        Session session = sessionFactory.getCurrentSession();
        for (T entity : entities) {
            session.saveOrUpdate(entity);
        }
    }

    @Override
	public void update(T t) {
		sessionFactory.getCurrentSession().update(t);
	}

	@Override
	public void merge(T t) {
		sessionFactory.getCurrentSession().merge(t);
	}

	@Transactional
    public void deleteAll() throws Exception{
        sessionFactory.getCurrentSession().createQuery("delete from "+this.domainClass.getName()).executeUpdate();
    }

    public void attachDirty(T t) {
        log.debug("attaching dirty instance");
        try {
            this.save(t);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }

    public void attachClean(T t) {
        log.debug("attaching clean instance");
        try {
            sessionFactory.getCurrentSession().buildLockRequest(LockOptions.NONE).lock(t);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}

