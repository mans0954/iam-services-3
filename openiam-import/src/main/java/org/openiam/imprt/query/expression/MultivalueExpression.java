package org.openiam.imprt.query.expression;

import java.util.List;

public class MultivalueExpression extends Expression {
    private final String property;
    private final List<String> values;

    public MultivalueExpression(String property, List<String> values, String op) {
        super(op);
        this.property = property;
        this.values = values;
    }

    @Override
    public String toSqlString() {
        StringBuilder sb = new StringBuilder();
        sb.append(property);
        sb.append(" ");
        sb.append(op);
        sb.append(" ");
        sb.append("(");
        for (int i = 0; i < values.size(); i++) {
            sb.append(values.get(i));
            if (i < (values.size() - 1)) {
                sb.append(',');
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
