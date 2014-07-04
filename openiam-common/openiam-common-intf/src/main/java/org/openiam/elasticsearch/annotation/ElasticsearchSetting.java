package org.openiam.elasticsearch.annotation;

import org.openiam.elasticsearch.constants.ElasticsearchSettingName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by: Alexander Duckardt
 * Date: 7/3/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ElasticsearchSetting {
    ElasticsearchSettingName name();
    String value();
}
