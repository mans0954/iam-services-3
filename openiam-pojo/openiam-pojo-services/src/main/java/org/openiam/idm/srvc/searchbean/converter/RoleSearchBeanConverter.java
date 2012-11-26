package org.openiam.idm.srvc.searchbean.converter;

import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleSearchBeanConverter implements SearchBeanConverter<RoleEntity, RoleSearchBean> {

	@Override
	public RoleEntity convert(RoleSearchBean searchBean) {
		final RoleEntity entity = new RoleEntity();
		entity.setRoleName(searchBean.getName());
		entity.setRoleId(searchBean.getKey());
		entity.setServiceId(searchBean.getServiceId());
		return entity;
	}

}
