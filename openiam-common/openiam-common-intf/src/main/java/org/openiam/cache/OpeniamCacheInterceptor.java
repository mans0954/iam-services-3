package org.openiam.cache;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.BaseIdentity;
import org.openiam.cache.CacheKeyEvict;
import org.openiam.cache.CacheKeyEviction;
import org.openiam.hazelcast.HazelcastConfiguration;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.constant.AuditAttributeName;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.springframework.aop.framework.AopProxyUtils;
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
	
	private AuditLogService auditLogService;
	private Map<String, Set<Object>> cacheManagementCache;
	
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
		auditLogService = applicationContext.getBean(AuditLogService.class);
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
	
	private abstract class AnnotationVisit {
		boolean exists;
	}
	
	private Map<Method, CacheKeyEvictionAnnotationVisit> cacheKeyEvictionMap = new HashMap<Method, CacheKeyEvictionAnnotationVisit>();
	private class CacheKeyEvictionAnnotationVisit extends AnnotationVisit {
		private CacheKeyEviction annotation;
		CacheKeyEvictionAnnotationVisit() {
			
		}
		
	}
	
	private Map<Parameter, CacheKeyEvictAnnotationVisit> cacheKeyEvictMap = new HashMap<Parameter, CacheKeyEvictAnnotationVisit>();
	private class CacheKeyEvictAnnotationVisit extends AnnotationVisit {
		private CacheKeyEvict annotation;
		CacheKeyEvictAnnotationVisit() {
			
		}
	}
	
	private CacheKeyEviction getCacheKeyEvictionAnnotation(final Method method) {
		CacheKeyEvictionAnnotationVisit visit = cacheKeyEvictionMap.get(method);
		if(visit == null) {
			final CacheKeyEviction annotation =  method.getAnnotation(CacheKeyEviction.class);
			visit = new CacheKeyEvictionAnnotationVisit();
			visit.exists = (annotation != null);
			visit.annotation = annotation;
			cacheKeyEvictionMap.put(method, visit);
		}
		return visit.exists ? visit.annotation : null;
	}
	
	private CacheKeyEvict getCacheKeyEvictAnnotation(final Parameter parameter) {
		CacheKeyEvictAnnotationVisit visit = cacheKeyEvictMap.get(parameter);
		if(visit == null) {
			final CacheKeyEvict annotation =  parameter.getAnnotation(CacheKeyEvict.class);
			visit = new CacheKeyEvictAnnotationVisit();
			visit.exists = (annotation != null);
			visit.annotation = annotation;
			cacheKeyEvictMap.put(parameter, visit);
		}
		return visit.exists ? visit.annotation : null;
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
		final CacheKeyEviction eviction = getCacheKeyEvictionAnnotation(method);
		if(eviction != null) {
			boolean parameterExists =  false;
			final String paramName = eviction.parameterName();
			if(parameters != null) {
				for(int index = 0; index < parameters.length; index++) {
					final Parameter parameter = parameters[index];
					if(parameter.getName().equals(paramName)) {
						parameterExists = true;
						for(final CacheKeyEvict evict : eviction.evictions()) {
							evictions.add(new CacheKeyEvictToken(evict, args[index]));
						}
					}
				}
			}
			
			if(!parameterExists) {
				throw new IllegalStateException(String.format("%s:%s had an %s annotation, but the parameterName %s was not found on the method", 
						targetClass, method, CacheKeyEviction.class, paramName));
			}
		} else {
			if(parameters != null) {
				for(int index = 0; index < parameters.length; index++) {
					final Parameter parameter = parameters[index];
					final CacheKeyEvict evict = getCacheKeyEvictAnnotation(parameter);
					if(evict != null) {
						evictions.add(new CacheKeyEvictToken(evict, args[index]));
					}
				}
			}
		}
		return evictions;
	}
	
	private String generatePrimaryKeyCacheKey(final Cache cache, final String id) {
		return new StringBuilder(cache.getName()).append(id).toString();
	}
	
	@Override
	protected Object execute(final CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
		final MutableBoolean cacheMiss = new MutableBoolean(false);
		final CacheOperationInvoker invokerWrapper = new CacheOperationInvoker() {
			
			@Override
			public Object invoke() throws ThrowableWrapper {
				cacheMiss.setValue(true);
				return invoker.invoke();
			}
		};
		final Object returnValue = super.execute(invokerWrapper, target, method, args);
		/* consider doing this in a separate thread */
		final Class<?> targetClass = getTargetClass(target);
		final Set<BaseIdentity> baseIdentities = getBaseIdentities(returnValue, targetClass, method) ;
		
		final List<CacheKeyEvictToken> evictions = getEvictionMetadata(target, method, args, targetClass);
		
		final Collection<CacheOperation> operations = getCacheOperationSource().getCacheOperations(method, targetClass);
		if(CollectionUtils.isNotEmpty(operations)) {
			for(final CacheOperation operation : operations) {
				final OpeniamCacheOperationContext cacheOperationContext = getOperationContext(operation, method, args, target, targetClass);
				final Object cacheKey = cacheOperationContext.getKey();
				if (cacheOperationContext.isConditionPassing() && cacheOperationContext.canPutInCache()) {
					for(final Cache cache : cacheOperationContext.getCaches()) {
						if(operation instanceof CacheableOperation || operation instanceof CachePutOperation) {
							if(cacheMiss.booleanValue()) { /* only process this on a cache miss */
								logPut(cache, cacheKey, baseIdentities.size());
								
								/*
								 * Now populate our custom cache, so that @CacheEvict operations will be able to refernece them
								 * without knowing the generated key (which may be quite complicated)
								 */
								if(CollectionUtils.isNotEmpty(baseIdentities)) {
									for(final BaseIdentity identity : baseIdentities) {
										final String cacheManagementKey = generatePrimaryKeyCacheKey(cache, identity.getId());
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
						} else if(operation instanceof CacheEvictOperation) {

							/*
							 * Now process all of the evictions that take place, as a result of the method call
							 */
							for(final CacheKeyEvictToken token : evictions) {
								Cache evictCache = cache;
								final List<String> primaryKeys = applicationContext.getBean(token.getEvict().keyGenerator()).generateKey(token.getArgument());
								if(primaryKeys != null) {
									if(StringUtils.isNotBlank(token.getEvict().cacheName())) {
										final String cacheName = token.getEvict().cacheName();
										evictCache = this.cacheManager.getCache(cacheName);
										if(evictCache == null) {
											throw new IllegalStateException(String.format("No cache called %s exists", cacheName));
										}
									}
									for(final String primaryKey : primaryKeys) {
										final String cacheManagementKey = generatePrimaryKeyCacheKey(evictCache, primaryKey);
										Set<Object> keySet = cacheManagementCache.get(cacheManagementKey);
										if(CollectionUtils.isNotEmpty(keySet)) {
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
					}
				}
			}
		}
		return returnValue;
	}
	
	private void logEviction(final Cache cache, final Object key, final int numOfMultikeys) {
		final IdmAuditLogEntity log = new IdmAuditLogEntity();
		log.setAction(AuditAction.CACHE_EVICT.value());
		log.put(AuditAttributeName.CACHE_NAME.name(), cache.getName());
		log.put(AuditAttributeName.CACHE_KEY.name(), key.toString());
		log.put(AuditAttributeName.NUM_OF_MULTIKEYS.name(), Integer.valueOf(numOfMultikeys).toString());
		auditLogService.enqueue(log);
	}
	
	private void logPut(final Cache cache, final Object key, final int numOfMultikeys) {
		final IdmAuditLogEntity log = new IdmAuditLogEntity();
		log.setAction(AuditAction.CACHE_PUT.value());
		log.put(AuditAttributeName.CACHE_NAME.name(), cache.getName());
		log.put(AuditAttributeName.CACHE_KEY.name(), key.toString());
		log.put(AuditAttributeName.NUM_OF_MULTIKEYS.name(), Integer.valueOf(numOfMultikeys).toString());
		auditLogService.enqueue(log);
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
		
		public Object getKey() {
			return this.generateKey(null);
		}
		
		public boolean isConditionPassing() {
			return super.isConditionPassing(null);
		}
		
		public boolean canPutInCache() {
			return super.canPutToCache(null);
		}
		
		public Collection<? extends Cache> getCaches() {
			return super.getCaches();
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
					return sb.toString();
				}
			}
			return key;
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
