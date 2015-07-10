package org.openiam.authmanager.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openiam.authmanager.common.model.AuthorizationUser;
import org.openiam.authmanager.common.model.InternalAuthroizationUser;

public interface UserDAO extends AbstractDAO<AuthorizationUser> {


    public List<String> getUserIdsList();

    public List<String> getUserIdsForRoles(Set<String> roleIds);
    public List<String> getUserIdsForGroups(Set<String> groupIds);
    public List<String> getUserIdsForResources(Set<String> resourceIds);

}
