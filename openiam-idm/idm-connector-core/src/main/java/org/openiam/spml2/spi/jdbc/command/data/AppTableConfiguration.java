package org.openiam.spml2.spi.jdbc.command.data;

import org.openiam.connector.data.ConnectorConfiguration;

public class AppTableConfiguration extends ConnectorConfiguration {
    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
