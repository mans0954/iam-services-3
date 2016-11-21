package org.openiam.elasticsearch.converter;

import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.openiam.elasticsearch.model.GroupDoc;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.springframework.stereotype.Component;

@Component("goleDocumentToEntityConverter")
public class GroupDocumentToEntityConverter extends AbstractMetdataTypeDocumentToEntityConverter<GroupDoc, GroupEntity> {

	@Override
	protected GroupDoc newDocument() {
		return new GroupDoc();
	}

	@Override
	protected GroupEntity newEntity() {
		return new GroupEntity();
	}

	@Override
	public Class<GroupDoc> getDocumentClass() {
		return GroupDoc.class;
	}

	@Override
	public Class<GroupEntity> getEntityClass() {
		return GroupEntity.class;
	}

	@Override
	public GroupDoc convertToDocument(GroupEntity entity) {
		final GroupDoc doc = super.convertToDocument(entity);
		doc.setName(entity.getName());
		if(entity.getManagedSystem() != null && StringUtils.isNotBlank(entity.getManagedSystem().getId())) {
			doc.setManagedSysId(entity.getManagedSystem().getId());
		}
		if(CollectionUtils.isNotEmpty(entity.getAttributes())) {
			entity.getAttributes().forEach(e -> {
				if(StringUtils.isNotBlank(e.getName())) {
					if(StringUtils.isNotBlank(e.getValue())) {
						doc.addAttribute(e.getName(), e.getValue());
					} else if(CollectionUtils.isNotEmpty(e.getValues())) {
						e.getValues().forEach(value -> {
							if(StringUtils.isNotBlank(value)) {
								doc.addAttribute(e.getName(), value);
							}
						});
					}
				}
			});
		}
		return doc;
	}

	@Override
	public GroupEntity convertToEntity(GroupDoc doc) {
		final GroupEntity entity = super.convertToEntity(doc);
		entity.setName(doc.getName());
		if(StringUtils.isNotBlank(doc.getManagedSysId())) {
			entity.setManagedSystem(new ManagedSysEntity());
			entity.getManagedSystem().setId(doc.getManagedSysId());
		}
		//no need to map Attributes back to the entity
		return entity;
	}

	
}
