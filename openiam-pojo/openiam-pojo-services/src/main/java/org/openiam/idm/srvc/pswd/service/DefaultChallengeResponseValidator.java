/*
 * Copyright 2009, OpenIAM LLC 
 * This file is part of the OpenIAM Identity and Access Management Suite
 *
 *   OpenIAM Identity and Access Management Suite is free software: 
 *   you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License 
 *   version 3 as published by the Free Software Foundation.
 *
 *   OpenIAM is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Lesser GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.pswd.service;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.data.IdentityAnswerNotFoundException;
import org.openiam.exception.data.PrincipalNotFoundException;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestGroupEntity;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the challenge response validator. This implementation uses the information stored in the OpenIAM repository
 *
 * @author suneet
 *
 */
@Service("challengeResponseValidator")
public class DefaultChallengeResponseValidator implements ChallengeResponseValidator {

	private static final Log LOG = LogFactory.getLog(DefaultChallengeResponseValidator.class);

    @Autowired
    private LoginDataService loginManager;

    @Autowired
    private IdentityQuestionDAO questionDAO;

    @Autowired
    private UserIdentityAnswerDAO answerDAO;

    @Autowired
    private IdentityQuestGroupDAO questionGroupDAO;

/*    @Autowired
    private IdentityAnswerSearchBeanConverter answerSearchBeanConverter;

    @Autowired
    private IdentityQuestionSearchBeanConverter questionSearchBeanConverter;*/

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordService passwordService;
    @Autowired
    private KeyManagementService keyManagementService;

    private static final Log log = LogFactory.getLog(DefaultChallengeResponseValidator.class);

    private static final Integer maxLengthAnswer = 255;

    @Override
    @Transactional(readOnly=true)
    public boolean isResponseValid(final String userId, 
    							   final List<UserIdentityAnswerEntity> newAnswerList, 
    							   final List<UserIdentityAnswerEntity> savedAnsList, 
    							   final int requiredCorrectAns, 
    							   final boolean isEnterprise) throws BasicDataServiceException {
        final int correctAns = getNumOfCorrectAnswers(userId, newAnswerList, savedAnsList, isEnterprise);
        return correctAns >= requiredCorrectAns && requiredCorrectAns > 0;
    }

    /**
     * Gets the number of questions to show on challenge response questions based on isEnterprise flag
     */
    @Override
    @Transactional(readOnly=true)
    public Integer getNumOfRequiredQuestions(final String userId, boolean isEnterprise) {
        Policy passwordPolicy = null;
        if (StringUtils.isNotBlank(userId)) {
            PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
            searchBean.setUserId(userId);
            passwordPolicy = passwordService.getPasswordPolicy(searchBean);
        }
        if (passwordPolicy == null) {
            passwordPolicy = passwordService.getGlobalPasswordPolicy();
        }

        Integer count = null;
        if (passwordPolicy != null) {
            PolicyAttribute countAttr = isEnterprise ? passwordPolicy.getAttribute("QUEST_COUNT") : passwordPolicy.getAttribute("CUSTOM_QUEST_COUNT");
            try {
                count = countAttr.isRequired() ? Integer.valueOf(countAttr.getValue1()) : 0;
            } catch (Throwable e) {
                log.warn("Cannot parse policy attribute value");
            }
        }
        return count;
    }

    /**
     * How many questions the user must answer correctly
     */
    @Override
    @Transactional(readOnly=true)
    public Integer getNumOfCorrectAnswers(final String userId, boolean isEnterprise) {
        Policy passwordPolicy = null;
        if (StringUtils.isNotBlank(userId)) {
            PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
            searchBean.setUserId(userId);
            passwordPolicy = passwordService.getPasswordPolicy(searchBean);
        }
        if (passwordPolicy == null) {
            passwordPolicy = passwordService.getGlobalPasswordPolicy();
        }

        Integer count = null;
        if (passwordPolicy != null) {
            PolicyAttribute countAttr = isEnterprise ? passwordPolicy.getAttribute("QUEST_ANSWER_CORRECT") : passwordPolicy.getAttribute("CUSTOM_QUEST_ANSWER_COUNT");
            try {
                count = Integer.valueOf(countAttr.getValue1());
            } catch (Throwable e) {
                log.warn("Cannot parse policy attribute value");
            }
        }
        return count;
    }

    @Override
    @Transactional(readOnly=true)
    public boolean isUserAnsweredSecurityQuestions(final String userId) throws BasicDataServiceException {
    	final List<UserIdentityAnswerEntity> answerList = answersByUser(userId);
        return this.isUserAnsweredSecurityQuestions(userId, true, answerList) && this.isUserAnsweredSecurityQuestions(userId, false, answerList);
    }

    private boolean isUserAnsweredSecurityQuestions(final String userId, final boolean isEnterprise, final List<UserIdentityAnswerEntity> answerList) throws BasicDataServiceException {
        final Integer numOfRequiredQuestions = getNumOfRequiredQuestions(userId, isEnterprise);


        boolean retVal = false;
        if (numOfRequiredQuestions == null) {
            retVal = true;
        } else if (CollectionUtils.isNotEmpty(answerList)) {
            if (answerList.size() >= numOfRequiredQuestions.intValue()) {
                retVal = true;
            }
        }

        return retVal;
    }

    private int getNumOfCorrectAnswers(final String userId, final List<UserIdentityAnswerEntity> newAnswerList, final List<UserIdentityAnswerEntity> savedAnsList, boolean isEnterprise)
            throws BasicDataServiceException {
        int correctAns = 0;

        LoginEntity lg = loginManager.getPrimaryIdentity(userId);

        if (lg == null) {
            throw new PrincipalNotFoundException(String.format("Login object not found for userId=%s", userId));
        }
        // get the answers in the system to validate the response.
        if (CollectionUtils.isEmpty(savedAnsList)) {
            throw new IdentityAnswerNotFoundException();
        }

        for (UserIdentityAnswerEntity savedAns : savedAnsList) {
            if ((isEnterprise && savedAns.getIdentityQuestion() == null) || (!isEnterprise && savedAns.getIdentityQuestion() != null)) {
                continue;
            }
            for (UserIdentityAnswerEntity newAns : newAnswerList) {
                if (StringUtils.equalsIgnoreCase(newAns.getId(), savedAns.getId())) {

                	/* savedAns.getQuestionAnswer() should contain the plaintext answer at this point */
                    if (StringUtils.equalsIgnoreCase(newAns.getQuestionAnswer(), savedAns.getQuestionAnswer())) {
                        correctAns++;
                    }
                }
            }
        }
        return correctAns;
    }

    private List<UserIdentityAnswerEntity> answersByUser(String userId) throws BasicDataServiceException {
        if (userId == null) {
            throw new NullPointerException("UserId is null");
        }

        final IdentityAnswerSearchBean sb = new IdentityAnswerSearchBean();
        sb.setUserId(userId);
        return findAnswerBeans(sb, null, 0, Integer.MAX_VALUE);
    }

    @Override
    @Transactional(readOnly=true)
    public List<IdentityQuestionEntity> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size) {
        List<IdentityQuestionEntity> resultList = null;
        if (searchBean != null && CollectionUtils.isNotEmpty(searchBean.getKeySet())) {
            final List<IdentityQuestionEntity> entityList = questionDAO.findByIds(searchBean.getKeySet());
            if (CollectionUtils.isNotEmpty(entityList)) {
                resultList = new LinkedList<IdentityQuestionEntity>();
                resultList.addAll(entityList);
            }
        } else {
            resultList = questionDAO.getByExample(searchBean, from, size);
        }
        return resultList;
    }

    @Override
    @Transactional(readOnly=true)
    public Integer count(final IdentityQuestionSearchBean searchBean) {
        return questionDAO.count(searchBean);
    }


    @Override
    @Transactional(readOnly=true)
    public List<UserIdentityAnswerEntity> findAnswerBeans(final IdentityAnswerSearchBean searchBean, String requesterId, final int from, final int size)
            throws BasicDataServiceException {
        List<UserIdentityAnswerEntity> resultList = null;
        if(searchBean != null) {
	        if (CollectionUtils.isNotEmpty(searchBean.getKeySet())) {
	            final List<UserIdentityAnswerEntity> entityList = answerDAO.findByIds(searchBean.getKeySet());
	            if (CollectionUtils.isNotEmpty(entityList)) {
	                resultList = new LinkedList<UserIdentityAnswerEntity>();
	                resultList.addAll(entityList);
	            }
	        } else {
	        	if(Boolean.TRUE.equals(searchBean.getIsEncrypted()) && StringUtils.isNotBlank(searchBean.getQuestionText()) && StringUtils.isNotBlank(searchBean.getUserId())) {
	        		if(StringUtils.isNotBlank(searchBean.getQuestionText())) {
                        try {
                            searchBean.setQuestionText(keyManagementService.encrypt(searchBean.getUserId(), KeyName.challengeResponse, searchBean.getQuestionText()));
                        } catch (Exception e) {
                            throw new BasicDataServiceException(ResponseCode.DATA_ENCRYPTION_ERROR, e.getMessage());
                        }
                    }
	        	}
	            resultList = answerDAO.getByExample(searchBean, from, size);
	        }
        }
        return decryptAnswers(resultList, requesterId);
    }

    @Override
    @Transactional
    public void saveQuestion(final IdentityQuestionEntity entity) throws BasicDataServiceException {
        if (entity.getIdentityQuestGrp() != null && StringUtils.isNotBlank(entity.getIdentityQuestGrp().getId())) {
            entity.setIdentityQuestGrp(questionGroupDAO.findById(entity.getIdentityQuestGrp().getId()));
        }
        if (entity.getId() == null) {
            questionDAO.save(entity);
        } else {
            questionDAO.merge(entity);
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(final String questionId) throws BasicDataServiceException {
        final IdentityQuestionEntity entity = questionDAO.findById(questionId);
        if (entity != null) {
            answerDAO.deleteAnswersByQuestionId(entity.getId());
            questionDAO.delete(entity);
        }
    }

    @Override
    @Transactional(readOnly=true)
    public IdentityQuestionEntity getQuestion(final String questionId) {
        final IdentityQuestionEntity entity = questionDAO.findById(questionId);
        return entity;
    }

    @Override
    @Transactional
    public void saveAnswer(final UserIdentityAnswerEntity entity) throws BasicDataServiceException {
        if (entity.getIdentityQuestion() != null && StringUtils.isNotBlank(entity.getIdentityQuestion().getId())) {
            entity.setIdentityQuestion(questionDAO.findById(entity.getIdentityQuestion().getId()));
        }

        if (StringUtils.isBlank(entity.getQuestionAnswer())) {
            throw new BasicDataServiceException(ResponseCode.NO_ANSWER_TO_QUESTION);
        }

        try {
            entity.setQuestionAnswer(keyManagementService.encrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionAnswer()));
        } catch (Exception e) {
            throw new BasicDataServiceException(ResponseCode.DATA_ENCRYPTION_ERROR, e.getMessage());
        }
        entity.setIsEncrypted(true);

        answerDAO.merge(entity);
    }

    @Override
    @Transactional
    public void deleteAnswer(final String answerId) throws BasicDataServiceException {
        final UserIdentityAnswerEntity entity = answerDAO.findById(answerId);
        if (entity != null) {
            answerDAO.delete(entity);
        }
    }

    @Override
    @Transactional
    public void saveAnswers(final List<UserIdentityAnswerEntity> answerList) throws BasicDataServiceException {
        if (answerList != null) {
            for (final UserIdentityAnswerEntity entity : answerList) {

                if (validateAnswerLength(entity.getQuestionAnswer())) {
                    try{
                        if (entity.getIdentityQuestion() != null && StringUtils.isNotBlank(entity.getIdentityQuestion().getId())) {
                            entity.setIdentityQuestion(questionDAO.findById(entity.getIdentityQuestion().getId()));
                        }
                        entity.setQuestionAnswer(keyManagementService.encrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionAnswer()));
                        //enncrypt Custom question
                        if (entity.getIdentityQuestion() == null && StringUtils.isNotBlank(entity.getQuestionText())) {
                            entity.setQuestionText(keyManagementService.encrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionText()));
                        }
                        entity.setIsEncrypted(true);
                    } catch (Exception ex){
                        throw new BasicDataServiceException(ResponseCode.DATA_ENCRYPTION_ERROR, ex.getMessage());
                    }

                } else {
                    throw new BasicDataServiceException(ResponseCode.ANSWER_IS_TOO_LONG);
                }
                //entity.setQuestionAnswer(keyManagementService.encrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionAnswer()));
                entity.setIsEncrypted(true);
            }
            answerDAO.save(answerList);
        }
    }

    private boolean validateAnswerLength(String answer) {
        if (answer.length() <= maxLengthAnswer) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void resetQuestionsForUser(String userId) {
        answerDAO.deleteByUser(userId);
        LoginEntity login = loginManager.getPrimaryIdentity(userId);
        if (login != null) {
            login.setChallengeResponseFailCount(0);
            loginManager.updateLogin(login);
        }
    }


    private List<UserIdentityAnswerEntity> decryptAnswers(List<UserIdentityAnswerEntity> answerList, String requesterId)
            throws BasicDataServiceException {
        if (CollectionUtils.isNotEmpty(answerList)) {
            try {
                for (UserIdentityAnswerEntity entity : answerList) {
            	/*
            	 * if requesterId is null, then it's an unauthenticated user trying to answer his response questions
            	 * this should happen *only* if the user is trying to unlock his password.
            	 */
                    if ((StringUtils.isBlank(requesterId) || requesterId.equals(entity.getUserId())) && entity.getIsEncrypted()) {

                        entity.setQuestionAnswer(keyManagementService.decrypt(entity.getUserId(), KeyName.challengeResponse,
                                entity.getQuestionAnswer()));

                    }
                    //decrypt Custom question
                    if ((entity.getIdentityQuestion() == null || entity.getIdentityQuestion().getId() == null) && StringUtils.isNotBlank(entity.getQuestionText())) {
                        entity.setQuestionText(keyManagementService.decrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionText()));
                    }
                }
            } catch (Exception ex){
                throw new BasicDataServiceException(ResponseCode.DATA_ENCRYPTION_ERROR, ex.getMessage());
            }
        }
        return answerList;
    }

	@Override
	@Transactional(readOnly=true)
	public List<IdentityQuestGroupEntity> getAllIdentityQuestionGroups() {
		return questionGroupDAO.findAll();
	}
}
