package org.openiam.service.integration.challengeresponse;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.BatchTaskSearchBean;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.batch.dto.BatchTask;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService;
import org.openiam.service.integration.AbstractKeyServiceTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.testng.annotations.Test;

public class IdentityQuestionServiceTest extends AbstractChallengeResponseServiceTest<IdentityQuestion, IdentityQuestionSearchBean> {

	@Override
	protected IdentityQuestion newInstance() {
		final IdentityQuestion question = new IdentityQuestion();
		question.setActive(false);
		question.setDisplayNameMap(generateRandomLanguageMapping());
		question.setIdentityQuestGrpId("GLOBAL");
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

	@Override
	protected String getId(IdentityQuestion bean) {
		return bean.getId();
	}

	@Override
	protected void setId(IdentityQuestion bean, String id) {
		bean.setId(id);
	}
	
	@Test
	public void clusterTest() throws Exception {
		final ClusterKey<IdentityQuestion, IdentityQuestionSearchBean> key = doClusterTest();
		final IdentityQuestion instance = key.getDto();
		if(instance != null && instance.getId() != null) {
			deleteAndAssert(instance);
    	}
	}
	
	public ClusterKey<IdentityQuestion, IdentityQuestionSearchBean> doClusterTest() throws Exception {
		/* create and save */
		IdentityQuestion instance = createBean();
		Response response = saveAndAssert(instance);
		instance.setId((String)response.getResponseValue());

		/* find */
		final IdentityQuestionSearchBean searchBean = newSearchBean();
		searchBean.setDeepCopy(useDeepCopyOnFindBeans());
		searchBean.setKey(instance.getId());

		/* confirm save on both nodes */
		instance = assertClusteredSave(searchBean);
		return new ClusterKey<IdentityQuestion, IdentityQuestionSearchBean>(instance, searchBean);
	}
}
