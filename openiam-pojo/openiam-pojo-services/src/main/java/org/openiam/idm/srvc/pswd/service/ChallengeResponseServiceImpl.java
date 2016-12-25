package org.openiam.idm.srvc.pswd.service;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.ResponseCode;
import org.openiam.dozer.converter.IdentityQuestGroupDozerConverter;
import org.openiam.dozer.converter.IdentityQuestionDozerConverter;
import org.openiam.dozer.converter.UserIdentityAnswerDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestGroupEntity;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
//@Transactional
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
    private PasswordService passwordMgr;
    
    @Autowired
    private IdentityQuestGroupDozerConverter groupDozerConverter;

    @Autowired
    private IdentityQuestionDozerConverter questionDozerConverter;
    @Autowired
    private UserIdentityAnswerDozerConverter answerDozerConverter;

    @Override
    public Integer getNumOfRequiredQuestions(String userId, boolean isEnterprise) {
        return getResponseValidator().getNumOfRequiredQuestions(userId, isEnterprise);
    }

    /**
     * How many questions the user must answer correctly
     */
    @Override
    public Integer getNumOfCorrectAnswers(String userId, boolean isEnterprise) {
        return getResponseValidator().getNumOfCorrectAnswers(userId, isEnterprise);
    }

    @Override
    public Integer count(final IdentityQuestionSearchBean searchBean) {
        return getResponseValidator().count(searchBean);
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public List<IdentityQuestion> findQuestionBeans(IdentityQuestionSearchBean searchBean, int from, int size) {
        List<IdentityQuestionEntity> beans = getResponseValidator().findQuestionBeans(searchBean, from, size);
        return (beans != null) ? questionDozerConverter.convertToDTOList(beans, (searchBean != null) ? searchBean.isDeepCopy() : false) : null;
    }

    @Override
    @LocalizedServiceGet
    @Transactional(readOnly = true)
    public IdentityQuestion getQuestion(final String questionId) {
        IdentityQuestionEntity question = getResponseValidator().getQuestion(questionId);
        return (question != null) ? questionDozerConverter.convertToDTO(question, false) : null;
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserIdentityAnswer> findAnswerBeans(IdentityAnswerSearchBean searchBean, String requesterId, int from, int size) throws BasicDataServiceException {
        final List<UserIdentityAnswerEntity> beans = getResponseValidator().findAnswerBeans(searchBean, requesterId, from, size);
        return (beans != null) ? answerDozerConverter.convertToDTOList(beans, searchBean.isDeepCopy()) : null;
    }
    

    @Override
    @Transactional
    public String saveQuestion(IdentityQuestion question) throws BasicDataServiceException {
        if (question == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if (StringUtils.isBlank(question.getIdentityQuestGrpId())) {
            throw new BasicDataServiceException(ResponseCode.NO_IDENTITY_QUESTION_GROUP);
        }
        if (MapUtils.isEmpty(question.getDisplayNameMap())) {
            throw new BasicDataServiceException(ResponseCode.NO_IDENTITY_QUESTION);
        }
        final IdentityQuestionEntity entity = questionDozerConverter.convertToEntity(question, false);
        getResponseValidator().saveQuestion(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void deleteQuestion(String questionId) throws BasicDataServiceException {
        if (StringUtils.isBlank(questionId)) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }
        getResponseValidator().deleteQuestion(questionId);
    }

    @Override
    @Transactional
    public String saveAnswer(UserIdentityAnswer answer) throws BasicDataServiceException {
        if (answer == null) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }

        if (StringUtils.isBlank(answer.getQuestionId())) {
            throw new BasicDataServiceException(ResponseCode.NO_IDENTITY_QUESTION);
        }

        final UserIdentityAnswerEntity entity = answerDozerConverter.convertToEntity(answer, true);
        if (answer.getQuestionId() == null) {
            entity.setIdentityQuestion(null);
        }
        getResponseValidator().saveAnswer(entity);
        return entity.getId();
    }

    @Override
    @Transactional
    public void deleteAnswer(String answerId) throws BasicDataServiceException {
        if (StringUtils.isBlank(answerId)) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }
        getResponseValidator().deleteAnswer(answerId);
    }

    @Override
    @Transactional
    public void saveAnswers(List<UserIdentityAnswer> answerList) throws BasicDataServiceException {
        this.validateAnswers(answerList);
        final List<UserIdentityAnswerEntity> answerEntityList = new LinkedList<UserIdentityAnswerEntity>();
        for (final UserIdentityAnswer answer : answerList) {
            final UserIdentityAnswerEntity entity = answerDozerConverter.convertToEntity(answer, true);
            if (answer.getQuestionId() == null)
                entity.setIdentityQuestion(null);
            answerEntityList.add(entity);
        }
        getResponseValidator().saveAnswers(answerEntityList);

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
    public boolean isResponseValid(String userId, List<UserIdentityAnswer> newAnswerDtoList) throws BasicDataServiceException {
        final List<UserIdentityAnswerEntity> entityList = answerDozerConverter.convertToEntityList(newAnswerDtoList, true);

        int requiredCorrectEnterprise = 0;
        int requiredCorrectUserSpecified = 0;
        PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
        searchBean.setUserId(userId);
        final Policy policy = passwordMgr.getPasswordPolicy(searchBean);
        final PolicyAttribute attrEnterprise = policy.getAttribute("QUEST_ANSWER_CORRECT");
        final PolicyAttribute attrUserSpecified = policy.getAttribute("CUSTOM_QUEST_ANSWER_COUNT");
        if (attrEnterprise != null) {
            if (StringUtils.isNotBlank(attrEnterprise.getValue1())) {
                requiredCorrectEnterprise = Integer.parseInt(attrEnterprise.getValue1());
            }
        }

        if (attrUserSpecified != null) {
            if (StringUtils.isNotBlank(attrUserSpecified.getValue1())) {
                requiredCorrectUserSpecified = Integer.parseInt(attrUserSpecified.getValue1());
            }
        }
        
        final IdentityAnswerSearchBean sb = new IdentityAnswerSearchBean();
        sb.setUserId(userId);
        final List<UserIdentityAnswerEntity> savedAnsList = getResponseValidator().findAnswerBeans(sb, null, 0, Integer.MAX_VALUE);

        return getResponseValidator().isResponseValid(userId, entityList, savedAnsList, requiredCorrectEnterprise, true)
                && getResponseValidator().isResponseValid(userId, entityList, savedAnsList, requiredCorrectUserSpecified, false);
    }

    private ChallengeResponseValidator getResponseValidator() {
        return respValidatorFactory.createValidator(respValidatorObjName, respValidatorObjType);
    }

    @Override
    public boolean isUserAnsweredSecurityQuestions(final String userId) throws BasicDataServiceException {
        return getResponseValidator().isUserAnsweredSecurityQuestions(userId);
    }

    @Override
    public void resetQuestionsForUser(String userId) {
        getResponseValidator().resetQuestionsForUser(userId);
    }

	@Override
	@Transactional(readOnly=true)
	public List<IdentityQuestGroup> getAllIdentityQuestionGroupsDTO() {
		final List<IdentityQuestGroupEntity> entities = getResponseValidator().getAllIdentityQuestionGroups();
		return groupDozerConverter.convertToDTOList(entities, false);
	}
    @Override
    public void validateAnswers(List<UserIdentityAnswer> answerList)  throws BasicDataServiceException{
        if (CollectionUtils.isEmpty(answerList)) {
            throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
        }
        String requestId = "R" + UUIDGen.getUUID();

			// check for duplicates
        final Set<String> questionIdSet = new HashSet<String>();
        final Set<String> questionTextSet = new HashSet<String>();
        for (final UserIdentityAnswer answer : answerList) {
            if (StringUtils.isNotBlank(answer.getQuestionId()) && questionIdSet.contains(answer.getQuestionId())) {
                throw new BasicDataServiceException(ResponseCode.IDENTICAL_QUESTIONS);
            }
            if (StringUtils.isNotBlank(answer.getQuestionText()) && questionTextSet.contains(answer.getQuestionText())) {
                throw new BasicDataServiceException(ResponseCode.IDENTICAL_QUESTIONS);
            }
            if (StringUtils.isBlank(answer.getQuestionId()) && StringUtils.isBlank(answer.getQuestionText())) {
                throw new BasicDataServiceException(ResponseCode.QUEST_NOT_SELECTED);
            }

            if(StringUtils.isBlank(answer.getQuestionAnswer())){
                throw new BasicDataServiceException(ResponseCode.ANSWER_NOT_TAKEN);
            }
            if (StringUtils.isNotBlank(answer.getQuestionId()))
                questionIdSet.add(answer.getQuestionId());

            if (StringUtils.isNotBlank(answer.getQuestionText()))
                questionTextSet.add(answer.getQuestionText());
        }
    }
}
