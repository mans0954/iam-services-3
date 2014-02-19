package org.openiam.connector.jdbc.command.data;

import org.openiam.connector.common.data.ConnectorConfiguration;

public class AppTableConfiguration extends ConnectorConfiguration {
    private String userTableName;
    private String groupTableName;
    private String userGroupTableName;
    private String userGroupTableNameUserId;
    private String userGroupTableNameGroupId;
    private String groupGroupTableName;
    private String groupGroupTableNameGroupId;
    private String groupGroupTableNameGroupChildId;
    private String roleTableName;
    private String emailTableName;

    public void setUserTableName(String tableName) {
        this.userTableName = tableName;
    }

    public String getGroupTableName() {
        return groupTableName;
    }

    public void setGroupTableName(String groupTableName) {
        this.groupTableName = groupTableName;
    }

    public String getRoleTableName() {
        return roleTableName;
    }

    public void setRoleTableName(String roleTableName) {
        this.roleTableName = roleTableName;
    }

    public String getEmailTableName() {
        return emailTableName;
    }

    public void setEmailTableName(String emailTableName) {
        this.emailTableName = emailTableName;
    }

    public String getUserTableName() {
        return userTableName;
    }

    public String getUserGroupTableName() {
        return userGroupTableName;
    }

    public void setUserGroupTableName(String userGroupTableName) {
        this.userGroupTableName = userGroupTableName;
    }

    public String getUserGroupTableNameUserId() {
        return userGroupTableNameUserId;
    }

    public void setUserGroupTableNameUserId(String userGroupTableNameUserId) {
        this.userGroupTableNameUserId = userGroupTableNameUserId;
    }

    public String getUserGroupTableNameGroupId() {
        return userGroupTableNameGroupId;
    }

    public void setUserGroupTableNameGroupId(String userGroupTableNameGroupId) {
        this.userGroupTableNameGroupId = userGroupTableNameGroupId;
    }

    public String getGroupGroupTableName() {
        return groupGroupTableName;
    }

    public void setGroupGroupTableName(String groupGroupTableName) {
        this.groupGroupTableName = groupGroupTableName;
    }

    public String getGroupGroupTableNameGroupId() {
        return groupGroupTableNameGroupId;
    }

    public void setGroupGroupTableNameGroupId(String groupGroupTableNameGroupId) {
        this.groupGroupTableNameGroupId = groupGroupTableNameGroupId;
    }

    public String getGroupGroupTableNameGroupChildId() {
        return groupGroupTableNameGroupChildId;
    }

    public void setGroupGroupTableNameGroupChildId(String groupGroupTableNameGroupChildId) {
        this.groupGroupTableNameGroupChildId = groupGroupTableNameGroupChildId;
    }
}
