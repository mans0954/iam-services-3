package org.openiam.core.dao;

import static org.hibernate.criterion.Projections.id;
import static org.hibernate.criterion.Projections.rowCount;
import static org.hibernate.criterion.Restrictions.eq;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.OrderConstants;
import org.openiam.base.ws.SortParam;
import org.openiam.idm.searchbeans.AbstractSearchBean;
import org.openiam.idm.searchbeans.SearchBean;
import org.openiam.internationalization.LocalizedDatabaseGet;
import org.openiam.internationalization.LocalizedDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseDaoImpl<T, PrimaryKey extends Serializable>
implements BaseDao<T, PrimaryKey> {
	protected final Log log = LogFactory.getLog(this.getClass());
    protected final Class<T> domainClass;
    private SessionFactory sessionFactory;
    
    protected static final int MAX_IN_CLAUSE = 1000;
    
    /* 
     * by default we set cachable to false to prevent problems with invalidation of results. 
     * a caller should explicitly set cacheable to true if he wants to cache query results
     */
    protected boolean cachable() {
    	return false;
    }

	@Autowired
	public void setTemplate(final @Qualifier("sessionFactory") SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
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
    @Deprecated
    protected Criteria getExampleCriteria(T t) {
        return getCriteria().add(Example.create(t));
    }

    @Override
	public Class<T> getDomainClass() {
		return domainClass;
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
    

    protected Criterion getStringCriterion(String fieldName, String value, boolean caseInsensitive) {
        Criterion criterion = null;
        MatchMode matchMode = null;
        if (StringUtils.indexOf(value, "*") == 0) {
            matchMode = MatchMode.END;
            value = value.substring(1);
        }
        if (StringUtils.isNotEmpty(value) && StringUtils.indexOf(value, "*") == value.length() - 1) {
            value = value.substring(0, value.length() - 1);
            matchMode = (matchMode == MatchMode.END) ? MatchMode.ANYWHERE : MatchMode.START;
        }

        if (StringUtils.isNotEmpty(value)) {
            if (matchMode != null) {
                criterion = Restrictions.ilike(fieldName, value, matchMode);
            } else {
                criterion = (caseInsensitive) ? Restrictions.eq(fieldName, value).ignoreCase() : Restrictions.eq(fieldName, value);
            }
        }
        return criterion;
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

    protected Session getSession() {
    	return sessionFactory.getCurrentSession();
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
        criteria.setCacheable(this.cachable());
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
    @Deprecated
    public List<T> getByExample(T t, int startAt, int size) {
        final Criteria criteria = getExampleCriteria(t);
        if (startAt > -1) {
            criteria.setFirstResult(startAt);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }
        criteria.setCacheable(this.cachable());
        return (List<T>) criteria.list();
    }

    @Deprecated
    public List<T> getByExampleNoLocalize(T t) {
        return getByExample(t, -1, -1);
    }

    public List<T> getByExampleNoLocalize(SearchBean searchBean, int from, int size) {
        return this.getByExample(searchBean, from, size);
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
/*
            if (StringUtils.isNotBlank(sb.getSortBy())) {
                criteria.addOrder(sb.getOrderBy().equals(OrderConstants.DESC) ?
                        Order.desc(sb.getSortBy()) :
                        Order.asc(sb.getSortBy()));
            }
*/

            if(CollectionUtils.isNotEmpty(sb.getSortBy())){
                this.setOderByCriteria(criteria, sb);
            }
        }
        criteria.setCacheable(this.cachable());
         return (List<T>) criteria.list();
    }

    protected Criteria getCriteria() {
        return getSession().createCriteria(domainClass).setCacheable(cachable());
    }

    @SuppressWarnings({ "unchecked" })
    @LocalizedDatabaseGet
    public T findById(PrimaryKey id) {
        if (id == null) {
            return null;
        }
        return (T) getCriteria().add(eq(getPKfieldName(), id)).setCacheable(true).uniqueResult(); //this.getSession().get(domainClass, id);
    }

    @SuppressWarnings("unchecked")
    @LocalizedDatabaseGet
    public List<T> findByIds(Collection<PrimaryKey> idCollection) {
        return findByIds(idCollection,-1,-1);
    }

    @SuppressWarnings({"unchecked"})
    public T findByIdNoLocalized(PrimaryKey id, String... fetchFields) {
        if (id == null) {
            return null;
        }
        return (T) getCriteria().add(eq(getPKfieldName(), id)).setCacheable(cachable()).uniqueResult(); //this.getSession().get(domainClass, id);
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
        criteria.setCacheable(this.cachable());
        return criteria.list();
    }

    @SuppressWarnings({ "unchecked" })
    @LocalizedDatabaseGet
    public T findById(PrimaryKey id, String... fetchFields) {
        if (id == null) {
            return null;
        }
        Criteria criteria = getCriteria().add(eq(getPKfieldName(), id))
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).setCacheable(true);
        if (fetchFields != null) {
            for (String field : fetchFields) {
                criteria.setFetchMode(field, FetchMode.JOIN);
            }
        }
        criteria.setCacheable(this.cachable());
        return (T) criteria.uniqueResult();
    }

    protected abstract String getPKfieldName();

    @SuppressWarnings({ "unchecked" })
    @LocalizedDatabaseGet
    public List<T> findAll() {
        return getCriteria().setCacheable(this.cachable()).list();
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

	@Override
	public List<T> find(int from, int size) {
		final Criteria criteria = getCriteria();
		if (from > -1) {
            criteria.setFirstResult(from);
        }

        if (size > -1) {
            criteria.setMaxResults(size);
        }

        return (List<T>) criteria.list();
	}
        public void evictCache() {
        this.getSession().getSessionFactory().getCache().evictDefaultQueryRegion();
    }

    public void evictCollectionRegions() {
        this.getSession().getSessionFactory().getCache().evictCollectionRegions();
    }
    
}
