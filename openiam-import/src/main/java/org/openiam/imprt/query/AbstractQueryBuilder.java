package org.openiam.imprt.query;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.expression.Column;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.query.expression.GroupBy;
import org.openiam.imprt.query.expression.OrderByList;
import org.openiam.imprt.util.DataHolder;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQueryBuilder {
    protected String tableName;
    protected List<Column> columns = new ArrayList<Column>();
    protected Expression e;
    protected OrderByList orderBy;
    protected GroupBy groupBy;
    int size;

    public AbstractQueryBuilder(String tableName) {
        this.tableName = tableName;
    }

    public AbstractQueryBuilder(String tableName, Column... columns) {
        this.tableName = tableName;
        if (this.columns == null) {
            this.columns = new ArrayList<Column>();
        }
        if (columns != null)
            for (Column col : columns) {
                this.columns.add(col);
            }
    }

    public AbstractQueryBuilder(ImportPropertiesKey tableName, int size, Column... columns) {
        DataHolder h = DataHolder.getInstance();
        this.tableName = h.getProperty(tableName);
        if (this.columns == null) {
            this.columns = new ArrayList<Column>();
        }
        for (Column col : columns) {
            this.columns.add(col);
        }
        this.size = size;
    }

    public AbstractQueryBuilder(ImportPropertiesKey tableName, Column... columns) {
        DataHolder h = DataHolder.getInstance();
        this.tableName = h.getProperty(tableName);
        if (this.columns == null) {
            this.columns = new ArrayList<Column>();
        }
        for (Column col : columns) {
            this.columns.add(col);
        }
        size = 1;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public String getTableName() {
        return tableName;
    }

    public AbstractQueryBuilder setTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public AbstractQueryBuilder addColumn(Column column) {
        if (this.columns == null) {
            this.columns = new ArrayList<Column>();
        }
        this.columns.add(column);
        return this;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupBy groupBy) {
        this.groupBy = groupBy;
    }

}
