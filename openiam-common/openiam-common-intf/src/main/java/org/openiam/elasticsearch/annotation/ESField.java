package org.openiam.elasticsearch.annotation;

import org.openiam.elasticsearch.constants.ESAnalyze;
import org.openiam.elasticsearch.constants.ESType;
import org.openiam.elasticsearch.constants.EsStore;

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
public @interface ESField {

    String name();

    ESAnalyze analyze() default ESAnalyze.No;

    EsStore store() default EsStore.Yes;

    ESType type() default ESType.String;
}
