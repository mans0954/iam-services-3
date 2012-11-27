package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.cat.domain.CategoryLanguageEntity;
import org.openiam.idm.srvc.cat.dto.CategoryLanguage;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("categoryLanguageDozerMapper")
public class CategoryLanguageDozerConverter extends
        AbstractDozerEntityConverter<CategoryLanguage, CategoryLanguageEntity> {

    @Override
    public CategoryLanguageEntity convertEntity(CategoryLanguageEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, CategoryLanguageEntity.class);
    }

    @Override
    public CategoryLanguage convertDTO(CategoryLanguage entity, boolean isDeep) {
        return convert(entity, isDeep, CategoryLanguage.class);
    }

    @Override
    public CategoryLanguageEntity convertToEntity(CategoryLanguage entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep,
                CategoryLanguageEntity.class);
    }

    @Override
    public CategoryLanguage convertToDTO(CategoryLanguageEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, CategoryLanguage.class);
    }

    @Override
    public List<CategoryLanguageEntity> convertToEntityList(
            List<CategoryLanguage> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep,
                CategoryLanguageEntity.class);
    }

    @Override
    public List<CategoryLanguage> convertToDTOList(
            List<CategoryLanguageEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, CategoryLanguage.class);
    }

}
