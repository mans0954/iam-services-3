package org.openiam.idm.srvc.lang.service;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.idm.srvc.lang.dto.Language;

@WebService(targetNamespace = "urn:idm.openiam.org/srvc/lang/service", name = "LanguageWebService")
public interface LanguageWebService {

	@WebMethod
	public List<Language> getAll();
}
