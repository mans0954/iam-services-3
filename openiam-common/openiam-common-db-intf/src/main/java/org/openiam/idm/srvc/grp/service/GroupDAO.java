package org.openiam.idm.srvc.grp.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.grp.domain.GroupEntity;

import java.util.List;
import java.util.Set;


/**
 * Data access object interface for Group.
 *
 * @author Suneet Shah
 */
public interface GroupDAO extends BaseDao<GroupEntity, String> {

    /**
     * Return a list of root level group object. Root level group object do not have parent groups.
     *
     * @return
     */
    List<GroupEntity> findRootGroups(final int from, final int size);
    
    @Deprecated
    int getNumOfGroupsForUser(final String userId, Set<String> filter);
    
    @Deprecated
    List<GroupEntity> getGroupsForUser(final String userId, Set<String> filter, final int from, final int size);
    
    @Deprecated
    List<GroupEntity> getGroupsForRole(final String roleId, Set<String> filter, int from, int size);
    
    @Deprecated
    int getNumOfGroupsForRole(final String roleId, Set<String> filter);

    @Deprecated
    List<GroupEntity> getGroupsForResource(final String resourceId, Set<String> filter, int from, int size);
    
    @Deprecated
    int getNumOfGroupsForResource(final String resourceId, Set<String> filter);

}
