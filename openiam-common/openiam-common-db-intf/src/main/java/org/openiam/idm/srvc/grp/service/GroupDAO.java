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
    
    public int getNumOfGroupsForUser(final String userId, Set<String> filter);
    public List<GroupEntity> getGroupsForUser(final String userId, Set<String> filter, final int from, final int size);
    
    public List<GroupEntity> getGroupsForRole(final String roleId, Set<String> filter, int from, int size);
    public int getNumOfGroupsForRole(final String roleId, Set<String> filter);

    public List<GroupEntity> getGroupsForResource(final String resourceId, Set<String> filter, int from, int size);
    public int getNumOfGroupsForResource(final String resourceId, Set<String> filter);
    
    public int getNumOfChildGroups(String groupId, Set<String> filter);
    public int getNumOfParentGroups(String groupId, Set<String> filter);
    
    public List<GroupEntity> getChildGroups(final String groupId, Set<String> filter, final int from, final int size);
    public List<GroupEntity> getParentGroups(final String groupId, Set<String> filter, final int from, final int size);
}
