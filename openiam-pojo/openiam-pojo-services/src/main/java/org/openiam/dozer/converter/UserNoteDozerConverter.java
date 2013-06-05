package org.openiam.dozer.converter;

import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.UserNote;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 16.11.12
 */
@Component("userNoteDozerConverter")
public class UserNoteDozerConverter extends AbstractDozerEntityConverter<UserNote, UserNoteEntity> {
    @Override
    public UserNoteEntity convertEntity(UserNoteEntity userEntity, boolean isDeep) {
        return convert(userEntity, isDeep, UserNoteEntity.class);
    }

    @Override
    public UserNote convertDTO(UserNote entity, boolean isDeep) {
        return convert(entity, isDeep, UserNote.class);
    }

    @Override
    public UserNoteEntity convertToEntity(UserNote entity, boolean isDeep) {
        return convertToCrossEntity(entity, isDeep, UserNoteEntity.class);
    }

    @Override
    public UserNote convertToDTO(UserNoteEntity userEntity, boolean isDeep) {
        return convertToCrossEntity(userEntity, isDeep, UserNote.class);
    }

    @Override
    public List<UserNoteEntity> convertToEntityList(List<UserNote> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserNoteEntity.class);
    }

    @Override
    public List<UserNote> convertToDTOList(List<UserNoteEntity> list, boolean isDeep) {
        return convertListToCrossEntity(list, isDeep, UserNote.class);
    }
}
