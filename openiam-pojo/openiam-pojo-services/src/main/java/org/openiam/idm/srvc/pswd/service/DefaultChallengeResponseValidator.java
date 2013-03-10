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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.BaseObject;
import org.openiam.exception.data.IdentityAnswerNotFoundException;
import org.openiam.exception.data.PrincipalNotFoundException;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestGroupEntity;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.ChallengeResponseUser;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Default implementation of the challenge response validator. This implementation uses the information stored in the OpenIAM repository
 * @author suneet
 *
 */
@Service("challengeResponseValidator")
public class DefaultChallengeResponseValidator implements ChallengeResponseValidator {

	@Autowired
	private IdentityQuestionDAO identityQuestDao;
	
	@Autowired
	private UserIdentityAnswerDAO identityAnswerDao;
	
	@Autowired
	private LoginDataService loginManager;
	
	private static final Log log = LogFactory.getLog(DefaultChallengeResponseValidator.class);
	
	@Override
	public boolean isResponseValid(ChallengeResponseUser req, List<UserIdentityAnswerEntity> newAnswerList, int requiredCorrectAns) {
		final int correctAns = getNumOfCorrectAnswers(req, newAnswerList);
		if (correctAns >= requiredCorrectAns) {
			return true;
		}
		return false;
		
	}

	@Override
	public boolean isResponseValid(final ChallengeResponseUser req, final List<UserIdentityAnswerEntity> newAnswerList) {
		final int correctAns = getNumOfCorrectAnswers(req, newAnswerList);
		return correctAns == newAnswerList.size();
	}
	
	private int getNumOfCorrectAnswers(final ChallengeResponseUser req, final List<UserIdentityAnswerEntity> newAnswerList) {
		int correctAns = 0;
		
		LoginEntity lg = loginManager.getLoginByManagedSys(req.getDomain(), req.getPrincipal(), req.getManagedSysId());
		
		if (lg == null) {
			throw new PrincipalNotFoundException("Login object not found for login=" + req.getPrincipal());
		}
		// get the answers in the system to validate the response.
		final List<UserIdentityAnswerEntity> savedAnsList = answersByUser(lg.getUserId());
		if (CollectionUtils.isEmpty(savedAnsList)) {
			throw new IdentityAnswerNotFoundException();
		}

		for (UserIdentityAnswerEntity savedAns : savedAnsList) {
			for (UserIdentityAnswerEntity newAns : newAnswerList) {
				if(StringUtils.equalsIgnoreCase(newAns.getIdentityAnsId(), savedAns.getIdentityAnsId())) {
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
		return this.identityAnswerDao.getByExample(example);
	}
}
