package org.openiam.cache;

import java.lang.reflect.Method;
import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.CacheOperation;

public class OpeniamAnnotationCacheOperationSource extends AnnotationCacheOperationSource {

	public OpeniamAnnotationCacheOperationSource() {
		super();
	}

	@Override
	protected Collection<CacheOperation> findCacheOperations(final Method method) {
		return super.findCacheOperations(method);
	}
	
	
}
