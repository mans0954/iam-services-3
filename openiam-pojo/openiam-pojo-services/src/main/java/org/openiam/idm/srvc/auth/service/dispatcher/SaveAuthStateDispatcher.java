package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.AuthStateCrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.AuthStateListResponse;
import org.openiam.base.response.IdServiceResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.AuthStateSearchBean;
import org.openiam.idm.srvc.auth.service.AuthenticationServiceService;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 11/08/16.
 */
@Component
public class SaveAuthStateDispatcher extends AbstractAPIDispatcher<AuthStateCrudServiceRequest, IdServiceResponse> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public SaveAuthStateDispatcher() {
        super(IdServiceResponse.class);
    }

    @Override
    protected IdServiceResponse processingApiRequest(OpenIAMAPI openIAMAPI, AuthStateCrudServiceRequest request) throws BasicDataServiceException {
        IdServiceResponse resp = new IdServiceResponse();
        Response response = authenticationServiceService.save(request.getAuthStateEntity());
        resp.setErrorCode(response.getErrorCode());
        resp.setStatus(response.getStatus());
        resp.setErrorText(response.getErrorText());
        return resp;
    }
}
