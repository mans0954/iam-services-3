package org.openiam.elasticsearch.converter;

import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.elasticsearch.model.RoleDoc;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.stereotype.Component;

@Component("roleDocumentToEntityConverter")
public class RoleDocumentToEntityConverter extends AbstractMetadataTypeDocumentToEntityConverter<RoleDoc, RoleEntity> {

	@Override
	protected RoleDoc newDocument() {
		return new RoleDoc();
	}

	@Override
	protected RoleEntity newEntity() {
		return new RoleEntity();
	}

	@Override
	public Class<RoleDoc> getDocumentClass() {
		return RoleDoc.class;
	}

	@Override
	public Class<RoleEntity> getEntityClass() {
		return RoleEntity.class;
	}

	@Override
	public RoleDoc convertToDocument(RoleEntity entity) {
		final RoleDoc doc = super.convertToDocument(entity);
		if(entity.getManagedSystem() != null && StringUtils.isNotBlank(entity.getManagedSystem().getId())) {
			doc.setManagedSysId(entity.getManagedSystem().getId());
		}
		return doc;
	}

	@Override
	public RoleEntity convertToEntity(RoleDoc doc) {
		final RoleEntity entity = super.convertToEntity(doc);
		if(StringUtils.isNotBlank(doc.getManagedSysId())) {
			entity.setManagedSystem(new ManagedSysEntity());
			entity.getManagedSystem().setId(doc.getManagedSysId());
		}
		return entity;
	}

	
}
