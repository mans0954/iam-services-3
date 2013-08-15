package org.openiam.idm.srvc.lang.service;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("languageWebService")
@WebService(endpointInterface = "org.openiam.idm.srvc.lang.service.LanguageWebService", 
			targetNamespace = "urn:idm.openiam.org/srvc/lang/service", 
			portName = "LanguageWebServicePort", 
			serviceName = "LanguageWebService")
public class LanguageWebServiceImpl implements LanguageWebService {

	@Autowired
	private LanguageDataService languageService;
	
	@Autowired
	private LanguageDozerConverter languageDozerConverter;
	
	@Override
	public List<Language> getAll() {
		final List<LanguageEntity> entityList = languageService.allLanguages();
		return (entityList != null) ? languageDozerConverter.convertToDTOList(entityList, true) : null;
	}

}
