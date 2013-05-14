package org.openiam.idm.srvc.grp.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.MembershipGroupSearchBean;
import org.openiam.idm.srvc.grp.domain.GroupEntity;

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
    
//    public int getNumOfGroupsForUser(final MembershipGroupSearchBean searchBean);
//    public List<GroupEntity> getGroupsForUser(final MembershipGroupSearchBean searchBean, final int from, final int size);
    
//    public List<GroupEntity> getGroupsForRole(MembershipGroupSearchBean searchBean, int from, int size);
//    public int getNumOfGroupsForRole(MembershipGroupSearchBean searchBean);

    public List<GroupEntity> getEntitlementGroups(MembershipGroupSearchBean searchBean, int from, int size);
    public int getNumOfEntitlementGroups(MembershipGroupSearchBean searchBean);
    
    public int getNumOfChildGroups(MembershipGroupSearchBean searchBean);
    public int getNumOfParentGroups(MembershipGroupSearchBean searchBean);
    
    public List<GroupEntity> getChildGroups(final MembershipGroupSearchBean searchBean, final int from, final int size);
    public List<GroupEntity> getParentGroups(final MembershipGroupSearchBean searchBean, final int from, final int size);
}
