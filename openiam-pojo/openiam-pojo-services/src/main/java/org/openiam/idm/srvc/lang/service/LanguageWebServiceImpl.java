package org.openiam.idm.srvc.lang.service;

import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.hsqldb.lib.HashMap;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageLocale;
import org.openiam.internationalization.LocalizedServiceGet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("languageWebService")
@WebService(endpointInterface = "org.openiam.idm.srvc.lang.service.LanguageWebService", targetNamespace = "urn:idm.openiam.org/srvc/lang/service", portName = "LanguageWebServicePort", serviceName = "LanguageWebService")
public class LanguageWebServiceImpl implements LanguageWebService {

    @Autowired
    private LanguageDataService languageService;

    @Autowired
    private LanguageDozerConverter languageDozerConverter;

    @Override
    @LocalizedServiceGet
    public List<Language> getUsedLanguages(final Language language) {
        final List<LanguageEntity> entityList = languageService.getUsedLanguages();
        return (entityList != null) ? languageDozerConverter.convertToDTOList(entityList, true) : null;
    }

    @Override
    @LocalizedServiceGet
    public List<Language> findBeans(final LanguageSearchBean searchBean, final int from, final int size,
            final Language language) {
        final List<LanguageEntity> entityList = languageService.findBeans(searchBean, from, size,
                languageDozerConverter.convertToEntity(language, false));
        return languageDozerConverter.convertToDTOList(entityList, searchBean.isDeepCopy());
    }

    @Override
    public int count(final LanguageSearchBean searchBean) {
        List<LanguageEntity> list = languageService.allLanguages();
        return CollectionUtils.isEmpty(list) ? 0 : list.size();
    }

    @Override
    public void save(final Language language) {
        if (language == null)
            throw new NullPointerException("language is null");
        Map<String, LanguageLocale> locales = null;
        if (MapUtils.isNotEmpty(language.getLocales())) {
            locales = new java.util.HashMap<String, LanguageLocale>(language.getLocales());
            language.setLocales(null);
        }
        if (StringUtils.isBlank(language.getId())) {
            languageService.addLanguage(languageDozerConverter.convertToEntity(language, true));
        } else {
            languageService.updateLanguage(languageDozerConverter.convertToEntity(language, true));
        }
    }
}
