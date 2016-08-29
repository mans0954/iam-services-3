package org.openiam.provision.service;

import java.util.List;

import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.mq.dto.MQRequest;
import org.openiam.mq.gateway.RequestServiceGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("provQueueService")
public class ProvisionQueueService {

    @Autowired
    private RequestServiceGateway  requestServiceGateway;

    public void enqueue(final ProvisionDataContainer data) {
        MQRequest<ProvisionDataContainer, OpenIAMAPICommon> request = new MQRequest();
        request.setRequestBody(data);
        request.setRequestApi(OpenIAMAPICommon.UserProvisioning);
        requestServiceGateway.send(OpenIAMQueue.ProvisionQueue, request);
    }

    public void enqueue(final List<ProvisionDataContainer> dataList) {
        for (final ProvisionDataContainer data : dataList) {
            enqueue(data);
        }
    }
}
