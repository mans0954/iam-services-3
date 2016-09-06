package org.openiam.srvc.am;

import java.util.Date;

import org.openiam.base.request.AccessReviewRequest;
import org.openiam.model.AccessViewFilterBean;
import org.openiam.model.AccessViewResponse;
import org.openiam.am.srvc.service.AccessReviewService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.mq.constants.AccessReviewAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.AbstractApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("accessReviewWS")
@WebService(endpointInterface = "org.openiam.srvc.am.AccessReviewWebService", targetNamespace = "urn:idm.openiam.org/srvc/access/review/service",
            portName = "AccessReviewWebServicePort", serviceName = "AccessReviewWebService")
public class AccessReviewWebServiceImpl extends AbstractApiService implements AccessReviewWebService {
    public AccessReviewWebServiceImpl() {
        super(OpenIAMQueue.AccessReviewQueue);
    }

    @Override
    public AccessViewResponse getAccessReviewTree(AccessViewFilterBean filter, String viewType, Date date, Language language) {
        AccessReviewRequest request = new AccessReviewRequest();
        request.setFilterBean(filter);
        request.setViewType(viewType);
        request.setDate(date);
        request.setLanguage(language);
        return this.manageApiRequest(AccessReviewAPI.AccessReviewTree, request, AccessViewResponse.class);
    }

    @Override
    public AccessViewResponse getAccessReviewSubTree(String parentId, String parentBeanType, boolean isRootOnly, AccessViewFilterBean filter, String viewType, Date date, Language language) {
        AccessReviewRequest request = new AccessReviewRequest();
        request.setParentId(parentId);
        request.setParentBeanType(parentBeanType);
        request.setRootOnly(isRootOnly);
        request.setFilterBean(filter);
        request.setViewType(viewType);
        request.setDate(date);
        request.setLanguage(language);
        return this.manageApiRequest(AccessReviewAPI.AccessReviewSubTree, request, AccessViewResponse.class);
    }
}
