package org.openiam.provision.service;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.mq.constants.api.OpenIAMAPICommon;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("provDispatcher")
public class ProvisionDispatcher extends AbstractAPIDispatcher<ProvisionDataContainer, Response, OpenIAMAPICommon> {
    @Autowired
    protected ProvisionConnectorService connectorService;

    @Autowired
    protected ProvisionDispatcherTransactionHelper provisionTransactionHelper;

    public ProvisionDispatcher() {
        super(Response.class);
    }



    @Override
    protected Response processingApiRequest(final OpenIAMAPICommon openIAMAPI, final ProvisionDataContainer entity) throws BasicDataServiceException {
        provisionTransactionHelper.process(entity);
        return new Response(ResponseStatus.SUCCESS);
    }
}
