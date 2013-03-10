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
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

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
import org.openiam.idm.srvc.auth.login.LoginDataService;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.dto.PolicyAttribute;
import org.openiam.idm.srvc.pswd.domain.IdentityQuestionEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;
import org.openiam.idm.srvc.pswd.dto.ChallengeResponseUser;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.searchbean.converter.IdentityAnswerSearchBeanConverter;
import org.openiam.idm.srvc.searchbean.converter.IdentityQuestionSearchBeanConverter;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.User;
import org.openiam.idm.srvc.user.service.UserDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("challengeResponse")
@WebService(endpointInterface = "org.openiam.idm.srvc.pswd.service.ChallengeResponseService", targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", portName = "ChallengeResponseWebServicePort", serviceName = "ChallengeResponseWebService")
public class ChallengeResponseServiceImpl implements ChallengeResponseService {

	@Autowired
    private IdentityQuestionDAO questionDAO;
    
    @Autowired
    private UserIdentityAnswerDAO answerDAO;
    
    @Autowired
    private LoginDataService loginManager;

    @Value("${challengeResponse.respValidatorObjName}")
    private String respValidatorObjName;
    
    @Value("${challengeResponse.respValidatorObjType}")
    private String respValidatorObjType;
    
    @Autowired
    private ValidatorFactory respValidatorFactory;

    @Autowired
    private UserDataService userMgr;
    
    @Autowired
    private AuditHelper auditHelper;
    
    @Autowired
    private PasswordService passwordMgr;
    
    @Autowired
    private IdentityAnswerSearchBeanConverter answerSearchBeanConverter;
    
    @Autowired
    private IdentityQuestionSearchBeanConverter questionSearchBeanConverter;
    
    @Autowired
    private IdentityQuestionDozerConverter questionDozerConverter;
    
    @Autowired
    private UserIdentityAnswerDozerConverter answerDozerConverter;

    private static final Log log = LogFactory
            .getLog(ChallengeResponseServiceImpl.class);

    
	@Override
	public List<IdentityQuestion> findQuestionBeans(final IdentityQuestionSearchBean searchBean, final int from, final int size) {
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
		
		return (resultList != null) ? questionDozerConverter.convertToDTOList(resultList, searchBean.isDeepCopy()) : null;
	}
	
	@Override
	public List<UserIdentityAnswer> findAnswerBeans(final IdentityAnswerSearchBean searchBean, final int from, final int size) {
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
		return (resultList != null) ? answerDozerConverter.convertToDTOList(resultList, searchBean.isDeepCopy()) : null;
	}

	@Override
	public Response saveQuestion(final IdentityQuestion question) {
		final Response response = new Response(ResponseStatus.SUCCESS);
    	try {
    		if(question == null) {
    			throw new BasicDataServiceException(ResponseCode.OBJECT_NOT_FOUND);
    		}
    		
    		final IdentityQuestionEntity entity = questionDozerConverter.convertToEntity(question, true);
    		if(StringUtils.isNotBlank(entity.getIdentityQuestionId())) {
    			questionDAO.save(entity);
    		} else {
    			questionDAO.update(entity);
    		}
    		
    		response.setResponseValue(entity.getIdentityQuestionId());
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
    		
    		final IdentityQuestionEntity entity = questionDAO.findById(questionId);
    		if(entity != null) {
    			questionDAO.delete(entity);
    		}
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
    		
    		final UserIdentityAnswerEntity entity = answerDozerConverter.convertToEntity(answer, true);
    		if(StringUtils.isNotBlank(entity.getIdentityAnsId())) {
    			answerDAO.save(entity);
    		} else {
    			answerDAO.update(entity);
    		}
    		
    		response.setResponseValue(entity.getIdentityAnsId());
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
    		
    		final UserIdentityAnswerEntity entity = answerDAO.findById(answerId);
    		if(entity != null) {
    			answerDAO.delete(entity);
    		}
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
		        
		        for(final UserIdentityAnswer answer : answerList) {
		        	saveAnswer(answer);
		        }
	
		        // add to audit log and update the user record that challenge response
		        // answers have been updated
		        // get the user Id
		        final String userId = answerList.get(0).getUserId();
		        final UserEntity usr = userMgr.getUser(userId);
		        usr.setDateChallengeRespChanged(new Date(System.currentTimeMillis()));
		        userMgr.updateUserWithDependent(usr, false);
	
		        auditHelper.addLog("SET CHALLENGE QUESTIONS", null, null,
		                "IDM SERVICE", userId, "PASSWORD", "CHALLENGE QUESTION", null,
		                null, "SUCCESS", null, null, null, requestId, null, null, null);
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

    public boolean isResponseValid(String domainId, String login,
            String managedSysId, String questGrpId,
            List<UserIdentityAnswer> newAnswerList) {

        int requiredCorrect = newAnswerList.size();

        // get the password policy to determine how many answers are required.
        Policy polcy = passwordMgr.getPasswordPolicy(domainId, login,
                managedSysId);
        PolicyAttribute attr = polcy.getAttribute("QUEST_ANSWER_CORRECT");

        if (attr != null) {
            if (attr.getValue1() != null && attr.getValue1().length() > 0) {
                requiredCorrect = Integer.parseInt(attr.getValue1());
            }
        }

        /*
         * Validate that there are no null responses
         */
        ChallengeResponseUser req = new ChallengeResponseUser();
        if (domainId != null) {
            req.setDomain(domainId);
        }
        if (managedSysId != null) {
            req.setManagedSysId(managedSysId);
        }
        if (login != null) {
            req.setPrincipal(login);
        }
        if (questGrpId != null) {
            req.setQuestionGroup(questGrpId);
        }

        final List<UserIdentityAnswerEntity> entityList = answerDozerConverter.convertToEntityList(newAnswerList, true);
        return getResponseValidator().isResponseValid(req, entityList, requiredCorrect);

    }
    
    private ChallengeResponseValidator getResponseValidator() {
    	final ChallengeResponseValidator responseValidator = respValidatorFactory
                .createValidator(respValidatorObjName, respValidatorObjType);
    	return responseValidator;
    }
}
