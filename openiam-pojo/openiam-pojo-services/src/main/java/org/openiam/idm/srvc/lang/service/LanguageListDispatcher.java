package org.openiam.idm.srvc.lang.service;

import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.LanguageListResponse;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMAPICommon;
import org.openiam.mq.processor.AbstractAPIDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alexander on 09/08/16.
 */
@Component
public class LanguageListDispatcher extends AbstractAPIDispatcher<BaseSearchServiceRequest<LanguageSearchBean>, LanguageListResponse, OpenIAMAPICommon> {
    @Autowired
    private LanguageDataService languageDataService;

    public LanguageListDispatcher() {
        super(LanguageListResponse.class);
    }

    @Override
    protected LanguageListResponse processingApiRequest(final OpenIAMAPICommon openIAMAPI,  BaseSearchServiceRequest<LanguageSearchBean> request) throws BasicDataServiceException {
        LanguageListResponse languageListResponse = new LanguageListResponse();
        switch (openIAMAPI){
            case GetUsedLanguages:
                languageListResponse.setLanguageList(languageDataService.getUsedLanguages(request.getLanguage()));
                break;
            case FindLanguages:
                languageListResponse.setLanguageList(languageDataService.findBeans(request.getSearchBean(), request.getFrom(), request.getSize(), request.getLanguage()));
                break;
            default:
                break;
        }
        return languageListResponse;
    }
}
