package org.openiam.connector.util.connect;

import java.io.File;
import java.io.FileInputStream;

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
}
