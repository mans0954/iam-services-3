package org.openiam.imprt.query.expression;

public abstract class Expression {
	protected final String op;

	public Expression(String op) {
		this.op = op;
	}

	public abstract String toSqlString();
}
