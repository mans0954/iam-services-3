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
 *   GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenIAM.  If not, see <http://www.gnu.org/licenses/>. *
 */

/**
 *
 */
package org.openiam.idm.srvc.pswd.service;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;


/**
 * ChallengeResponseService provides operations to manage the challenge response functionality that is needed by the self service application
 *
 * @author Suneet Shah
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/pswd/service", name = "ChallengeResponseWebService")
public interface ChallengeResponseWebService {
    
	@WebMethod
	public Integer getNumOfRequiredQuestions(@WebParam(name = "userId", targetNamespace = "") String userId, @WebParam(name = "isEnterprise", targetNamespace = "")boolean isEnterprise);

    @WebMethod
	public Integer getNumOfCorrectAnswers(@WebParam(name = "userId", targetNamespace = "") String userId, @WebParam(name = "isEnterprise", targetNamespace = "") boolean isEnterprise);

	@WebMethod
	Integer count(@WebParam(name = "searchBean", targetNamespace = "") final IdentityQuestionSearchBean searchBean);

	@WebMethod
	List<IdentityQuestion> findQuestionBeans(@WebParam(name = "searchBean", targetNamespace = "") final IdentityQuestionSearchBean searchBean,
											 @WebParam(name = "from", targetNamespace = "") int from,
											 @WebParam(name = "size", targetNamespace = "") int size,
											 @WebParam(name = "lang", targetNamespace = "") final Language language);
	
	@WebMethod
	Response saveQuestion(@WebParam(name = "question", targetNamespace = "") final IdentityQuestion question);
	
	@WebMethod
	Response deleteQuestion(@WebParam(name = "questionId", targetNamespace = "") final String questionId);
	
	@WebMethod
	IdentityQuestion getQuestion(
			@WebParam(name = "questionId", targetNamespace = "") final String questionId,
			@WebParam(name = "lang", targetNamespace = "") final Language language);
	
	@WebMethod
	List<UserIdentityAnswer> findAnswerBeans(@WebParam(name = "searchBean", targetNamespace = "") final IdentityAnswerSearchBean searchBean,
											 @WebParam(name = "requesterId", targetNamespace = "") final String requesterId,
											 @WebParam(name = "from", targetNamespace = "") int from,
											 @WebParam(name = "size", targetNamespace = "") int size) throws Exception;
	
	@WebMethod
	Response resetQuestionsForUser(final @WebParam(name = "userId", targetNamespace = "") String userId);
	
	@WebMethod
	Response saveAnswer(@WebParam(name = "answer", targetNamespace = "") final UserIdentityAnswer answer);
	
	@WebMethod
	Response deleteAnswer(@WebParam(name = "answerId", targetNamespace = "") final String answerId);
	
	@WebMethod
	Response saveAnswers(@WebParam(name = "answerList", targetNamespace = "") final List<UserIdentityAnswer> answerList);

    @WebMethod
	Response validateAnswers(@WebParam(name = "answerList", targetNamespace = "") final List<UserIdentityAnswer> answerList);

    /**
     * Determines is the answers that are provided by the user are the same as those stored in
     * the system.
     *
     * @param login      - login of which we want to validate the questions.
     * @param answerList - List of QuestionValue objects
     * @return
     * @throws RemoteException
     */
	@WebMethod
    boolean isResponseValid(String userId, List<UserIdentityAnswer> answerList) throws Exception;
    
	@WebMethod
	boolean isUserAnsweredSecurityQuestions(final String userId) throws Exception;

}
