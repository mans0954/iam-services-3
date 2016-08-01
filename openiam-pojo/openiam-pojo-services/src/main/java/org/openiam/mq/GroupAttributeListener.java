package org.openiam.mq;

import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.openiam.idm.srvc.grp.service.UpdateGroupAttributeByMetadataDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/07/16.
 */
@Component
public class GroupAttributeListener extends AbstractAttributeListener {
    @Autowired
    private UpdateGroupAttributeByMetadataDispatcher attributeByMetadataProcessor;

    public GroupAttributeListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }


    public GroupAttributeListener() {
        this(OpenIAMQueue.GroupAttributeQueue);
    }

    @Override
    protected AbstractAPIDispatcher getProcessorTask() {
        return attributeByMetadataProcessor;
    }
}
