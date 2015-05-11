package org.openiam.service.integration.challengeresponse;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.service.integration.AbstractKeyNameServiceTest;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class UserIdentityAnswerServiceTest extends AbstractChallengeResponseServiceTest<UserIdentityAnswer, IdentityAnswerSearchBean> {

	private User user = null;
	
	@BeforeClass
	public void _init() {
		user = super.createUser();
	}
	
	@AfterClass
	public void _destroy() {
		userServiceClient.removeUser(user.getId());
	}
	
	@Override
	protected UserIdentityAnswer newInstance() {
		final UserIdentityAnswer answer = new UserIdentityAnswer();
		answer.setQuestionId(getFirstQuestion().getId());
		answer.setUserId(user.getId());
		answer.setQuestionAnswer(getRandomName());
		return answer;
	}

	@Override
	protected IdentityAnswerSearchBean newSearchBean() {
		return new IdentityAnswerSearchBean();
	}

	@Override
	protected Response save(UserIdentityAnswer t) {
		return challengeResponseServiceClient.saveAnswer(t);
	}

	@Override
	protected Response delete(UserIdentityAnswer t) {
		return challengeResponseServiceClient.deleteAnswer(t.getId());
	}

	@Override
	protected UserIdentityAnswer get(String key) {
		final IdentityAnswerSearchBean searchBean = new IdentityAnswerSearchBean();
		searchBean.setKey(key);
		final List<UserIdentityAnswer> answerList = find(searchBean, 0, 1);
		return (CollectionUtils.isNotEmpty(answerList)) ? answerList.get(0) : null;
	}

	@Override
	public List<UserIdentityAnswer> find(IdentityAnswerSearchBean searchBean,
			int from, int size) {
		try {
			return challengeResponseServiceClient.findAnswerBeans(searchBean, null, from, size);
		} catch (Exception e) {
			logger.error("Can't execute find beans", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	protected String getId(UserIdentityAnswer bean) {
		return bean.getId();
	}

	@Override
	protected void setId(UserIdentityAnswer bean, String id) {
		bean.setId(id);
	}
}
