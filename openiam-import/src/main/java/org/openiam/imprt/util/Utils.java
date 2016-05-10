package org.openiam.imprt.util;

import org.openiam.imprt.constant.ImportPropertiesKey;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

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
}
