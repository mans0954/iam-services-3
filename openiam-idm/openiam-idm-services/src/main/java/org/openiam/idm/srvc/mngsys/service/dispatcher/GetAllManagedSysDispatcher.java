package org.openiam.idm.srvc.mngsys.service.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.response.ManagedSysListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.mngsys.service.ManagedSystemService;
import org.openiam.mq.constants.api.ManagedSystemAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 06/09/16.
 */
@Component
public class GetAllManagedSysDispatcher extends AbstractAPIDispatcher<BaseServiceRequest, ManagedSysListResponse, ManagedSystemAPI> {
    @Autowired
    private ManagedSystemService managedSystemService;

    public GetAllManagedSysDispatcher() {
        super(ManagedSysListResponse.class);
    }

    @Override
    protected ManagedSysListResponse processingApiRequest(ManagedSystemAPI openIAMAPI, BaseServiceRequest baseServiceRequest) throws BasicDataServiceException {
        ManagedSysListResponse response = new ManagedSysListResponse();
        response.setManagedSysList(managedSystemService.getAllManagedSysDTO());
        return response;
    }
}
