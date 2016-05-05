package org.openiam.imprt.query;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.util.DataHolder;

public class AddQueryBuilder extends AbstractQueryBuilder {

    public AddQueryBuilder(String tableName) {
        super(tableName);
    }

    public AddQueryBuilder(String tableName, Column... columns) {
        super(tableName, columns);
    }

    public AddQueryBuilder(ImportPropertiesKey tableName, Column... columns) {
        super(tableName, columns);
    }

    public AddQueryBuilder(ImportPropertiesKey tableName, int size, Column... columns) {
        super(tableName, size, columns);
    }

    public AddQueryBuilder addRestriction(Expression e) {
        this.e = e;
        return this;
    }

    public String toSqlString() {
        final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        boolean isMySql = JDBC_DRIVER.equals(DataHolder.getInstance().getProperty(ImportPropertiesKey.JDBC_DRIVER));
        if (!isMySql)
            this.size = 1;
        String insert = "INSERT INTO %s (%s) VALUES %s";

        StringBuilder cols = new StringBuilder();
        StringBuilder vals = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            cols.append(columns.get(i).getColumn());
            if (i < columns.size() - 1) {
                cols.append(',');
            }
        }
        for (int j = 0; j < size; j++) {
            vals.append("(");
            for (int i = 0; i < columns.size(); i++) {
                vals.append("?");
                if (i < columns.size() - 1) {
                    vals.append(',');
                }
            }
            vals.append(")");
            if (j < size - 1) {
                vals.append(',');
            }
        }
        return String.format(insert, tableName, cols, vals);
    }
}
