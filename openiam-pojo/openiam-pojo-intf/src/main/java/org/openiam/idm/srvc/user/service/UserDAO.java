package org.openiam.idm.srvc.user.service;

import org.openiam.core.dao.BaseDao;
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

    public List<UserEntity> findByDelegationProperties(DelegationFilterSearch search);

    public List<String> getUserIdList(int startPos, int count);

    public Long getUserCount();

    public List<UserEntity> getByExample(UserSearchBean searchBean, int startAt, int size);

    public Long getUserCount(UserSearchBean searchBean);
}
