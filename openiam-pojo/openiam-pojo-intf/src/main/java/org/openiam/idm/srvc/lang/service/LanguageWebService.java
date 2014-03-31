package org.openiam.idm.srvc.lang.service;

import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/lang/service", name = "LanguageWebService")
public interface LanguageWebService {

    @WebMethod
    public List<Language> getUsedLanguages(final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    public List<Language> findBeans(
            final @WebParam(name = "searchBean", targetNamespace = "") LanguageSearchBean searchBean,
            final @WebParam(name = "from", targetNamespace = "") int from,
            final @WebParam(name = "size", targetNamespace = "") int size,
            final @WebParam(name = "language", targetNamespace = "") Language language);

    @WebMethod
    int count(final @WebParam(name = "searchBean", targetNamespace = "") LanguageSearchBean searchBean);

    @WebMethod
    void save(final @WebParam(name = "language", targetNamespace = "") Language language);

}
