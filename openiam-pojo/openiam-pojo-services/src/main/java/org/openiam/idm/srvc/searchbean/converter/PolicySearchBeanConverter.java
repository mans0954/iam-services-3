package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.PhoneSearchBean;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.continfo.domain.PhoneEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.springframework.stereotype.Component;

@Component
public class PolicySearchBeanConverter implements SearchBeanConverter<PolicyEntity, PolicySearchBean> {

	@Override
	public PolicyEntity convert(PolicySearchBean searchBean) {
		final PolicyEntity entity = new PolicyEntity();
		entity.setPolicyId(StringUtils.trimToNull(searchBean.getKey()));
		entity.setPolicyDefId(StringUtils.trimToNull(searchBean.getPolicyDefId()));
		return entity;
	}

}
