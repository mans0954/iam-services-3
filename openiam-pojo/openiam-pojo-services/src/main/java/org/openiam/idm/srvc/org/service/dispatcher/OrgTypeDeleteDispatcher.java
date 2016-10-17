package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.OrganizationTypeAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 17/10/16.
 */
@Component
public class OrgTypeDeleteDispatcher extends AbstractOrganizationTypeDispatcher<IdServiceRequest, Response> {

    public OrgTypeDeleteDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(OrganizationTypeAPI openIAMAPI, IdServiceRequest request) throws BasicDataServiceException {
        organizationTypeService.delete(request.getId());
        return new Response();
    }
}
