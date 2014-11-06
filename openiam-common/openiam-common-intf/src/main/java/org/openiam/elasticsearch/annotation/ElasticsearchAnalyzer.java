/**
 *
 */
package org.openiam.elasticsearch.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * ElasticsearchAnalyzer annotation, used to define an analyzer in the analysis settings of an index
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticsearchAnalyzer {

    /**
     * The name of the analyzer
     */
    String name();

    /**
     * The name of the tokenizer
     */
    String tokenizer();

    /**
     * Filters names
     */
    String[] filtersNames() default {};

}
