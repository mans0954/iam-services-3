package org.openiam.dozer.converter;

import org.openiam.idm.srvc.lang.domain.LanguageEntity;
import org.openiam.idm.srvc.lang.dto.Language;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("languageDozerConverter")
public class LanguageDozerConverter extends
        AbstractDozerEntityConverter<Language, LanguageEntity> {

    @Override
    public LanguageEntity convertEntity(LanguageEntity entity,
                                            boolean isDeep) {
        return convert(entity, isDeep, LanguageEntity.class);
    }

    @Override
    public Language convertDTO(Language entity, boolean isDeep) {
        return convert(entity, isDeep, Language.class);
    }

    @Override
    public LanguageEntity convertToEntity(Language entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, LanguageEntity.class);
    }

    @Override
    public Language convertToDTO(LanguageEntity entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, Language.class);
    }

    @Override
    public List<LanguageEntity> convertToEntityList(List<Language> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, LanguageEntity.class);
    }

    @Override
    public List<Language> convertToDTOList(List<LanguageEntity> list,  boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Language.class);
    }
}
