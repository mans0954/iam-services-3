package org.openiam.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface
 * Methods annotated with this imply that Spring Cache will use the current languageID (in the Spring Security Context)
 * as part of the cache key
 * @author lbornova
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LanguageCacheKey {

}
