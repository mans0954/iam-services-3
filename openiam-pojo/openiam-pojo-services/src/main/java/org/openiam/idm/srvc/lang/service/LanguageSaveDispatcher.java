package org.openiam.idm.srvc.lang.service;

import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.CountResponse;
import org.openiam.base.response.IdServiceResponse;
import org.openiam.base.ws.Response;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 09/08/16.
 */
@Component
public class LanguageSaveDispatcher extends AbstractAPIDispatcher<BaseGrudServiceRequest<Language>, IdServiceResponse> {
    @Autowired
    private LanguageDataService languageDataService;

    public LanguageSaveDispatcher() {
        super(IdServiceResponse.class);
    }

    @Override
    protected void processingApiRequest(final OpenIAMAPI openIAMAPI,  BaseGrudServiceRequest<Language> request, IdServiceResponse response) throws BasicDataServiceException {
        response.setId(languageDataService.save(request.getObject()));
    }
}
