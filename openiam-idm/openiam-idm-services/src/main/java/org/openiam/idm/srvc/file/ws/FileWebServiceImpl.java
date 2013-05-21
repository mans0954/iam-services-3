/**
 * @author zaporozhec
 */
package org.openiam.idm.srvc.file.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.idm.srvc.file.ws.FileWebService", targetNamespace = "urn:idm.openiam.org/srvc/file/ws", portName = "FileWebServicePort", serviceName = "FileWebService")
@Service("fileWebService")
public class FileWebServiceImpl implements FileWebService {

    @Value("${iam.files.location}")
    private String absolutePath;

    private static final Log log = LogFactory.getLog(FileWebServiceImpl.class);

    @Override
    public String getFile(
            @WebParam(name = "fileName", targetNamespace = "") String fName) {
        try {
            return this.get(fName);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public File saveFile(
            @WebParam(name = "fileName", targetNamespace = "") String fName,
            @WebParam(name = "fileContent", targetNamespace = "") String value) {
        try {
            return this.save(fName, value);
        } catch (Exception e) {
            e.printStackTrace();

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
