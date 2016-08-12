package org.openiam.idm.srvc.auth.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.request.RenewTokenRequest;
import org.openiam.base.response.AuthStateListResponse;
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
public class FindAuthStateDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<AuthStateSearchBean>, AuthStateListResponse> {
    @Autowired
    private AuthenticationServiceService authenticationServiceService;

    public FindAuthStateDispatcher() {
        super(AuthStateListResponse.class);
    }

    @Override
    protected AuthStateListResponse processingApiRequest(OpenIAMAPI openIAMAPI, BaseSearchServiceRequest<AuthStateSearchBean> request) throws BasicDataServiceException {
        AuthStateListResponse resp = new AuthStateListResponse();
        resp.setAuthStateList(authenticationServiceService.findBeans(request.getSearchBean(), request.getFrom(), request.getSize()));
        return resp;
    }
}
