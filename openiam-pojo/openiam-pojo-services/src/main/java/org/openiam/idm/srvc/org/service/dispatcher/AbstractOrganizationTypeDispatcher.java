package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.BaseServiceRequest;
import org.openiam.base.ws.Response;
import org.openiam.idm.srvc.org.service.OrganizationTypeService;
import org.openiam.mq.constants.api.OrganizationTypeAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by alexander on 17/10/16.
 */
public abstract class AbstractOrganizationTypeDispatcher<RequestBody extends BaseServiceRequest, ResponseBody extends Response>
                        extends AbstractAPIDispatcher<RequestBody, ResponseBody, OrganizationTypeAPI> {
    @Autowired
    protected OrganizationTypeService organizationTypeService;

    public AbstractOrganizationTypeDispatcher(Class<ResponseBody> responseBodyClass) {
        super(responseBodyClass);
    }
}
