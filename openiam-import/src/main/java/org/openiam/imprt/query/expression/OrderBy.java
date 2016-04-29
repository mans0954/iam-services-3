package org.openiam.imprt.query.expression;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.constant.OrderByType;
import org.openiam.imprt.util.DataHolder;

public class OrderBy extends Expression {
    private String field;
    private OrderByType type;

    public OrderBy(String field, OrderByType type) {
        super("ORDER BY");
        this.field = field;
        this.type = type;
    }

    @Override
    public String toSqlString() {
        return op + " " + field + " " + type.getValue();
    }

    public static OrderBy orderBy(String field, OrderByType type) {
        return new OrderBy(field, type);
    }

    public static OrderBy orderBy(ImportPropertiesKey field, OrderByType type) {
        return new OrderBy(DataHolder.getInstance().getProperty(field), type);
    }

    public String getField() {
        return field;
    }

    public OrderByType getType() {
        return type;
    }

}
