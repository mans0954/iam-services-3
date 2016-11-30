package org.openiam.mq;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.idm.ProvisionAPI;
import org.openiam.mq.constants.queue.idm.ProvisionQueue;
import org.openiam.mq.listener.AbstractListener;
import org.openiam.provision.service.ProvisionDataContainer;
import org.openiam.provision.service.ProvisionDispatcherTransactionHelper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 01/08/16.
 */
@Component
@RabbitListener(id="provisionMessageListener",
        queues = "#{ProvisionQueue.name}",
        containerFactory = "idmRabbitListenerContainerFactory")
public class ProvisionMessageListener extends AbstractListener<ProvisionAPI> {
    @Autowired
    protected ProvisionDispatcherTransactionHelper provisionTransactionHelper;

    @Autowired
    public ProvisionMessageListener(ProvisionQueue queue) {
        super(queue);
    }

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) ProvisionAPI api, ProvisionDataContainer request)  throws BasicDataServiceException {
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<ProvisionAPI, ProvisionDataContainer>(){
            @Override
            public Response doProcess(ProvisionAPI api, ProvisionDataContainer request) throws BasicDataServiceException {
                provisionTransactionHelper.process(request);
                return new Response(ResponseStatus.SUCCESS);
            }
        });
    }
}
