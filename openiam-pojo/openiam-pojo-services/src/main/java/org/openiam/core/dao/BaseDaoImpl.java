package org.openiam.core.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.openiam.idm.searchbeans.SearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

public abstract class BaseDaoImpl<T, PrimaryKey extends Serializable> extends HibernateDaoSupport
        implements BaseDao<T, PrimaryKey> {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected final Class<T> domainClass;

	@Autowired
	public void setTemplate(final @Qualifier("hibernateTemplate") HibernateTemplate hibernateTemplate) {
		super.setHibernateTemplate(hibernateTemplate);
	}

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public BaseDaoImpl() {
        Type t = getClass().getGenericSuperclass();
        Type arg;
        if (t instanceof ParameterizedType) {
            arg = ((ParameterizedType) t).getActualTypeArguments()[0];
        } else if (t instanceof Class) {
            arg = ((ParameterizedType) ((Class) t).getGenericSuperclass())
                    .getActualTypeArguments()[0];

        } else {
            throw new RuntimeException("Can not handle type construction for '"
                    + getClass() + "'!");
        }

        if (arg instanceof Class) {
            this.domainClass = (Class<T>) arg;
        } else if (arg instanceof ParameterizedType) {
            this.domainClass = (Class<T>) ((ParameterizedType) arg)
                    .getRawType();
        } else {
            throw new RuntimeException(
                    "Problem determining generic class for '" + getClass()
                            + "'! ");
        }
    }

    protected Criteria getExampleCriteria(T t) {
        return getCriteria().add(Example.create(t));
    }

    protected Criteria getExampleCriteria(final SearchBean searchBean) {
    	throw new UnsupportedOperationException("Method must be overridden");
    }

    @Override
    public int count(final SearchBean searchBean) {
    	 return ((Number) getExampleCriteria(searchBean).setProjection(rowCount())
                 .uniqueResult()).intValue();
    }

    @Override
    public List<T> getByExample(T t, int startAt, int size) {
        final Criteria criteria = getExampleCriteria(t);
        if (startAt > -1) {
            criteria.setFirstResult(startAt);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        return (List<T>) criteria.list();
    }

    @Override
    public List<T> getByExample(final SearchBean searchBean) {
    	return getByExample(searchBean, -1, -1);
    }

    @Override
    public List<T> getByExample(final SearchBean searchBean, int from, int size) {
    	 final Criteria criteria = getExampleCriteria(searchBean);
         if (from > -1) {
             criteria.setFirstResult(from);
         }

         if (size > -1) {
             criteria.setMaxResults(size);
         }

         return (List<T>) criteria.list();
    }

    @Override
    public List<T> getByExample(T t) {
        return getByExample(t, -1, -1);
    }

    @Override
    public int count(T t) {
        return ((Number) getExampleCriteria(t).setProjection(rowCount())
                .uniqueResult()).intValue();
    }

    protected Criteria getCriteria() {
        return getSession().createCriteria(domainClass);
    }

    @SuppressWarnings({ "unchecked" })
    public T findById(PrimaryKey id) {
        if (id == null) {
            return null;
        }
        return (T) getCriteria().add(eq(getPKfieldName(), id)).uniqueResult(); //this.getSession().get(domainClass, id);
    }

    @SuppressWarnings("unchecked")
    public List<T> findByIds(Collection<PrimaryKey> idCollection) {
        return findByIds(idCollection,-1,-1);
    }

    @SuppressWarnings("unchecked")
    public List<T> findByIds(Collection<PrimaryKey> idCollection,  final int from, final int size) {
        if (CollectionUtils.isEmpty(idCollection)) {
            return (List<T>) Collections.EMPTY_LIST;
        }

        final Criteria criteria = getCriteria().add( Restrictions.in(getPKfieldName(), idCollection));

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    @SuppressWarnings({ "unchecked" })
    public T findById(PrimaryKey id, String... fetchFields) {
        if (id == null) {
            return null;
        }
        Criteria criteria = getCriteria().add(eq(getPKfieldName(), id))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        if (fetchFields != null) {
            for (String field : fetchFields) {
                criteria.setFetchMode(field, FetchMode.JOIN);
            }
        }
        return (T) criteria.uniqueResult();
    }

    protected abstract String getPKfieldName();

    @SuppressWarnings({ "unchecked" })
    public List<T> findAll() {
        return getCriteria().list();
    }

    public Long countAll() {
        return ((Number) getCriteria().setProjection(rowCount())
                .uniqueResult()).longValue();
    }
    @Transactional
    public void save(T entity) {
    	if(entity != null) {
    		getSession().saveOrUpdate(entity);
    	}
    }

    @Transactional
    public  T add(T entity){
        if(entity!=null){
        	getSession().persist(entity);
        }
        return entity;
    }
    @Transactional
    public void delete(T entity) {
    	if(entity != null) {
    		getSession().delete(entity);
    	}
    }
    @Transactional
    public void save(Collection<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return;
        }
        Session session = getSession();
        for (T entity : entities) {
            session.saveOrUpdate(entity);
        }
    }

    @Override
    @Transactional
    public void update(T t) {
    	if(t != null) {
    		getSession().saveOrUpdate(t);
    	}
    }

    @Override
    @Transactional
    public T merge(T t) {
        try {
            if(t != null) {
                return (T)getSession().merge(t);
            }
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
        return t;
    }

    @Transactional
    public void deleteAll() throws Exception {
    	getSession()
                .createQuery("delete from " + this.domainClass.getName())
                .executeUpdate();
    }
    @Transactional
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
        	getSession()
                    .buildLockRequest(LockOptions.NONE).lock(t);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
}
