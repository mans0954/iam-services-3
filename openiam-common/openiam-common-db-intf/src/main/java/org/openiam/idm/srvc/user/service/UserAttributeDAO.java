package org.openiam.idm.srvc.user.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.util.List;

public interface UserAttributeDAO extends BaseDao<UserAttributeEntity, String>{

    List<UserAttributeEntity> findUserAttributes(String userId);

    void deleteUserAttributes(String userId);
}
