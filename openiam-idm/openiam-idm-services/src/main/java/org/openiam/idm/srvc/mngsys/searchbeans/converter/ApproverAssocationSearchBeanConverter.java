package org.openiam.idm.srvc.mngsys.searchbeans.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.srvc.mngsys.domain.ApproverAssociationEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.mngsys.dto.ApproverAssocationSearchBean;
import org.openiam.idm.srvc.mngsys.dto.ManagedSysSearchBean;
import org.openiam.idm.srvc.searchbean.converter.SearchBeanConverter;
import org.springframework.stereotype.Component;

@Component
public class ApproverAssocationSearchBeanConverter implements SearchBeanConverter<ApproverAssociationEntity, ApproverAssocationSearchBean> {

	@Override
	public ApproverAssociationEntity convert(final ApproverAssocationSearchBean searchBean) {
		final ApproverAssociationEntity entity = new ApproverAssociationEntity();
		if(searchBean != null) {
			entity.setId(StringUtils.trimToNull(searchBean.getKey()));
			entity.setAssociationType(searchBean.getAssociationType());
			entity.setAssociationEntityId(StringUtils.trimToNull(searchBean.getAssociationEntityId()));
		}
		return entity;
	}

}
