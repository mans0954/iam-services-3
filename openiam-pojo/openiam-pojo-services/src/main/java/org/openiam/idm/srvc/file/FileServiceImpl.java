package org.openiam.idm.srvc.file;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

/**
 * Created by alexander on 16/08/16.
 */
@Service
public class FileServiceImpl implements FileService {
    private static final Log log = LogFactory.getLog(FileServiceImpl.class);
    @Value("${iam.files.location}")
    private String absolutePath;

    @Override
    public String getFile(String fName) {
        try {
            return this.get(fName);
        } catch (Exception e) {
            log.error("getFile", e);

        }
        return null;
    }

    @Override
    public File saveFile(String fName, String value) {
        try {
            return this.save(fName, value);
        } catch (Exception e) {
            log.error("Error in saveFile", e);

        }
        return null;
    }

    private String readFile(File file) {
        int ch;
        StringBuffer strContent = new StringBuffer("");
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(file);
            while ((ch = fin.read()) != -1)
                strContent.append((char) ch);
            fin.close();
        } catch (Exception e) {
            log.info(e);
        }
        return strContent.toString();
    }

    private String get(String fName) throws Exception {
        File file = new File(absolutePath + fName);
        if (!file.exists()) {
            log.info("FILE: " + absolutePath + fName + "NOT EXIST");
            return null;
        }
        return readFile(file);
    }

    private File save(String fName, String value) throws Exception {
        FileWriter fw = new FileWriter(absolutePath + fName, false);
        fw.append(value);
        fw.flush();
        fw.close();

        return new File(absolutePath + fName);
    }
}
