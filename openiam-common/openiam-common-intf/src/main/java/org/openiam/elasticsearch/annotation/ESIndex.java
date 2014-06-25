package org.openiam.elasticsearch.annotation;

import org.openiam.elasticsearch.constants.ESIndexName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by: Alexander Duckardt
 * Date: 6/25/14.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ESIndex {

    String indexName() default ESIndexName.USERS;

    String indexType();
}
