package org.openiam.dozer.converter;

import org.openiam.idm.srvc.lang.domain.LanguageLocaleEntity;
import org.openiam.idm.srvc.lang.dto.LanguageLocale;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component("languageLocaleDozerConverter")
public class LanguageLocaleDozerConverter extends AbstractDozerEntityConverter<LanguageLocale, LanguageLocaleEntity> {

    @Override
    public LanguageLocaleEntity convertEntity(LanguageLocaleEntity entity, boolean isDeep) {
        return convert(entity, isDeep, LanguageLocaleEntity.class);
    }

    @Override
    public LanguageLocale convertDTO(LanguageLocale entity, boolean isDeep) {
        return convert(entity, isDeep, LanguageLocale.class);
    }

    @Override
    public LanguageLocaleEntity convertToEntity(LanguageLocale entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, LanguageLocaleEntity.class);
    }

    @Override
    public LanguageLocale convertToDTO(LanguageLocaleEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, LanguageLocale.class);
    }

    @Override
    public List<LanguageLocaleEntity> convertToEntityList(List<LanguageLocale> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, LanguageLocaleEntity.class);
    }

    @Override
    public List<LanguageLocale> convertToDTOList(List<LanguageLocaleEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, LanguageLocale.class);
    }

    @Override
    public Set<LanguageLocaleEntity> convertToEntitySet(Set<LanguageLocale> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, LanguageLocaleEntity.class);
    }

    @Override
    public Set<LanguageLocale> convertToDTOSet(Set<LanguageLocaleEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, LanguageLocale.class);
    }
}
