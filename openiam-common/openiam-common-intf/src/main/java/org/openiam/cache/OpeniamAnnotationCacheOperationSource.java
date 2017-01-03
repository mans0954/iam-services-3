package org.openiam.cache;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.context.expression.AnnotatedElementKey;

public class OpeniamAnnotationCacheOperationSource extends AnnotationCacheOperationSource {

	private final static Collection<CacheOperation> NULL_CACHING_ATTRIBUTE = Collections.emptyList();
	
	/**
	 * Cache of CacheOperations, keyed by {@link AnnotatedElementKey}.
	 * <p>As this base class is not marked Serializable, the cache will be recreated
	 * after serialization - provided that the concrete subclass is Serializable.
	 */
	private final Map<Object, Collection<CacheOperation>> attributeCache =
			new ConcurrentHashMap<Object, Collection<CacheOperation>>(1024);
	
	public OpeniamAnnotationCacheOperationSource() {
		super();
	}
	
	private Method getActualMethod(Method method, Class<?> targetClass) {
		/*
		 * The method is from the interface, which will *not* have the annotations that we
		 * need.  As a result, let's get it from the class instead
		 */
		if(Modifier.isPublic(method.getModifiers())) {
			try {
				method = targetClass.getMethod(method.getName(), method.getParameterTypes());
			} catch (Throwable e) {
				throw new RuntimeException(String.format("Cannot get actual method: %s", method), e);
			}
		}
		return method;
	}

	private Collection<CacheOperation> computeCacheOperations(Method method, final Class<?> targetClass) {
		Collection<CacheOperation> operations = super.getCacheOperations(method, targetClass);
		if(methodHasCacheEvictAnnotation(method, targetClass)) {
			if(operations == null) {
				operations = new ArrayList<CacheOperation>(1);
				operations.add(new OpeniamCacheEviction(new OpeniamCacheEviction.Builder()));
			}
		}
		return operations;
	}

	private boolean methodHasCacheEvictAnnotation(Method method) {
		return method != null && (method.getAnnotation(CacheKeyEviction.class) != null) || (method.getAnnotation(CacheKeyEvictions.class) != null);
	}
	
	private boolean methodHasCacheEvictAnnotation(Method method, final Class<?> targetClass) {
		if(methodHasCacheEvictAnnotation(method) || methodHasCacheEvictAnnotation(getActualMethod(method, targetClass))) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public Collection<CacheOperation> getCacheOperations(Method method,
			Class<?> targetClass) {
		Object cacheKey = getCacheKey(method, targetClass);
		Collection<CacheOperation> cached = this.attributeCache.get(cacheKey);

		if (cached != null) {
			return (cached != NULL_CACHING_ATTRIBUTE ? cached : null);
		} else {
			Collection<CacheOperation> cacheOps = computeCacheOperations(method, targetClass);
			if (cacheOps != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Adding cacheable method '" + method.getName() + "' with attribute: " + cacheOps);
				}
				this.attributeCache.put(cacheKey, cacheOps);
			}
			else {
				this.attributeCache.put(cacheKey, NULL_CACHING_ATTRIBUTE);
			}
			return cacheOps;
		}
	}

	/*
	 *TODO - this is a hack until Spring core 4.3 goes into release mode.
	 * See https://jira.spring.io/browse/SPR-14162
	 */
	@Override
	protected Collection<CacheOperation> findCacheOperations(final Method method) {
		Collection<CacheOperation> operations = new ArrayList<CacheOperation>();
		if(methodHasCacheEvictAnnotation(method)) {
			/* this is a placeholder, so that the OpeniamCacheInterceptor gets called */
			operations.add(new OpeniamCacheEviction(new OpeniamCacheEviction.Builder()));
		}
		
		final Collection<CacheOperation> actualOperations =  super.findCacheOperations(method);
		if(CollectionUtils.isNotEmpty(actualOperations)) {
			operations.addAll(actualOperations);
		}
		
		return (CollectionUtils.isNotEmpty(operations)) ? operations : null;
	}
}
