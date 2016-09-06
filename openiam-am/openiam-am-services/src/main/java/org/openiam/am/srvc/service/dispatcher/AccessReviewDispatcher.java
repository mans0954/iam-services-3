package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.service.AccessReviewService;
import org.openiam.base.request.AccessReviewRequest;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.model.AccessViewResponse;
import org.openiam.mq.constants.AccessReviewAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 02/09/16.
 */
@Component
public class AccessReviewDispatcher  extends AbstractAPIDispatcher<AccessReviewRequest, AccessViewResponse, AccessReviewAPI> {
    @Autowired
    private AccessReviewService accessReviewService;

    public AccessReviewDispatcher() {
        super(AccessViewResponse.class);
    }


    @Override
    protected AccessViewResponse processingApiRequest(AccessReviewAPI openIAMAPI, AccessReviewRequest request) throws BasicDataServiceException {
        AccessViewResponse response = new AccessViewResponse();
        switch (openIAMAPI){
            case AccessReviewTree:
                response = accessReviewService.getAccessReviewTree(request.getFilterBean(), request.getViewType(), request.getDate(), request.getLanguage());
                break;
            case AccessReviewSubTree:
                response = accessReviewService.getAccessReviewSubTree(request.getParentId(), request.getParentBeanType(), request.isRootOnly(),
                                                           request.getFilterBean(), request.getViewType(), request.getDate(), request.getLanguage());
                break;
        }
        return response;
    }
}
