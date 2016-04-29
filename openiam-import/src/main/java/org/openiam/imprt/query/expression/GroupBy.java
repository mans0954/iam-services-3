package org.openiam.imprt.query.expression;

import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.util.DataHolder;

import java.util.ArrayList;
import java.util.List;

public class GroupBy extends Expression {
    private List<String> field;

    public GroupBy(List<String> field) {
        super("GROUP BY");
        this.field = field;
    }

    public static GroupBy groupBy(List<String> field) {
        return new GroupBy(field);
    }

    public static GroupBy groupByKeys(List<ImportPropertiesKey> field) {
        DataHolder h = DataHolder.getInstance();
        List<String> flist = new ArrayList<String>();
        for (ImportPropertiesKey f : field) {
            flist.add(h.getProperty(f));
        }
        return new GroupBy(flist);
    }

    @Override
    public String toSqlString() {
        StringBuilder sb = new StringBuilder(op);
        sb.append(" ");
        for (int i = 0; i < field.size(); i++) {
            sb.append(field.get(i));
            if (i != field.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
