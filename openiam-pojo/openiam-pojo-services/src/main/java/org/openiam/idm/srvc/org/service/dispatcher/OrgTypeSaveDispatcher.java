package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.IdServiceRequest;
import org.openiam.base.response.OrganizationTypeResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.org.dto.OrganizationType;
import org.openiam.mq.constants.OrganizationTypeAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 17/10/16.
 */
@Component
public class OrgTypeSaveDispatcher extends AbstractOrganizationTypeDispatcher<BaseGrudServiceRequest<OrganizationType>, StringResponse> {

    public OrgTypeSaveDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(OrganizationTypeAPI openIAMAPI, BaseGrudServiceRequest<OrganizationType> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(organizationTypeService.save(request.getObject()));
        return response;
    }
}
