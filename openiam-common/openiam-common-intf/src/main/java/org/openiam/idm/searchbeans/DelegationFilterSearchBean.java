package org.openiam.idm.searchbeans;

import java.util.Set;

public class DelegationFilterSearchBean {
    private Set<String> organizationIdSet = null;
    //private Set<String> deptIdSet = null;
    //private Set<String> divisionIdSet = null;
    private Set<String> groupIdSet = null;
    private Set<String> roleIdSet = null;
    private Set<String> appIdSet = null;

    public Set<String> getOrganizationIdSet() {
        return organizationIdSet;
    }

    public void setOrganizationIdSet(Set<String> organizationIdSet) {
        this.organizationIdSet = organizationIdSet;
    }

    /*
    public Set<String> getDeptIdSet() {
        return deptIdSet;
    }

    public void setDeptIdSet(Set<String> deptIdSet) {
        this.deptIdSet = deptIdSet;
    }

    public Set<String> getDivisionIdSet() {
        return divisionIdSet;
    }

    public void setDivisionIdSet(Set<String> divisionIdSet) {
        this.divisionIdSet = divisionIdSet;
    }
    */

    public Set<String> getGroupIdSet() {
        return groupIdSet;
    }

    public void setGroupIdSet(Set<String> groupIdSet) {
        this.groupIdSet = groupIdSet;
    }

    public Set<String> getRoleIdSet() {
        return roleIdSet;
    }

    public void setRoleIdSet(Set<String> roleIdSet) {
        this.roleIdSet = roleIdSet;
    }

    public Set<String> getAppIdSet() {
        return appIdSet;
    }

    public void setAppIdSet(Set<String> appIdSet) {
        this.appIdSet = appIdSet;
    }
}
