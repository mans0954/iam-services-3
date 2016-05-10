package org.openiam.idm.parser.csv;


import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class CSVUtil {

    public static List<String> merge(InputStream oldData, InputStream newData) {
        return merge(read(oldData), read(newData));
    }

    public static List<String> merge(List<String> oldAllValue, List<String> newAllValue) {

        if (oldAllValue == null || oldAllValue.isEmpty())
            return newAllValue;

        if (newAllValue == null || newAllValue.isEmpty())
            return oldAllValue;

        //

        String oldHeaders = null;
        List<String> oldBody = new ArrayList<>();

        if (oldAllValue.size() > 0)
            oldHeaders = oldAllValue.get(0);

        if (oldAllValue.size() > 1)
            oldBody.addAll(oldAllValue.subList(1, oldAllValue.size()));

        //

        String newHeaders = null;
        List<String> newBody = new ArrayList<>();

        if (newAllValue.size() > 0)
            newHeaders = newAllValue.get(0);

        if (newAllValue.size() > 1)
            newBody.addAll(newAllValue.subList(1, newAllValue.size()));

        //

        List<Map<String, String>> sortValue = new ArrayList<>();
        sortValue.addAll(sortDataByColumnName(oldBody, oldHeaders.split(",")));
        sortValue.addAll(sortDataByColumnName(newBody, newHeaders.split(",")));

        List<String> columns = new ArrayList<>();
        columns.addAll(Arrays.asList(oldHeaders.split(",")));

        if (!equalsHeaders(oldHeaders, newHeaders))
            columns.addAll(getNewHeaders(oldHeaders, newHeaders));

        //

        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i <= columns.size() - 1; i++) {
            sb.append(columns.get(i) != null ? columns.get(i) : "");
            sb.append(columns.size() - 1 == i ? "" : ",");
        }
        result.add(sb.toString());
        result.addAll(buildData(sortValue, columns));

        return result;

    }

    public static List<String> read(InputStream input) {
        List<String> result = new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static List<String> buildData(List<Map<String, String>> values, List<String> columns) {
        if (values == null || values.isEmpty())
            return null;

        List<String> result = new ArrayList<>();

        for (Map<String, String> vals : values) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i <= columns.size() - 1; i++) {
                String val = vals.get(columns.get(i));
                sb.append(val != null ? val : "").append(columns.size() - 1 == i ? "" : ",");
            }
            result.add(sb.toString());
        }

        return result;
    }

    private static List<Map<String, String>> sortDataByColumnName(List<String> value, String[] columns) {
        if (value == null || value.isEmpty())
            return null;

        List<Map<String, String>> result = new ArrayList<>();

        for (String line : value) {
            String[] val = line.split(",");
            Map<String, String> items = new HashMap<>();

            for (int i = 0; i <= val.length - 1; i++)
                items.put(columns[i], val[i]);

            result.add(items);
        }

        return result;
    }

    private static boolean equalsHeaders(String oldHeaders, String newHeaders) {

        Set<String> oldH = new HashSet<>(Arrays.asList(oldHeaders.split(",")));
        Set<String> newH = new HashSet<>(Arrays.asList(newHeaders.split(",")));

        if (oldH.size() != newH.size() || !oldH.equals(newH))
            return false;

        return true;
    }

    private static List<String> getNewHeaders(String oldHeaders, String newHeaders) {

        List<String> columns = new ArrayList<String>();
        Set<String> oldH = new HashSet<>(Arrays.asList(oldHeaders.split(",")));

        for (String head : newHeaders.split(","))
            if (!oldH.contains(head))
                columns.add(head);

        return columns;
    }

    public static String toCSV(String val) throws JSONException {
        JSONArray arr = new JSONArray();
        arr.put(new JSONObject(val));
        return toCSV(arr);
    }

    public static String toCSV(JSONArray ja) throws JSONException {
        JSONObject jo = ja.optJSONObject(0);
        if (jo != null) {
            JSONArray names = jo.names();
            if (names != null) {
                return rowToCSV(names) + toCSV(names, ja);
            }
        }

        return null;
    }

    private static String toCSV(JSONArray names, JSONArray ja) throws JSONException {
        if (names != null && names.length() != 0) {
            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < ja.length(); ++i) {
                JSONObject jo = ja.optJSONObject(i);
                if (jo != null) {
                    sb.append(rowToCSV(jo.toJSONArray(names)));
                }
            }

            return sb.toString();
        } else {
            return null;
        }
    }

    private static String rowToCSV(JSONArray ja) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < ja.length(); ++i) {
            if (i > 0) {
                sb.append(',');
            }

            Object o = ja.opt(i);
            if (o != null) {
                String s = o.toString();
                if (s.length() > 0 && (s.indexOf(44) >= 0 || s.indexOf(10) >= 0 || s.indexOf(13) >= 0 || s.indexOf(0) >= 0 || s.charAt(0) == 34)) {
                    sb.append('\"');
                    int length = s.length();

                    for (int j = 0; j < length; ++j) {
                        char c = s.charAt(j);
                        if (c == 44) {
                            sb.append(";");
                        } else if (c >= 32 && c != 34) {
                            sb.append(c);
                        }
                    }

                    sb.append('\"');
                } else {
                    sb.append(s);
                }
            }
        }

        sb.append('\n');
        return sb.toString();
    }
}
