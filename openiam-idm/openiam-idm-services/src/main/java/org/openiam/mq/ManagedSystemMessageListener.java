package org.openiam.mq;

import org.openiam.base.request.EmptyServiceRequest;
import org.openiam.base.response.list.ManagedSysListResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.mq.constants.api.idm.ManagedSystemAPI;
import org.openiam.mq.constants.queue.idm.ManagedSysQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
@RabbitListener(id="managedSystemMessageListener",
        queues = "#{ManagedSysQueue.name}",
        containerFactory = "idmRabbitListenerContainerFactory")
public class ManagedSystemMessageListener extends AbstractListener<ManagedSystemAPI> {
    @Autowired
    private ManagedSystemService managedSystemService;

    @Autowired
    public ManagedSystemMessageListener(ManagedSysQueue queue) {
        super(queue);
    }

    protected RequestProcessor<ManagedSystemAPI, EmptyServiceRequest> getEmptyRequestProcessor(){
        return new RequestProcessor<ManagedSystemAPI, EmptyServiceRequest>(){
            @Override
            public Response doProcess(ManagedSystemAPI api, EmptyServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetAllManagedSys:
                        response = new ManagedSysListResponse();
                        ((ManagedSysListResponse)response).setList(managedSystemService.getAllManagedSysDTO());
                        break;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
                return response;
            }
        };
    }
}
