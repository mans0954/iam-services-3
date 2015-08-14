package org.openiam.internationalization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LocalizedDatabaseOperation {
	boolean saveOrUpdate() default false;
	boolean delete() default false;
}
