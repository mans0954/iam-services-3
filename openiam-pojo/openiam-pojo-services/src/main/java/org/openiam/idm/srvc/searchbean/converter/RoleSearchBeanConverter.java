package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.RoleSearchBean;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.role.domain.RoleEntity;
import org.springframework.stereotype.Component;

@Component
public class RoleSearchBeanConverter implements SearchBeanConverter<RoleEntity, RoleSearchBean> {

	@Override
	public RoleEntity convert(RoleSearchBean searchBean) {
		final RoleEntity entity = new RoleEntity();
		entity.setName(searchBean.getName());
        entity.setDescription(searchBean.getDescription());

        if(!searchBean.hasMultipleKeys()){
		    entity.setId(searchBean.getKey());
        }

        if(StringUtils.isNotBlank(searchBean.getManagedSysId())){
            ManagedSysEntity mngsys = new ManagedSysEntity();
            mngsys.setId(searchBean.getManagedSysId());
            entity.setManagedSystem(mngsys);
        }
		return entity;
	}

}
