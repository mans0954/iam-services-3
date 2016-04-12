package org.openiam.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.interceptor.KeyGenerator;

/**
 * When used in conjunction with @CacheEvict, this will tell our custom 
 * OpeniamCacheInterceptor which keys to purge.
 * 
 * When a @CacheEvict takes place
 * 
 * OpeniamCacheInterceptor will first look at the keyGenerator of this annotation.  If it is defined,
 * the cache key will be generated using this class
 * 
 * If keyGenerator is undefined, the Cache Interceptor will make the assumption that the annotated parameter is either
 * a class that extends BaseEntity, or a String representing a primary key
 * 
 *
 * @author Lev Bornovalov
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface CacheKeyEvict {
	/**
	 * The cache name to evict
	 * If not specified, it the cache specified in @CacheEvict will be used
	 */
	String cacheName() default "";
	
	/**
	 * A spring bean that will return the key for this parameter
	 * @return
	 */
	Class<? extends OpeniamKeyGenerator> keyGenerator() default DefaultOpeniamKeyGenerator.class;
}
