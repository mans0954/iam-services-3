package org.openiam.imprt.query.expression;

public class ComplexExpression extends Expression {
	private final Expression e1;
	private final Expression e2;

	public ComplexExpression(Expression e1, Expression e2, String op) {
		super(op);
		this.e1 = e1;
		this.e2 = e2;
	}

	public String toSqlString() {
		return "(" + e1.toSqlString() + " " + op + " " + e2.toSqlString() + ")";
	}
}
