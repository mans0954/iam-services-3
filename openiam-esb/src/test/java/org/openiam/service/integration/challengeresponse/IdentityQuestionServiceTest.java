package org.openiam.service.integration.challengeresponse;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

public class IdentityQuestionServiceTest extends AbstractChallengeResponseServiceTest<IdentityQuestion, IdentityQuestionSearchBean> {

	@Override
	protected IdentityQuestion newInstance() {
		final IdentityQuestion question = new IdentityQuestion();
		question.setActive(false);
		question.setDisplayNameMap(generateRandomLanguageMapping());
		question.setIdentityQuestGrpId(getString("org.openiam.selfservice.challenge.response.group"));
		return question;
	}

	@Override
	protected IdentityQuestionSearchBean newSearchBean() {
		return new IdentityQuestionSearchBean();
	}

	@Override
	protected Response save(IdentityQuestion t) {
		return challengeResponseServiceClient.saveQuestion(t);
	}

	@Override
	protected Response delete(IdentityQuestion t) {
		return challengeResponseServiceClient.deleteQuestion(t.getId());
	}

	@Override
	protected IdentityQuestion get(String key) {
		return challengeResponseServiceClient.getQuestion(key, getDefaultLanguage());
	}

	@Override
	public List<IdentityQuestion> find(IdentityQuestionSearchBean searchBean,
			int from, int size) {
		return challengeResponseServiceClient.findQuestionBeans(searchBean, from, size, getDefaultLanguage());
	}
}
