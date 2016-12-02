package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.data.*;
import org.openiam.base.response.list.PolicyDefParamListResponse;
import org.openiam.base.response.list.PolicyListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.PolicySearchBean;
import org.openiam.idm.srvc.policy.dto.ITPolicy;
import org.openiam.idm.srvc.policy.dto.Policy;
import org.openiam.idm.srvc.policy.service.PolicyService;
import org.openiam.mq.constants.api.PolicyAPI;
import org.openiam.mq.constants.queue.common.PolicyQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
@RabbitListener(id="policyListener",
        queues = "#{PolicyQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
public class PolicyListener extends AbstractListener<PolicyAPI> {
    @Autowired
    protected PolicyService policyService;
    @Autowired
    public PolicyListener(PolicyQueue queue) {
        super(queue);
    }

    protected RequestProcessor<PolicyAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return new RequestProcessor<PolicyAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(PolicyAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api) {
                    case FindITPolicy:
                        response = new ITPolicyResponse();
                        ((ITPolicyResponse)response).setValue(policyService.findITPolicy());
                        break;
                    case ResetITPolicy:
                        policyService.resetITPolicy();
                        response = new Response(ResponseStatus.SUCCESS);
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<PolicyAPI, BaseSearchServiceRequest> getSearchRequestProcessor(){
        return new RequestProcessor<PolicyAPI, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(PolicyAPI api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                BaseSearchServiceRequest<PolicySearchBean> req = (BaseSearchServiceRequest<PolicySearchBean>)request;
                Response response;
                switch (api) {
                    case FindBeans:
                        response = new PolicyListResponse();
                        ((PolicyListResponse)response).setList(policyService.findBeans(req.getSearchBean(), req.getFrom(), req.getSize()));
                        break;
                    case Count:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(policyService.count(req.getSearchBean()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<PolicyAPI, IdServiceRequest> getGetRequestProcessor(){
        return new RequestProcessor<PolicyAPI, IdServiceRequest>(){
            @Override
            public Response doProcess(PolicyAPI api, IdServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api) {
                    case GetPolicy:
                        response = new PolicyResponse();
                        ((PolicyResponse)response).setValue(policyService.getPolicy(request.getId()));
                        break;
                    case GetAllPolicyAttributes:
                        response = new PolicyDefParamListResponse();
                        ((PolicyDefParamListResponse)response).setList(policyService.findPolicyDefParamByGroup(request.getId(), ((PolicyGetAppPolicyAttrubutesRequest)request).getPswdGroup()));
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
    protected RequestProcessor<PolicyAPI, BaseCrudServiceRequest> getCrudRequestProcessor(){
        return new RequestProcessor<PolicyAPI, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(PolicyAPI api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api) {
                    case SaveOrUpdateITPolicy:
                        response = new BooleanResponse();
                        policyService.saveITPolicy(((BaseCrudServiceRequest<ITPolicy>)request).getObject());
                        ((BooleanResponse)response).setValue(Boolean.TRUE);
                        break;
                    case SavePolicy:
                        response = new StringResponse();
                        ((StringResponse)response).setValue(policyService.savePolicy(((BaseCrudServiceRequest<Policy>)request).getObject()));
                        break;
                    case DeletePolicy:
                        response = new BooleanResponse();
                        policyService.deletePolicy(((BaseCrudServiceRequest<Policy>)request).getObject().getId());
                        ((BooleanResponse)response).setValue(Boolean.TRUE);
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
}
