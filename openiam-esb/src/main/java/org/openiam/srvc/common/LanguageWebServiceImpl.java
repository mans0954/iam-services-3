package org.openiam.srvc.common;

import org.openiam.base.response.IntResponse;
import org.openiam.mq.constants.api.OpenIAMAPICommon;
import org.openiam.mq.constants.queue.common.LanguageServiceQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.LanguageListResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.List;

/**
 * Created by alexander on 08/08/16.
 */
@WebService(endpointInterface = "org.openiam.srvc.common.LanguageWebService",
            targetNamespace = "urn:idm.openiam.org/srvc/lang/service", portName = "LanguageWebServicePort", serviceName = "LanguageWebService")
@Service("languageWebService")
public class LanguageWebServiceImpl extends AbstractApiService implements LanguageWebService {

    @Autowired
    public LanguageWebServiceImpl(LanguageServiceQueue queue){
        super(queue);
    }

    @Override
    public List<Language> getUsedLanguages(Language language) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>();
        request.setLanguage(language);
        return this.getValueList(OpenIAMAPICommon.GetUsedLanguages, request, LanguageListResponse.class);
    }

    @Override
    public List<Language> findBeans(LanguageSearchBean searchBean, int from, int size, Language language) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>(searchBean, from, size, language);
        return this.getValueList(OpenIAMAPICommon.FindLanguages, request, LanguageListResponse.class);
    }

    @Override
    public int count(LanguageSearchBean searchBean) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>(searchBean);
        return this.getValue(OpenIAMAPICommon.CountLanguages, request, IntResponse.class);
    }

    @Override
    public Response save(Language language) {
       return this.manageCrudApiRequest(OpenIAMAPICommon.SaveLanguage, language);
    }
}
