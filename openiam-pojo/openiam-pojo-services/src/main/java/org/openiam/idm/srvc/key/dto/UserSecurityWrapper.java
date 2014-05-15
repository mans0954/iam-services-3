package org.openiam.idm.srvc.key.dto;

import org.openiam.core.domain.UserKey;
import org.openiam.idm.srvc.auth.domain.LoginEntity;
import org.openiam.idm.srvc.mngsys.domain.ManagedSysEntity;
import org.openiam.idm.srvc.pswd.domain.PasswordHistoryEntity;
import org.openiam.idm.srvc.pswd.domain.UserIdentityAnswerEntity;

import java.util.List;

/**
 * Created by: Alexander Duckardt
 * Date: 30.10.12
 */
public class UserSecurityWrapper {
    private String userId;
    private List<LoginEntity> loginList;
    private List<UserKey> userKeyList;
    private List<PasswordHistoryEntity> passwordHistoryList;
    private List<ManagedSysEntity>  managedSysList;
    private List<UserIdentityAnswerEntity>  userIdentityAnswerList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<LoginEntity> getLoginList() {
        return loginList;
    }

    public void setLoginList(List<LoginEntity> loginList) {
        this.loginList = loginList;
    }

    public List<UserKey> getUserKeyList() {
        return userKeyList;
    }

    public void setUserKeyList(List<UserKey> userKeyList) {
        this.userKeyList = userKeyList;
    }

    public List<PasswordHistoryEntity> getPasswordHistoryList() {
        return passwordHistoryList;
    }

    public void setPasswordHistoryList(List<PasswordHistoryEntity> passwordHistoryList) {
        this.passwordHistoryList = passwordHistoryList;
    }

    public List<ManagedSysEntity> getManagedSysList() {
        return managedSysList;
    }

    public void setManagedSysList(List<ManagedSysEntity> managedSysList) {
        this.managedSysList = managedSysList;
    }

    public List<UserIdentityAnswerEntity> getUserIdentityAnswerList() {
        return userIdentityAnswerList;
    }

    public void setUserIdentityAnswerList(List<UserIdentityAnswerEntity> userIdentityAnswerList) {
        this.userIdentityAnswerList = userIdentityAnswerList;
    }
}
