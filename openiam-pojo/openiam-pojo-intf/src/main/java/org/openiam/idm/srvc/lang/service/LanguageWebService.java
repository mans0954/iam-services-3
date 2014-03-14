package org.openiam.idm.srvc.lang.service;

import org.openiam.idm.srvc.lang.dto.Language;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/lang/service", name = "LanguageWebService")
public interface LanguageWebService {

	@WebMethod
	public List<Language> getAll();

    /**
     * Returns an list of those languages that are in use
     *
     * @return
     */
    @WebMethod
    public List<Language> getUsedLanguages();
}
