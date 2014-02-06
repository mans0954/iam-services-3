package org.openiam.connector.jdbc.command.data;

import org.openiam.connector.common.data.ConnectorConfiguration;

public class AppTableConfiguration extends ConnectorConfiguration {
    private String userTableName;
    private String groupTableName;
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
}
