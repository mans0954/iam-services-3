package org.openiam.mq;

import org.openiam.mq.constants.queue.MqQueue;
import org.openiam.mq.constants.queue.OpenIAMQueue;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.openiam.idm.srvc.role.service.UpdateRoleAttributeByMetadataDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/07/16.
 */
@Component
public class RoleAttributeListener extends AbstractAttributeListener {
    @Autowired
    private UpdateRoleAttributeByMetadataDispatcher attributeByMetadataProcessor;

    public RoleAttributeListener(MqQueue queueToListen) {
        super(queueToListen);
    }


    public RoleAttributeListener() {
        this(OpenIAMQueue.RoleAttributeQueue);
    }

    @Override
    protected AbstractAPIDispatcher getProcessorTask() {
        return attributeByMetadataProcessor;
    }
}
