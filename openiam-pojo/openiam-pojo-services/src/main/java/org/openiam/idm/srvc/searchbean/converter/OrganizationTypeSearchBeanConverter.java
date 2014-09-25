package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.AddressSearchBean;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.idm.srvc.continfo.domain.AddressEntity;
import org.openiam.idm.srvc.org.domain.OrganizationTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class OrganizationTypeSearchBeanConverter implements SearchBeanConverter<OrganizationTypeEntity, OrganizationTypeSearchBean> {

	@Override
	public OrganizationTypeEntity convert(final OrganizationTypeSearchBean searchBean) {
		final OrganizationTypeEntity entity = new OrganizationTypeEntity();
		entity.setId(StringUtils.trimToNull(searchBean.getKey()));
		entity.setName(StringUtils.trimToNull(searchBean.getName()));
		return entity;
	}

}
