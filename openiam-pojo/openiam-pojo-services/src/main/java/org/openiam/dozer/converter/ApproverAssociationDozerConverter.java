package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssociation;
import org.springframework.stereotype.Component;

@Component
public class ApproverAssociationDozerConverter extends AbstractDozerEntityConverter<ApproverAssociation, ApproverAssociationEntity> {

	@Override
	public ApproverAssociationEntity convertEntity(
			ApproverAssociationEntity entity, boolean isDeep) {
		return convert(entity, isDeep, ApproverAssociationEntity.class);
	}

	@Override
	public ApproverAssociation convertDTO(ApproverAssociation entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ApproverAssociation.class);
	}

	@Override
	public ApproverAssociationEntity convertToEntity(
			ApproverAssociation entity, boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ApproverAssociationEntity.class);
	}

	@Override
	public ApproverAssociation convertToDTO(ApproverAssociationEntity entity,
			boolean isDeep) {
		return convertToCrossEntity(entity, isDeep, ApproverAssociation.class);
	}

	@Override
	public List<ApproverAssociationEntity> convertToEntityList(
			List<ApproverAssociation> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, ApproverAssociationEntity.class);
	}

	@Override
	public List<ApproverAssociation> convertToDTOList(
			List<ApproverAssociationEntity> list, boolean isDeep) {
		return convertListToCrossEntity(list, isDeep, ApproverAssociation.class);
	}

	@Override
	public Set<ApproverAssociationEntity> convertToEntitySet(
			Set<ApproverAssociation> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, ApproverAssociationEntity.class);
	}

	@Override
	public Set<ApproverAssociation> convertToDTOSet(
			Set<ApproverAssociationEntity> set, boolean isDeep) {
		return convertSetToCrossEntity(set, isDeep, ApproverAssociation.class);
	}

}
