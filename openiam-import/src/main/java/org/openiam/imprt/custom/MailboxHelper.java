package org.openiam.imprt.custom;

import org.apache.commons.csv.CSVStrategy;
import org.apache.commons.lang.StringUtils;
import org.openiam.imprt.util.CSVHelper;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zaporozhec on 5/4/16.
 */
public class MailboxHelper implements Serializable{

    private static final Map<String, Set<String>> sortedSets;

    static {
        sortedSets = new HashMap<String, Set<String>>();
        sortedSets.put("Small", new HashSet<String>());
        sortedSets.put("Medium", new HashSet<String>());
        sortedSets.put("Regular", new HashSet<String>());
        sortedSets.put("Large", new HashSet<String>());
    }


    public MailboxHelper(InputStream is) throws Exception {

        String activeSet = null;
        String tagContent = null;

        if (is != null) {
            final CSVHelper parser = new CSVHelper(is, "UTF-8", CSVStrategy.EXCEL_STRATEGY);
            final String[][] rows = parser.getAllValues();
            for (int i = 1; i < rows.length; ++i) {
                final String[] row = rows[i];
                if (row.length == 2) {
                    String size = "Regular";
                    if (row[1].startsWith("250 MB")) size = "Small";
                    else if (row[1].startsWith("1 GB")) size = "Medium";
                    else if (row[1].startsWith("10.01 GB")) size = "Large";
                    sortedSets.get(size).add(row[0]);
                }
            }
        }
    }

    public String getBoxSize(String mailServerDN) {
        if (StringUtils.isNotBlank(mailServerDN)) {
            Pattern pattern = Pattern.compile("CN=(.*?),.*");
            Matcher matcher = pattern.matcher(mailServerDN);
            String a = null;
            if (matcher.matches()) {
                a = matcher.group(1);
            }

            String serverName = a == null ? null : a;
            if (serverName != null) {
                for (String str : sortedSets.keySet()) {
                    if (sortedSets.get(str).contains(serverName))
                        return str;
                }
            }
        }
        return null;
    }
}
