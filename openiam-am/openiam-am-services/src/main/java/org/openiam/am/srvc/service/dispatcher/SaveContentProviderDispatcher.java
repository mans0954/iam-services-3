package org.openiam.am.srvc.service.dispatcher;

import org.openiam.am.srvc.dto.ContentProvider;
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
public class SaveContentProviderDispatcher extends AbstractAPIDispatcher<BaseCrudServiceRequest<ContentProvider>, StringResponse, ContentProviderAPI> {
    @Autowired
    private ContentProviderService contentProviderService;

    public SaveContentProviderDispatcher() {
        super(StringResponse.class);
    }

    @Override
    protected StringResponse processingApiRequest(ContentProviderAPI openIAMAPI, BaseCrudServiceRequest<ContentProvider> request) throws BasicDataServiceException {
        StringResponse response = new StringResponse();
        switch (openIAMAPI){
            case SaveContentProvider:
                response.setValue(contentProviderService.saveContentProvider(request.getObject()));
                break;
            case SetupApplication:
                response.setValue(contentProviderService.setupApplication(request.getObject()));
                break;
        }


        return response;
    }
}
