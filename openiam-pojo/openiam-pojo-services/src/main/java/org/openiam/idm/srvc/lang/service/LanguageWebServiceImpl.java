package org.openiam.idm.srvc.lang.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.openiam.base.ws.Response;
import org.openiam.base.ws.ResponseCode;
import org.openiam.base.ws.ResponseStatus;
import org.openiam.dozer.converter.LanguageDozerConverter;
import org.openiam.dozer.converter.LanguageLocaleDozerConverter;
import org.openiam.exception.BasicDataServiceException;
import org.openiam.idm.searchbeans.LanguageSearchBean;
import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;
import org.openiam.idm.srvc.lang.domain.LanguageMappingEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.openiam.idm.srvc.lang.dto.LanguageLocale;
import org.openiam.idm.srvc.lang.dto.LanguageMapping;
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
    @Autowired
    private LanguageLocaleDozerConverter languageLocaleDozerConverter;

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

    // @Override
    // public void save(final Language language) throws Exception {
    // if (language == null)
    // throw new NullPointerException("language is null");
    // if (StringUtils.isBlank(language.getId())) {
    // languageService.addLanguage(languageDozerConverter.convertToEntity(language,
    // false)).getId();
    // } else {
    // languageService.updateLanguage(languageDozerConverter.convertToEntity(language,
    // true));
    // }
    // }
    @Override
    public Response save(final Language language) {
        Response response = new Response();
        response.setStatus(ResponseStatus.SUCCESS);
        boolean isAdd = false;
        try {
            if (language == null)
                throw new NullPointerException("language is null");
            Map<String, LanguageLocale> localesUI = null;
            List<LanguageLocaleEntity> db = null;
            if (MapUtils.isNotEmpty(language.getLocales())) {
                localesUI = new java.util.HashMap<String, LanguageLocale>(language.getLocales());
                language.setLocales(null);
            }
            LanguageEntity entity = null;
            String id = null;
            if (StringUtils.isBlank(language.getId())) {
                id = languageService.addLanguage(languageDozerConverter.convertToEntity(language, false)).getId();
                isAdd = true;
            } else {
                id = language.getId();
                db = languageService.getLanguageLocaleByLanguage(id);
                languageService.updateLanguage(languageDozerConverter.convertToEntity(language, true));
            }
            LanguageSearchBean sb = new LanguageSearchBean();
            sb.setKey(id);
            sb.setDeepCopy(true);

            entity = languageService.findBeans(sb, 0, 1).get(0);
            if (entity == null)
                throw new BasicDataServiceException(ResponseCode.INTERNAL_ERROR);
            // save locales
            // 1. All locales are deleted
            if (MapUtils.isEmpty(localesUI) && !CollectionUtils.isEmpty(db)) {
                for (LanguageLocaleEntity lle : db) {
                    languageService.removeLanguageLocale(lle);
                }
                // If all locales is new;
            } else if (!MapUtils.isEmpty(localesUI) && CollectionUtils.isEmpty(db)) {
                for (LanguageLocale ll : localesUI.values()) {
                    LanguageLocaleEntity lle = new LanguageLocaleEntity();
                    lle.setId(null);
                    lle.setLanguage(entity);
                    lle.setLocale(ll.getLocale());
                    languageService.addLanguageLocale(lle);
                }
            } else if (!MapUtils.isEmpty(localesUI) && !CollectionUtils.isEmpty(db)) {
                List<LanguageLocaleEntity> ui = languageLocaleDozerConverter.convertToEntityList(
                        new ArrayList<LanguageLocale>(localesUI.values()), false);
                for (LanguageLocaleEntity lle : ui) {
                    if (StringUtils.isEmpty(lle.getId())) {
                        lle.setLanguage(entity);
                        lle.setId(languageService.addLanguageLocale(lle).getId());
                    } else {
                        Iterator<LanguageLocaleEntity> iter = db.iterator();
                        while (iter.hasNext()) {
                            LanguageLocaleEntity lledb = iter.next();
                            if (lledb.getId().equals(lle.getId())) {
                                lle.setLanguage(entity);
                                languageService.updateLanguageLocale(lle);
                                iter.remove();
                            }
                        }
                    }
                }
                if (!CollectionUtils.isEmpty(db)) {
                    for (LanguageLocaleEntity lledb : db) {
                        languageService.removeLanguageLocale(lledb);
                    }
                }
            }
            if (isAdd) {
                for (String str : language.getDisplayNameMap().keySet()) {
                    LanguageMappingEntity newE = new LanguageMappingEntity();
                    LanguageMapping oldE = language.getDisplayNameMap().get(str);
                    if (oldE != null) {
                        newE.setLanguageId(str);
                        newE.setReferenceId(entity.getId());
                        newE.setReferenceType(LanguageEntity.class.getSimpleName());
                        newE.setValue(oldE.getValue());
                        newE.setId(languageService.addLanguageMapping(newE).getId());
                    }
                }
            }
            response.setResponseValue(entity.getId());
        } catch (BasicDataServiceException e) {
            response.setErrorCode(e.getCode());
            response.setStatus(ResponseStatus.FAILURE);
        }
        return response;
    }
}
