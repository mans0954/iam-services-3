package org.openiam.idm.srvc.user.service;

import org.openiam.base.ws.SortParam;
import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.SearchAttribute;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Data access interface for domain model class User.
 * 
 * @author Suneet Shah
 * @see org.openiam.idm.srvc.user
 */
public interface UserDAO extends BaseDao<UserEntity, String> {

    UserEntity findByIdDelFlt(String userId, DelegationFilterSearchBean delegationFilter);

    List<UserEntity> findByDelegationProperties(DelegationFilterSearch search);

    List<String> getUserIdList(int startPos, int count);

    Long getUserCount();

    List<UserEntity> getByExample(UserSearchBean searchBean, int startAt, int size);

    Long getUserCount(UserSearchBean searchBean);

    List<UserEntity> getUsersForResource(final String resourceId, DelegationFilterSearchBean delegationFilter, List<SortParam> sortParamList, final int from, final int size);

    int getNumOfUsersForResource(final String resourceId, DelegationFilterSearchBean delegationFilter);

    List<UserEntity> getUsersForGroup(final String groupId, DelegationFilterSearchBean delegationFilter, final int from, final int size);

    @Deprecated
    int getNumOfUsersForGroup(final String groupId, DelegationFilterSearchBean delegationFilter);

    List<UserEntity> getUsersForRole(final String roleId, DelegationFilterSearchBean delegationFilter, final int from, final int size);

    @Deprecated
    int getNumOfUsersForRole(final String roleId, DelegationFilterSearchBean delegationFilter);

    List<UserEntity> getSuperiors(String userId, final int from, final int size);

    int getSuperiorsCount(String userId);

    List<UserEntity> getAllSuperiors(final int from, final int size);

    int getAllSuperiorsCount();

    List<UserEntity> getSubordinates(String userId, final int from, final int size);

    int getSubordinatesCount(String userId);

    List<String> getSubordinatesIds(String userId);

    List<String> getAllAttachedSupSubIds(List<String> userIds);

    List<UserEntity> getUsersForOrganization(final String organizationId, DelegationFilterSearchBean delegationFilter, final int from,
                                             final int size);

    List<String> getUserIdsForRoles(final Set<String> roleIds, final int from, final int size);

    List<String> getUserIdsForGroups(final Set<String> groupIds, final int from, final int size);

    List<String> getUserIdsForOrganizations(final Set<String> organizationIds, final int from, final int size);

    List<String> getUserIdsForResources(final Set<String> resourceIds, final int from, final int size);

    List<String> getUserIdsForAttributes(final List<SearchAttribute> searchAttributeSet, final int from, final int size);

    boolean isUserInGroup(final String userId, final String groupId);

    boolean isUserInRole(final String userId, final String roleId);

    boolean isUserInOrg(final String userId, final String orgId);

    boolean isUserEntitledToResoruce(final String userId, final String resourceId);

    UserEntity findPrimarySupervisor(String employeeId);

    List<UserEntity> getUserByLastDate(Date lastDate);

    List<UserEntity> getByEmail(String email);

    List<UserEntity> findByIds(List<String> idCollection, UserSearchBean searchBean);
    
    List<UserEntity> getUsersForMSys(final String mSysId);
}
