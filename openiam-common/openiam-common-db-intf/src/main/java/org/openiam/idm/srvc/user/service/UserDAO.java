package org.openiam.idm.srvc.user.service;

import org.openiam.core.dao.BaseDao;
import org.openiam.idm.searchbeans.DelegationFilterSearchBean;
import org.openiam.idm.searchbeans.UserSearchBean;
import org.openiam.idm.srvc.user.domain.UserEntity;
import org.openiam.idm.srvc.user.dto.DelegationFilterSearch;
import org.openiam.idm.srvc.user.dto.UserSearch;

import java.util.Date;
import java.util.List;
import org.openiam.idm.srvc.user.dto.UserStatusEnum;

/**
 * Data access interface for domain model class User.
 *
 * @author Suneet Shah
 * @see org.openiam.idm.srvc.user
 */
public interface UserDAO extends BaseDao<UserEntity, String> {

    public List<UserEntity> findByLastUpdateRange(Date startDate, Date endDate);
    @Deprecated
    public List<UserEntity> search(UserSearch search);

    public UserEntity findByIdDelFlt(String userId, DelegationFilterSearchBean delegationFilter);

    public List<UserEntity> findByDelegationProperties(DelegationFilterSearch search);

    public List<String> getUserIdList(int startPos, int count);

    public Long getUserCount();

    public List<UserEntity> getByExample(UserSearchBean searchBean, int startAt, int size);

    public Long getUserCount(UserSearchBean searchBean);
    
    public List<UserEntity> getUsersForResource(final String resourceId, final int from, final int size);
    public int getNumOfUsersForResource(final String resourceId);
    
    public List<UserEntity> getUsersForGroup(final String groupId, final int from, final int size);
    public int getNumOfUsersForGroup(final String groupId);
    
    public List<UserEntity> getUsersForRole(final String roleId, final int from, final int size);
    public int getNumOfUsersForRole(final String roleId);
    
    /**
     * Should only be called when an Organization gets deleted, to disassocate the user from
     * that Organization
     * @param organizationId - organizationId
     */
    public void disassociateUsersFromOrganization(final String organizationId);
    public List<UserEntity> getUsersForMSys(String mSysId);
}
