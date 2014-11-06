package org.openiam.elasticsearch.annotation;

import org.openiam.elasticsearch.constants.ElasticsearchStore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ElasticsearchMapping Annotation, used to create mapping for a given document type
 *
 * Created by: Alexander Duckardt
 * Date: 7/2/14.
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticsearchMapping {
    /**
     * The type's name for which the mapping is defined
     */
    String typeName();

    String parent() default "";

    ElasticsearchStore source() default ElasticsearchStore.Yes;
}
