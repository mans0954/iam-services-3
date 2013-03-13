package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestGroupEntity;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.springframework.stereotype.Component;

@Component("identityQuestionSearchBeanConverter")
public class IdentityQuestionSearchBeanConverter implements SearchBeanConverter<IdentityQuestionEntity, IdentityQuestionSearchBean> {

	@Override
	public IdentityQuestionEntity convert(final IdentityQuestionSearchBean searchBean) {
		final IdentityQuestionEntity entity = new IdentityQuestionEntity();
		if(StringUtils.isNotBlank(searchBean.getGroupId())) {
			IdentityQuestGroupEntity group = new IdentityQuestGroupEntity();
			group.setId(searchBean.getGroupId());
			entity.setIdentityQuestGrp(group);
		}
		
		entity.setActive(searchBean.isActive());
		entity.setId(StringUtils.trimToNull(searchBean.getKey()));
		return entity;
	}

}
