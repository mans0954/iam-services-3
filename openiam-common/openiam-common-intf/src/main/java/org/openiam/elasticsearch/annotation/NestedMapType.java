package org.openiam.elasticsearch.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openiam.elasticsearch.converter.FieldMapper;

@Retention( RetentionPolicy.RUNTIME )
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NestedMapType {

	Class<? extends FieldMapper> keyMapper();
	Class<? extends FieldMapper> valueMapper();
}
