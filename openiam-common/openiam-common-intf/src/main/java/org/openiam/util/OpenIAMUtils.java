package org.openiam.util;

/**
 * Created by alexander on 26/07/16.
 */
public class OpenIAMUtils {
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static String byteArrayToString(byte[] data) {
        return byteArrayToString(data,'0');
    }

    public static String byteArrayToString(byte[] data, char separator) {
        int size = data.length;
        char[] chars = null;
        if (separator == '0') {
            chars = new char[2 * size];
            for (int i = 0, j = 0; i < size; ++i, j += 2) {
                chars[j] = HEX_CHARS[(data[i] & 0xF0) >>> 4];
                chars[j + 1] = HEX_CHARS[data[i] & 0x0F];
            }
        } else {
            chars = new char[3 * size + 1];
            chars[0] = separator;
            for (int i = 0, j = 1; i < size; ++i, j += 3) {
                chars[j] = HEX_CHARS[(data[i] & 0xF0) >>> 4];
                chars[j + 1] = HEX_CHARS[data[i] & 0x0F];
                chars[j + 2] = separator;
            }
        }
        String result = new String(chars);
        if (separator != '0')
            result = result.substring(0, result.lastIndexOf(separator));
        return result.trim();
    }
}
