package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.MembershipRequest;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.api.OrganizationTypeAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 17/10/16.
 */
@Component
public class OrgTypeMembershipDispatcher extends AbstractOrganizationTypeDispatcher<MembershipRequest, Response> {

    public OrgTypeMembershipDispatcher() {
        super(Response.class);
    }

    @Override
    protected Response processingApiRequest(OrganizationTypeAPI openIAMAPI, MembershipRequest request) throws BasicDataServiceException {
        switch (openIAMAPI) {
            case AddChild:
                organizationTypeService.addChild(request.getObjectId(), request.getLinkedObjectId());
                break;
            case RemoveChild:
                organizationTypeService.removeChild(request.getObjectId(), request.getLinkedObjectId());
                break;
        }
        return new Response();
    }
}
