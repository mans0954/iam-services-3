package org.openiam.idm.srvc.user.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;

/**
 * Data access interface for domain model class User.
 * 
 * @author Suneet Shah
 * @see org.openiam.idm.srvc.user
 */
public interface UserDAO extends BaseDao<UserEntity, String> {

    public List<UserEntity> findByLastUpdateRange(Date startDate, Date endDate);

    public UserEntity findByIdDelFlt(String userId, DelegationFilterSearchBean delegationFilter);

    public List<UserEntity> findByDelegationProperties(DelegationFilterSearch search);

    public List<String> getUserIdList(int startPos, int count);

    public Long getUserCount();

    public List<UserEntity> getByExample(UserSearchBean searchBean, int startAt, int size);

    public Long getUserCount(UserSearchBean searchBean);

    public List<UserEntity> getUsersForResource(final String resourceId, DelegationFilterSearchBean delegationFilter, final int from, final int size);

    public int getNumOfUsersForResource(final String resourceId, DelegationFilterSearchBean delegationFilter);

    public List<UserEntity> getUsersForGroup(final String groupId, DelegationFilterSearchBean delegationFilter, final int from, final int size);

    public int getNumOfUsersForGroup(final String groupId, DelegationFilterSearchBean delegationFilter);

    public List<UserEntity> getUsersForRole(final String roleId, DelegationFilterSearchBean delegationFilter, final int from, final int size);

    public int getNumOfUsersForRole(final String roleId, DelegationFilterSearchBean delegationFilter);

    public List<UserEntity> getUsersForMSys(String mSysId);

    public List<UserEntity> getSuperiors(String userId, final int from, final int size);

    public int getSuperiorsCount(String userId);

    public List<UserEntity> getSubordinates(String userId, final int from, final int size);

    public int getSubordinatesCount(String userId);

    public List<String> getAllAttachedSupSubIds(String userId);

    public List<UserEntity> getUsersForOrganization(final String organizationId, DelegationFilterSearchBean delegationFilter, final int from, final int size);
    
    public List<String> getUserIdsForRoles(final Set<String> roleIds, final int from, final int size);
    
    public List<String> getUserIdsForGroups(final Set<String> groupIds, final int from, final int size);

    public List<String> getUserIdsForOrganizations(final Set<String> organizationIds, final int from, final int size);
    
    public List<String> getUserIdsForResources(final Set<String> resourceIds, final int from, final int size);
}
