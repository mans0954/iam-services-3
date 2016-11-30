package org.openiam.elasticsearch.converter;

import java.util.Optional;

import org.openiam.elasticsearch.model.MetadataTypeDoc;
import org.openiam.idm.srvc.meta.domain.MetadataTypeEntity;
import org.openiam.idm.srvc.meta.domain.MetadataTypeGrouping;
import org.springframework.stereotype.Component;

@Component("metadataTypeToEntityConverter")
public class MetadataTypeDocToEntityConverter extends AbstractKeyNameDocumentToEntityConverter<MetadataTypeDoc, MetadataTypeEntity> {

	@Override
	protected MetadataTypeDoc newDocument() {
		return new MetadataTypeDoc();
	}

	@Override
	protected MetadataTypeEntity newEntity() {
		return new MetadataTypeEntity();
	}

	@Override
	public Class<MetadataTypeDoc> getDocumentClass() {
		return MetadataTypeDoc.class;
	}

	@Override
	public Class<MetadataTypeEntity> getEntityClass() {
		return MetadataTypeEntity.class;
	}

	@Override
	public MetadataTypeDoc convertToDocument(MetadataTypeEntity entity) {
		final MetadataTypeDoc doc = super.convertToDocument(entity);
		if(entity.getGrouping() != null) {
			doc.setGrouping(entity.getGrouping().name());
		}
		return doc;
	}

	@Override
	public MetadataTypeEntity convertToEntity(MetadataTypeDoc doc) {
		final MetadataTypeEntity entity = super.convertToEntity(doc);
		if(doc.getGrouping() != null) {
			entity.setGrouping(MetadataTypeGrouping.getByName(doc.getGrouping()));
		}
		return entity;
	}

	
}
