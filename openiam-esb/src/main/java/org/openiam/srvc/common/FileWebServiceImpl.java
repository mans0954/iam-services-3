/**
 * @author zaporozhec
 */
package org.openiam.srvc.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openiam.idm.srvc.file.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@WebService(endpointInterface = "org.openiam.srvc.common.FileWebService", targetNamespace = "urn:idm.openiam.org/srvc/file/ws", portName = "FileWebServicePort", serviceName = "FileWebService")
@Service("fileWebService")
public class FileWebServiceImpl implements FileWebService {
    @Autowired
    private FileService fileService;

    private static final Log log = LogFactory.getLog(FileWebServiceImpl.class);

    @Override
    public String getFile(
            @WebParam(name = "fileName", targetNamespace = "") String fName) {
        return fileService.getFile(fName);
    }

    @Override
    public File saveFile(
            @WebParam(name = "fileName", targetNamespace = "") String fName,
            @WebParam(name = "fileContent", targetNamespace = "") String value) {
        return fileService.saveFile(fName, value);
    }



}
