package org.openiam.mq;

import org.openiam.base.request.*;
import org.openiam.base.response.IdServiceResponse;
import org.openiam.base.response.IntResponse;
import org.openiam.base.response.LanguageListResponse;
import org.openiam.base.response.StringResponse;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.service.LanguageDataService;
import org.openiam.mq.constants.api.OpenIAMAPICommon;
import org.openiam.mq.constants.queue.common.LanguageServiceQueue;
import org.openiam.mq.listener.AbstractListener;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 09/08/16.
 */
@RabbitListener(id="languageListener",
        queues = "#{LanguageServiceQueue.name}",
        containerFactory = "commonRabbitListenerContainerFactory")
@Component
public class LanguageListener extends AbstractListener<OpenIAMAPICommon> {

    @Autowired
    private LanguageDataService languageDataService;

    @Autowired
    public LanguageListener(LanguageServiceQueue queue) {
        super(queue);
    }

    @Override
    protected RequestProcessor<OpenIAMAPICommon, BaseSearchServiceRequest> getSearchRequestProcessor() {
        return new RequestProcessor<OpenIAMAPICommon, BaseSearchServiceRequest>(){
            @Override
            public Response doProcess(OpenIAMAPICommon api, BaseSearchServiceRequest request) throws BasicDataServiceException {
                Response response;
                switch (api){
                    case GetUsedLanguages:
                        response = new LanguageListResponse();
                        ((LanguageListResponse)response).setList(languageDataService.getUsedLanguages(request.getLanguage()));
                        return response;
                    case FindLanguages:
                        response = new LanguageListResponse();
                        ((LanguageListResponse)response).setList(languageDataService.findBeans(((BaseSearchServiceRequest<LanguageSearchBean>)request).getSearchBean(), request.getFrom(), request.getSize(), request.getLanguage()));
                        return response;
                    case CountLanguages:
                        response = new IntResponse();
                        ((IntResponse)response).setValue(languageDataService.count(((BaseSearchServiceRequest<LanguageSearchBean>)request).getSearchBean()));
                        return response;
                    default:
                        throw new BasicDataServiceException(ResponseCode.INVALID_ARGUMENTS, "Unknown API name: " + api.name());
                }
            }
        };
    }

    @Override
    protected RequestProcessor<OpenIAMAPICommon, BaseCrudServiceRequest> getCrudRequestProcessor() {
        return new RequestProcessor<OpenIAMAPICommon, BaseCrudServiceRequest>(){
            @Override
            public Response doProcess(OpenIAMAPICommon api, BaseCrudServiceRequest request) throws BasicDataServiceException {
                StringResponse response = new StringResponse();
                response.setValue(languageDataService.save(((BaseCrudServiceRequest<Language>)request).getObject()));
                return response;
            }
        };
    }
}
