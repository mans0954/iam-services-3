package org.openiam.idm.srvc.org.service.dispatcher;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.OrganizationTypeListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.OrganizationTypeSearchBean;
import org.openiam.mq.constants.OrganizationTypeAPI;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 17/10/16.
 */
@Component
public class FindOrganizationTypeDispatcher extends AbstractOrganizationTypeDispatcher<BaseSearchServiceRequest<OrganizationTypeSearchBean>, OrganizationTypeListResponse> {

    public FindOrganizationTypeDispatcher() {
        super(OrganizationTypeListResponse.class);
    }

    @Override
    protected OrganizationTypeListResponse processingApiRequest(OrganizationTypeAPI openIAMAPI, BaseSearchServiceRequest<OrganizationTypeSearchBean> request) throws BasicDataServiceException {
        OrganizationTypeListResponse response = new OrganizationTypeListResponse();
        switch (openIAMAPI){
            case FindBeans:
                response.setList(organizationTypeService.findBeans(request.getSearchBean(), request.getFrom(), request.getSize(), request.getLanguage()));
                break;
            case FindAllowedChildren:
                response.setList(organizationTypeService.findAllowedChildrenByDelegationFilter(request.getRequesterId(), request.getLanguage()));
                break;
            case GetAllowedParents:
                response.setList(organizationTypeService.getAllowedParents(request.getSearchBean().getKey(), request.getRequesterId(), request.getLanguage()));
                break;
        }
        return response;
    }
}
