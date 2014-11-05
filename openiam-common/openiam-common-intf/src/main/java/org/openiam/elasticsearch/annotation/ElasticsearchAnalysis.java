/**
 *
 */
package org.openiam.elasticsearch.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ElasticsearchAnalysis Annotation, used to define analysis settings of an
 * ElasticsearchIndex
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticsearchAnalysis {

    /**
     * Filters
     */
    ElasticsearchFilter[] filters() default {};

    /**
     * Analyzers
     */
    ElasticsearchAnalyzer[] analyzers() default {};
}
