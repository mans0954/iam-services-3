package org.openiam.srvc.common;

import org.openiam.srvc.AbstractApiService;
import org.openiam.base.request.BaseGrudServiceRequest;
import org.openiam.base.request.BaseSearchServiceRequest;
import org.openiam.base.response.CountResponse;
import org.openiam.base.response.IdServiceResponse;
import org.openiam.base.response.LanguageListResponse;
import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.mq.constants.OpenIAMAPI;
import org.openiam.mq.constants.OpenIAMQueue;
import org.openiam.srvc.common.LanguageWebService;
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

    public LanguageWebServiceImpl(){
        super(OpenIAMQueue.LanguageServiceQueue);
    }

    @Override
    public List<Language> getUsedLanguages(Language language) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>();
        request.setLanguage(language);
        LanguageListResponse response = this.manageApiRequest(OpenIAMAPI.GetUsedLanguages, request, LanguageListResponse.class);
        return response.getLanguageList();
    }

    @Override
    public List<Language> findBeans(LanguageSearchBean searchBean, int from, int size, Language language) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>(searchBean, from, size, language);

        LanguageListResponse response = this.manageApiRequest(OpenIAMAPI.FindLanguages, request, LanguageListResponse.class);
        return response.getLanguageList();
    }

    @Override
    public int count(LanguageSearchBean searchBean) {
        BaseSearchServiceRequest<LanguageSearchBean> request = new BaseSearchServiceRequest<LanguageSearchBean>(searchBean);
        CountResponse response = this.manageApiRequest(OpenIAMAPI.CountLanguages, request, CountResponse.class);
        return response.getRowCount();
    }

    @Override
    public Response save(Language language) {
        BaseGrudServiceRequest<Language> request = new BaseGrudServiceRequest<>(language);
        IdServiceResponse response =  this.manageApiRequest(OpenIAMAPI.SaveLanguage, request, IdServiceResponse.class);

       return response.convertToBase();
    }
}
