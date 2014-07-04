package org.openiam.elasticsearch.annotation;

import org.openiam.elasticsearch.constants.ElasticsearchStore;
import org.openiam.elasticsearch.constants.ElasticsearchType;
import org.openiam.elasticsearch.constants.Index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by: Alexander Duckardt
 * Date: 6/25/14.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ElasticsearchField {

    String name();

    ElasticsearchType type() default ElasticsearchType.String;

    ElasticsearchStore store() default ElasticsearchStore.Yes;

    /**
     * Index property (analyzed, not_analyzed, no)
     */
    Index index() default Index.Undefined;
    /**
     * Name of the analyzer
     */
    String analyzerName() default "";

    /**
     * The analyzer used to analyze the text contents when analyzed during indexing.
     */
    String indexAnalyzerName() default "";

    /**
     * The analyzer used to analyze the field when part of a query string.
     */
    String searchAnalyzerName() default "";



}
