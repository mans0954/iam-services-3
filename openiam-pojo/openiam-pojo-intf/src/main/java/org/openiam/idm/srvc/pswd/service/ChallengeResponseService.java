package org.openiam.idm.srvc.pswd.service;

import java.util.List;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

public interface ChallengeResponseService {

	public Integer getNumOfRequiredQuestions(String userId, String domainId);
	public Integer count(final IdentityQuestionSearchBean searchBean);
	public List<IdentityQuestionEntity> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size);
	public List<UserIdentityAnswerEntity> findAnswerBeans(final IdentityAnswerSearchBean searchBean, final int from, final int size) ;
	public IdentityQuestionEntity saveQuestion(final IdentityQuestionEntity question) throws Exception;
	public IdentityQuestionEntity getQuestion(final String questionId);
	public void deleteQuestion(final String questionId) throws Exception ;
	public void saveAnswer(final UserIdentityAnswerEntity answer) throws Exception ;
	public void deleteAnswer(final String answerId) throws Exception ;
	public void saveAnswers(List<UserIdentityAnswerEntity> answerList) throws Exception ;
	public boolean isResponseValid(String domainId, String userId, List<UserIdentityAnswerEntity> newAnswerList);
	public boolean isUserAnsweredSecurityQuestions(final String userId, final String domainId);
}
