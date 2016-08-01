package org.openiam.mq;

import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.idm.srvc.user.service.UpdateUserAttributeByMetadataDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/07/16.
 */
@Component
public class UserAttributeListener extends AbstractAttributeListener {
    @Autowired
    private UpdateUserAttributeByMetadataDispatcher attributeByMetadataProcessor;

    public UserAttributeListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }


    public UserAttributeListener() {
        this(OpenIAMQueue.UserAttributeQueue);
    }

    @Override
    protected AbstractAPIDispatcher getProcessorTask() {
        return attributeByMetadataProcessor;
    }
}
