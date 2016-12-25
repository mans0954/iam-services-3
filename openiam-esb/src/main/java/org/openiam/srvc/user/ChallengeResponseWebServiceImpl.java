/*
 * Copyright 2009, OpenIAM LLC This file is part of the OpenIAM Identity and
 * Access Management Suite
 * 
 * OpenIAM Identity and Access Management Suite is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License version 3 as published by the Free Software Foundation.
 * 
 * OpenIAM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the Lesser GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenIAM. If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 * 
 */
package org.openiam.srvc.user;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.ChallengeResponseCountRequest;
import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.request.UserIdentityAnswerListCrudRequest;
import org.openiam.base.response.data.IdentityQuestionResponse;
import org.openiam.base.response.list.IdentityQuestGroupListResponse;
import org.openiam.base.response.list.IdentityQuestionListResponse;
import org.openiam.base.response.list.UserIdentityAnswerListResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestGroup;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.mq.constants.api.user.ChallengeResponseAPI;
import org.openiam.mq.constants.queue.user.ChallengeResponseQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("challengeResponse")
@WebService(endpointInterface = "org.openiam.srvc.user.ChallengeResponseWebService", targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", portName = "ChallengeResponseWebServicePort", serviceName = "ChallengeResponseWebService")
public class ChallengeResponseWebServiceImpl extends AbstractApiService implements ChallengeResponseWebService {

	private static final Log log = LogFactory.getLog(ChallengeResponseWebServiceImpl.class);

	@Autowired
	public ChallengeResponseWebServiceImpl(ChallengeResponseQueue queue) {
		super(queue);
	}

	@Override
	public Integer getNumOfRequiredQuestions(String userId, boolean isEnterprise) {
		ChallengeResponseCountRequest request = new ChallengeResponseCountRequest();
		request.setUserId(userId);
		request.setEnterprise(isEnterprise);
		return this.getIntValue(ChallengeResponseAPI.GetNumOfRequiredQuestions, request);
	}

	@Override
	public Integer getNumOfCorrectAnswers(String userId, boolean isEnterprise) {
		ChallengeResponseCountRequest request = new ChallengeResponseCountRequest();
		request.setUserId(userId);
		request.setEnterprise(isEnterprise);
		return this.getIntValue(ChallengeResponseAPI.GetNumOfCorrectAnswers, request);
	}

	@Override
	public Integer count(final IdentityQuestionSearchBean searchBean) {
		return this.getIntValue(ChallengeResponseAPI.Count, new BaseSearchServiceRequest<>(searchBean));
	}
	
	@Override
	public IdentityQuestion getQuestion(final String questionId) {
		IdServiceRequest request = new IdServiceRequest();
		request.setId(questionId);
		return this.getValue(ChallengeResponseAPI.GetQuestion, request, IdentityQuestionResponse.class);
	}

	@Override
	public List<IdentityQuestion> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size) {
		return this.getValueList(ChallengeResponseAPI.FindQuestionBeans, new BaseSearchServiceRequest<>(searchBean, from, size), IdentityQuestionListResponse.class);
	}

	@Override
	public List<UserIdentityAnswer> findAnswerBeans(final IdentityAnswerSearchBean searchBean, final  String requesterId, final int from, final int size){
		BaseSearchServiceRequest request =new BaseSearchServiceRequest<>(searchBean, from, size);
		request.setRequesterId(requesterId);
		return this.getValueList(ChallengeResponseAPI.FindAnswerBeans, new BaseSearchServiceRequest<>(searchBean, from, size), UserIdentityAnswerListResponse.class);
	}

	@Override
	public Response saveQuestion(final IdentityQuestion question) {
		return this.manageCrudApiRequest(ChallengeResponseAPI.SaveQuestion, question);
	}

	@Override
	public Response deleteQuestion(final String questionId) {
		IdentityQuestion dto = new IdentityQuestion();
		dto.setId(questionId);
		return this.manageCrudApiRequest(ChallengeResponseAPI.DeleteQuestion, dto);
	}

	@Override
	public Response saveAnswer(final UserIdentityAnswer answer) {
		return this.manageCrudApiRequest(ChallengeResponseAPI.SaveAnswer, answer);
    }

	@Override
	public Response deleteAnswer(final String answerId) {
		UserIdentityAnswer dto = new UserIdentityAnswer();
		dto.setId(answerId);
		return this.manageCrudApiRequest(ChallengeResponseAPI.DeleteAnswer, dto);
	}
    @Override
    public Response validateAnswers(List<UserIdentityAnswer> answerList) {
		UserIdentityAnswerListCrudRequest request = new UserIdentityAnswerListCrudRequest();
		request.setAnswerList(answerList);
		return this.manageCrudApiRequest(ChallengeResponseAPI.ValidateAnswers, request);

    }
    @Override
	public Response saveAnswers(List<UserIdentityAnswer> answerList) {
		UserIdentityAnswerListCrudRequest request = new UserIdentityAnswerListCrudRequest();
		request.setAnswerList(answerList);
		return this.manageCrudApiRequest(ChallengeResponseAPI.SaveAnswers, request);
	}
    @Override
	public boolean isResponseValid(String userId, List<UserIdentityAnswer> newAnswerList) throws Exception {
		UserIdentityAnswerListCrudRequest request = new UserIdentityAnswerListCrudRequest();
		request.setAnswerList(newAnswerList);
		request.setUserId(userId);
		return this.getBooleanValue(ChallengeResponseAPI.isResponseValid, request);
	}
	@Override
	public boolean isUserAnsweredSecurityQuestions(final String userId) throws Exception {
		ChallengeResponseCountRequest request = new ChallengeResponseCountRequest();
		request.setUserId(userId);
		return this.getBooleanValue(ChallengeResponseAPI.IsUserAnsweredSecurityQuestions, request);
	}

	@Override
	public Response resetQuestionsForUser(String userId) {
		UserIdentityAnswerListCrudRequest request = new UserIdentityAnswerListCrudRequest();
		request.setUserId(userId);
		return this.manageCrudApiRequest(ChallengeResponseAPI.ResetQuestionsForUser, request);
	}

	@Override
	public List<IdentityQuestGroup> getAllIdentityQuestionGroups() {
		return this.getValueList(ChallengeResponseAPI.ResetQuestionsForUser, new EmptyServiceRequest(), IdentityQuestGroupListResponse.class);
	}
}
