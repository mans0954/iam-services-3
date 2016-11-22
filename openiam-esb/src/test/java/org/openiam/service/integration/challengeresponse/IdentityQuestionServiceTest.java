package org.openiam.service.integration.challengeresponse;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.testng.Assert;
import org.testng.annotations.Test;

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

/*    @Override
    protected String getId(IdentityQuestion bean) {
        return bean.getId();
    }

    @Override
    protected void setId(IdentityQuestion bean, String id) {
        bean.setId(id);
    }*/

    @Test
    public void getCorrectNumberTest() {
        String userId = "3000";
        Policy policy = this.getDefaultPasswordPolicy();

        String enterpriseAnswerPolicy = policy.getAttribute("QUEST_ANSWER_CORRECT").getValue1();
        String customAnswerPolicy = policy.getAttribute("CUSTOM_QUEST_ANSWER_COUNT").getValue1();
        String enterpriseQuestionsPolicy = policy.getAttribute("QUEST_COUNT").getValue1();
        String customQuestionsPolicy = policy.getAttribute("CUSTOM_QUEST_COUNT").getValue1();

        Integer enterpriseAnswers = challengeResponseServiceClient.getNumOfCorrectAnswers(userId, true);
        Integer customAnswers = challengeResponseServiceClient.getNumOfCorrectAnswers(userId, false);
        Integer enterpriseQuestions = challengeResponseServiceClient.getNumOfRequiredQuestions(userId, true);
        Integer customQuestions = challengeResponseServiceClient.getNumOfRequiredQuestions(userId, false);

        Assert.assertEquals(String.valueOf(enterpriseAnswerPolicy), String.valueOf(enterpriseAnswers), "enterpriseAnswers wrong!");
        Assert.assertEquals(String.valueOf(customAnswerPolicy), String.valueOf(customAnswers), "customAnswers wrong!");
        Assert.assertEquals(String.valueOf(enterpriseQuestionsPolicy), String.valueOf(enterpriseQuestions), "enterpriseQuestions wrong!");
        Assert.assertEquals(String.valueOf(customQuestionsPolicy), String.valueOf(customQuestions), "customQuestions wrong!");
    }


    @Test
    public void clusterTest() throws Exception {
        final ClusterKey<IdentityQuestion, IdentityQuestionSearchBean> key = doClusterTest();
        final IdentityQuestion instance = key.getDto();
        if (instance != null && instance.getId() != null) {
            deleteAndAssert(instance);
        }
    }

    public ClusterKey<IdentityQuestion, IdentityQuestionSearchBean> doClusterTest() throws Exception {
/*
 create and save
*/

        IdentityQuestion instance = createBean();
        Response response = saveAndAssert(instance);
        instance.setId((String) response.getResponseValue());

 /*find*/

        final IdentityQuestionSearchBean searchBean = newSearchBean();
        searchBean.setDeepCopy(useDeepCopyOnFindBeans());
        searchBean.addKey(instance.getId());

/*
 confirm save on both nodes
*/

        instance = assertClusteredSave(searchBean);
        return new ClusterKey<IdentityQuestion, IdentityQuestionSearchBean>(instance, searchBean);
    }
}
