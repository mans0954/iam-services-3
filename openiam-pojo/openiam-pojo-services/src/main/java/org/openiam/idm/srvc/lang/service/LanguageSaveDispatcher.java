package org.openiam.idm.srvc.lang.service;

import org.openiam.base.request.BaseCrudServiceRequest;
import org.openiam.base.response.IdServiceResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 09/08/16.
 */
@Component
public class LanguageSaveDispatcher extends AbstractAPIDispatcher<BaseCrudServiceRequest<Language>, IdServiceResponse, OpenIAMAPICommon> {
    @Autowired
    private LanguageDataService languageDataService;

    public LanguageSaveDispatcher() {
        super(IdServiceResponse.class);
    }

    @Override
    protected IdServiceResponse processingApiRequest(final OpenIAMAPICommon openIAMAPI,  BaseCrudServiceRequest<Language> request) throws BasicDataServiceException {
        IdServiceResponse response = new IdServiceResponse();
        response.setId(languageDataService.save(request.getObject()));
        return response;
    }
}
