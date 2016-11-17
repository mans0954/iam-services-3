package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.BaseCrudServiceRequest;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.mq.constants.api.OrganizationTypeAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 17/10/16.
 */
@Component
public class OrgTypeSaveDispatcher extends AbstractOrganizationTypeDispatcher<BaseCrudServiceRequest<OrganizationType>, StringResponse> {

    public OrgTypeSaveDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(OrganizationTypeAPI openIAMAPI, BaseCrudServiceRequest<OrganizationType> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(organizationTypeService.save(request.getObject()));
        return response;
    }
}
