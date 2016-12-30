package org.openiam.srvc.common;

import org.openiam.mq.constants.api.common.LanguageAPI;
import org.openiam.mq.constants.queue.common.LanguageServiceQueue;
import org.openiam.srvc.AbstractApiService;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.list.LanguageListResponse;
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
    public List<Language> getUsedLanguages() {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>();
        return this.getValueList(LanguageAPI.GetUsedLanguages, request, LanguageListResponse.class);
    }

    @Override
    public List<Language> findBeans(LanguageSearchBean searchBean, int from, int size) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>(searchBean, from, size);
        return this.getValueList(LanguageAPI.FindLanguages, request, LanguageListResponse.class);
    }

    @Override
    public int count(LanguageSearchBean searchBean) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>(searchBean);
        return this.getIntValue(LanguageAPI.CountLanguages, request);
    }

    @Override
    public Response save(Language language) {
       return this.manageCrudApiRequest(LanguageAPI.SaveLanguage, language);
    }
}
