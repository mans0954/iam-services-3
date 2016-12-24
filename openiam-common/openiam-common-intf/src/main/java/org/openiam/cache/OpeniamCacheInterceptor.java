package org.openiam.cache;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.common.lang3.ArrayUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.BaseIdentity;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.util.AuditLogHelper;
import org.openiam.util.SpringSecurityHelper;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.context.ApplicationContext;

/**
 * @author Lev Bornovalov
 *
 * This is our custom Cache Interceptor.  This is required for IDMAPPS-3644.
 * 
 * The cache interceptor is required to get access to the generated @Cacheable and @Cachevict keys,
 * as well as the evict() operation, which we would normally not have access to.
 * 
 * This is <b>not</b> a Spring Bean, and hence, @Autowiring will not work.
 * 
 * Note that half of this class is a hack around the fact that Spring does not not purposefully expose
 * most of this functionality
 */
public class OpeniamCacheInterceptor extends CacheInterceptor {
	
	private static final Log LOG = LogFactory.getLog(OpeniamCacheInterceptor.class);

	private AuditLogHelper auditLogHelper;
	private Map<String, Set<Object>> cacheManagementCache;
	
	private final Map<InvocationKey, Boolean> languageAnnotationCache = new HashMap<>();
	
	private HazelcastConfiguration hazelcastConfiguration;
	
	private ApplicationContext applicationContext;
	private CacheManager cacheManager;

	public void setHazelcastConfiguration(
			HazelcastConfiguration hazelcastConfiguration) {
		this.hazelcastConfiguration = hazelcastConfiguration;
	}
	
	@Override
	public void setCacheManager(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
		super.setCacheManager(cacheManager);
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		super.setApplicationContext(applicationContext);
	}

	public void init() {
		cacheManagementCache = hazelcastConfiguration.getMap("cacheManagementCache");
		auditLogHelper = applicationContext.getBean(AuditLogHelper.class);
	}
	
	private Set<BaseIdentity> getBaseIdentities(final Object cacheResult, final Class<?> clazz, Method method) {
		final Set<BaseIdentity> baseIdentities = new HashSet<BaseIdentity>();
		if(cacheResult != null) {
			if(cacheResult instanceof BaseIdentity) {
				baseIdentities.add((BaseIdentity)cacheResult);
			} else if(cacheResult instanceof Collection) {
				for(final Object o : (Collection)cacheResult) {
					if(o != null) {
						if(o instanceof BaseIdentity) {
							baseIdentities.add((BaseIdentity)o);
						} else {
							if(LOG.isDebugEnabled()) {
								LOG.debug(String.format("%s:%s had a Collection return value, but the objects were not an instance of BaseIdentity, but it wasn't an instnace of BaseIdentity.  If this is a @Cacheable method, autopurge logic for our cache will not be utilized on this call", clazz, method));
							}						
						}
					}
				}
			} else {
				if(LOG.isDebugEnabled()) {
					LOG.debug(String.format("%s:%s had a return value, but it wasn't an instnace of BaseIdentity.  If this is a @Cacheable method, autopurge logic for our cache will not be utilized on this call", clazz, method));
				}
			}
		}
		return baseIdentities;
	}
	
	private Class<?> getTargetClass(Object target) {
		Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
		if (targetClass == null && target != null) {
			targetClass = target.getClass();
		}
		return targetClass;
	}
	
	//private Map<Class<?>, Map<Method, List<CacheKeyEvictToken>>> annotationCache = new HashMap<Class<?>, Map<Method,List<CacheKeyEvictToken>>>();

	private Map<Method, CacheKeyEvictionAnnotationVisit> cacheKeyEvictionMap = new HashMap<Method, CacheKeyEvictionAnnotationVisit>();
	private class CacheKeyEvictionAnnotationVisit {
		private List<CacheKeyEviction> annotations;
		CacheKeyEvictionAnnotationVisit() {
			
		}
		
		void add(final CacheKeyEviction[] evictions) {
			if(evictions != null) {
				if(annotations == null) {
					annotations = new LinkedList<CacheKeyEviction>();
				}
				for(CacheKeyEviction e : evictions) {
					if(e != null) {
						annotations.add(e);
					}
				}
			}
		}
		
		void add(final CacheKeyEviction eviction) {
			if(eviction != null) {
				add(new CacheKeyEviction[] {eviction});
			}
		}
		
		public List<CacheKeyEviction> getAnnotations() {
			return annotations;
		}
	}
	
	private Map<Parameter, CacheKeyEvictAnnotationVisit> cacheKeyEvictMap = new HashMap<Parameter, CacheKeyEvictAnnotationVisit>();
	private class CacheKeyEvictAnnotationVisit {
		private CacheKeyEvict annotation;
		CacheKeyEvictAnnotationVisit() {
			
		}
	}
	
	private List<CacheKeyEviction> getCacheKeyEvictionAnnotations(final Method method) {
		CacheKeyEvictionAnnotationVisit visit = cacheKeyEvictionMap.get(method);
		if(visit == null) {
			visit = new CacheKeyEvictionAnnotationVisit();
			
			if(method.isAnnotationPresent(CacheKeyEviction.class)) {
				final CacheKeyEviction annotation =  method.getAnnotation(CacheKeyEviction.class);
				visit.add(annotation);
			} else if(method.isAnnotationPresent(CacheKeyEvictions.class)) {
				visit.add(method.getAnnotation(CacheKeyEvictions.class).value());
			}
			cacheKeyEvictionMap.put(method, visit);
		}
		return visit.getAnnotations();
	}
	
	private List<CacheKeyEvictToken> getEvictionMetadata(final Object target, 
														 Method method, 
														 final Object[] args, 
														 final Class<?> targetClass) {
		/*
		 * The method is from the interface, which will *not* have the annotations that we
		 * need.  As a result, let's get it from the class instead
		 */
		try {
			method = targetClass.getMethod(method.getName(), method.getParameterTypes());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		
		/*
		if(!annotationCache.containsKey(targetClass)) {
			annotationCache.put(targetClass, new HashMap<Method, List<CacheKeyEvictToken>>());
		}
		
		List<CacheKeyEvictToken> evictions = annotationCache.get(targetClass).get(method);
		if(evictions != null) {
			return evictions;
		}

		annotationCache.get(targetClass).put(method, evictions);
		*/
		final List<CacheKeyEvictToken> evictions = new LinkedList<CacheKeyEvictToken>();
		final Parameter[] parameters = method.getParameters();
		final List<CacheKeyEviction> evictionAnnotations = getCacheKeyEvictionAnnotations(method);
		if(evictionAnnotations != null) {
			evictionAnnotations.forEach(eviction -> {
				for(final CacheKeyEvict evict : eviction.evictions()) {
					evictions.add(new CacheKeyEvictToken(evict, args[eviction.parameterIndex()]));
				}
			});
		}
		return evictions;
	}
	
	private String generatePrimaryKeyCacheKey(final Cache cache, final String id) {
		return new StringBuilder(cache.getName()).append(id).toString();
	}
	
	@Override
	protected Object execute(final CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
		final long threadId = Thread.currentThread().getId();
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Thread ID.  Starting execution...", threadId));
		}
		
		
		final MutableBoolean cacheMiss = new MutableBoolean(false);
		final CacheOperationInvoker invokerWrapper = new CacheOperationInvoker() {
			
			@Override
			public Object invoke() throws ThrowableWrapper {
				cacheMiss.setValue(true);
				return invoker.invoke();
			}
		};
		final Class<?> targetClass = getTargetClass(target);
		final Collection<CacheOperation> operations = getCacheOperationSource().getCacheOperations(method, targetClass);
		
		final boolean isCustomCacheEviction = (operations.stream().filter(e -> e instanceof OpeniamCacheEviction).count() > 0);
		final boolean isCustomCacheEvictionOnlyOperation = (isCustomCacheEviction && operations.size() == 1);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Thread ID: %s.  Called OpeniamCacheInterceptor with Target %s, method %s, args %s", threadId, target, method, ArrayUtils.toString(args)));
		}
		
		final Object returnValue = (isCustomCacheEvictionOnlyOperation) ? 
				invokerWrapper.invoke() :
				super.execute(invokerWrapper, target, method, args);
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Thread ID: %s.  OpeniamCacheInterceptor return Value %s.  Is cache miss? %s", threadId, returnValue, cacheMiss.booleanValue()));
		}
				
		/* consider doing this in a separate thread */
		final Set<BaseIdentity> baseIdentities = getBaseIdentities(returnValue, targetClass, method) ;
		
		final List<CacheKeyEvictToken> evictions = getEvictionMetadata(target, method, args, targetClass);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug(String.format("Thread ID: %s.  OpeniamCacheInterceptor getting read to custom cache put.  Base Entities: %s.  Evictions: %s", threadId, baseIdentities, evictions));
		}
		
		if(CollectionUtils.isNotEmpty(operations)) {
			for(final CacheOperation operation : operations) {
				if(operation instanceof CacheableOperation || operation instanceof CachePutOperation) {
					final OpeniamCacheOperationContext cacheOperationContext = getOperationContext(operation, method, args, target, targetClass);
					final Object cacheKey = cacheOperationContext.getKey();
					if (cacheOperationContext.isConditionPassing() && cacheOperationContext.canPutInCache()) {
						for(final Cache cache : cacheOperationContext.getCaches()) {
							if(cacheMiss.booleanValue()) { /* only process this on a cache miss */
								logPut(cache, cacheKey, baseIdentities.size());
								
								if(LOG.isDebugEnabled()) {
									LOG.debug(String.format("Thread ID: %s.  OpeniamCacheInterceptor ready to do custom cache put.  Cache: %s.  Cache key %s.  Number of Entities being put: %s", 
											threadId, cache.getName(), cacheKey, baseIdentities.size()));
								}
								
								/*
								 * Now populate our custom cache, so that @CacheEvict operations will be able to refernece them
								 * without knowing the generated key (which may be quite complicated)
								 */
								if(CollectionUtils.isNotEmpty(baseIdentities)) {
									for(final BaseIdentity identity : baseIdentities) {
										final String cacheManagementKey = generatePrimaryKeyCacheKey(cache, identity.getId());
								
										if(LOG.isDebugEnabled()) {
											LOG.debug(String.format("Thread ID: %s.  Putting custom cache key: %s", threadId, cacheManagementKey));
										}
										
										Set<Object> keySet = cacheManagementCache.get(cacheManagementKey);
										if(keySet == null) {
											keySet = Collections.synchronizedSet(new HashSet<Object>());
										}
										keySet.add(cacheKey);
									
										/* 
										 * always do put(), so that the change to the Set propagates to all nodes 
										 * simply calling add() on the Set will <b>not</b> force propagation 
										 */
										cacheManagementCache.put(cacheManagementKey, keySet);
									}
								}
							}
						}
					}
				}
			}
		}
		
		/*
		 * Now process all of the evictions that take place, as a result of the method call
		 */
		if(isCustomCacheEviction) {
			if(LOG.isDebugEnabled()) {
				LOG.debug(String.format("Thread ID: %s.  Processing Custom Cache Eviction", threadId));
			}
			for(final CacheKeyEvictToken token : evictions) {
				Cache evictCache = null;
				final List<String> primaryKeys = applicationContext.getBean(token.getEvict().keyGenerator()).generateKey(token.getArgument());
				
				if(LOG.isDebugEnabled()) {
					LOG.debug(String.format("Thread ID: %s.  Going to purge primary keys %s", threadId, primaryKeys));
				}
				if(primaryKeys != null) {
					final String cacheName = token.getEvict().value();
					evictCache = this.cacheManager.getCache(cacheName);
					if(evictCache == null) {
						throw new IllegalStateException(String.format("No cache called %s exists", cacheName));
					}
					for(final String primaryKey : primaryKeys) {
						final String cacheManagementKey = generatePrimaryKeyCacheKey(evictCache, primaryKey);
						if(LOG.isDebugEnabled()) {
							LOG.debug(String.format("Thread ID: %s.  Purging entites for key %s", threadId, cacheManagementKey));
						}
						Set<Object> keySet = cacheManagementCache.get(cacheManagementKey);
						if(CollectionUtils.isNotEmpty(keySet)) {
							
							if(LOG.isDebugEnabled()) {
								LOG.debug(String.format("Thread ID: %s.  Purging keys %s for Cache Key %s", threadId, keySet, cacheManagementKey));
							}
							
							logEviction(evictCache, cacheManagementKey, keySet.size());
							if(keySet != null) {
								/* 
								 * this is a synchronizedSet, and thus requires a synchronized block around it
								 * https://docs.oracle.com/javase/7/docs/api/java/util/Collections.html#synchronizedSet(java.util.Set)
								 */
								synchronized(keySet) {
									for(final Iterator<Object> it = keySet.iterator(); it.hasNext();) {
										final Object keyToPurge = it.next();
										doEvict(evictCache, keyToPurge);
										it.remove();
									}
								}
								cacheManagementCache.put(cacheManagementKey, keySet);
							}
						}
					}
				}
			}
		}
		
		return returnValue;
	}
	
	private void logEviction(final Cache cache, final Object key, final int numOfMultikeys) {
		/*
		final IdmAuditLogEntity log = new IdmAuditLogEntity();
		log.setAction(AuditAction.CACHE_EVICT.value());
		log.put(AuditAttributeName.CACHE_NAME.name(), cache.getName());
		log.put(AuditAttributeName.CACHE_KEY.name(), key.toString());
		log.put(AuditAttributeName.NUM_OF_MULTIKEYS.name(), Integer.valueOf(numOfMultikeys).toString());
		auditLogHelper.enqueue(log);
		*/
	}
	
	private void logPut(final Cache cache, final Object key, final int numOfMultikeys) {
		/*
		final IdmAuditLogEntity log = new IdmAuditLogEntity();
		log.setAction(AuditAction.CACHE_PUT.value());
		log.put(AuditAttributeName.CACHE_NAME.name(), cache.getName());
		log.put(AuditAttributeName.CACHE_KEY.name(), key.toString());
		log.put(AuditAttributeName.NUM_OF_MULTIKEYS.name(), Integer.valueOf(numOfMultikeys).toString());
		auditLogHelper.enqueue(log);
		*/
	}
	
	@Override
	protected OpeniamCacheOperationContext getOperationContext(CacheOperation operation, Method method, Object[] args, Object target, Class<?> targetClass) {
		CacheOperationMetadata metadata = getCacheOperationMetadata(operation, method, targetClass);
		final OpeniamCacheOperationContext cacheOperationContext = new OpeniamCacheOperationContext(metadata, args, target);
		return cacheOperationContext;
	}
	
	protected class OpeniamCacheOperationContext extends CacheOperationContext {
		
		private boolean isHazelcastCache;
		
		public OpeniamCacheOperationContext(CacheOperationMetadata metadata, Object[] args, Object target) {
			super(metadata, args, target);
			isHazelcastCache = (cacheManager.getClass().getCanonicalName().endsWith("HazelcastCacheManager"));
		}
		
		private boolean isLanguageCacheKey() {
			final InvocationKey key = new InvocationKey(this.getTarget(), this.getMethod());
			if(languageAnnotationCache.containsKey(key)) {
				return languageAnnotationCache.get(key).booleanValue();
			} else {
				Method method = null;
				try {
					method = getTargetClass(this.getTarget()).getMethod(this.getMethod().getName(), this.getMethod().getParameterTypes());
				} catch (Throwable e) {
					LOG.error(String.format("Unkonwn error while trying to resolve method.  Target: %s, Method: %s", this.getTarget(), this.getMethod()), e);
				}
				final Boolean isAnnotationPresent = Boolean.valueOf(method.isAnnotationPresent(LanguageCacheKey.class));
				languageAnnotationCache.put(key, isAnnotationPresent);
				return isAnnotationPresent.booleanValue();
			}
		}
		
		public Object getKey() {
			return this.generateKey(null);
		}
		
		public boolean isConditionPassing() {
			return isConditionPassing(null);
		}
		
		public boolean canPutInCache() {
			return canPutToCache(null);
		}
		
		public Collection<? extends Cache> getCaches() {
			return super.getCaches();
		}
		
		

		@Override
		protected boolean isConditionPassing(Object result) {
			// TODO Auto-generated method stub
			return super.isConditionPassing(result);
		}

		@Override
		protected Object generateKey(Object result) {
			final Object key = super.generateKey(result);
			/* 
			 * @Cacheable or @CachePut with a 'key' will have an ArrayList key,
			 * which doesn't play well with Hazelcast
			 */
			if(isHazelcastCache) {
				if(key instanceof Collection) {
					final StringBuilder sb = new StringBuilder();
					for(final Object k : (Collection)key) {
						if(k != null) {
							sb.append(k.hashCode()).append(";");
						} else {
							sb.append(k).append(";");
						}
					}
					if(isLanguageCacheKey()) {
						sb.append(SpringSecurityHelper.getLanguageId());
					}
					return sb.toString();
				}
			}
			return key;
		}
	}
	
	private class InvocationKey {
		
		private Object target;
		private Method method;
		
		InvocationKey(final Object target, final Method method) {
			this.target = target;
			this.method = method;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			result = prime * result + ((target == null) ? 0 : target.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InvocationKey other = (InvocationKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			return true;
		}

		private OpeniamCacheInterceptor getOuterType() {
			return OpeniamCacheInterceptor.this;
		}

		
	}
	
	private final class CacheKeyEvictToken {
		private Object argument;
		private CacheKeyEvict eviction;
		CacheKeyEvictToken(final CacheKeyEvict eviction, final Object argument) {
			this.eviction = eviction;
			this.argument = argument;
		}
		public CacheKeyEvict getEvict() {
			return eviction;
		}
		public Object getArgument() {
			return argument;
		}
	}
}
