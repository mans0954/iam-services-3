package org.openiam.imprt.query;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.query.expression.GroupBy;
import org.openiam.imprt.query.expression.OrderByList;

public class SelectQueryBuilder extends AbstractQueryBuilder {

    public SelectQueryBuilder(String tableName) {
        super(tableName);
    }

    public SelectQueryBuilder(String tableName, Column... columns) {
        super(tableName, columns);
    }

    public SelectQueryBuilder(ImportPropertiesKey tableName, Column... columns) {
        super(tableName, columns);
    }

    public SelectQueryBuilder addRestriction(Expression e) {
        this.e = e;
        return this;
    }

    public SelectQueryBuilder orderBy(OrderByList e) {
        this.orderBy = e;
        return this;
    }

    public SelectQueryBuilder groupBy(GroupBy e) {
        this.groupBy = e;
        return this;
    }

    public String toSqlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for (int i = 0; i < columns.size(); i++) {
            sb.append(columns.get(i).toString());
            if (i != columns.size() - 1) {
                sb.append(", ");
            } else {
                sb.append(" ");
            }
        }
        sb.append("FROM ");
        sb.append(tableName);
        if (e != null) {
            sb.append(" WHERE ");
            sb.append(e.toSqlString());
        }
        if (groupBy != null) {
            sb.append(" ");
            sb.append(groupBy.toSqlString());
        }
        if (orderBy != null) {
            sb.append(" ");
            sb.append(orderBy.toSqlString());
        }
        return sb.toString();
    }
}
