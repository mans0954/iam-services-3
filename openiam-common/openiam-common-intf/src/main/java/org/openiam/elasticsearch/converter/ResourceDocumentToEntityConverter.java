package org.openiam.elasticsearch.converter;

import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.elasticsearch.model.ResourceDoc;
import org.openiam.idm.srvc.res.domain.ResourceEntity;
import org.openiam.idm.srvc.res.domain.ResourceTypeEntity;
import org.openiam.idm.srvc.res.dto.ResourceRisk;
import org.springframework.stereotype.Component;

@Component("resourceDocumentToEntityConverter")
public class ResourceDocumentToEntityConverter extends AbstractMetadataTypeDocumentToEntityConverter<ResourceDoc, ResourceEntity> {

	@Override
	protected ResourceDoc newDocument() {
		return new ResourceDoc();
	}

	@Override
	protected ResourceEntity newEntity() {
		return new ResourceEntity();
	}

	@Override
	public Class<ResourceDoc> getDocumentClass() {
		return ResourceDoc.class;
	}

	@Override
	public Class<ResourceEntity> getEntityClass() {
		return ResourceEntity.class;
	}

	@Override
	public ResourceDoc convertToDocument(ResourceEntity entity) {
		final ResourceDoc doc = super.convertToDocument(entity);
		if(CollectionUtils.isNotEmpty(entity.getResourceProps())) {
			entity.getResourceProps().forEach(e -> {
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
		if(entity.getResourceType() != null && StringUtils.isNotBlank(entity.getResourceType().getId())) {
			doc.setResourceTypeId(entity.getResourceType().getId());
		}
		if(entity.getRisk() != null) {
			doc.setRisk(entity.getRisk().getValue());
		}
		if(CollectionUtils.isNotEmpty(entity.getParentResources())) {
			doc.setParentIds(entity.getParentResources().stream().map(e -> e.getEntity().getId()).collect(Collectors.toList()));
		}
		if(CollectionUtils.isNotEmpty(entity.getChildResources())) {
			doc.setChildIds(entity.getChildResources().stream().map(e -> e.getMemberEntity().getId()).collect(Collectors.toList()));
		}
		doc.setRoot(CollectionUtils.isNotEmpty(entity.getParentResources()));
		return doc;
	}

	@Override
	public ResourceEntity convertToEntity(ResourceDoc doc) {
		final ResourceEntity entity = super.convertToEntity(doc);
		if(StringUtils.isNotBlank(doc.getResourceTypeId())) { 
			entity.setResourceType(new ResourceTypeEntity());
			entity.getResourceType().setId(doc.getResourceTypeId());
		}
		
		if(StringUtils.isNotBlank(doc.getRisk())) {
			entity.setRisk(ResourceRisk.getByValue(doc.getRisk()));
		}
		return entity;
	}
}
