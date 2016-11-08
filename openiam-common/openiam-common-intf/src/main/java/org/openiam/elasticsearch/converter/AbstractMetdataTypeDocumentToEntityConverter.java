package org.openiam.elasticsearch.converter;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.base.domain.AbstractMetdataTypeEntity;
import org.openiam.elasticsearch.model.AbstractMetdataTypeDoc;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;

public abstract class AbstractMetdataTypeDocumentToEntityConverter<D extends AbstractMetdataTypeDoc, E extends AbstractMetdataTypeEntity> extends AbstractKeyDocumentToEntityConverter<D, E> {

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
