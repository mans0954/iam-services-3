package org.openiam.am.srvc.mq;

import org.openiam.am.srvc.service.dispatcher.AccessReviewDispatcher;
import org.openiam.am.srvc.service.dispatcher.CachedContentProviderDispatcher;
import org.openiam.am.srvc.service.dispatcher.CachedURIPatternDispatcher;
import org.openiam.am.srvc.service.dispatcher.URIFederationMetadataDispatcher;
import org.openiam.base.request.BaseServiceRequest;
import org.openiam.mq.constants.AccessReviewAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.exception.RejectMessageException;
import org.openiam.mq.listener.AbstractRabbitMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 10/08/16.
 */
@Component
public class AccessReviewListener extends AbstractRabbitMQListener<AccessReviewAPI> {
    @Autowired
    private AccessReviewDispatcher accessReviewDispatcher;


    public AccessReviewListener() {
        super(OpenIAMQueue.AccessReviewQueue);
    }

    @Override
    protected void doOnMessage(MQRequest<BaseServiceRequest, AccessReviewAPI> message, byte[] correlationId, boolean isAsync) throws RejectMessageException, CloneNotSupportedException {
        addTask(accessReviewDispatcher, correlationId, message, message.getRequestApi(), isAsync);
    }
}
