package org.openiam.spml2.spi.jdbc.command.data;

public class AppTableConfiguration {
    private String resourceId;
    private String tableName;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
