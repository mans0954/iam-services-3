/**
 *
 */
package org.openiam.elasticsearch.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ElasticsearchFilter annotation, used to define a filter in the analysis settings of an index
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticsearchFilter {

    /**
     * The name of the filter
     */
    String name();

    /**
     * The type's name of the filter
     */
    String typeName();

    /**
     * Filter settings
     */
    ElasticsearchSetting[] settings() default {};

}
