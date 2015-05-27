package org.openiam.idm.srvc.lang.service;

import java.util.List;

import javax.jws.WebService;

import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@WebService(endpointInterface = "org.openiam.idm.srvc.lang.service.LanguageWebService", targetNamespace = "urn:idm.openiam.org/srvc/lang/service", portName = "LanguageWebServicePort", serviceName = "LanguageWebService")
@Component("languageWebService")
public class LanguageWebServiceImpl implements LanguageWebService {

    @Autowired
    private LanguageDataService languageService;

    @Override
    public List<Language> getUsedLanguages(final Language language) {
        return languageService.getUsedLanguages(language);
    }

    @Override
     public List<Language> findBeans(final LanguageSearchBean searchBean, final int from, final int size,
                                    final Language language) {
         return languageService.findBeans(searchBean, from, size, language);
    }

    @Override
    public int count(final LanguageSearchBean searchBean) {
        return languageService.count(searchBean);
    }

    @Override
    public Response save(final Language language) {
        Response response = new Response();
        response.setStatus(ResponseStatus.SUCCESS);
        try {

            String languageId = languageService.save(language);
            response.setResponseValue(languageId);
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setResponseValue(e.getResponseValue());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}
