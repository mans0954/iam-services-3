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
package org.openiam.idm.srvc.pswd.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.id.UUIDGen;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.dozer.converter.IdentityQuestionDozerConverter;
import org.openiam.dozer.converter.UserIdentityAnswerDozerConverter;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("challengeResponse")
@WebService(endpointInterface = "org.openiam.idm.srvc.pswd.service.ChallengeResponseWebService", targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", portName = "ChallengeResponseWebServicePort", serviceName = "ChallengeResponseWebService")
public class ChallengeResponseWebServiceImpl implements
		ChallengeResponseWebService {

	@Autowired
	private ChallengeResponseService challengeResponseService;

	@Autowired
	private IdentityQuestionDozerConverter questionDozerConverter;

	@Autowired
	private UserIdentityAnswerDozerConverter answerDozerConverter;

	private static final Log log = LogFactory
			.getLog(ChallengeResponseWebServiceImpl.class);

	@Override
	public Integer getNumOfRequiredQuestions(String userId, String domainId) {
		return challengeResponseService.getNumOfRequiredQuestions(userId,
				domainId);
	}

	@Override
	public Integer count(final IdentityQuestionSearchBean searchBean) {
		return challengeResponseService.count(searchBean);
	}

	@Override
	public List<IdentityQuestion> findQuestionBeans(
			final IdentityQuestionSearchBean searchBean, final int from,
			final int size) {
		final List<IdentityQuestionEntity> resultList = challengeResponseService
				.findQuestionBeans(searchBean, from, size);
		return (resultList != null) ? questionDozerConverter.convertToDTOList(
				resultList, searchBean.isDeepCopy()) : null;
	}

	@Override
	public List<UserIdentityAnswer> findAnswerBeans(
			final IdentityAnswerSearchBean searchBean, final int from,
			final int size) {
		final List<UserIdentityAnswerEntity> resultList = challengeResponseService
				.findAnswerBeans(searchBean, from, size);
		return (resultList != null) ? answerDozerConverter.convertToDTOList(
				resultList, searchBean.isDeepCopy()) : null;
	}

	@Override
	public Response saveQuestion(final IdentityQuestion question) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (question == null) {
				throw new BasicDataServiceException(
						ResponseCode.OBJECT_NOT_FOUND);
			}

			if (StringUtils.isBlank(question.getIdentityQuestGrpId())) {
				throw new BasicDataServiceException(
						ResponseCode.NO_IDENTITY_QUESTION_GROUP);
			}
			if (StringUtils.isBlank(question.getQuestionText())) {
				throw new BasicDataServiceException(
						ResponseCode.NO_IDENTITY_QUESTION);
			}
			final IdentityQuestionSearchBean searchBean = new IdentityQuestionSearchBean();
			searchBean.setQuestionText(question.getQuestionText());
			final List<IdentityQuestionEntity> found = challengeResponseService.findQuestionBeans(searchBean, 0, 2);
			if (found.size() > 0) {
				if (StringUtils.isBlank(question.getId())) {
					throw new BasicDataServiceException(ResponseCode.IDENTICAL_QUESTIONS);
				}

				if (StringUtils.isNotBlank(question.getId())
						&& !question.getId().equals(
								found.get(0).getId())) {
					throw new BasicDataServiceException(ResponseCode.IDENTICAL_QUESTIONS);
				}
								
			}

			IdentityQuestionEntity entity = questionDozerConverter
					.convertToEntity(question, false);
			entity = challengeResponseService.saveQuestion(entity);
			response.setResponseValue(entity.getId());
		} catch (BasicDataServiceException e) {
			log.error("Can't save or update resource", e);
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't save or update resource", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response deleteQuestion(final String questionId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(questionId)) {
				throw new BasicDataServiceException(
						ResponseCode.OBJECT_NOT_FOUND);
			}

			challengeResponseService.deleteQuestion(questionId);
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't save or update resource", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public IdentityQuestion getQuestion(final String questionId) {
		final IdentityQuestionEntity question;
		question = challengeResponseService.getQuestion(questionId);
		return (question != null) ? questionDozerConverter.convertToDTO(
				question, false) : null;

	}

	@Override
	public Response saveAnswer(final UserIdentityAnswer answer) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (answer == null) {
				throw new BasicDataServiceException(
						ResponseCode.OBJECT_NOT_FOUND);
			}

			if (StringUtils.isNotBlank(answer.getQuestionId())) {
				throw new BasicDataServiceException(
						ResponseCode.NO_IDENTITY_QUESTION);
			}

			final UserIdentityAnswerEntity entity = answerDozerConverter
					.convertToEntity(answer, true);
			challengeResponseService.saveAnswer(entity);
			response.setResponseValue(entity.getId());
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't save or update resource", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	@Override
	public Response deleteAnswer(final String answerId) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (StringUtils.isBlank(answerId)) {
				throw new BasicDataServiceException(
						ResponseCode.OBJECT_NOT_FOUND);
			}

			challengeResponseService.deleteAnswer(answerId);
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't save or update resource", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	public Response saveAnswers(List<UserIdentityAnswer> answerList) {
		final Response response = new Response(ResponseStatus.SUCCESS);
		try {
			if (CollectionUtils.isEmpty(answerList)) {
				throw new BasicDataServiceException(
						ResponseCode.OBJECT_NOT_FOUND);
			}
			String requestId = "R" + UUIDGen.getUUID();

			/* check for duplicates */
			final Set<String> questionIdSet = new HashSet<String>();
			for (final UserIdentityAnswer answer : answerList) {
				if (questionIdSet.contains(answer.getQuestionId())) {
					throw new BasicDataServiceException(
							ResponseCode.IDENTICAL_QUESTIONS);
				}
				if(StringUtils.isBlank(answer.getQuestionId())){
					throw new BasicDataServiceException(
							ResponseCode.QUEST_NOT_SELECTED);
				}
				
				if(StringUtils.isBlank(answer.getQuestionAnswer())){
					throw new BasicDataServiceException(
							ResponseCode.ANSWER_NOT_TAKEN);
				}
				questionIdSet.add(answer.getQuestionId());
			}

			final List<UserIdentityAnswerEntity> answerEntityList = new LinkedList<UserIdentityAnswerEntity>();
			for (final UserIdentityAnswer answer : answerList) {
				
				final UserIdentityAnswerEntity entity = answerDozerConverter
						.convertToEntity(answer, true);
				answerEntityList.add(entity);
			}
			challengeResponseService.saveAnswers(answerEntityList);
		} catch (BasicDataServiceException e) {
			response.setErrorCode(e.getCode());
			response.setStatus(ResponseStatus.FAILURE);
		} catch (Throwable e) {
			log.error("Can't save or update resource", e);
			response.setErrorText(e.getMessage());
			response.setStatus(ResponseStatus.FAILURE);
		}
		return response;
	}

	public boolean isResponseValid(String domainId, String userId,
			List<UserIdentityAnswer> newAnswerList) {
		final List<UserIdentityAnswerEntity> entityList = answerDozerConverter
				.convertToEntityList(newAnswerList, true);
		return challengeResponseService.isResponseValid(domainId, userId,
				entityList);

	}
}
