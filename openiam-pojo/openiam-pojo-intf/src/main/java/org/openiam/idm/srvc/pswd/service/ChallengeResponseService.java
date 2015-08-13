package org.openiam.idm.srvc.pswd.service;

import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;

import java.util.List;

public interface ChallengeResponseService {

	Integer getNumOfRequiredQuestions(String userId);
    Integer getNumOfCorrectAnswers(String userId);
	Integer count(final IdentityQuestionSearchBean searchBean);
	List<IdentityQuestionEntity> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size);
	List<UserIdentityAnswerEntity> findAnswerBeans(final IdentityAnswerSearchBean searchBean, String requesterId, final int from, final int size)  throws Exception;
	void saveQuestion(final IdentityQuestionEntity question) throws Exception;
	IdentityQuestionEntity getQuestion(final String questionId);
	void deleteQuestion(final String questionId) throws Exception ;
	void saveAnswer(final UserIdentityAnswerEntity answer) throws Exception ;
	void deleteAnswer(final String answerId) throws Exception ;
	void saveAnswers(List<UserIdentityAnswerEntity> answerList) throws Exception ;
	boolean isResponseValid(String userId, List<UserIdentityAnswerEntity> newAnswerList) throws Exception;
	boolean isUserAnsweredSecurityQuestions(final String userId) throws Exception;
	void resetQuestionsForUser(String userId);
}
