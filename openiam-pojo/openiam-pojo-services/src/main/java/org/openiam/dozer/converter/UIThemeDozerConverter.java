package org.openiam.dozer.converter;

import java.util.List;
import java.util.Set;

import org.openiam.idm.srvc.ui.theme.domain.UIThemeEntity;
import org.openiam.idm.srvc.ui.theme.dto.UITheme;
import org.springframework.stereotype.Component;

@Component
public class UIThemeDozerConverter extends AbstractDozerEntityConverter<UITheme, UIThemeEntity> {

	 @Override
    public UIThemeEntity convertEntity(UIThemeEntity entity, boolean isDeep) {
        return convert(entity, isDeep, UIThemeEntity.class);
    }

    @Override
    public UITheme convertDTO(UITheme entity, boolean isDeep) {
        return convert(entity, isDeep, UITheme.class);
    }

    @Override
    public UIThemeEntity convertToEntity(UITheme entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, UIThemeEntity.class);
    }

    @Override
    public UITheme convertToDTO(UIThemeEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, UITheme.class);
    }

    @Override
    public List<UIThemeEntity> convertToEntityList(List<UITheme> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UIThemeEntity.class);
    }

    @Override
    public List<UITheme> convertToDTOList(List<UIThemeEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UITheme.class);
    }

    @Override
    public Set<UIThemeEntity> convertToEntitySet(Set<UITheme> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, UIThemeEntity.class);
    }

    @Override
    public Set<UITheme> convertToDTOSet(Set<UIThemeEntity> set, boolean isDeep) {
        return convertSetToCrossEntity(set, isDeep, UITheme.class);
    }

}
