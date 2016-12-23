package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.BooleanResponse;
import org.openiam.base.response.data.IdentityQuestionResponse;
import org.openiam.base.response.data.IntResponse;
import org.openiam.base.response.data.StringResponse;
import org.openiam.base.response.list.IdentityQuestGroupListResponse;
import org.openiam.base.response.list.IdentityQuestionListResponse;
import org.openiam.base.response.list.UserIdentityAnswerListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.IdentityAnswerSearchBean;
import org.openiam.idm.searchbeans.IdentityQuestionSearchBean;
import org.openiam.idm.srvc.pswd.dto.IdentityQuestion;
import org.openiam.idm.srvc.pswd.dto.UserIdentityAnswer;
import org.openiam.idm.srvc.pswd.service.ChallengeResponseService;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.user.ChallengeResponseAPI;
import org.openiam.mq.constants.queue.user.ChallengeResponseQueue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.openiam.mq.listener.AbstractListener;

/**
 * Created by Alexander Dukkardt on 2016-12-22.
 */
@Component
@RabbitListener(id = "ChallengeResponseQueueListener",
        queues = "#{ChallengeResponseQueue.name}",
        containerFactory = "userRabbitListenerContainerFactory")
public class ChallengeResponseQueueListener extends AbstractListener<ChallengeResponseAPI> {
    @Autowired
    private ChallengeResponseService challengeResponseService;
    @Autowired
    public ChallengeResponseQueueListener(ChallengeResponseQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<ChallengeResponseAPI, EmptyServiceRequest> getEmptyRequestProcessor() {
        return new RequestProcessor<ChallengeResponseAPI, EmptyServiceRequest>() {
            @Override
            public Response doProcess(ChallengeResponseAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                IdentityQuestGroupListResponse response;
                switch (api){
                    case ResetQuestionsForUser:
                        response=new IdentityQuestGroupListResponse();
                        response.setList(challengeResponseService.getAllIdentityQuestionGroupsDTO());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<ChallengeResponseAPI, BaseSearchServiceRequest> getSearchRequestProcessor() {
        return new RequestProcessor<ChallengeResponseAPI, BaseSearchServiceRequest>() {
            @Override
            public Response doProcess(ChallengeResponseAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case FindQuestionBeans:
                        response=new IdentityQuestionListResponse();
                        ((IdentityQuestionListResponse)response).setList(challengeResponseService.findQuestionBeans((IdentityQuestionSearchBean)request.getSearchBean(), request.getFrom(),
                                request.getSize(),request.getLanguage()));
                        break;
                    case FindAnswerBeans:
                        response=new UserIdentityAnswerListResponse();
                        ((UserIdentityAnswerListResponse)response).setList(challengeResponseService.findAnswerBeans((IdentityAnswerSearchBean)request.getSearchBean(), request.getRequesterId(), request.getFrom(),
                                request.getSize()));
                        break;
                    case Count:
                        response=new IntResponse();
                        ((IntResponse)response).setValue(challengeResponseService.count((IdentityQuestionSearchBean)request.getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<ChallengeResponseAPI, IdServiceRequest> getGetRequestProcessor() {
        return new RequestProcessor<ChallengeResponseAPI, IdServiceRequest>() {
            @Override
            public Response doProcess(ChallengeResponseAPI api, IdServiceRequest request) throws BasicDataServiceException {
                IdentityQuestionResponse response;
                switch (api){
                    case GetQuestion:
                        response=new IdentityQuestionResponse();
                        response.setValue(challengeResponseService.getQuestion(request.getId(), request.getLanguage()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @Override
    protected RequestProcessor<ChallengeResponseAPI, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<ChallengeResponseAPI, BaseCrudServiceRequest>() {
            @Override
            public Response doProcess(ChallengeResponseAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case SaveQuestion:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(challengeResponseService.saveQuestion((IdentityQuestion)request.getObject()));
                        break;
                    case DeleteQuestion:
                        response=new Response();
                        challengeResponseService.deleteQuestion(request.getObject().getId());
                        break;
                    case SaveAnswer:
                        response=new StringResponse();
                        ((StringResponse)response).setValue(challengeResponseService.saveAnswer((UserIdentityAnswer)request.getObject()));
                        break;
                    case DeleteAnswer:
                        response=new Response();
                        challengeResponseService.deleteAnswer(request.getObject().getId());
                        break;
                    case ValidateAnswers:
                        response=new Response();
                        challengeResponseService.validateAnswers(((UserIdentityAnswerListCrudRequest)request).getAnswerList());
                        break;
                    case SaveAnswers:
                        response=new Response();
                        challengeResponseService.saveAnswers(((UserIdentityAnswerListCrudRequest)request).getAnswerList());
                        break;
                    case isResponseValid:
                        response=new BooleanResponse();
                        ((BooleanResponse)response).setValue(challengeResponseService.isResponseValid(((UserIdentityAnswerListCrudRequest)request).getUserId(),
                                                        ((UserIdentityAnswerListCrudRequest)request).getAnswerList()));
                        break;
                    case ResetQuestionsForUser:
                        response=new Response();
                        challengeResponseService.resetQuestionsForUser(((UserIdentityAnswerListCrudRequest)request).getUserId());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ChallengeResponseAPI api, ChallengeResponseCountRequest request)  throws BasicDataServiceException{
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<ChallengeResponseAPI, ChallengeResponseCountRequest>() {
            @Override
            public Response doProcess(ChallengeResponseAPI api, ChallengeResponseCountRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case IsUserAnsweredSecurityQuestions:
                        response=new BooleanResponse();
                        ((BooleanResponse)response).setValue(challengeResponseService.isUserAnsweredSecurityQuestions(request.getUserId()));
                        break;
                    case GetNumOfRequiredQuestions:
                        response=new IntResponse();
                        ((IntResponse)response).setValue(challengeResponseService.getNumOfRequiredQuestions(request.getUserId(), request.isEnterprise()));
                        break;
                    case GetNumOfCorrectAnswers:
                        response=new IntResponse();
                        ((IntResponse)response).setValue(challengeResponseService.getNumOfCorrectAnswers(request.getUserId(), request.isEnterprise()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        });
    }
}
