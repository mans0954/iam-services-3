package org.openiam.core.dao;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.openiam.base.OrderConstants;
import org.openiam.base.ws.SortParam;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.openiam.internationalization.LocalizedDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hibernate.criterion.Projections.id;
import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

public abstract class BaseDaoImpl<T, PrimaryKey extends Serializable> extends HibernateDaoSupport
        implements BaseDao<T, PrimaryKey> {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected final Class<T> domainClass;

    protected static final int MAX_IN_CLAUSE = 1000;
	@Autowired
	public void setTemplate(final @Qualifier("hibernateTemplate") HibernateTemplate hibernateTemplate) {
		super.setHibernateTemplate(hibernateTemplate);
	}
	
	protected boolean cachable() {
		return false;
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
    protected void setOderByCriteria(Criteria criteria, AbstractSearchBean sb) {
        List<SortParam> sortParamList = sb.getSortBy();
        for (SortParam sort: sortParamList){
            criteria.addOrder(createOrder(sort.getSortBy(), sort.getOrderBy()));
        }
    }
    protected Order createOrder(String field, OrderConstants orderDir){
        return orderDir.equals(OrderConstants.DESC) ? Order.desc(field) : Order.asc(field);
    }

    @Override
    public int count(final SearchBean searchBean) {
    	 return ((Number) getExampleCriteria(searchBean).setProjection(rowCount())
                 .uniqueResult()).intValue();
    }

    @Override
    public void flush() {
    	getSession().flush();
    }

    @Override
    public void clear() {
        getSession().clear();
    }

    @Override
    @LocalizedDatabaseGet
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

    public List<T> getByExampleNoLocalize(T t, int startAt, int size) {
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
    public List<String> getIDsByExample(SearchBean searchBean, int from, int size) {
        final Criteria criteria = getExampleCriteria(searchBean);
        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        criteria.setProjection(Projections.id());
        return (List<String>)criteria.list();
    }

    @Override
    @LocalizedDatabaseGet
    public List<T> getByExample(final SearchBean searchBean) {
    	return getByExample(searchBean, -1, -1);
    }

    @Override
    @LocalizedDatabaseGet
    public List<T> getByExample(final SearchBean searchBean, int from, int size) {
    	 final Criteria criteria = getExampleCriteria(searchBean);
         if (from > -1) {
             criteria.setFirstResult(from);
         }

         if (size > -1) {
             criteria.setMaxResults(size);
         }

        if (searchBean instanceof AbstractSearchBean) {
            AbstractSearchBean sb = (AbstractSearchBean)searchBean;
//            if (StringUtils.isNotBlank(sb.getSortBy())) {
//                criteria.addOrder(sb.getOrderBy().equals(OrderConstants.DESC) ?
//                        Order.desc(sb.getSortBy()) :
//                        Order.asc(sb.getSortBy()));
//            }

            if(CollectionUtils.isNotEmpty(sb.getSortBy())){
                this.setOderByCriteria(criteria, sb);
            }
        }
         return (List<T>) criteria.list();
    }

    @Override
    @LocalizedDatabaseGet
    public List<T> getByExample(T t) {
        return getByExample(t, -1, -1);
    }

    public List<T> getByExampleNoLocalize(T t) {
        return getByExample(t, -1, -1);
    }

    @Override
    public List<T> getByExampleNoLocalize(SearchBean searchBean, int from, int size) {
        return this.getByExample(searchBean, from, size);
    }

    @Override
    public int count(T t) {
        return ((Number) getExampleCriteria(t).setProjection(rowCount())
                .uniqueResult()).intValue();
    }

    protected Criteria getCriteria() {
        return getSession().createCriteria(domainClass).setCacheable(cachable());//.setCacheRegion("org.hibernate.cache.StandardQueryCache");
    }

    @SuppressWarnings({ "unchecked" })
    @LocalizedDatabaseGet
    public T findById(PrimaryKey id) {
        if (id == null) {
            return null;
        }
        return (T) getCriteria().add(eq(getPKfieldName(), id)).uniqueResult(); //this.getSession().get(domainClass, id);
    }

    @SuppressWarnings({ "unchecked" })
    public T findByIdNoLocalized(PrimaryKey id, String ... fetchFields) {
        if (id == null) {
            return null;
        }
        return (T) getCriteria().add(eq(getPKfieldName(), id)).uniqueResult(); //this.getSession().get(domainClass, id);
    }
    
    /**
     * So... the reason for this method, is that findById was returning a non-intialized object.  WHen setting attributes on Resources,
     * Groups, Roles, etc, this casued a TransientObjectException.  The only thing that fixed that, was by calling this method,
     * which calles Session.get.  According to the Hibernate docs, 'get' never returns a non-initialized object.
     * Consider removing this in 3.2
     * @param id
     * @return
     */
    @LocalizedDatabaseGet
    public T findInitializedObjectById(PrimaryKey id) {
    	final Object o = this.getSession().get(domainClass, id);
    	return (o != null) ? (T)o : null;
    }

    @SuppressWarnings("unchecked")
    @LocalizedDatabaseGet
    public List<T> findByIds(Collection<PrimaryKey> idCollection) {
        return findByIds(idCollection,-1,-1);
    }

    @SuppressWarnings("unchecked")
    @LocalizedDatabaseGet
    public List<T> findByIds(Collection<PrimaryKey> idCollection,  final int from, final int size) {
        if (CollectionUtils.isEmpty(idCollection)) {
            return (List<T>) Collections.EMPTY_LIST;
        }
        final Criteria criteria = getCriteria().add( createInClauseForList(new ArrayList<PrimaryKey>(idCollection)));

        if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        return criteria.list();
    }

    @SuppressWarnings({ "unchecked" })
    @LocalizedDatabaseGet
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
    @LocalizedDatabaseGet
    public List<T> findAll() {
        return getCriteria().list();
    }

    public List<PrimaryKey> getAllIds(){
        Criteria criteria = getCriteria().setProjection(id());
        return criteria.list();
    }

    public Long countAll() {
        return ((Number) getCriteria().setProjection(rowCount())
                .uniqueResult()).longValue();
    }
    @Transactional
    @LocalizedDatabaseOperation(saveOrUpdate=true)
    public void save(T entity) {
    	if(entity != null) {
    		getSession().saveOrUpdate(entity);
    	}
    }
    @Transactional
    @LocalizedDatabaseOperation(saveOrUpdate=true)
    public void refresh(T entity) {
        if(entity != null) {
            getSession().refresh(entity);
        }
    }
    @Transactional
    @LocalizedDatabaseOperation(saveOrUpdate=true)
    public  T add(T entity){
        if(entity!=null){
        	getSession().persist(entity);
        }
        return entity;
    }
    @Transactional
    @LocalizedDatabaseOperation(delete=true)
    public void delete(T entity) {
    	if(entity != null) {
    		getSession().delete(entity);
    	}
    }
    @Transactional
    @LocalizedDatabaseOperation(saveOrUpdate=true)
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
    @LocalizedDatabaseOperation(saveOrUpdate=true)
    public void update(T t) {
    	if(t != null) {
    		getSession().update(t);
    	}
    }

    @Override
    @Transactional
    @LocalizedDatabaseOperation(saveOrUpdate=true)
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

    @Override
    @Transactional
    public void persist(T t) {
        try {
            if(t != null) {
                getSession().persist(t);
            }
        } catch (RuntimeException re) {
            log.error("persist failed", re);
            throw re;
        }
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

    public void evict(T t) {
        log.debug("evicting instance");
        try {
            getSession().evict(t);
            log.debug("evict successful");
        } catch (RuntimeException re) {
            log.error("evict failed", re);
            throw re;
        }
    }


    protected Disjunction createInClauseForList(List<PrimaryKey> idCollection) {
        Disjunction orClause = Restrictions.disjunction();
        int start = 0;
        int end = 0;
        while (start < idCollection.size()) {
            end = start + MAX_IN_CLAUSE;
            if (end > idCollection.size()) {
                end = idCollection.size();
            }
            orClause.add(Restrictions.in(getPKfieldName(), idCollection.subList(start, end)));
            start = end;
        }
        return orClause;
    }
}
