package org.openiam.provision.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.base.AttributeOperationEnum;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.mngsys.service.ProvisionConnectorService;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.openiam.provision.type.ExtensibleAttribute;
import org.openiam.provision.type.ExtensibleObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("provDispatcher")
public class ProvisionDispatcher extends AbstractAPIDispatcher<ProvisionDataContainer, Response> {
    @Autowired
    protected ProvisionConnectorService connectorService;

    @Autowired
    protected ProvisionDispatcherTransactionHelper provisionTransactionHelper;

    public ProvisionDispatcher() {
        super(Response.class);
    }



    @Override
    protected Response processingApiRequest(final OpenIAMAPI openIAMAPI, final ProvisionDataContainer entity) throws BasicDataServiceException {
        provisionTransactionHelper.process(entity);
        return new Response(ResponseStatus.SUCCESS);
    }
}
