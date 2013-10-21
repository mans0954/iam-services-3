package org.openiam.connector.util.connect;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileUtil {

    public static String get(String pathToFile) throws Exception {
        File file = new File(pathToFile);
        if (!file.exists()) {
            throw new Exception("FILE: " + pathToFile + "NOT EXIST");
        }
        int ch;
        StringBuffer strContent = new StringBuffer("");
        FileInputStream fin = null;
        fin = new FileInputStream(file);
        while ((ch = fin.read()) != -1)
            strContent.append((char) ch);
        fin.close();
        return strContent.toString();
    }

    public static String getMD5Sum(String pathToFile) throws Exception {
        String plaintext = get(pathToFile);
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(plaintext.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }
}
