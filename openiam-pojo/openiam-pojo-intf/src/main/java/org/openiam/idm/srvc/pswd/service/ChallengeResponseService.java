package org.openiam.idm.srvc.pswd.service;

import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;

import java.util.List;

public interface ChallengeResponseService {

	public Integer getNumOfRequiredQuestions(String userId);
	public Integer count(final IdentityQuestionSearchBean searchBean);
	public List<IdentityQuestionEntity> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size);
	public List<UserIdentityAnswerEntity> findAnswerBeans(final IdentityAnswerSearchBean searchBean, final int from, final int size) ;
	public void saveQuestion(final IdentityQuestionEntity question) throws Exception;
	public IdentityQuestionEntity getQuestion(final String questionId);
	public void deleteQuestion(final String questionId) throws Exception ;
	public void saveAnswer(final UserIdentityAnswerEntity answer) throws Exception ;
	public void deleteAnswer(final String answerId) throws Exception ;
	public void saveAnswers(List<UserIdentityAnswerEntity> answerList) throws Exception ;
	public boolean isResponseValid(String userId, List<UserIdentityAnswerEntity> newAnswerList);
	public boolean isUserAnsweredSecurityQuestions(final String userId);
	public void resetQuestionsForUser(String userId);
}
