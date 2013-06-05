package org.openiam.idm.srvc.searchbean.converter;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.springframework.stereotype.Component;

@Component("identityAnswerSearchBeanConverter")
public class IdentityAnswerSearchBeanConverter implements SearchBeanConverter<UserIdentityAnswerEntity, IdentityAnswerSearchBean> {

	@Override
	public UserIdentityAnswerEntity convert(final IdentityAnswerSearchBean searchBean) {
		final UserIdentityAnswerEntity entity = new UserIdentityAnswerEntity();
		entity.setId(StringUtils.trimToNull(searchBean.getKey()));
		entity.setUserId(StringUtils.trimToNull(searchBean.getUserId()));
		if(StringUtils.isNotBlank(searchBean.getQuestionId())) {
			final IdentityQuestionEntity question = new IdentityQuestionEntity();
			question.setId(searchBean.getQuestionId());
			entity.setIdentityQuestion(question);
		}
		return entity;
	}

}
