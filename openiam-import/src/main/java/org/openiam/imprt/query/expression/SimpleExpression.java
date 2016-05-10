package org.openiam.imprt.query.expression;

import java.util.Date;

public class SimpleExpression extends Expression {
	private final String property;
	private final Object value;

	public SimpleExpression(String property, Object value, String op) {
		super(op);
		this.property = property;
		this.value = value;
	}

	public String toSqlString() {
		boolean needQuote=value instanceof String || value instanceof Date;

		StringBuilder sb =new StringBuilder("(");
		sb.append(property);
		sb.append(op);

		if(needQuote){
			sb.append("'");
		}
		sb.append(value);
		if(needQuote){
			sb.append("'");
		}
		sb.append(")");

		return sb.toString();
	}
}
