package org.openiam.idm.srvc.user.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.user.domain.UserAttributeEntity;
import org.openiam.idm.srvc.user.dto.UserAttribute;

import java.util.List;
import java.util.Set;

public interface UserAttributeDAO extends BaseDao<UserAttributeEntity, String>{

	public List<UserAttributeEntity> findUserAttributes(String userId, final Set<String> metadataElementIds);
	
    public List<UserAttributeEntity> findUserAttributes(String userId);

    public List<UserAttributeEntity> findUserAttributesLocalized(String userId);

    void deleteUserAttributes(String userId);
}
