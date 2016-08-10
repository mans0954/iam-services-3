package org.openiam.srvc.lang;

import org.openiam.base.ws.Response;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.dto.Language;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Created by alexander on 08/08/16.
 */
@WebService(targetNamespace = "urn:idm.openiam.org/srvc/lang/service", name = "LanguageWebService")
public interface LanguageWebService {
    @WebMethod
    List<Language> getUsedLanguages(final @WebParam(name = "lang", targetNamespace = "") Language language);

    @WebMethod
    List<Language> findBeans(
            final @WebParam(name = "searchBean", targetNamespace = "") LanguageSearchBean searchBean,
            final @WebParam(name = "from", targetNamespace = "") int from,
            final @WebParam(name = "size", targetNamespace = "") int size,
            final @WebParam(name = "lang", targetNamespace = "") Language language);

    @WebMethod
    int count(final @WebParam(name = "searchBean", targetNamespace = "") LanguageSearchBean searchBean);

    @WebMethod
    Response save(final @WebParam(name = "lang", targetNamespace = "") Language language);
}
