package org.openiam.elasticsearch.converter;

import org.openiam.base.domain.KeyEntity;
import org.openiam.elasticsearch.model.AbstractKeyDoc;

public abstract class AbstractKeyDocumentToEntityConverter<D extends AbstractKeyDoc, E extends KeyEntity> extends AbstractDocumentToEntityConverter<D, E> {

	@Override
	public D convertToDocument(final E entity) {
		final D doc = newDocument();
		doc.setId(entity.getId());
		return doc;
	}
	
	@Override
	public E convertToEntity(final D doc) {
		final E entity = newEntity();
		entity.setId(doc.getId());
		return entity;
	}

}
