package org.openiam.cache;

/**
 * This is a placeholder method to tell Spring that we are doing our own cache eviction.
 * Initially, I wanted to include an empty @Caching annotation on methods that require
 * our custom processing.  Unforutnately, an empty @Caching throws a NullPointerException
 * 
 * The whole reason for the existence of this class is that we need spring to call our custom implementation of
 * CacheInterceptor without actually doing anything.  Our code in that interceptor does some custom cache evict logic.
 * 
 * @author Lev Bornovalov
 *
 */
public @interface CustomCacheEviction {

}
