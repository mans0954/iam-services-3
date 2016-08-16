package org.openiam.authmanager.dao;

import java.util.List;
import java.util.Set;

import org.openiam.am.srvc.dto.jdbc.AuthorizationUser;

public interface UserDAO extends AbstractDAO<AuthorizationUser> {


    public List<String> getUserIdsList();

    public List<String> getUserIdsForRoles(Set<String> roleIds);
    public List<String> getUserIdsForGroups(Set<String> groupIds);
    public List<String> getUserIdsForResources(Set<String> resourceIds);

}
