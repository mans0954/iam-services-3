package org.openiam.idm.srvc.lang.service;

import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jws.WebService;
import java.util.List;

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
    public List<Language> getUsedLanguages(){
        final List<LanguageEntity> entityList = languageService.getUsedLanguages();
        return (entityList != null) ? languageDozerConverter.convertToDTOList(entityList, true) : null;
    }

	@Override
	public List<Language> findBeans(final LanguageSearchBean searchBean, final int from, final int size) {
		final List<LanguageEntity> entityList = languageService.findBeans(searchBean, from, size);
		return languageDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
	}

}
