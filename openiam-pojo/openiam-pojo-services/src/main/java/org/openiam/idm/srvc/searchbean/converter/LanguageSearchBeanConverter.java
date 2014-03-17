package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.GroupSearchBean;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.springframework.stereotype.Component;

@Component
public class LanguageSearchBeanConverter implements SearchBeanConverter<LanguageEntity, LanguageSearchBean> {

	@Override
	public LanguageEntity convert(final LanguageSearchBean searchBean) {
		final LanguageEntity entity = new LanguageEntity();
		entity.setLanguageCode(StringUtils.trimToNull(searchBean.getCode()));
		entity.setId(StringUtils.trimToNull(searchBean.getKey()));
		return entity;
	}

}
