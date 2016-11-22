package org.openiam.service.integration.challengeresponse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.user.dto.User;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserIdentityAnswerServiceTest extends AbstractChallengeResponseServiceTest<UserIdentityAnswer, IdentityAnswerSearchBean> {

    private User user = null;

    private static final String invalidAnswer = "sadasdasdadasdasdasdasdadasadasdsadasdasdadasdasdasdasdadasadasdsadasdasdadasdasdasdasdadasadasdsadasdasdadasdasdasdasdadasadasdsadasdasdadasdasdasdasdadasadasdsadasdasdadasdasdasdasdadasadasdsadasdasdadasdasdasdasdadasadasdsadasdasdadasdasdasdasdadasassss";


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
        searchBean.addKey(key);
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

/*    @Override
    protected String getId(UserIdentityAnswer bean) {
        return bean.getId();
    }

    @Override
    protected void setId(UserIdentityAnswer bean, String id) {
        bean.setId(id);
    }*/

    @Test
    public void clusterTest() throws Exception {
        final ClusterKey<UserIdentityAnswer, IdentityAnswerSearchBean> key = doClusterTest();
        final UserIdentityAnswer instance = key.getDto();
        if (instance != null && instance.getId() != null) {
            deleteAndAssert(instance);
        }
    }

    @Test
    public void saveAnswersWithInvalidLength() throws Exception {
        List<UserIdentityAnswer> userIdentityAnswerList = new ArrayList<UserIdentityAnswer>();
        UserIdentityAnswer uia1 = new UserIdentityAnswer();
        uia1.setUserId("3000");
        uia1.setQuestionId("200");
        uia1.setQuestionAnswer(invalidAnswer);

        UserIdentityAnswer uia2 = new UserIdentityAnswer();
        uia2.setUserId("3000");
        uia2.setQuestionId("202");
        uia2.setQuestionAnswer("dsfdsf");

        UserIdentityAnswer uia3 = new UserIdentityAnswer();
        uia3.setUserId("3000");
        uia3.setQuestionId("210");
        uia3.setQuestionAnswer("sdasdasdasd");

        userIdentityAnswerList.add(uia1);
        userIdentityAnswerList.add(uia2);
        userIdentityAnswerList.add(uia3);

        Response res = challengeResponseServiceClient.saveAnswers(userIdentityAnswerList);

        Assert.assertFalse(res.isSuccess());
        Assert.assertEquals(res.getErrorCode(), ResponseCode.ANSWER_IS_TOO_LONG);
    }

    @Test
    public void deleteAndSaveAnswers() {
        String userId = "3000";
        Assert.assertTrue(challengeResponseServiceClient.resetQuestionsForUser(userId).isSuccess());
        List<UserIdentityAnswer> userIdentityAnswers = new ArrayList<UserIdentityAnswer>();
        for (int i = 0; i < 3; i++) {
            UserIdentityAnswer userIdentityAnswer = new UserIdentityAnswer();
            userIdentityAnswer.setUserId(userId);
            userIdentityAnswer.setQuestionAnswer("1");
            userIdentityAnswer.setQuestionText(String.valueOf("QUESTION_" + i));
            userIdentityAnswers.add(userIdentityAnswer);
        }
        for (int i = 0; i < 3; i++) {
            UserIdentityAnswer userIdentityAnswer = new UserIdentityAnswer();
            userIdentityAnswer.setUserId(userId);
            userIdentityAnswer.setQuestionId(String.valueOf(200 + i));
            userIdentityAnswer.setQuestionAnswer("1");
            userIdentityAnswers.add(userIdentityAnswer);
        }
        Response response = challengeResponseServiceClient.saveAnswers(userIdentityAnswers);
        Assert.assertTrue(response.isSuccess());
    }

    public ClusterKey<UserIdentityAnswer, IdentityAnswerSearchBean> doClusterTest() throws Exception {
/*
 create and save
*/

        UserIdentityAnswer instance = createBean();
        Response response = saveAndAssert(instance);
        instance.setId((String) response.getResponseValue());

/*
 find
*/

        final IdentityAnswerSearchBean searchBean = newSearchBean();
        searchBean.setDeepCopy(useDeepCopyOnFindBeans());
        searchBean.addKey(instance.getId());

/*
 confirm save on both nodes
*/

        instance = assertClusteredSave(searchBean);
        return new ClusterKey<UserIdentityAnswer, IdentityAnswerSearchBean>(instance, searchBean);
    }
}
