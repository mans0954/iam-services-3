package org.openiam.idm.srvc.user.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.user.domain.ProfilePictureEntity;

public interface ProfilePictureDAO extends BaseDao<ProfilePictureEntity, String> {

    void deleteById(String picId);

    ProfilePictureEntity getByUserId(String userId);

    void deleteByUserId(String userId);

}
