package org.openiam.idm.searchbeans;

import java.util.ArrayList;
import java.util.List;

public class PotentialSupSubSearchBean extends UserSearchBean {
    private List<String> targetUserIds;

    public List<String> getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(List<String> targetUserIds) {
        this.targetUserIds = targetUserIds;
    }

    public void addTargetUserId(String userId) {
        if (targetUserIds == null)
            targetUserIds = new ArrayList<String>(0);
        targetUserIds.add(userId);
    }
}
