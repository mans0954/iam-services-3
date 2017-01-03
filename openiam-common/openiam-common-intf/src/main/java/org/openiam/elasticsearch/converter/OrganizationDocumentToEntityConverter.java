package org.openiam.elasticsearch.converter;

import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.elasticsearch.model.OrganizationDoc;
import org.openiam.idm.srvc.org.domain.OrganizationEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.springframework.stereotype.Component;

@Component("organizationDocumentToEntityConverter")
public class OrganizationDocumentToEntityConverter extends AbstractMetadataTypeDocumentToEntityConverter<OrganizationDoc, OrganizationEntity> {

	@Override
	public OrganizationDoc convertToDocument(final OrganizationEntity entity) {
		final OrganizationDoc doc = super.convertToDocument(entity);
		if(entity.getOrganizationType() != null && StringUtils.isNotBlank(entity.getOrganizationType().getId())) {
			doc.setOrganizationTypeId(entity.getOrganizationType().getId());
		}
		
		if(CollectionUtils.isNotEmpty(entity.getParentOrganizations())) {
			doc.setParentIds(entity.getParentOrganizations().stream().map(e -> e.getOrganization().getId()).collect(Collectors.toList()));
			doc.setParentOrganizationTypeIds(entity.getParentOrganizations().stream().map(e -> e.getOrganization().getOrganizationType().getId()).collect(Collectors.toList()));
		}
		return doc;
	}

	@Override
	public OrganizationEntity convertToEntity(final OrganizationDoc doc) {
		final OrganizationEntity entity = super.convertToEntity(doc);
		if(StringUtils.isNotBlank(doc.getOrganizationTypeId())) {
			entity.setOrganizationType(new OrganizationTypeEntity());
			entity.getOrganizationType().setId(doc.getOrganizationTypeId());
		}
		return entity;
	}

	@Override
	protected OrganizationDoc newDocument() {
		return new OrganizationDoc();
	}

	@Override
	protected OrganizationEntity newEntity() {
		return new OrganizationEntity();
	}

	@Override
	public Class<OrganizationDoc> getDocumentClass() {
		return OrganizationDoc.class;
	}

	@Override
	public Class<OrganizationEntity> getEntityClass() {
		return OrganizationEntity.class;
	}

}
