package org.openiam.idm.srvc.user.service;

import java.util.Date;
import java.util.List;

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

    /**
     * Should only be called when an Organization gets deleted, to disassocate
     * the user from that Organization
     * 
     * @param organizationId
     *            - organizationId
     */
    public void disassociateUsersFromOrganization(final String organizationId);

    public List<UserEntity> getUsersForMSys(String mSysId);
}
