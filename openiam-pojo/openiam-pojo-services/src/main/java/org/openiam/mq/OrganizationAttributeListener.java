package org.openiam.mq;

import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.openiam.idm.srvc.org.service.UpdateOrgAttributeByMetadataDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 12/07/16.
 */
@Component
public class OrganizationAttributeListener extends AbstractAttributeListener {
    @Autowired
    private UpdateOrgAttributeByMetadataDispatcher attributeByMetadataProcessor;

    public OrganizationAttributeListener(OpenIAMQueue queueToListen) {
        super(queueToListen);
    }


    public OrganizationAttributeListener() {
        this(OpenIAMQueue.OrganizationAttributeQueue);
    }


    @Override
    protected AbstractAPIDispatcher getProcessorTask() {
        return attributeByMetadataProcessor;
    }
}
