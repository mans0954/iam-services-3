package org.openiam.provision.service;

import java.util.List;

import org.openiam.mq.constants.api.idm.ProvisionAPI;
import org.openiam.mq.constants.queue.idm.ProvisionQueue;
import org.openiam.mq.utils.RabbitMQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("provQueueService")
public class ProvisionQueueService {

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private ProvisionQueue provisionQueue;

    public void enqueue(final ProvisionDataContainer data) {
        rabbitMQSender.send(provisionQueue, ProvisionAPI.UserProvisioning, data);
    }

    public void enqueue(final List<ProvisionDataContainer> dataList) {
        for (final ProvisionDataContainer data : dataList) {
            enqueue(data);
        }
    }
}
