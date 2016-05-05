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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.exception.data.IdentityAnswerNotFoundException;
import org.openiam.exception.data.PrincipalNotFoundException;
import org.openiam.idm.searchbeans.AuditLogSearchBean;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.audit.constant.AuditAction;
import org.openiam.idm.srvc.audit.dto.IdmAuditLog;
import org.openiam.idm.srvc.audit.service.AuditLogService;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.key.constant.KeyName;
import org.openiam.idm.srvc.key.service.KeyManagementService;
import org.openiam.idm.srvc.policy.dto.PasswordPolicyAssocSearchBean;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.searchbean.converter.IdentityAnswerSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.IdentityQuestionSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of the challenge response validator. This implementation uses the information stored in the OpenIAM repository
 *
 * @author suneet
 */
@Service("challengeResponseValidator")
public class DefaultChallengeResponseValidator implements ChallengeResponseValidator {

    private static Logger LOG = Logger.getLogger(DefaultChallengeResponseValidator.class);

    @Autowired
    private LoginDataService loginManager;

    @Autowired
    private IdentityQuestionDAO questionDAO;

    @Autowired
    private UserIdentityAnswerDAO answerDAO;

    @Autowired
    private IdentityQuestGroupDAO questionGroupDAO;

    @Autowired
    private IdentityAnswerSearchBeanConverter answerSearchBeanConverter;

    @Autowired
    private IdentityQuestionSearchBeanConverter questionSearchBeanConverter;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordService passwordService;
    @Autowired
    private KeyManagementService keyManagementService;

    @Autowired
    protected AuditLogService auditLogService;

    @Autowired
    protected UserDataService userMgr;

    private static final Log log = LogFactory.getLog(DefaultChallengeResponseValidator.class);

    private static final Integer maxLengthAnswer = 255;

    @Override
    public boolean isResponseValid(String userId, List<UserIdentityAnswerEntity> newAnswerList, int requiredCorrectAns, boolean isEnterprise)
            throws Exception {
        final int correctAns = getNumOfCorrectAnswers(userId, newAnswerList, isEnterprise);
        log.debug("DefaultChallengeResponseValidator:isEnterprise" + isEnterprise);
        log.debug("DefaultChallengeResponseValidator:requiredCorrectAns=" + requiredCorrectAns);
        log.debug("DefaultChallengeResponseValidator:correctAns=" + correctAns);

        if (correctAns >= requiredCorrectAns) {
            return true;
        }
        return false;
    }

    @Override
    public Integer getNumOfRequiredQuestions(final String userId, boolean isEnterprise) {
        Policy passwordPolicy = null;
        if (StringUtils.isNotBlank(userId)) {
            PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
            searchBean.setUserId(userId);
            passwordPolicy = passwordService.getPasswordPolicyForUser(searchBean);
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

    @Override
    public Integer getNumOfCorrectAnswers(final String userId, boolean isEnterprise) {
        Policy passwordPolicy = null;
        if (StringUtils.isNotBlank(userId)) {
            PasswordPolicyAssocSearchBean searchBean = new PasswordPolicyAssocSearchBean();
            searchBean.setUserId(userId);
            passwordPolicy = passwordService.getPasswordPolicyForUser(searchBean);
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
    public boolean isUserAnsweredSecurityQuestions(final String userId) throws Exception {
        return this.isUserAnsweredSecurityQuestions(userId, true) && this.isUserAnsweredSecurityQuestions(userId, false);
    }

    @Override
    public boolean isUserAnsweredSecurityQuestions(final String userId, boolean isEnterprise) throws Exception {
        final Integer numOfRequiredQuestions = getNumOfRequiredQuestions(userId, isEnterprise);
        final List<UserIdentityAnswerEntity> answerList = answersByUser(userId);
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

    private int getNumOfCorrectAnswers(final String userId, final List<UserIdentityAnswerEntity> newAnswerList, boolean isEnterprise)
            throws Exception {
        int correctAns = 0;

        LoginEntity lg = loginManager.getPrimaryIdentity(userId);

        if (lg == null) {
            throw new PrincipalNotFoundException(String.format("Login object not found for userId=%s", userId));
        }
        // get the answers in the system to validate the response.
        final List<UserIdentityAnswerEntity> savedAnsList = answersByUser(lg.getUserId());
        if (CollectionUtils.isEmpty(savedAnsList)) {
            throw new IdentityAnswerNotFoundException();
        }

        for (UserIdentityAnswerEntity savedAns : savedAnsList) {
            if ((isEnterprise && savedAns.getIdentityQuestion() == null) || (!isEnterprise && savedAns.getIdentityQuestion() != null)) {
                continue;
            }
            for (UserIdentityAnswerEntity newAns : newAnswerList) {
                if (StringUtils.equalsIgnoreCase(newAns.getId(), savedAns.getId())) {
                    String savedAnswer = (savedAns.getIsEncrypted()) ? keyManagementService.decrypt(lg.getUserId(), KeyName.challengeResponse, savedAns.getQuestionAnswer())
                            : savedAns.getQuestionAnswer();

                    if (StringUtils.equalsIgnoreCase(newAns.getQuestionAnswer(), savedAnswer)) {
                        correctAns++;
                    }
                }
            }
        }
        return correctAns;
    }

    private List<UserIdentityAnswerEntity> answersByUser(String userId) throws Exception {
        if (userId == null) {
            throw new NullPointerException("UserId is null");
        }

        final UserIdentityAnswerEntity example = new UserIdentityAnswerEntity();
        example.setUserId(userId);
        return answerDAO.getByExample(example);
    }

    @Override
    public List<IdentityQuestionEntity> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size) {
        List<IdentityQuestionEntity> resultList = null;
        if (searchBean.getKey() != null) {
            final IdentityQuestionEntity entity = questionDAO.findById(searchBean.getKey());
            if (entity != null) {
                resultList = new LinkedList<IdentityQuestionEntity>();
                resultList.add(entity);
            }
        } else {
            resultList = questionDAO.getByExample(searchBean, from, size);
        }
        return resultList;
    }

    @Override
    public Integer count(final IdentityQuestionSearchBean searchBean) {
        final IdentityQuestionEntity entity = questionSearchBeanConverter.convert(searchBean);
        return questionDAO.count(entity);
    }

    @Override
    public List<UserIdentityAnswerEntity> findAnswerBeans(final IdentityAnswerSearchBean searchBean, String requesterId, final int from, final int size)
            throws Exception {
        List<UserIdentityAnswerEntity> resultList = null;
        if (searchBean.getKey() != null) {
            final UserIdentityAnswerEntity entity = answerDAO.findById(searchBean.getKey());
            if (entity != null) {
                resultList = new LinkedList<UserIdentityAnswerEntity>();
                resultList.add(entity);
            }
        } else {
            resultList = answerDAO.getByExample(answerSearchBeanConverter.convert(searchBean), from, size);
        }
        return decryptAnswers(resultList, requesterId);
    }

    @Override
    @Transactional
    public void saveQuestion(final IdentityQuestionEntity entity) throws Exception {
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
    public void deleteQuestion(final String questionId) throws Exception {
        final IdentityQuestionEntity entity = questionDAO.findById(questionId);
        if (entity != null) {
            answerDAO.deleteAnswersByQuestionId(entity.getId());
            questionDAO.delete(entity);
        }
    }

    @Override
    @Transactional
    public IdentityQuestionEntity getQuestion(final String questionId) {
        final IdentityQuestionEntity entity = questionDAO.findById(questionId);
        return entity;
    }

    @Override
    @Transactional
    public void saveAnswer(final UserIdentityAnswerEntity entity) throws Exception {
        if (entity.getIdentityQuestion() != null && StringUtils.isNotBlank(entity.getIdentityQuestion().getId())) {
            entity.setIdentityQuestion(questionDAO.findById(entity.getIdentityQuestion().getId()));
        }

        if (StringUtils.isBlank(entity.getQuestionAnswer())) {
            throw new BasicDataServiceException(ResponseCode.NO_ANSWER_TO_QUESTION);
        }

        entity.setQuestionAnswer(keyManagementService.encrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionAnswer()));
        entity.setIsEncrypted(true);

        answerDAO.merge(entity);
    }

    @Override
    @Transactional
    public void deleteAnswer(final String answerId) throws Exception {
        final UserIdentityAnswerEntity entity = answerDAO.findById(answerId);
        if (entity != null) {
            answerDAO.delete(entity);
        }
    }

    @Override
    @Transactional
    public void saveAnswers(final List<UserIdentityAnswerEntity> answerList) throws Exception {
        if (answerList != null) {

            final IdmAuditLog auditLog = new IdmAuditLog();
            auditLog.succeed();
            auditLog.setUserId(answerList.get(0).getUserId());

            AuditLogSearchBean logSearchBean = new AuditLogSearchBean();
            logSearchBean.setUserId(answerList.get(0).getUserId());
            logSearchBean.setAction(AuditAction.SAVE_CHALLENGE_ANSWERS.value());

            Integer countLogs = auditLogService.count(logSearchBean);
            if (countLogs >= 1) {
                auditLog.setAction(AuditAction.CHANGE_CHALLENGE_ANSWERS.value());
            } else {
                auditLog.setAction(AuditAction.SAVE_CHALLENGE_ANSWERS.value());
            }

            LoginEntity loginEntity = loginManager.getPrimaryIdentity(answerList.get(0).getUserId());
            auditLog.setPrincipal(loginEntity.getLogin());

            for (final UserIdentityAnswerEntity entity : answerList) {

                if (validateAnswerLength(entity.getQuestionAnswer())) {

                    if (entity.getIdentityQuestion() != null && StringUtils.isNotBlank(entity.getIdentityQuestion().getId())) {
                        entity.setIdentityQuestion(questionDAO.findById(entity.getIdentityQuestion().getId()));
                    }
                    entity.setQuestionAnswer(keyManagementService.encrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionAnswer()));
                    //enncrypt Custom question
                    if (entity.getIdentityQuestion() == null && StringUtils.isNotBlank(entity.getQuestionText())) {
                        entity.setQuestionText(keyManagementService.encrypt(entity.getUserId(), KeyName.challengeResponse, entity.getQuestionText()));
                    }
                    entity.setIsEncrypted(true);
                } else {
                    auditLog.fail();
                    auditLog.setFailureReason(ResponseCode.ANSWER_IS_TOO_LONG);
                    throw new BasicDataServiceException(ResponseCode.ANSWER_IS_TOO_LONG);
                }
            }
            answerDAO.save(answerList);
            auditLogService.save(auditLog);

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
            throws Exception {
        if (CollectionUtils.isNotEmpty(answerList)) {
            for (UserIdentityAnswerEntity entity : answerList) {
                if (StringUtils.isNotBlank(requesterId)
                        && requesterId.equals(entity.getUserId())
                        && entity.getIsEncrypted()) {

                    entity.setQuestionAnswer(keyManagementService.decrypt(entity.getUserId(), KeyName.challengeResponse,
                            entity.getQuestionAnswer()));

                }
                //enncrypt Custom question
                if ((entity.getIdentityQuestion() == null || entity.getIdentityQuestion().getId() == null) && StringUtils.isNotBlank(entity.getQuestionText())) {
                    entity.setQuestionText(keyManagementService.decrypt(entity.getUserId(), KeyName.challengeResponse,
                            entity.getQuestionText()));
                }
            }
        }
        return answerList;
    }
}
