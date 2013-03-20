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

import java.util.Date;
import java.util.HashSet;
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
import org.openiam.base.ws.exception.BasicDataServiceException;
import org.openiam.dozer.converter.IdentityQuestionDozerConverter;
import org.openiam.dozer.converter.UserIdentityAnswerDozerConverter;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.audit.service.AuditHelper;
import org.openiam.idm.srvc.auth.login.LoginDAO;
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.policy.domain.PolicyAttributeEntity;
import org.openiam.idm.srvc.policy.domain.PolicyEntity;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.policy.service.PolicyDAO;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.ChallengeResponseUser;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.secdomain.domain.SecurityDomainEntity;
import org.openiam.idm.srvc.secdomain.service.SecurityDomainDAO;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.service.UserDAO;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("challengeResponse")
@WebService(endpointInterface = "org.openiam.idm.srvc.pswd.service.ChallengeResponseService", targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", portName = "ChallengeResponseWebServicePort", serviceName = "ChallengeResponseWebService")
public class ChallengeResponseServiceImpl implements ChallengeResponseService {
    
    @Autowired
    private LoginDataService loginManager;

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
    private LoginDAO loginDAO;
    
    @Autowired
    private AuditHelper auditHelper;
    
    @Autowired
    private PasswordService passwordMgr;
    
    @Autowired
    private PasswordService policyService;
    
    @Autowired
    private PolicyDAO policyDAO;
    
    @Autowired
    private SecurityDomainDAO securityDomainDAO;
    
    @Autowired
    private IdentityQuestionDozerConverter questionDozerConverter;
    
    @Autowired
    private UserIdentityAnswerDozerConverter answerDozerConverter;

    private static final Log log = LogFactory.getLog(ChallengeResponseServiceImpl.class);
    
    @Override
	public Integer getNumOfRequiredQuestions(String userId, String domainId) {
    	PolicyEntity passwordPolicy = null;
		if(StringUtils.isNotBlank(userId)) {
			final UserEntity user = userDAO.findById(userId);
			passwordPolicy = policyService.getPasswordPolicyForUser(domainId, user);
		}
		if(passwordPolicy == null) {
			final SecurityDomainEntity securityDomainEntity = securityDomainDAO.findById(domainId);
			if(securityDomainEntity != null) {
				passwordPolicy = policyDAO.findById(securityDomainEntity.getPasswordPolicyId());
			}
		}
		
		Integer count = null;
		if(passwordPolicy != null) {
			PolicyAttributeEntity countAttr = passwordPolicy.getAttribute("QUEST_COUNT");
			try {
				count = Integer.valueOf(countAttr.getValue1());
			} catch(Throwable e) {
				
			}
		}
		return count;
	}
    
	@Override
	public List<IdentityQuestion> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size) {
		final List<IdentityQuestionEntity> resultList = getResponseValidator().findQuestionBeans(searchBean, from, size);
		return (resultList != null) ? questionDozerConverter.convertToDTOList(resultList, searchBean.isDeepCopy()) : null;
	}
	
	@Override
	public List<UserIdentityAnswer> findAnswerBeans(final IdentityAnswerSearchBean searchBean, final int from, final int size) {
		final List<UserIdentityAnswerEntity> resultList = getResponseValidator().findAnswerBeans(searchBean, from, size);
		return (resultList != null) ? answerDozerConverter.convertToDTOList(resultList, searchBean.isDeepCopy()) : null;
	}

	@Override
	public Response saveQuestion(final IdentityQuestion question) {
		final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(question == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		if(StringUtils.isNotBlank(question.getIdentityQuestGrpId())) {
    			throw new BasicDataServiceException(ResponseCode.NO_IDENTITY_QUESTION_GROUP);
    		}
    		
    		final IdentityQuestionEntity entity = questionDozerConverter.convertToEntity(question, true);
    		getResponseValidator().saveQuestion(entity);
    		response.setResponseValue(entity.getId());
    	} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
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
    		if(StringUtils.isBlank(questionId)) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		getResponseValidator().deleteQuestion(questionId);
    	} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't save or update resource", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
    	return response;
	}

	@Override
	public Response saveAnswer(final UserIdentityAnswer answer) {
		final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(answer == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		if(StringUtils.isNotBlank(answer.getQuestionId())) {
    			throw new BasicDataServiceException(ResponseCode.NO_IDENTITY_QUESTION);
    		}
    		
    		final UserIdentityAnswerEntity entity = answerDozerConverter.convertToEntity(answer, true);
    		getResponseValidator().saveAnswer(entity);
    		response.setResponseValue(entity.getId());
    	} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
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
    		if(StringUtils.isBlank(answerId)) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		getResponseValidator().deleteAnswer(answerId);
    	} catch(BasicDataServiceException e) {
    		response.setErrorCode(e.getCode());
    		response.setStatus(ResponseStatus.FAILURE);
    	} catch(Throwable e) {
    		log.error("Can't save or update resource", e);
    		response.setErrorText(e.getMessage());
    		response.setStatus(ResponseStatus.FAILURE);
    	}
    	return response;
	}
	
	 public Response saveAnswers(List<UserIdentityAnswer> answerList) {
		 final Response response = new Response(ResponseStatus.SUCCESS);
		 try {
			 if(CollectionUtils.isEmpty(answerList)) {
				 throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
			 }
			 String requestId = "R" + UUIDGen.getUUID();
	        
			 /* check for duplicates */
			 final Set<String> questionIdSet = new HashSet<String>();
			 for(final UserIdentityAnswer answer : answerList) {
				 if(questionIdSet.contains(answer.getQuestionId())) {
					 throw new BasicDataServiceException(ResponseCode.IDENTICAL_QUESTIONS);
				 }
				 questionIdSet.add(answer.getQuestionId());
			 }
			 
			 for(final UserIdentityAnswer answer : answerList) {
				 final UserIdentityAnswerEntity entity = answerDozerConverter.convertToEntity(answer, true);
				 getResponseValidator().saveAnswer(entity);
			 }

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
		 } catch(BasicDataServiceException e) {
			 response.setErrorCode(e.getCode());
			 response.setStatus(ResponseStatus.FAILURE);
		 } catch(Throwable e) {
			 log.error("Can't save or update resource", e);
			 response.setErrorText(e.getMessage());
			 response.setStatus(ResponseStatus.FAILURE);
		 }
		 return response;
    }

    public boolean isResponseValid(String domainId, String userId, List<UserIdentityAnswer> newAnswerList) {

        int requiredCorrect = newAnswerList.size();

        // get the password policy to determine how many answers are required.
        final UserEntity user = userDAO.findById(userId);
        final PolicyEntity policy = passwordMgr.getPasswordPolicyForUser(domainId, user);
        final PolicyAttributeEntity attr = policy.getAttribute("QUEST_ANSWER_CORRECT");

        if (attr != null) {
            if (StringUtils.isNotBlank(attr.getValue1())) {
                requiredCorrect = Integer.parseInt(attr.getValue1());
            }
        }

        /*
         * Validate that there are no null responses
         */

        final List<UserIdentityAnswerEntity> entityList = answerDozerConverter.convertToEntityList(newAnswerList, true);
        return getResponseValidator().isResponseValid(userId, entityList, requiredCorrect);

    }
    
    private ChallengeResponseValidator getResponseValidator() {
    	return respValidatorFactory.createValidator(respValidatorObjName, respValidatorObjType);
    }
}
