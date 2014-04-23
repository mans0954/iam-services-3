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

import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;

import java.util.List;

/**
 * Challenge response interface that allows extension of the challenge response model to be outside the OpenIAM repository
 *
 * @author suneet
 */
public interface ChallengeResponseValidator {
    
    public boolean isResponseValid(String userId, List<UserIdentityAnswerEntity> newAnswerList, int requiredCorrectAns);
    public Integer count(final IdentityQuestionSearchBean searchBean);
    public List<IdentityQuestionEntity> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size);
    public List<UserIdentityAnswerEntity> findAnswerBeans(final IdentityAnswerSearchBean searchBean, final int from, final int size);
    public void saveQuestion(final IdentityQuestionEntity entity) throws Exception;
    public void deleteQuestion(final String questionId) throws Exception;
    public IdentityQuestionEntity getQuestion(final String questionId);
    public void saveAnswer(final UserIdentityAnswerEntity answer) throws Exception;
    public void deleteAnswer(final String answerId) throws Exception;
    public void saveAnswers(List<UserIdentityAnswerEntity> answerList) throws Exception;
    public boolean isUserAnsweredSecurityQuestions(final String userId);
    public Integer getNumOfRequiredQuestions(final String userId);
    public void resetQuestionsForUser(final String userId);
}
