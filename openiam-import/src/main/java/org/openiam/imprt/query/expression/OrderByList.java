package org.openiam.imprt.query.expression;

import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.constant.OrderByType;
import org.openiam.imprt.util.DataHolder;

import java.util.Arrays;
import java.util.List;

public class OrderByList extends Expression {
    List<OrderBy> orderByList;

    public OrderByList(List<OrderBy> orderByList) {
        super("ORDER BY");
        this.orderByList = orderByList;
    }

    @Override
    public String toSqlString() {
        StringBuilder sb = new StringBuilder();
        sb.append(op);
        sb.append(" ");
        for (int i = 0; i < orderByList.size(); i++) {
            sb.append(orderByList.get(i).getField());
            sb.append(" ");
            sb.append(orderByList.get(i).getType().getValue());
            if (i < orderByList.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static OrderByList orderBy(List<OrderBy> orderByList) {
        return new OrderByList(orderByList);
    }

    public static OrderByList orderBy(OrderBy orderBy) {
        return new OrderByList(Arrays.asList(orderBy));
    }

    public static OrderByList orderBy(String key, OrderByType obt) {
        return new OrderByList(Arrays.asList(new OrderBy(key, obt)));
    }

    public static OrderByList orderBy(ImportPropertiesKey key, OrderByType obt) {
        return new OrderByList(Arrays.asList(new OrderBy(DataHolder.getInstance().getProperty(key), obt)));
    }

    public static void main(String[] args) {
        List<OrderBy> obl = Arrays
                .asList(OrderBy.orderBy("A", OrderByType.ASC), OrderBy.orderBy("D", OrderByType.DESC));
        System.out.println(OrderByList.orderBy(obl).toSqlString());
    }
}
