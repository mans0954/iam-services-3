package org.openiam.idm.srvc.pswd.service;

import org.apache.commons.lang.StringUtils;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ChallengeResponseServiceImpl implements ChallengeResponseService {

    @Value("${org.openiam.challenge.response.validator.object.name}")
    private String respValidatorObjName;
    
    @Value("${org.openiam.challenge.response.validator.object.type}")
    private String respValidatorObjType;
    
    @Autowired
    private ValidatorFactory respValidatorFactory;

    @Autowired
    private UserDataService userMgr;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private PasswordService passwordMgr;
	
	@Override
	public Integer getNumOfRequiredQuestions(String userId) {
		return getResponseValidator().getNumOfRequiredQuestions(userId);
	}

	@Override
	public Integer count(final IdentityQuestionSearchBean searchBean){
		return getResponseValidator().count(searchBean);
	}
	
	@Override
	public List<IdentityQuestionEntity> findQuestionBeans(
			IdentityQuestionSearchBean searchBean, int from, int size) {
		return getResponseValidator().findQuestionBeans(searchBean, from, size);
	}
    @Override
	public IdentityQuestionEntity getQuestion(final String questionId)
	{
		return getResponseValidator().getQuestion(questionId);
	}
	

	@Override
	public List<UserIdentityAnswerEntity> findAnswerBeans(
			IdentityAnswerSearchBean searchBean, int from, int size) {
		return getResponseValidator().findAnswerBeans(searchBean, from, size);
	}

	@Override
	public void saveQuestion(IdentityQuestionEntity entity) throws Exception {
		getResponseValidator().saveQuestion(entity);
	}

	@Override
	public void deleteQuestion(String questionId) throws Exception {
		getResponseValidator().deleteQuestion(questionId);
	}

	@Override
	public void saveAnswer(UserIdentityAnswerEntity entity) throws Exception  {
		getResponseValidator().saveAnswer(entity);
	}

	@Override
	public void deleteAnswer(String answerId) throws Exception  {
		getResponseValidator().deleteAnswer(answerId);
	}

	@Override
	public void saveAnswers(List<UserIdentityAnswerEntity> answerList) throws Exception  {
		 getResponseValidator().saveAnswers(answerList);

		 // add to audit log and update the user record that challenge response
		 // answers have been updated
		 // get the user Id
		 final String userId = answerList.get(0).getUserId();
		 final UserEntity usr = userMgr.getUser(userId);
		 usr.setDateChallengeRespChanged(new Date(System.currentTimeMillis()));
		 userMgr.updateUserWithDependent(usr, false);

		 /*
		  * This was throwing errors - removing for now
		 auditHelper.addLog("SET CHALLENGE QUESTIONS", null, null,
                "IDM SERVICE", userId, "PASSWORD", "CHALLENGE QUESTION", null,
                null, "SUCCESS", null, null, null, requestId, null, null, null);
		*/
	}

	@Override
	public boolean isResponseValid(String userId, List<UserIdentityAnswerEntity> newAnswerList) {
		 int requiredCorrect = newAnswerList.size();

		 final UserEntity user = userDAO.findById(userId);
	     final Policy policy = passwordMgr.getPasswordPolicyForUser(user);
	     final PolicyAttribute attr = policy.getAttribute("QUEST_ANSWER_CORRECT");

	     if (attr != null) {
	    	 if (StringUtils.isNotBlank(attr.getValue1())) {
	    		 requiredCorrect = Integer.parseInt(attr.getValue1());
	    	 }
	     }
	     return getResponseValidator().isResponseValid(userId, newAnswerList, requiredCorrect);
	}

	 private ChallengeResponseValidator getResponseValidator() {
		 return respValidatorFactory.createValidator(respValidatorObjName, respValidatorObjType);
	 }

	@Override
	public boolean isUserAnsweredSecurityQuestions(final String userId) {
		return getResponseValidator().isUserAnsweredSecurityQuestions(userId);
	}

	@Override
	public void resetQuestionsForUser(String userId) {
		getResponseValidator().resetQuestionsForUser(userId);
	}
}
