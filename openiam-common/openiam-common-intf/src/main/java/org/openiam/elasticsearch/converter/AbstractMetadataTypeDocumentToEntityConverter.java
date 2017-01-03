package org.openiam.elasticsearch.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.elasticsearch.model.AbstractMetadataTypeDoc;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

public abstract class AbstractMetadataTypeDocumentToEntityConverter<D extends AbstractMetadataTypeDoc, E extends AbstractMetdataTypeEntity> extends AbstractKeyNameDocumentToEntityConverter<D, E> {

	@Override
	public D convertToDocument(E entity) {
		final D doc = super.convertToDocument(entity);
		if(entity.getType() != null && StringUtils.isNotBlank(entity.getType().getId())) {
			doc.setMetadataTypeId(entity.getType().getId());
		}
		return doc;
	}

	@Override
	public E convertToEntity(D doc) {
		final E entity = super.convertToEntity(doc);
		if(StringUtils.isNotBlank(doc.getMetadataTypeId())) {
			entity.setType(new MetadataTypeEntity());
			entity.getType().setId(doc.getMetadataTypeId());
		}
		return entity;
	}

	
	
}
