package org.openiam.idm.srvc.user.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.user.domain.UserNoteEntity;
import org.openiam.idm.srvc.user.dto.UserNote;

import java.util.List;

public interface UserNoteDAO extends BaseDao<UserNoteEntity, String>{

    List<UserNoteEntity> findUserNotes(String userId);

    void deleteUserNotes(String userId);
}
