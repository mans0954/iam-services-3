package org.openiam.imprt.query;


import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.expression.ComplexExpression;
import org.openiam.imprt.query.expression.Expression;
import org.openiam.imprt.query.expression.MultivalueExpression;
import org.openiam.imprt.query.expression.SimpleExpression;
import org.openiam.imprt.util.DataHolder;

import java.util.List;

public class Restriction {

    public static Expression eq(String name, String value) {
        return new SimpleExpression(name, value, "=");
    }

    public static Expression mr(String name, String value) {
        return new SimpleExpression(name, value, ">");
    }

    public static Expression mrEq(String name, String value) {
        return new SimpleExpression(name, value, ">=");
    }

    public static Expression ls(String name, String value) {
        return new SimpleExpression(name, value, "<");
    }

    public static Expression lsEq(String name, String value) {
        return new SimpleExpression(name, value, "<=");
    }

    public static Expression in(String name, List<String> values) {
        return new MultivalueExpression(name, values, "IN");
    }

    public static Expression mr(ImportPropertiesKey name, String value) {
        return Restriction.mr(DataHolder.getInstance().getProperty(name), value);
    }

    public static Expression mrEq(ImportPropertiesKey name, String value) {
        return Restriction.mrEq(DataHolder.getInstance().getProperty(name), value);
    }

    public static Expression ls(ImportPropertiesKey name, String value) {
        return Restriction.ls(DataHolder.getInstance().getProperty(name), value);
    }

    public static Expression lsEq(ImportPropertiesKey name, String value) {
        return Restriction.lsEq(DataHolder.getInstance().getProperty(name), value);
    }

    public static Expression eq(ImportPropertiesKey name, String value) {
        return new SimpleExpression(DataHolder.getInstance().getProperty(name), value, "=");
    }

    public static Expression notEq(String name, String value) {
        return new SimpleExpression(name, value, "!=");
    }

    public static Expression notEq(ImportPropertiesKey name, String value) {
        return new SimpleExpression(DataHolder.getInstance().getProperty(name), value, "!=");
    }

    public static Expression and(Expression e1, Expression e2) {
        return new ComplexExpression(e1, e2, "AND");
    }

    public static Expression or(Expression e1, Expression e2) {
        return new ComplexExpression(e1, e2, "OR");
    }
}
