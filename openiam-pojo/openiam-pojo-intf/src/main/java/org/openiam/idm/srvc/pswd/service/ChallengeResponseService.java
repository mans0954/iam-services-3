package org.openiam.idm.srvc.pswd.service;

import java.util.List;

import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

public interface ChallengeResponseService {

	public Integer getNumOfRequiredQuestions(String userId,boolean isEnterprise);
    public Integer getNumOfCorrectAnswers(String userId,boolean isEnterprise);
	Integer count(final IdentityQuestionSearchBean searchBean);
	List<IdentityQuestion> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size);
	List<UserIdentityAnswer> findAnswerBeans(final IdentityAnswerSearchBean searchBean, String requesterId, final int from, final int size) throws BasicDataServiceException;
	String saveQuestion(final IdentityQuestion question) throws BasicDataServiceException;
	IdentityQuestion getQuestion(final String questionId);
	void deleteQuestion(final String questionId) throws BasicDataServiceException ;
	String saveAnswer(final UserIdentityAnswer answer) throws BasicDataServiceException ;
	void deleteAnswer(final String answerId) throws BasicDataServiceException ;
	void saveAnswers(List<UserIdentityAnswer> answerList) throws BasicDataServiceException ;
	boolean isResponseValid(String userId, List<UserIdentityAnswer> newAnswerList) throws BasicDataServiceException;
	boolean isUserAnsweredSecurityQuestions(final String userId) throws BasicDataServiceException;
	void resetQuestionsForUser(String userId);
	List<IdentityQuestGroup> getAllIdentityQuestionGroupsDTO();

	void validateAnswers(List<UserIdentityAnswer> answerList)  throws BasicDataServiceException;
}
