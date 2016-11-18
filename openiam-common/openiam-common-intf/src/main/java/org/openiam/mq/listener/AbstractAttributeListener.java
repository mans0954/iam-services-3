package org.openiam.mq.listener;

import org.openiam.base.request.UpdateAttributeByMetadataRequest;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.MQConstant;
import org.openiam.mq.constants.api.OpenIAMAPICommon;
import org.openiam.mq.constants.queue.MqQueue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Created by alexander on 01/08/16.
 */
public abstract class AbstractAttributeListener extends AbstractListener<OpenIAMAPICommon> {
    public AbstractAttributeListener(MqQueue queue) {
        super(queue);
    }

    @Autowired
    @Qualifier("transactionManager")
    private PlatformTransactionManager platformTransactionManager;

    @RabbitHandler
    public Response processingApiRequest(@Header(MQConstant.API_NAME) OpenIAMAPICommon api, UpdateAttributeByMetadataRequest request)  throws BasicDataServiceException {
        log.debug("Got message in Listener:  {} API. Message:  {}", api, request);
        return  this.processRequest(api, request, new RequestProcessor<OpenIAMAPICommon, UpdateAttributeByMetadataRequest>(){
            @Override
            public Response doProcess(OpenIAMAPICommon api, UpdateAttributeByMetadataRequest request) throws BasicDataServiceException {
                if(request.isRequired()) {
                    final TransactionTemplate transactionTemplate = new TransactionTemplate(platformTransactionManager);
                    transactionTemplate.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
                    transactionTemplate.execute(new TransactionCallback<Void>() {
                        @Override
                        public Void doInTransaction(TransactionStatus status) {
                            process(request);
                            return null;
                        }
                    });
                }
                return new Response(ResponseStatus.SUCCESS);
            }
        });
    }
    protected abstract void process(UpdateAttributeByMetadataRequest request);
}
