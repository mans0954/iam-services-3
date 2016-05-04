package org.openiam.imprt.query;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.util.DataHolder;

public class UpdateQueryBuilder extends AbstractQueryBuilder {
    private String pkColumnName;

    public UpdateQueryBuilder(String tableName, String pkColumnName) {
        super(tableName);
        this.pkColumnName = pkColumnName;
    }

    public UpdateQueryBuilder(String tableName, String pkColumnName, Column... columns) {
        super(tableName, columns);
        this.pkColumnName = pkColumnName;
    }

    public UpdateQueryBuilder(ImportPropertiesKey tableName, String pkColumnName, Column... columns) {
        super(tableName, columns);
        this.pkColumnName = pkColumnName;
    }

    public UpdateQueryBuilder(ImportPropertiesKey tableName, String pkColumnName, int size, Column... columns) {
        super(tableName, size, columns);
        this.pkColumnName = pkColumnName;
    }

    public UpdateQueryBuilder addRestriction(Expression e) {
        this.e = e;
        return this;
    }

    public String toSqlString() {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        boolean isMySql = JDBC_DRIVER.equals(DataHolder.getInstance().getProperty(ImportPropertiesKey.JDBC_DRIVER));
        if (!isMySql)
            this.size = 1;
        String update = "UPDATE %s SET %s WHERE %s = %s";

        StringBuilder cols = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            cols.append(columns.get(i).getColumn());
            cols.append("=?");
            if (i < columns.size() - 1) {
                cols.append(',');
            }
        }
        return String.format(update, tableName, cols, pkColumnName, "?");
    }
}
