package org.openiam.elasticsearch.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.openiam.base.domain.KeyEntity;
import org.openiam.elasticsearch.converter.AbstractDocumentToEntityConverter;

@Target(TYPE)
@Retention(RUNTIME)
public @interface EntityRepresentation {

	Class<? extends KeyEntity> value();
	Class<? extends AbstractDocumentToEntityConverter> converter();
}
