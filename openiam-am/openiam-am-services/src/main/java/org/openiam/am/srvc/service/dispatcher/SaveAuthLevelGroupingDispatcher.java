package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.dto.AuthLevelGrouping;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.response.StringResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.mq.constants.ContentProviderAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 23/09/16.
 */
@Component
public class SaveAuthLevelGroupingDispatcher extends AbstractAPIDispatcher<BaseGrudServiceRequest<AuthLevelGrouping>, StringResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public SaveAuthLevelGroupingDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseGrudServiceRequest<AuthLevelGrouping> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(contentProviderService.saveAuthLevelGrouping(request.getObject()));
        return response;
    }
}
