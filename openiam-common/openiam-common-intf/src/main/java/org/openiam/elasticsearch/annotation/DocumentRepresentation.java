package org.openiam.elasticsearch.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.openiam.elasticsearch.converter.AbstractDocumentToEntityConverter;
import org.openiam.elasticsearch.model.AbstractKeyDoc;

@Target(TYPE)
@Retention(RUNTIME)
public @interface DocumentRepresentation {

	Class<? extends AbstractKeyDoc> value();
	Class<? extends AbstractDocumentToEntityConverter> converter();
}
