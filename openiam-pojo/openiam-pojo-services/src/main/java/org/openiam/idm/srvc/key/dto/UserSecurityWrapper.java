package org.openiam.idm.srvc.key.dto;

import org.openiam.core.domain.UserKey;
import org.openiam.idm.srvc.auth.dto.Login;
import org.openiam.idm.srvc.mngsys.dto.ManagedSys;
import org.openiam.idm.srvc.pswd.dto.PasswordHistory;

import java.util.List;
import java.util.Set;

/**
 * Created by: Alexander Duckardt
 * Date: 30.10.12
 */
public class UserSecurityWrapper {
    private String userId;
    private List<Login> loginList;
    private List<UserKey> userKeyList;
    private List<PasswordHistory> passwordHistoryList;
    private List<ManagedSys>  managedSysList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Login> getLoginList() {
        return loginList;
    }

    public void setLoginList(List<Login> loginList) {
        this.loginList = loginList;
    }

    public List<UserKey> getUserKeyList() {
        return userKeyList;
    }

    public void setUserKeyList(List<UserKey> userKeyList) {
        this.userKeyList = userKeyList;
    }

    public List<PasswordHistory> getPasswordHistoryList() {
        return passwordHistoryList;
    }

    public void setPasswordHistoryList(List<PasswordHistory> passwordHistoryList) {
        this.passwordHistoryList = passwordHistoryList;
    }

    public List<ManagedSys> getManagedSysList() {
        return managedSysList;
    }

    public void setManagedSysList(List<ManagedSys> managedSysList) {
        this.managedSysList = managedSysList;
    }
}
