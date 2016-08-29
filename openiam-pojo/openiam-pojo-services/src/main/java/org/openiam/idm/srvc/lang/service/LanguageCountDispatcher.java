package org.openiam.idm.srvc.lang.service;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.CountResponse;
import org.openiam.base.response.LanguageListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by alexander on 09/08/16.
 */
@Component
public class LanguageCountDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<LanguageSearchBean>, CountResponse, OpenIAMAPICommon> {
    @Autowired
    private LanguageDataService languageDataService;

    public LanguageCountDispatcher() {
        super(CountResponse.class);
    }

    @Override
    protected CountResponse processingApiRequest(final OpenIAMAPICommon openIAMAPI,  BaseSearchServiceRequest<LanguageSearchBean> request) throws BasicDataServiceException {
        CountResponse response = new CountResponse();
        response.setRowCount(languageDataService.count(request.getSearchBean()));
        return response;
    }
}
