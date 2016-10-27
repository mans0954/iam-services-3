package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.AuthLevelAttribute;
import org.openiam.am.srvc.service.ContentProviderService;
import org.openiam.base.request.BaseCrudServiceRequest;
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
public class SaveAuthLevelAttributeDispatcher extends AbstractAPIDispatcher<BaseCrudServiceRequest<AuthLevelAttribute>, StringResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public SaveAuthLevelAttributeDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseCrudServiceRequest<AuthLevelAttribute> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        response.setValue(contentProviderService.saveAuthLevelAttibute(request.getObject()));
        return response;
    }
}
