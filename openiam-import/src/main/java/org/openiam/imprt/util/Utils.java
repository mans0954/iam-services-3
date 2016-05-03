package org.openiam.imprt.util;

import org.openiam.imprt.constant.ImportPropertiesKey;
import org.openiam.imprt.query.expression.Column;

import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * Clear and set null to <b>collection</b>
     *
     * @param collection
     */
    public static void clear(Collection<?> collection) {
        if (collection != null) {
            collection.clear();
            collection = null;
        }
    }

    /**
     * Clear and set null to <b>collection</b>
     *
     * @param collection
     */
    public static void clear(Map<?, ?> collection) {
        if (collection != null) {
            collection.clear();
            collection = null;
        }
    }

    public static Date getDate(String date) {
        String pattern = DataHolder.getInstance().getProperty(
                ImportPropertiesKey.SIMPLE_DATE_FORMAT);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        java.util.Date nDate = null;
        try {
            nDate = sdf.parse(date);
        } catch (Exception e) {
            System.out.println(e);
        }
        return nDate;
    }

    public static List<Column> getColumns(ImportPropertiesKey[] keys) {
        List<Column> columns = new ArrayList<Column>();
        for (ImportPropertiesKey key : keys) {
            columns.add(new Column(key));
        }
        return columns;
    }

    public static String columnsToSelectFields(List<Column> columns, String alias) {
        StringBuilder sb = new StringBuilder();
        for (Column column : columns) {
            sb.append(alias);
            sb.append(".");
            sb.append(column.getColumn());
            sb.append(", ");
        }

        sb.deleteCharAt(sb.length()-1);
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }
}
