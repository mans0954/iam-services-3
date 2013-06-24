package org.openiam.dozer.converter;

import java.util.List;
import org.openiam.idm.srvc.cat.domain.CategoryEntity;
import org.openiam.idm.srvc.cat.dto.Category;
import org.springframework.stereotype.Component;

/**
 * 
 * @author zaporozhec
 *
 */
@Component("categoryDozerMapper")
public class CategoryDozerConverter extends
        AbstractDozerEntityConverter<Category, CategoryEntity> {

    @Override
    public CategoryEntity convertEntity(CategoryEntity entity,
            boolean isDeep) {
        return convert(entity, isDeep, CategoryEntity.class);
    }

    @Override
    public Category convertDTO(Category entity, boolean isDeep) {
        return convert(entity, isDeep, Category.class);
    }

    @Override
    public CategoryEntity convertToEntity(Category entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, CategoryEntity.class);
    }

    @Override
    public Category convertToDTO(CategoryEntity entity,
            boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, Category.class);
    }

    @Override
    public List<CategoryEntity> convertToEntityList(List<Category> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, CategoryEntity.class);
    }

    @Override
    public List<Category> convertToDTOList(List<CategoryEntity> list,
            boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, Category.class);
    }

}
