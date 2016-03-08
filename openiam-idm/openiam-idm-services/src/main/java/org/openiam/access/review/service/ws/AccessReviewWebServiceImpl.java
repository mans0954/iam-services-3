package org.openiam.access.review.service.ws;

import java.util.Date;

import org.openiam.access.review.model.AccessViewFilterBean;
import org.openiam.access.review.model.AccessViewResponse;
import org.openiam.access.review.service.AccessReviewService;
import org.openiam.idm.srvc.lang.dto.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;

@Service("accessReviewWS")
@WebService(endpointInterface = "org.openiam.access.review.service.ws.AccessReviewWebService", targetNamespace = "urn:idm.openiam.org/srvc/access/review/service",
            portName = "AccessReviewWebServicePort", serviceName = "AccessReviewWebService")
public class AccessReviewWebServiceImpl implements AccessReviewWebService {
    @Autowired
    private AccessReviewService accessReviewService;

    @Override
    public AccessViewResponse getAccessReviewTree(AccessViewFilterBean filter, String viewType, Date date, Language language) {
        return accessReviewService.getAccessReviewTree(filter,viewType,date,language);
    }

    @Override
    public AccessViewResponse getAccessReviewSubTree(String parentId, String parentBeanType, boolean isRootOnly, AccessViewFilterBean filter, String viewType, Date date, Language language) {
        return accessReviewService.getAccessReviewSubTree(parentId,parentBeanType,isRootOnly,filter,viewType,date,language);
    }
}
