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
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.searchbean.converter.IdentityAnswerSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.IdentityQuestionSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

/**
 * Default implementation of the challenge response validator. This implementation uses the information stored in the OpenIAM repository
 * @author suneet
 *
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
    
	private static final Log log = LogFactory.getLog(DefaultChallengeResponseValidator.class);
	
	@Override
	public boolean isResponseValid(String userId, List<UserIdentityAnswerEntity> newAnswerList, int requiredCorrectAns) {
		final int correctAns = getNumOfCorrectAnswers(userId, newAnswerList);
		if (correctAns >= requiredCorrectAns && requiredCorrectAns > 0) {
			return true;
		}
		return false;
	}
	
	@Override
	public Integer getNumOfRequiredQuestions(final String userId) {
		Policy passwordPolicy = null;
		if(StringUtils.isNotBlank(userId)) {
			final UserEntity user = userDAO.findById(userId);
			passwordPolicy = passwordService.getPasswordPolicyForUser(user);
		}
		if(passwordPolicy == null) {
            passwordPolicy = passwordService.getGlobalPasswordPolicy();
		}
		
		Integer count = null;
		if(passwordPolicy != null) {
			PolicyAttribute countAttr = passwordPolicy.getAttribute("QUEST_COUNT");
			try {
				count = Integer.valueOf(countAttr.getValue1());
			} catch(Throwable e) {
				log.warn("Cannot parse policy attribute value");
			}
		}
		return count;
	}
	
	@Override
	public boolean isUserAnsweredSecurityQuestions(final String userId) {
		final Integer numOfRequiredQuestions = getNumOfRequiredQuestions(userId);
		final List<UserIdentityAnswerEntity> answerList = answersByUser(userId);
		
		
		boolean retVal = false;
		if(numOfRequiredQuestions == null) {
			retVal = true;
		} else if(CollectionUtils.isNotEmpty(answerList)) {
			if(answerList.size() >= numOfRequiredQuestions.intValue()) {
				retVal = true;
			}
		}
		
		return retVal;
	}

	private int getNumOfCorrectAnswers(final String userId, final List<UserIdentityAnswerEntity> newAnswerList) {
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
			for (UserIdentityAnswerEntity newAns : newAnswerList) {
				if(StringUtils.equalsIgnoreCase(newAns.getId(), savedAns.getId())) {
					if(StringUtils.equalsIgnoreCase(newAns.getQuestionAnswer(), savedAns.getQuestionAnswer())) {
						correctAns++;
					}
				}
			}
		}
		return correctAns;
	}

	private List<UserIdentityAnswerEntity> answersByUser(String userId) {
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
		if(searchBean.getKey() != null) {
			final IdentityQuestionEntity entity = questionDAO.findById(searchBean.getKey());
			if(entity != null) {
				resultList = new LinkedList<IdentityQuestionEntity>();
				resultList.add(entity);
			}
		} else {
			resultList = questionDAO.getByExample(questionSearchBeanConverter.convert(searchBean), from, size);
		}
		return resultList;
	}

	@Override
	public Integer count(final IdentityQuestionSearchBean searchBean) {
		final IdentityQuestionEntity entity = questionSearchBeanConverter.convert(searchBean);
		return questionDAO.count(entity);
	}

	@Override
	public List<UserIdentityAnswerEntity> findAnswerBeans(final IdentityAnswerSearchBean searchBean, final int from, final int size) {
		List<UserIdentityAnswerEntity> resultList = null;
		if(searchBean.getKey() != null) {
			final UserIdentityAnswerEntity entity = answerDAO.findById(searchBean.getKey());
			if(entity != null) {
				resultList = new LinkedList<UserIdentityAnswerEntity>();
				resultList.add(entity);
			}
		} else {
			resultList = answerDAO.getByExample(answerSearchBeanConverter.convert(searchBean), from, size);
		}
		return resultList;
	}

	@Override
	@Transactional
	public void saveQuestion(final IdentityQuestionEntity entity) throws Exception {
		if(entity.getIdentityQuestGrp() != null && StringUtils.isNotBlank(entity.getIdentityQuestGrp().getId())) {
			entity.setIdentityQuestGrp(questionGroupDAO.findById(entity.getIdentityQuestGrp().getId()));
		}
		if(entity.getId() == null) {
			questionDAO.save(entity);
		} else {
			questionDAO.merge(entity);
		}
	}

	@Override
	@Transactional
	public void deleteQuestion(final String questionId) throws Exception {
		final IdentityQuestionEntity entity = questionDAO.findById(questionId);
		if(entity != null) {
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
		if(entity.getIdentityQuestion() != null && StringUtils.isNotBlank(entity.getIdentityQuestion().getId())) {
			entity.setIdentityQuestion(questionDAO.findById(entity.getIdentityQuestion().getId()));
		}
		
		if(StringUtils.isBlank(entity.getQuestionAnswer())) {
			throw new BasicDataServiceException(ResponseCode.NO_ANSWER_TO_QUESTION);
		}
		
		answerDAO.merge(entity);
	}

	@Override
	@Transactional
	public void deleteAnswer(final String answerId) throws Exception {
		final UserIdentityAnswerEntity entity = answerDAO.findById(answerId);
		if(entity != null) {
			answerDAO.delete(entity);
		}
	}

	@Override
	@Transactional
	public void saveAnswers(final List<UserIdentityAnswerEntity> answerList) throws Exception {
		if(answerList != null) {
			for(final UserIdentityAnswerEntity entity : answerList) {
				if(entity.getIdentityQuestion() != null && StringUtils.isNotBlank(entity.getIdentityQuestion().getId())) {
					entity.setIdentityQuestion(questionDAO.findById(entity.getIdentityQuestion().getId()));
				}
			}
			answerDAO.save(answerList);
		}
	}

	@Override
	@Transactional
	public void resetQuestionsForUser(String userId) {
		answerDAO.deleteByUser(userId);
	}
}
