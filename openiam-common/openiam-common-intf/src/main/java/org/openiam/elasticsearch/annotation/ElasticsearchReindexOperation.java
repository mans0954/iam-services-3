package org.openiam.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by: Alexander Duckardt
 * Date: 9/18/14.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticsearchReindexOperation {
    public boolean saveOrUpdate() default false;
    public boolean delete() default false;
}
