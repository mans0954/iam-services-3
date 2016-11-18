package org.openiam.elasticsearch.converter;

import org.openiam.base.domain.AbstractKeyNameEntity;
import org.openiam.elasticsearch.model.AbstractKeyNameDoc;

public abstract class AbstractKeyNameDocumentToEntityConverter<D extends AbstractKeyNameDoc, E extends AbstractKeyNameEntity> extends AbstractKeyDocumentToEntityConverter<D, E> {

	@Override
	public D convertToDocument(E entity) {
		final D doc = super.convertToDocument(entity);
		doc.setName(entity.getName());
		return doc;
	}

	@Override
	public E convertToEntity(D doc) {
		final E entity = super.convertToEntity(doc);
		entity.setName(doc.getName());
		return entity;
	}

	
}
