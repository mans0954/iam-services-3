package org.openiam.idm.srvc.grp.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.srvc.grp.domain.GroupEntity;
import org.openiam.idm.srvc.grp.dto.Group;

import java.util.List;


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
    
    public List<GroupEntity> findGroupsForUser(final String userId, final int from, final int size);
    
    public List<GroupEntity> getGroupsForRole(String roleId, int from, int size);
    public int getNumOfGroupsForRole(String roleId);
}
