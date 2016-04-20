package org.openiam.core.dao.lucene;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.SearchException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractHibernateSearchDao<T, Q, KeyType> extends HibernateDaoSupport implements HibernateSearchDao<T, Q, KeyType>, DisposableBean, InitializingBean {

	protected static Logger logger = Logger.getLogger(AbstractHibernateSearchDao.class);
	
	private final ReentrantLock reentrantLock = new ReentrantLock();

	//object is singleton, so it safe to have this field
	private transient Date lastUpdateDBDate;
	private transient Date reindexingCompletedOn;
	@Value("${org.openiam.usersearch.lucene.reindex.enabled}")
	private Boolean rebuildIndexesAtInit = true;
	private transient long reindexDuration;
	
	private String lastModifiedFieldName;
	private String idFieldName;

	@Autowired
	public void setTemplate(final @Qualifier("hibernateTemplate") HibernateTemplate hibernateTemplate) {
		super.setHibernateTemplate(hibernateTemplate);
	}

    static {
        BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
    }

    private Properties hibernateProperties;

    public void setRebuildIndexesAtInit(final boolean rebuildIndexesAtInit) {
		this.rebuildIndexesAtInit = rebuildIndexesAtInit;
	}

	@Resource(name = "hibernateProperties")
    public void setHibernateProperties(final Properties hibernateProperties) {
    	final String directoryName = String.format("%s.", getEntityClass().getName());
    	this.hibernateProperties = new Properties();
    	for (final Object key : hibernateProperties.keySet()) {
    		String _key = key.toString();
    		if (_key.startsWith("hibernate.search.")) {
    			_key = _key.substring("hibernate.search.".length());
    			if (_key.startsWith(directoryName)) {
    				_key = _key.substring(directoryName.length());
    			} else if (_key.startsWith("default.")) {
    				_key = _key.substring("default.".length());
    			}
    			this.hibernateProperties.put(_key, hibernateProperties.get(key));
    		}
    	}
	}

    @Override public int count(final Q query) {
    	int count = 0;
    	if (query != null) {
            final Query luceneQuery = parse(query);
            if (luceneQuery == null) {
            	// count all objects
                final Criteria criteria = getSession().createCriteria(getEntityClass()).setCacheable(true);
        		criteria.setProjection(Projections.rowCount());
        		count = ((Number) criteria.uniqueResult()).intValue();
            } else {
            	count = count(buildFullTextSessionQuery(getFullTextSession(), luceneQuery, null));
            }
    	}
        return count;
    }

    @Override public List<T> find(final int from, final int size, final SortType sort, final Q query) {
    	List<T> result = Collections.emptyList();
    	if ((from >=0) && (size > 0) && (query != null)) {
            final Query luceneQuery = parse(query);
            if (luceneQuery != null) {
            	result = find(buildFullTextSessionQuery(getFullTextSession(), luceneQuery, from, size, sort));
            }
    	}
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override public List<KeyType> findIds(final SortType sort, final Q query) {
    	final List<KeyType> result = new ArrayList<KeyType>();
    	if ((query != null)) {
            final Query luceneQuery = parse(query);
            if (luceneQuery != null) {
				final List idList = findIds(buildFullTextSessionQuery(getFullTextSession(), luceneQuery, sort).setProjection(idFieldName));
				for (final Object row : idList) {
					final Object[] columns = (Object[]) row;
					final KeyType id = (KeyType) columns[0];
					result.add(id);
				}
            }
    	}
        return result;
    }

    @SuppressWarnings("unchecked")
	@Override public List<KeyType> findIds(final int from, final int size, final SortType sort, final Q query) {
    	final List<KeyType> result = new ArrayList<KeyType>();
    	if ((from >=0) && (size > 0) && (query != null)) {
            final Query luceneQuery = parse(query);
            if (luceneQuery != null) {
				final List idList = findIds(buildFullTextSessionQuery(
						getFullTextSession(), luceneQuery, from, size, sort)
						.setProjection(idFieldName));
				for (final Object row : idList) {
					final Object[] columns = (Object[]) row;
					final KeyType id = (KeyType) columns[0];
					result.add(id);
				}
            }
    	}
        return result;
    }

    protected FullTextQuery buildFullTextSessionQuery(final FullTextSession fullTextSession, final Query luceneQuery,
            final int from, final int size, final SortType sort) {
        return buildFullTextSessionQuery(fullTextSession, luceneQuery, sort).setMaxResults(size).setFirstResult(from);
    }

    protected FullTextQuery buildFullTextSessionQuery(final FullTextSession fullTextSession, final Query luceneQuery, final SortType sort) {
        final FullTextQuery hiberQuery = fullTextSession.createFullTextQuery(luceneQuery, getEntityClass());
        hiberQuery.setReadOnly(true);
        hiberQuery.setCacheable(true);
        final Sort sortField = (sort == null) ? null : sort.getSort();
        if (sortField != null) {
        	hiberQuery.setSort(sortField);
        }
        return hiberQuery;
    }

    protected FullTextSession getFullTextSession() {
    	return Search.getFullTextSession(getSession());
    }

    @SuppressWarnings({ "unchecked" })
    protected List<T> find(final FullTextQuery fullTextQuery) {
        return fullTextQuery.list();
    }

    @SuppressWarnings({ "unchecked" })
    protected List<Object> findIds(final FullTextQuery fullTextQuery) {
        return fullTextQuery.list();
    }

    protected int count(final FullTextQuery fullTextQuery) {
        return fullTextQuery.getResultSize();
    }
    
    protected int getMaxFetchSizeOnReinex() {
    	return 1000;
    }

    protected abstract Query parse(Q query);
    protected abstract Class<T> getEntityClass();

    private void buidIndexes() throws Exception {
    	final DetachedCriteria criteria = DetachedCriteria.forClass(getEntityClass()).addOrder(Order.asc(idFieldName));
        try {
        	doIndex(criteria, true);
    	} catch (SearchException e) {
    		logger.error("Can't reindex", e);
    		throw e;
    		/*
    		//clean-up lucene indexes directory
    		final SearchFactory searchFactory = getFullTextSession().getSearchFactory();
    		if (searchFactory instanceof SearchFactoryImplementor) {
    			final Class<T> entityClass = getEntityClass();
    			final String directoryProviderName = entityClass.getName();
    			DirectoryProviderHelper.getSourceDirectory(directoryProviderName, hibernateProperties, true).delete();
    			FileUtils.deleteDirectory(DirectoryProviderHelper.getSourceDirectory(directoryProviderName, hibernateProperties, true));
    			for (final DirectoryProvider<?> directoryProvider : searchFactory.getDirectoryProviders(entityClass)) {
    				directoryProvider.initialize(directoryProviderName, hibernateProperties, (SearchFactoryImplementor) searchFactory);
    			}
    			//trying to build indexes again
    			//don't call buidIndexes(), cause if it's possible to get short circle recursion
    			doIndex(criteria, true);
    		} else {
    			//just rethrow exception
    			throw e;
    		}
    		*/
    	}
    }

    @SuppressWarnings("unchecked")
	private void doIndex(final DetachedCriteria load, final boolean purgeAll) {
        final FullTextSession fullTextSession = getFullTextSession();
        fullTextSession.setFlushMode(FlushMode.COMMIT);
        //fullTextSession.setCacheMode(CacheMode.IGNORE);
        fullTextSession.setCacheMode(CacheMode.REFRESH);
        final Class<T> entityClass = getEntityClass();
        try {
        	if (purgeAll) {
        		fullTextSession.purgeAll(entityClass);
        	}

        	final int maxSize = getMaxFetchSizeOnReinex();
        	final Criteria criteria = load.getExecutableCriteria(fullTextSession);
        	for (int from = 0; ; from += maxSize) {
        		final Transaction transaction = fullTextSession.beginTransaction();
        		try {
        			logger.info(String.format("Fetching from %s, size: %s", from, maxSize));
        			final List<T> list = criteria.setFirstResult(from).setMaxResults(maxSize).list();
        			logger.info(String.format("Fetched from %s, size: %s.  Indexing...", from, maxSize));
                	for (final T entity : list) {
                		fullTextSession.index(entity);
                	}
                	logger.info(String.format("Fetched from %s, size: %s.  Done indexing... committing", from, maxSize));
                	transaction.commit();
                	logger.info(String.format("Fetched from %s, size: %s.  Done indexing... committed", from, maxSize));
                	if (list.isEmpty() || list.size() < maxSize) {
                		break;
                	}
            	} catch (Exception e) {
            		logger.error("Can't index - rolling back", e);
            		transaction.rollback();
            	}
        	}
        } finally {
        	fullTextSession.close();
        }
    }

    @Override public void destroy() throws Exception {
    	hibernateProperties.clear();
    }

    @SuppressWarnings("unchecked")
	@Override public Class<T> getSearchEntityClass() {
		final Type type = ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		return ((Class<T>)type);
    }

    private Date getLastDbUpdateDateInternal() {
    	return (Date)getSession().createCriteria(getEntityClass())
			.setProjection(Projections.max(lastModifiedFieldName)).uniqueResult();
    }
    
    protected Query buildTokenizedClause(final String paramName, final String paramValue) {
    	if (StringUtils.isNotBlank(paramValue) && StringUtils.isNotBlank(paramName)) {
            final BooleanQuery paramsQuery = new BooleanQuery();
            paramsQuery.add(QueryBuilder.buildQuery(paramName, BooleanClause.Occur.SHOULD, paramValue), BooleanClause.Occur.SHOULD);
            return paramsQuery;
        }
    	return null;
	}
    
    protected Query buildExactClause(final String paramName, final String paramValue) {
    	if (StringUtils.isNotBlank(paramValue) && StringUtils.isNotBlank(paramName)) {
    		final BooleanQuery query = new BooleanQuery();
    		query.add(new TermQuery(new Term(paramName, paramValue)), BooleanClause.Occur.SHOULD);
    		return query;
        }
    	return null;
	}

    protected Query buildInClause(final String paramName, final Collection<String> paramValues) {
        if (paramValues!=null && !paramValues.isEmpty() && StringUtils.isNotBlank(paramName)) {
            final BooleanQuery query = new BooleanQuery();
            for( String value : paramValues ){
                query.add(new TermQuery(new Term(paramName, value)), BooleanClause.Occur.SHOULD);
            }
            return query;
        }
        return null;
    }

    @Override public Date getReindexingCompletedOn() {
    	return reindexingCompletedOn;
    }

    @Override public Date getLastDbUpdateDate() {
    	return lastUpdateDBDate;
    }

    @Override public void synchronizeIndexes(final boolean forcePurgeAll) {
    	reentrantLock.lock();
		final StopWatch stopWatch = new StopWatch();
		stopWatch.start();
    	try {
    		boolean reindexed = false;
    		final Date updateDate = getLastDbUpdateDateInternal();
    		if (lastUpdateDBDate == null || forcePurgeAll) {
    			final DetachedCriteria criteria = DetachedCriteria.forClass(getEntityClass()).addOrder(Order.asc(idFieldName));
    			doIndex(criteria, forcePurgeAll);
    			reindexed = true;
    		} else if (((null != updateDate) && (updateDate.after(lastUpdateDBDate)))) {
    			final DetachedCriteria criteria = DetachedCriteria.forClass(getEntityClass()).add(Restrictions.gt(lastModifiedFieldName, lastUpdateDBDate)).addOrder(Order.asc(idFieldName));
    			doIndex(criteria, forcePurgeAll);
    			reindexed = true;
        	}

    		if(reindexed) {
    			lastUpdateDBDate = updateDate;
    			reindexingCompletedOn = new Date(System.currentTimeMillis());
    		}
    	} finally {
    		if (reentrantLock.isHeldByCurrentThread()) {
    			stopWatch.stop();
    			synchronized (this) {
    				reindexDuration = stopWatch.getTime();
				}
    			reentrantLock.unlock();
    		}
    	}
    }

    public long getLastSynchronizationDuration() {
    	synchronized (this) {
        	return reindexDuration;
		}
    }

    public boolean isSynchronizing() {
    	return reentrantLock.isLocked();
    }
    
    private void initMetadata() throws Exception {
    	final Class<T> clazz = getEntityClass();    	
    	if(clazz != null) {
    		if(clazz.getDeclaredFields() != null) {
    			for(Field field : clazz.getDeclaredFields()) {
    				if(field.getAnnotation(LuceneLastUpdate.class) != null) {
    					lastModifiedFieldName = field.getName();
    				} else if(field.getAnnotation(LuceneId.class) != null) {
    					idFieldName = field.getName();
    				}
    			}
    		}
    	}
    	
    	if(clazz == null) {
    		throw new Exception("No class for Search DAO");
    	}
    	
    	if(lastModifiedFieldName == null) {
    		throw new Exception(String.format("No field with the %s annotation on entity %s", clazz, LuceneLastUpdate.class));
    	}
    	
    	if(idFieldName == null) {
    		throw new Exception(String.format("No field with the %s annotation on entity %s", clazz, LuceneId.class));
    	}
    }

	@Override
	public void initDao() throws Exception {
		super.initDao();
		initMetadata();
    	if (rebuildIndexesAtInit) {
    		StopWatch stopWatch = new StopWatch();
    		logger.info(String.format("begin re-indexing %s", getEntityClass().getSimpleName()));
    		stopWatch.start();
			buidIndexes();
    		stopWatch.stop();
			synchronized (this) {
				reindexDuration = stopWatch.getTime();
			}
			lastUpdateDBDate = getLastDbUpdateDateInternal();
			if (lastUpdateDBDate == null) {
				lastUpdateDBDate = new Date(System.currentTimeMillis());
			}
			reindexingCompletedOn = new Date(System.currentTimeMillis());
			logger.info(String.format("end re-indexing %s after %s", getEntityClass().getSimpleName(), stopWatch.toString()));
    	} else {
    		logger.info(String.format("skip re-indexing %s", getEntityClass().getSimpleName()));
    	}
	}
    
    
}