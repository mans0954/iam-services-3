package org.openiam.idm.srvc.auth.login;import org.openiam.core.dao.BaseDao;import org.openiam.idm.srvc.auth.domain.LoginEntity;import org.openiam.idm.srvc.user.dto.UserStatusEnum;import java.util.Date;import java.util.List;/** * Data access interface for domain model class Login. * * @author Suneet Shah */public interface LoginDAO extends BaseDao<LoginEntity, String>{    List<LoginEntity> findUser(String userId);//    List<LoginEntity> findLoginByDomain(String domain);    int changeIdentity(String principal, String pswd, String userId, String managedSysId);    int bulkUnlock(UserStatusEnum status, int autoUnlockTime);    int bulkResetPasswordChangeCount();    public List<LoginEntity> findLockedUsers(Date startTime);    List<LoginEntity> findInactiveUsers(int startDays, int endDays, String managedSysId);    List<LoginEntity> findUserNearPswdExp(int daysToExpiration);    List<LoginEntity> findUserPswdExpYesterday();    /**     * Returns a list of Login objects for the managed system specified by the sysId     *     * @param managedSysId     * @return     */    @Deprecated    List<LoginEntity> findAllLoginByManagedSys(String managedSysId);    List<LoginEntity> findLoginByManagedSys(String principalName, String managedSysId);    LoginEntity findByPasswordResetToken(String token);    List<LoginEntity> getLoginSublist(int startPos, int size);    Long getLoginCount();        public LoginEntity getRecord(final String login, final String managedSysId);    List<LoginEntity> findByUserIds(List<String> userIds, String defaultManagedSysId);}