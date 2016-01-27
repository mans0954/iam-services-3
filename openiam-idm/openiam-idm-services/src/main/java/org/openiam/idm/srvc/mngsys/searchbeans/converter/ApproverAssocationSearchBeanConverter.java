package org.openiam.idm.srvc.mngsys.searchbeans.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component
public class ApproverAssocationSearchBeanConverter implements SearchBeanConverter<ApproverAssociationEntity, ApproverAssocationSearchBean> {

	@Override
	public ApproverAssociationEntity convert(final ApproverAssocationSearchBean searchBean) {
		final ApproverAssociationEntity entity = new ApproverAssociationEntity();
		entity.setId(StringUtils.trimToNull(searchBean.getId()));
		entity.setAssociationType(searchBean.getAssociationType());
		entity.setAssociationEntityId(StringUtils.trimToNull(searchBean.getAssociationEntityId()));

		entity.setApproverEntityId(StringUtils.trimToNull(searchBean.getApproverEntityId()));
		entity.setApproverEntityType(searchBean.getApproverEntityType());
		entity.setOnApproveEntityId(StringUtils.trimToNull(searchBean.getOnApproveEntityId()));
		entity.setOnApproveEntityType(searchBean.getOnApproveEntityType());
		entity.setOnRejectEntityId(StringUtils.trimToNull(searchBean.getOnRejectEntityId()));
		entity.setOnRejectEntityType(searchBean.getOnRejectEntityType());

		return entity;
	}

}
