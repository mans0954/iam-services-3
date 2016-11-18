package org.openiam.elasticsearch.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.BaseIdentity;

public abstract class AbstractDocumentToEntityConverter<D extends BaseIdentity, E extends BaseIdentity> {

	public List<D> convertToDocumentList(final Collection<E> entities) {
		if(CollectionUtils.isNotEmpty(entities)) {
			return entities.stream().map(e -> convertToDocument(e)).collect(Collectors.toList());
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	public List<E> convertToEntityList(final Collection<D> docs) {
		if(CollectionUtils.isNotEmpty(docs)) {
			return docs.stream().map(e -> convertToEntity(e)).collect(Collectors.toList());
		} else {
			return Collections.EMPTY_LIST;
		}
	}
	public abstract D convertToDocument(final E entity);
	public abstract E convertToEntity(final D doc);
	protected abstract D newDocument();
	protected abstract E newEntity(); 
	
	public abstract Class<D> getDocumentClass();
	public abstract Class<E> getEntityClass();
}
