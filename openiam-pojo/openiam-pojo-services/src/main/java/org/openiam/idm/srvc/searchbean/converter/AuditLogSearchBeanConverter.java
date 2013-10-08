package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.srvc.audit.domain.IdmAuditLogEntity;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditLogSearchBeanConverter implements SearchBeanConverter<IdmAuditLogEntity, AuditLogSearchBean> {

	@Override
	public IdmAuditLogEntity convert(final AuditLogSearchBean searchBean) {
		final IdmAuditLogEntity entity = new IdmAuditLogEntity();
		if(searchBean != null) {
			entity.setId(StringUtils.trimToNull(searchBean.getKey()));
		}
		return entity;
	}

}
