package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.cat.domain.CategoryTypeEntity;
import org.openiam.idm.srvc.cat.dto.CategoryType;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("categoryTypeDozerMapper")
public class CategoryTypeDozerConverter extends
        AbstractDozerEntityConverter<CategoryType, CategoryTypeEntity> {

    @Override
    public CategoryTypeEntity convertEntity(CategoryTypeEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, CategoryTypeEntity.class);
    }

    @Override
    public CategoryType convertDTO(CategoryType entity, boolean isDeep) {
        return convert(entity, isDeep, CategoryType.class);
    }

    @Override
    public CategoryTypeEntity convertToEntity(CategoryType entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, CategoryTypeEntity.class);
    }

    @Override
    public CategoryType convertToDTO(CategoryTypeEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, CategoryType.class);
    }

    @Override
    public List<CategoryTypeEntity> convertToEntityList(
            List<CategoryType> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, CategoryTypeEntity.class);
    }

    @Override
    public List<CategoryType> convertToDTOList(List<CategoryTypeEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, CategoryType.class);
    }

    @Override
    public Set<CategoryTypeEntity> convertToEntitySet(Set<CategoryType> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, CategoryTypeEntity.class);
    }

    @Override
    public Set<CategoryType> convertToDTOSet(Set<CategoryTypeEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, CategoryType.class);
    }
}
