package org.openiam.core.dao.lucene;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openiam.base.ws.MatchType;
import org.openiam.base.ws.SearchMode;
import org.openiam.elasticsearch.service.ElasticsearchProvider;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

//import org.hibernate.search.FullTextQuery;
//import org.hibernate.search.FullTextSession;
//import org.hibernate.search.Search;
//import org.hibernate.search.SearchException;
//import org.hibernate.search.SearchFactory;
//import org.hibernate.search.engine.spi.SearchFactoryImplementor;
//import org.hibernate.search.store.impl.DirectoryProviderHelper;

public abstract class AbstractHibernateSearchDao<T, Q, KeyType extends Serializable> implements HibernateSearchDao<T, Q, KeyType>, DisposableBean {

	protected static Logger logger = Logger.getLogger(AbstractHibernateSearchDao.class);
	
	private final ReentrantLock reentrantLock = new ReentrantLock();

	//object is singleton, so it safe to have this field
	private transient Date lastUpdateDBDate;
	private transient Date reindexingCompletedOn;

	private boolean rebuildIndexesAtInit = true;
	private transient long reindexDuration;
	
	private String lastModifiedFieldName;
	private String idFieldName;
	private SessionFactory sessionFactory;

    @Autowired
    protected ElasticsearchProvider esHelper;
	
	@Autowired
	public void setTemplate(final @Qualifier("sessionFactory") SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
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
		this.hibernateProperties = hibernateProperties;
	}

    @Override public int count(final Q query) { 
        int count = 0;
    	if (query != null) {
            final QueryBuilder luceneQuery = parse(query);
            if (luceneQuery == null) {
            	// count all objects
                final Criteria criteria = getSession().createCriteria(getEntityClass()).setCacheable(true);
        		criteria.add(Restrictions.eq("active", Boolean.TRUE));
        		criteria.setProjection(Projections.rowCount());
        		count = ((Number) criteria.uniqueResult()).intValue();
            } else {
//            	count = count(buildFullTextSessionQuery(getFullTextSession(null), luceneQuery, null));
            }
    	}
        return count;
    }

    @Override public List<T> find(final int from, final int size, final SortType sort, final Q query) {        
        List<T> result = Collections.emptyList();
    	if ((from >=0) && (size > 0) && (query != null)) {
            final QueryBuilder luceneQuery = parse(query);
            if (luceneQuery != null) {
//            	result = find(buildFullTextSessionQuery(getFullTextSession(null), luceneQuery, from, size, sort));
            }
    	}
        return result;
    }
    
    @SuppressWarnings("unchecked")
    @Override public List<KeyType> findIds(final SortType sort, final Q query) {
    	final List<KeyType> result = new ArrayList<KeyType>();
    	if ((query != null)) {
            return findIds(0, Integer.MAX_VALUE, sort, query);
//            final QueryBuilder luceneQuery = parse(query);
//            if (luceneQuery != null) {
////				final List idList = findIds(buildFullTextSessionQuery(getFullTextSession(null), luceneQuery, sort).setProjection(idFieldName));
////				for (final Object row : idList) {
////					final Object[] columns = (Object[]) row;
////					final KeyType id = (KeyType) columns[0];
////					result.add(id);
////				}
//            }
    	}
        return result;
    }

    @SuppressWarnings("unchecked")
	@Override public List<KeyType> findIds(final int from, final int size, final SortType sort, final Q query) {
    	final List<KeyType> result = new ArrayList<KeyType>();
    	if ((from >=0) && (size > 0) && (query != null)) {
            final QueryBuilder luceneQuery = parse(query);
            if (luceneQuery != null) {
                SearchResponse searchResponse = esHelper.searchData(luceneQuery, getEntityClass());

                if(searchResponse!=null && searchResponse.getHits().getTotalHits()>0){
                    for (final SearchHit hit : searchResponse.getHits()) {
                        final KeyType id = (KeyType) hit.getId();
                        result.add(id);
                    }
                }
//				final List idList = findIds(buildFullTextSessionQuery(
//						getFullTextSession(null), luceneQuery, from, size, sort)
//						.setProjection(idFieldName));
//				for (final Object row : idList) {
//					final Object[] columns = (Object[]) row;
//					final KeyType id = (KeyType) columns[0];
//					result.add(id);
//				}
            }
    	}
        return result;
    }

//    protected FullTextQuery buildFullTextSessionQuery(final FullTextSession fullTextSession, final Query luceneQuery,
//            final int from, final int size, final SortType sort) {
//        return buildFullTextSessionQuery(fullTextSession, luceneQuery, sort).setMaxResults(size).setFirstResult(from);
//    }
//
//    protected FullTextQuery buildFullTextSessionQuery(final FullTextSession fullTextSession, final Query luceneQuery, final SortType sort) {
//        final FullTextQuery hiberQuery = fullTextSession.createFullTextQuery(luceneQuery, getEntityClass());
//        hiberQuery.setReadOnly(true);
//        hiberQuery.setCacheable(true);
//        final Sort sortField = (sort == null) ? null : sort.getSort();
//        if (sortField != null) {
//        	hiberQuery.setSort(sortField);
//        }
//        return hiberQuery;
//    }
//
//    protected FullTextSession getFullTextSession(Session session) {
//    	session = (session != null) ? session : getSession();
//        return Search.getFullTextSession(session);
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    protected List<T> find(final FullTextQuery fullTextQuery) {
//        return fullTextQuery.list();
//    }
//
//    @SuppressWarnings({ "unchecked" })
//    protected List<Object> findIds(final FullTextQuery fullTextQuery) {
//        return fullTextQuery.list();
//    }
//
//    protected int count(final FullTextQuery fullTextQuery) {
//        return fullTextQuery.getResultSize();
//    }
    
    protected int getMaxFetchSizeOnReinex() {
    	return 1000;
    }

    protected abstract QueryBuilder parse(Q query);
    protected abstract Class<T> getEntityClass();


    private DetachedCriteria getCriteria() throws Exception{
        return DetachedCriteria.forClass(getEntityClass()).addOrder(Order.asc(esHelper.getIdFieldName(getEntityClass())));
    }

    private void buidIndexes(Session session) throws Exception {
    	final DetachedCriteria criteria = getCriteria();
        try {
        	doIndex(criteria, true, session);
    	} catch (Exception e) {
//    		if (logger.isErrorEnabled()) {
    			logger.error(String.format("can't build indexes : '%s'. Trying to recreate indexes dir", e));
//    		}
    		//clean-up lucene indexes directory
//    		final SearchFactory searchFactory = getFullTextSession(session).getSearchFactory();
//    		if (searchFactory instanceof SearchFactoryImplementor) {
//    			reintializeCurrentIndex(session, (SearchFactoryImplementor)searchFactory);
//    			/*
//    			for (final DocumentBuilderContainedEntity<?> directoryProvider : searchFactory.getDirectoryProviders(entityClass)) {
//    				directoryProvider.initialize(arg0, arg1, arg2);
//    				directoryProvider.initialize(directoryProviderName, hibernateProperties, (SearchFactoryImplementor) searchFactory);
//    			}
//    			*/
//    			//trying to build indexes again
//    			//don't call buidIndexes(), cause if it's possible to get short circle recursion
//    			doIndex(criteria, true, session);
//    			if (logger.isDebugEnabled()) {
//    				logger.debug("indexes dir recreated, indexes rebuilt");
//    			}
//    		} else {
    			//just rethrow exception
    			throw e;
//    		}
    	}
    }
    
//    private void reintializeCurrentIndex(Session session, final SearchFactoryImplementor searchFactory) throws IOException {
//    	session = (session != null) ? session : getSession();
//    	final Class<T> entityClass = getEntityClass();
//		final String directoryProviderName = hibernateProperties.getProperty("hibernate.search.default.indexBase") + "/" + entityClass.getName();
//		final File sourceDir = DirectoryProviderHelper.getSourceDirectory(directoryProviderName, hibernateProperties, true);
//		sourceDir.delete();
//		FileUtils.deleteDirectory(sourceDir);
//		DirectoryProviderHelper.createFSIndex(sourceDir, hibernateProperties);
//
//		/*
//		for(final IndexManager indexManager : searchFactory.getIndexBindingForEntity(entityClass).getIndexManagers()) {
//			indexManager.initialize(directoryProviderName, hibernateProperties, searchFactory.getWorker());
//		}
//		*/
//    }
    
    @SuppressWarnings("unchecked")
	private void doIndex(final DetachedCriteria load, final boolean purgeAll, final Session session) throws Exception {
//        final FullTextSession fullTextSession = getFullTextSession(session);
//        fullTextSession.setFlushMode(FlushMode.COMMIT);
//        //fullTextSession.setCacheMode(CacheMode.IGNORE);
//        fullTextSession.setCacheMode(CacheMode.REFRESH);
        final Class<T> entityClass = getEntityClass();
        try {
            esHelper.buildIndex(entityClass, purgeAll);

        	final int maxSize = getMaxFetchSizeOnReinex();
        	final Criteria criteria = load.getExecutableCriteria(session);

        	for (int from = 0; ; from += maxSize) {
//        		final Transaction transaction = fullTextSession.beginTransaction();
        		try {
        			logger.info(String.format("Fetching from %s, size: %s", from, maxSize));
        			final List<T> list = criteria.setFirstResult(from).setMaxResults(maxSize).list();
        			logger.info(String.format("Fetched from %s, size: %s.  Indexing...", from, maxSize));
                    esHelper.doIndex(list, entityClass);

                	logger.info(String.format("Fetched from %s, size: %s.  Done indexing... committing", from, maxSize));
//                	transaction.commit();
                	logger.info(String.format("Fetched from %s, size: %s.  Done indexing... committed", from, maxSize));
                	if (list.isEmpty() || list.size() < maxSize) {
                		break;
                	}
            	} catch (Exception e) {
            		logger.error("Can't index - rolling back", e);
//            		transaction.rollback();
            	}
        	}
        } catch (Exception e){
            logger.error("Can't index ", e);
          //  throw e;
        } finally {
        	//fullTextSession.close();
        }
    }

    public void updateIndecies(List<String> idsList) throws Exception {
        if(CollectionUtils.isNotEmpty(idsList)){
            final DetachedCriteria criteria = getCriteria();
            criteria.add(Restrictions.in(esHelper.getIdFieldName(getEntityClass()), idsList));
            doIndex(criteria, false, sessionFactory.openSession());
        }
    }

    public void deleteIndecies(List<String> idsList) throws Exception{
        if(CollectionUtils.isNotEmpty(idsList)){
            esHelper.deleteData(idsList, getEntityClass());
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

//    private Date getLastDbUpdateDateInternal(Session session) {
//    	session = (session != null) ? session : getSession();
//    	return (Date)session.createCriteria(getEntityClass())
//			.setProjection(Projections.max(lastModifiedFieldName)).uniqueResult();
//    }
    
    protected QueryBuilder buildTokenizedClause(final String paramName, final String paramValue, MatchType matchType) {

    	if (StringUtils.isNotBlank(paramValue) && StringUtils.isNotBlank(paramName)) {
            final BoolQueryBuilder query = QueryBuilders.boolQuery();
            final String trimmedKeyword = StringUtils.trimToEmpty(paramValue.toLowerCase());
            final Set<String> terms = esHelper.separateTerms(trimmedKeyword, getEntityClass());
            if(CollectionUtils.isNotEmpty(terms)){
                for (final Iterator<String> iterator = terms.iterator(); iterator.hasNext();) {
                    String term = iterator.next();
                    //allows search by non-empty words
                    if (StringUtils.isNotEmpty(term)) {
                        addClause(query, QueryBuilders.wildcardQuery(paramName, wrapTerm(term, matchType)), SearchMode.OR);
                    }
                    iterator.remove();
                }
                return query;
            }
        }
    	return null;
	}

    protected QueryBuilder buildExactClause(final String paramName, final String paramValue) {
    	if (StringUtils.isNotBlank(paramValue) && StringUtils.isNotBlank(paramName)) {
    		return QueryBuilders.termQuery(paramName, paramValue);
        }
    	return null;
	}

    protected QueryBuilder buildInClause(final String paramName, final Collection<String> paramValues) {
        if (paramValues!=null && !paramValues.isEmpty() && StringUtils.isNotBlank(paramName)) {
            final BoolQueryBuilder query = QueryBuilders.boolQuery();
            for( String value : paramValues ){
                addClause(query, QueryBuilders.termQuery(paramName, value), SearchMode.OR);
            }
            return query;
        }
        return null;
    }

    protected void addClause(BoolQueryBuilder query, org.elasticsearch.index.query.QueryBuilder clause, SearchMode searchMode) {
        if(SearchMode.AND.equals(searchMode)) {
            query.must(clause);
        }  else {
            query.should(clause);
        }
    }

    @Override public Date getReindexingCompletedOn() {
    	return reindexingCompletedOn;
    }

    @Override public Date getLastDbUpdateDate() {
    	return lastUpdateDBDate;
    }

    @Override public void synchronizeIndexes(final boolean forcePurgeAll) {
//    	reentrantLock.lock();
//		final StopWatch stopWatch = new StopWatch();
//		stopWatch.start();
//		Session session = null;
//    	try {
//    		session = sessionFactory.openSession();
//    		boolean reindexed = false;
//    		final Date updateDate = getLastDbUpdateDateInternal(session);
//    		if (lastUpdateDBDate == null || forcePurgeAll) {
//    			final DetachedCriteria criteria = DetachedCriteria.forClass(getEntityClass()).addOrder(Order.asc(idFieldName));
//    			doIndex(criteria, forcePurgeAll, session);
//    			reindexed = true;
//    		} else if (((null != updateDate) && (updateDate.after(lastUpdateDBDate)))) {
//    			final DetachedCriteria criteria = DetachedCriteria.forClass(getEntityClass()).add(Restrictions.gt(lastModifiedFieldName, lastUpdateDBDate)).addOrder(Order.asc(idFieldName));
//    			doIndex(criteria, forcePurgeAll, session);
//    			reindexed = true;
//        	}
//
//    		if(reindexed) {
//    			lastUpdateDBDate = updateDate;
//    			reindexingCompletedOn = new Date(System.currentTimeMillis());
//    		}
//    	} finally {
//    		if (reentrantLock.isHeldByCurrentThread()) {
//    			stopWatch.stop();
//    			synchronized (this) {
//    				reindexDuration = stopWatch.getTime();
//				}
//    			reentrantLock.unlock();
//    		}
//    		if(session != null) {
//    			if(session.isOpen()) {
//    				session.close();
//    			}
//    		}
//    	}
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
    	Class currentClazz = clazz;
    	while(currentClazz != null) {
    		if(currentClazz.getDeclaredFields() != null) {
    			for(Field field : currentClazz.getDeclaredFields()) {
    				if(field.getAnnotation(LuceneLastUpdate.class) != null) {
    					lastModifiedFieldName = field.getName();
    				} else if(field.getAnnotation(LuceneId.class) != null) {
    					idFieldName = field.getName();
    				}
    			}
    		}
    		currentClazz = currentClazz.getSuperclass();
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

	@PostConstruct
	public void initDao() throws Exception {
		Session session = null;
		//initMetadata();
		try {
			session = sessionFactory.openSession();
	    	if (rebuildIndexesAtInit) {
	    		StopWatch stopWatch = new StopWatch();
	    		logger.info(String.format("begin re-indexing %s", getEntityClass().getSimpleName()));
	    		stopWatch.start();
	    		buidIndexes(session);
	    		stopWatch.stop();
				synchronized (this) {
					reindexDuration = stopWatch.getTime();
				}
//				lastUpdateDBDate = getLastDbUpdateDateInternal(session);
				if (lastUpdateDBDate == null) {
					lastUpdateDBDate = new Date(System.currentTimeMillis());
				}
				reindexingCompletedOn = new Date(System.currentTimeMillis());
				logger.info(String.format("end re-indexing %s after %s", getEntityClass().getSimpleName(), stopWatch.toString()));
	    	} else {
	    		logger.info(String.format("skip re-indexing %s", getEntityClass().getSimpleName()));
	    	}
		} catch(Throwable e) {
			logger.error("Can't reinex", e);
			throw e;
		} finally {
			if(session != null) {
				if(session.isOpen()) {
					session.close();
				}
			}
		}
	}

    private String wrapTerm(String term, MatchType matchType) {
        switch (matchType){
            case END_WITH:
                if (term.charAt(0) != '*') {
                    return String.format("*%s", term);
                }
            case STARTS_WITH:
                if (term.charAt(term.length() - 1) != '*') {
                    return String.format("%s*", term);
                }
            case CONTAINS:
                if (term.charAt(0) != '*') {
                    term = "*" + term;
                }
                if (term.charAt(term.length() - 1) != '*') {
                    term = term + "*";
                }
        }
        return term;
    }
}