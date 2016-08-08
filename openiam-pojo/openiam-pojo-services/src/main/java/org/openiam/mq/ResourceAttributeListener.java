package org.openiam.mq;

import org.openiam.idm.srvc.res.service.UpdateResourceAttributeByMetadataDispatcher;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.openiam.idm.srvc.grp.service.UpdateGroupAttributeByMetadataDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/07/16.
 */
@Component
public class ResourceAttributeListener extends AbstractAttributeListener {
    @Autowired
    private UpdateResourceAttributeByMetadataDispatcher attributeByMetadataProcessor;

    public ResourceAttributeListener() {
        super(OpenIAMQueue.ResourceAttributeQueue);
    }

    @Override
    protected AbstractAPIDispatcher getProcessorTask() {
        return attributeByMetadataProcessor;
    }
}
