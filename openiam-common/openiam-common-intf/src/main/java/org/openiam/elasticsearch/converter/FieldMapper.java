package org.openiam.elasticsearch.converter;

public interface FieldMapper<T> {
	T map(final Object o);
}
