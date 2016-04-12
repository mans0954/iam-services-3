package org.openiam.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When used in conjunction with @CacheEvict, this will tell our custom 
 * OpeniamCacheInterceptor to purge multiple caches
 * 
 * When a @CacheEvict takes place
 *
 * @author Lev Bornovalov
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheKeyEviction {
	String parameterName();
	CacheKeyEvict[] evictions();
}
